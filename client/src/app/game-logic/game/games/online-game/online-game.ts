import { Action } from '@app/game-logic/actions/action';
import { OnlineActionCompilerService } from '@app/game-logic/actions/online-actions/online-action-compiler.service';
import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { EMPTY_CHAR, JOKER_CHAR, NOT_FOUND, RACK_LETTER_COUNT } from '@app/game-logic/constants';
import { Direction } from '@app/game-logic/direction.enum';
import { Board } from '@app/game-logic/game/board/board';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { LetterCreator } from '@app/game-logic/game/board/letter-creator';
import { Letter } from '@app/game-logic/game/board/letter.interface';
import { Game } from '@app/game-logic/game/games/game';
import { GameState, SyncState } from '@app/game-logic/game/games/online-game/game-state';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { Player } from '@app/game-logic/player/player';
import { isCharUpperCase } from '@app/game-logic/utils';
import { AccountService } from '@app/services/account.service';
import { GameSocketHandlerService } from '@app/socket-handler/game-socket-handler/game-socket-handler.service';
import { OnlineAction } from '@app/socket-handler/interfaces/online-action.interface';
import { Subscription } from 'rxjs';

interface PlayerWithIndex {
    index: number;
    player: Player;
}

// TODO GL3A22107-5 : "Online/Offline" behavior doesn't exist anymore.
// Find better name (or simply migrate everything in parent class 'Game')
export class OnlineGame extends Game {
    players: Player[] = [];
    activePlayerIndex: number = 0;
    lettersRemaining: number = 0;
    hasGameEnded: boolean = false;
    winnerNames: string[];
    playersWithIndex = new Map<string, PlayerWithIndex>();
    userName: string;
    private letterCreator = new LetterCreator();

    private gameState$$: Subscription;
    private syncState$$: Subscription;
    private timerStartingTimes$$: Subscription;
    private timeLeft$$: Subscription;

    constructor(
        public gameToken: string,
        public timePerTurn: number,
        private timer: TimerService,
        private onlineSocket: GameSocketHandlerService,
        private boardService: BoardService,
        private onlineActionCompiler: OnlineActionCompilerService,
        private accountService: AccountService,
    ) {
        super();
        this.boardService.board = new Board();

        this.gameState$$ = this.onlineSocket.gameState$.subscribe((gameState: GameState) => {
            this.receiveState(gameState);
        });

        this.syncState$$ = this.onlineSocket.syncState$.subscribe((syncState: SyncState) => {
            this.receiveSyncState(syncState);
        });

        this.timerStartingTimes$$ = this.onlineSocket.timerStartingTimes$.subscribe((timerStartingTime: number) => {
            this.receiveTimerStartingTime(timerStartingTime);
        });

        this.timeLeft$$ = this.onlineSocket.timerTimeLeft$.subscribe((timeLeft: number) => {
            this.timer.timeLeftSubject.next(timeLeft);
        });

        this.accountService.actualizeAccount();
        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        this.userName = this.accountService.account!.name;
    }

    getNumberOfLettersRemaining(): number {
        return this.lettersRemaining;
    }

    isEndOfGame(): boolean {
        return this.hasGameEnded;
    }

    stop() {
        this.forfeit();
        this.close();
    }

    close() {
        this.gameState$$.unsubscribe();
        this.syncState$$.unsubscribe();
        this.timerStartingTimes$$.unsubscribe();
        this.timeLeft$$.unsubscribe();
    }

    receiveState(gameState: GameState) {
        if (this.playersWithIndex.size === 0) {
            this.setupPlayersWithIndex();
        }
        this.updateActivePositions([]);
        this.endTurnSubject.next();
        this.updateClient(gameState);
    }

    receiveSyncState(syncState: SyncState) {
        const positions = syncState.positions;
        if (this.userName === this.players[this.activePlayerIndex].name) return;
        this.updateActivePositions(positions);
    }

    handleUserActions() {
        const player = this.players.find((playerRef: Player) => {
            return playerRef.name === this.userName;
        });
        if (!player) {
            return;
        }
        (player as Player).action$.subscribe((action) => {
            const activePlayerName = this.players[this.activePlayerIndex].name;
            if (activePlayerName !== this.userName) {
                return;
            }
            this.receivePlayerAction(action);
        });
        (player as Player).syncronisation$.subscribe((sync) => {
            const activePlayerName = this.players[this.activePlayerIndex].name;
            if (activePlayerName !== this.userName) {
                return;
            }
            this.receiveSyncronisation(sync);
        });
    }

    getWinner() {
        const winners = this.winnerNames.map((name) => {
            const playerWithIndex = this.playersWithIndex.get(name);
            if (!playerWithIndex) {
                throw Error('Winner names does not fit with the player names');
            }
            return playerWithIndex.player;
        });
        return winners;
    }

    tileHasBonus(position: { x: number; y: number }): boolean {
        const tile = this.boardService.board.grid[position.y][position.x];
        return tile.letterMultiplicator !== 1 || tile.wordMultiplicator !== 1;
    }

    protected updateClient(gameState: GameState) {
        this.updateBoard(gameState);
        this.updateActivePlayer(gameState);
        this.updatePlayers(gameState);
        this.updateLettersRemaining(gameState);
        this.updateEndOfGame(gameState);
    }

    private forfeit() {
        if (!this.onlineSocket.socket) {
            return;
        }
        this.onlineSocket.disconnect();
    }

    private setupPlayersWithIndex() {
        for (let index = 0; index < this.players.length; index++) {
            const player = this.players[index];
            const name = player.name;
            this.playersWithIndex.set(name, { player, index });
        }
    }

    private receivePlayerAction(action: Action) {
        const onlineAction = this.onlineActionCompiler.compileActionOnline(action);
        if (!onlineAction) {
            throw Error('The action received is not supported by the compiler');
        }
        this.sendAction(onlineAction);

        if (action instanceof PlaceLetter) {
            this.placeTemporaryLetter(action);
        }
    }

    private receiveSyncronisation(sync: SyncState) {
        this.sendSync(sync);
    }

    private placeTemporaryLetter(action: PlaceLetter) {
        const startX = action.placement.x;
        const startY = action.placement.y;
        const direction = action.placement.direction;
        const word = action.word;
        const grid = this.boardService.board.grid;
        const player = action.player;
        for (let wordIndex = 0; wordIndex < word.length; wordIndex++) {
            const [x, y] = direction === Direction.Horizontal ? [startX + wordIndex, startY] : [startX, startY + wordIndex];
            const char = grid[y][x].letterObject.char;

            if (char === EMPTY_CHAR) {
                const charToCreate = word[wordIndex];
                const newLetter = this.createTmpLetter(charToCreate);
                grid[y][x].letterObject = newLetter;
                if (isCharUpperCase(charToCreate)) {
                    this.removeLetter(player.letterRack, JOKER_CHAR);
                }
                this.removeLetter(player.letterRack, newLetter.char);
            }
        }
    }

    private createTmpLetter(char: string) {
        const charToCreate = char.toLowerCase();
        if (isCharUpperCase(char)) {
            return this.letterCreator.createBlankLetter(charToCreate);
        }
        return this.letterCreator.createLetter(charToCreate);
    }

    private removeLetter(letterRack: Letter[], newLetter: string) {
        const index = letterRack.findIndex((letter) => {
            return letter.char === newLetter;
        });
        if (index !== NOT_FOUND) {
            letterRack.splice(index, 1);
        }
    }

    private sendAction(onlineAction: OnlineAction) {
        this.onlineSocket.playAction(onlineAction);
    }

    private sendSync(sync: SyncState) {
        this.onlineSocket.sendSync(sync);
    }

    private receiveTimerStartingTime(timerStartingTime: number) {
        this.timer.start(timerStartingTime);
    }

    private updateBoard(gameState: GameState) {
        this.boardService.board.grid = gameState.grid;
    }

    private updateActivePlayer(gameState: GameState) {
        const activePlayerIndex = gameState.activePlayerIndex;
        const activePlayerName = gameState.players[activePlayerIndex].name;
        const playerWithIndex = this.playersWithIndex.get(activePlayerName);
        if (!playerWithIndex) {
            throw Error('Players received with game state are not matching with those of the first turn');
        }
        this.activePlayerIndex = playerWithIndex.index;
    }

    private updateLettersRemaining(gameState: GameState) {
        this.lettersRemaining = gameState.lettersRemaining;
    }

    private updatePlayers(gameState: GameState) {
        for (const lightPlayer of gameState.players) {
            const name = lightPlayer.name;
            const playerWithIndex = this.playersWithIndex.get(name);
            if (!playerWithIndex) {
                throw Error('The players received in game state does not fit with those in the game');
            }
            const player = playerWithIndex.player;
            player.points = lightPlayer.points;

            const newLetterRack = lightPlayer.letterRack;
            if (this.isLetterRackChanged(newLetterRack, player)) {
                player.letterRack = [];
                for (const letter of newLetterRack) {
                    player.letterRack.push(letter);
                }
            }
        }
    }

    private updateActivePositions(positions?: { x: number; y: number }[]) {
        this.boardService.board.activeTiles = positions ?? [];
    }

    private isLetterRackChanged(newLetterRack: Letter[], player: Player): boolean {
        const mapRack = this.makeLetterRackMap(newLetterRack);
        let isChanged = false;
        if (player.letterRack.length < RACK_LETTER_COUNT) {
            isChanged = true;
            return isChanged;
        }

        for (const letter of player.letterRack) {
            const letterCount = mapRack.get(letter.char);
            if (letterCount === 0 || letterCount === undefined) {
                isChanged = true;
                return isChanged;
            }
            mapRack.set(letter.char, letterCount - 1);
        }
        return isChanged;
    }

    private makeLetterRackMap(letterRack: Letter[]): Map<string, number> {
        const mapRack = new Map<string, number>();
        for (const letter of letterRack) {
            const letterCount = mapRack.get(letter.char);
            if (letterCount) {
                mapRack.set(letter.char, letterCount + 1);
                continue;
            }
            mapRack.set(letter.char, 1);
        }
        return mapRack;
    }

    private updateEndOfGame(gameState: GameState) {
        this.hasGameEnded = gameState.isEndOfGame;
        this.winnerNames = gameState.winnerIndex.map((index: number) => {
            return gameState.players[index].name;
        });
        if (gameState.isEndOfGame) {
            this.isEndOfGameSubject.next();
        }
    }
}
