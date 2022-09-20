import { Injectable } from '@angular/core';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { OnlineGameCreationParams } from '@app/game-logic/game/games/game-creator/game-creation-params';
import { GameCreatorService } from '@app/game-logic/game/games/game-creator/game-creator.service';
import { PlayerInfoForfeit } from '@app/game-logic/game/games/online-game/game-state';
import { OnlineGame } from '@app/game-logic/game/games/online-game/online-game';
import { MessagesService } from '@app/game-logic/messages/messages.service';
import { OnlineChatHandlerService } from '@app/game-logic/messages/online-chat-handler/online-chat-handler.service';
import { Player } from '@app/game-logic/player/player';
import { GameSocketHandlerService } from '@app/socket-handler/game-socket-handler/game-socket-handler.service';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { UserAuth } from '@app/socket-handler/interfaces/user-auth.interface';
import { Observable, Subject } from 'rxjs';
@Injectable({
    providedIn: 'root',
})
export class GameManagerService {
    private game: OnlineGame | undefined;

    private newGameSubject = new Subject<void>();
    get newGame$(): Observable<void> {
        return this.newGameSubject;
    }

    private disconnectedFromServerSubject = new Subject<void>();
    get disconnectedFromServer$(): Observable<void> {
        return this.disconnectedFromServerSubject;
    }

    get forfeitGameState$(): Observable<PlayerInfoForfeit> {
        return this.gameSocketHandler.forfeitGameState$;
    }

    constructor(
        private messageService: MessagesService,
        private info: GameInfoService,
        private gameSocketHandler: GameSocketHandlerService,
        private onlineChat: OnlineChatHandlerService,
        private gameCreator: GameCreatorService,
    ) {
        this.gameSocketHandler.disconnectedFromServer$.subscribe(() => {
            this.disconnectedFromServerSubject.next();
        });

        this.forfeitGameState$.subscribe((playerInfo: PlayerInfoForfeit) => {
            this.updatePlayerInfo(playerInfo);
        });
    }

    updatePlayerInfo(playerInfo: PlayerInfoForfeit) {
        if (!this.game) {
            return;
        }
        const newName = playerInfo.name;
        const previousName = playerInfo.previousPlayerName;
        const index = this.game.players.findIndex((player) => player.name === previousName);

        this.game.players[index].name = newName;
        const playerRef = this.game.playersWithIndex.get(previousName)?.player;
        if (!playerRef) {
            return;
        }
        this.game.playersWithIndex.delete(playerInfo.name);
        this.game.playersWithIndex.set(newName, { index, player: playerRef });
    }

    joinOnlineGame(userAuth: UserAuth, gameSettings: OnlineGameSettings) {
        if (this.game) {
            this.stopGame();
        }
        if (gameSettings.playerNames.length === 0) {
            throw Error('No opponent name was entered');
        }

        const username = userAuth.playerName;
        const timePerTurn = Number(gameSettings.timePerTurn);
        const gameCreationParams: OnlineGameCreationParams = { id: gameSettings.id, timePerTurn, username };

        this.game = this.createOnlineGame(gameCreationParams, gameSettings.gameMode);

        const players = this.createOnlinePlayers(username, gameSettings.playerNames);
        this.allocatePlayers(players);
        this.game.handleUserActions();
        this.info.receiveGame(this.game);
        this.onlineChat.joinChatRoomWithUser(userAuth.gameToken);
        this.gameSocketHandler.joinGame(userAuth);
    }

    startGame(): void {
        this.resetServices();
        if (!this.game) {
            throw Error('No game created yet');
        }
    }

    stopGame(): void {
        this.game?.stop();
        this.onlineChat.leaveChatRoom();
        this.resetServices();
        this.game = undefined;
    }

    private resetServices() {
        this.messageService.clearLog();
    }

    private createOnlinePlayers(userName: string, allPlayerNames: string[]): Player[] {
        const players = allPlayerNames.map((playerName) => new Player(playerName));
        const player = players.find((playerRef) => playerRef.name === userName);
        if (player) {
            this.info.receivePlayer(player);
        }
        return players;
    }

    private allocatePlayers(players: Player[]) {
        if (!this.game) {
            return;
        }
        this.game.players = players;
    }

    private createOnlineGame(gameCreationParams: OnlineGameCreationParams, mode: GameMode) {
        if (mode === GameMode.Classic) {
            return this.gameCreator.createOnlineGame(gameCreationParams);
        }
        return this.gameCreator.createSpecialOnlineGame(gameCreationParams);
    }
}
