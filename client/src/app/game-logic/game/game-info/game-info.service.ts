import { Injectable } from '@angular/core';
import { EMPTY_CHAR, NOT_FOUND } from '@app/game-logic/constants';
import { Game } from '@app/game-logic/game/games/game';
import { MagicOnlineGame } from '@app/game-logic/game/games/magic-game/magic-game';
import { OnlineGame } from '@app/game-logic/game/games/online-game/online-game';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { Player } from '@app/game-logic/player/player';
import { IMagicCard } from '@app/game-logic/game/games/online-game/game-state';
import { Observable, Subject, Subscription } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class GameInfoService {
    players: Player[];
    player: Player;
    private game: Game | undefined;
    private endTurn$$: Subscription;
    private endTurnSubject = new Subject<void>();
    get endTurn$(): Observable<void> {
        return this.endTurnSubject;
    }

    get endOfGame() {
        return this.game?.isEndOfGame$;
    }

    private isEndOfGame$$: Subscription;
    private isEndOfGameSubject = new Subject<void>();
    get isEndOfGame$() {
        return this.isEndOfGameSubject;
    }

    constructor(private timer: TimerService) {}

    receiveGame(game: Game): void {
        this.endTurn$$?.unsubscribe();
        this.isEndOfGame$$?.unsubscribe();
        this.players = game.players;
        this.game = game;

        this.endTurn$$ = this.game.endTurn$.subscribe(() => {
            this.endTurnSubject.next();
        });

        this.isEndOfGame$$ = this.game.isEndOfGame$.subscribe(() => {
            this.isEndOfGameSubject.next();
        });
    }

    receivePlayer(player: Player): void {
        this.player = player;
    }

    getDrawnMagicCard(): IMagicCard[] {
        const index = this.playerIndex;
        if (index === NOT_FOUND) return [];
        return (this.game as MagicOnlineGame).drawnMagicCards[this.playerIndex];
    }

    getPlayer(index: number): Player {
        if (!this.players) {
            throw new Error('No Players in GameInfo');
        }
        return this.players[index];
    }

    getPlayerScore(index: number): number {
        if (!this.players) {
            throw new Error('No Players in GameInfo');
        }
        return this.players[index].points;
    }

    get playerIndex(): number {
        const playerWithIndex = (this.game as OnlineGame).playersWithIndex.get(this.player.name);
        if (!playerWithIndex) {
            return NOT_FOUND;
        }
        return playerWithIndex.index;
    }

    get opponent(): Player {
        if (!this.players) {
            throw new Error('No Players in GameInfo');
        }
        const opponent = this.player === this.players[0] ? this.players[1] : this.players[0];
        return opponent;
    }

    get numberOfPlayers(): number {
        return this.players ? this.players.length : NOT_FOUND;
    }

    get activePlayer(): Player {
        if (!this.players || !this.game) {
            throw Error('No Players in GameInfo');
        }
        return this.players[this.game.activePlayerIndex];
    }

    get timeLeftForTurn(): Observable<number | undefined> {
        return this.timer.timeLeft$;
    }

    get timeLeftPercentForTurn(): Observable<number | undefined> {
        return this.timer.timeLeftPercentage$;
    }

    get numberOfLettersRemaining(): number {
        return this.game ? this.game.getNumberOfLettersRemaining() : NOT_FOUND;
    }

    get isEndOfGame(): boolean {
        return this.game ? this.game.isEndOfGame() : false;
    }

    get isOnlineGame(): boolean {
        return this.game instanceof OnlineGame;
    }

    get winner(): Player[] {
        return this.game ? this.game.getWinner() : [];
    }

    get gameId(): string {
        if (!this.game) {
            return EMPTY_CHAR;
        }
        return (this.game as OnlineGame).gameToken;
    }

    get isMagicGame(): boolean {
        return this.game ? this.game instanceof MagicOnlineGame : false;
    }
}
