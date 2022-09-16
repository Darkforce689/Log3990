import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { Action } from '@app/game/game-logic/actions/action';
import { MagicCard } from '@app/game/game-logic/actions/magic-card';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
import { Board } from '@app/game/game-logic/board/board';
import { LetterBag } from '@app/game/game-logic/board/letter-bag';
import { MAX_CONSECUTIVE_PASS } from '@app/game/game-logic/constants';
import { EndOfGame, EndOfGameReason } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { Timer } from '@app/game/game-logic/timer/timer.service';
import { ServerLogger } from '@app/logger/logger';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { randomInt } from 'crypto';
import { first, mapTo, Subject } from 'rxjs';

export class ServerGame {
    static readonly maxConsecutivePass = MAX_CONSECUTIVE_PASS;
    letterBag: LetterBag = new LetterBag();
    players: Player[] = [];
    activePlayerIndex: number;
    consecutivePass: number = 0;
    board: Board;
    timer: Timer;

    isEnded$ = new Subject<undefined>();
    endReason: EndOfGameReason;

    constructor(
        timerController: TimerController,
        public randomBonus: boolean,
        public timePerTurn: number,
        public gameToken: string,
        private pointCalculator: PointCalculatorService,
        private gameCompiler: GameCompiler,
        protected messagesService: SystemMessagesService,
        private newGameStateSubject: Subject<GameStateToken>,
        private endGameSubject: Subject<EndOfGame>,
    ) {
        this.timer = new Timer(gameToken, timerController);
        this.board = new Board(randomBonus);
    }

    start(): void {
        if (this.players.length < 2) {
            throw Error('Game started with less than 2 players');
        }
        this.drawGameLetters();
        this.pickFirstPlayer();
        this.emitGameState();
        this.startTurn();
    }

    stop() {
        ServerLogger.logDebug('Game with id ', this.gameToken, ' manually stopped');
        this.endReason = EndOfGameReason.ManualStop;
        this.isEnded$.next(undefined);
    }

    isEndOfGame() {
        if (this.letterBag.isEmpty) {
            for (const player of this.players) {
                if (player.isLetterRackEmpty) {
                    return true;
                }
            }
        }
        if (this.consecutivePass >= ServerGame.maxConsecutivePass) {
            return true;
        }
        return false;
    }

    getActivePlayer() {
        return this.players[this.activePlayerIndex];
    }

    doAction(action: Action) {
        if (action instanceof PassTurn) {
            this.consecutivePass += 1;
        } else {
            this.consecutivePass = 0;
        }
    }

    getWinner(): Player[] {
        let highestScore = Number.MIN_SAFE_INTEGER;
        let winners: Player[] = [];

        for (const player of this.players) {
            if (player.points === highestScore) {
                winners.push(player);
            }
            if (player.points > highestScore) {
                highestScore = player.points;
                winners = [player];
            }
        }
        return winners;
    }

    getNonActiveTopPlayer(): Player {
        let topPlayers: Player[] = this.players[0] !== this.getActivePlayer() ? [this.players[0]] : [this.players[1]];
        let highestScore = topPlayers[0].points;
        for (const player of this.players) {
            if (player === this.getActivePlayer()) continue;
            if (player.points === highestScore) {
                topPlayers.push(player);
            }
            if (player.points > highestScore) {
                highestScore = player.points;
                topPlayers = [player];
            }
        }
        if (topPlayers.length > 1) {
            return topPlayers[randomInt(0, topPlayers.length)];
        }
        return topPlayers[0];
    }

    forcePlay() {
        this.startTurn();
    }

    forceEndturn() {
        this.timer.stop();
    }

    private onEndOfGame(reason: EndOfGameReason) {
        this.pointCalculator.endOfGamePointDeduction(this);
        this.displayLettersLeft();
        this.emitGameState();
        this.endGameSubject.next({ gameToken: this.gameToken, reason, players: this.players });
    }

    private nextPlayer() {
        this.activePlayerIndex = (this.activePlayerIndex + 1) % this.players.length;
    }

    private pickFirstPlayer() {
        const max = this.players.length;
        const firstPlayer = Math.floor(Math.random() * max);
        this.activePlayerIndex = firstPlayer;
    }

    private drawGameLetters() {
        for (const player of this.players) {
            player.letterRack = this.letterBag.drawEmptyRackLetters();
        }
    }

    private startTurn() {
        if (this.endReason) {
            this.onEndOfGame(this.endReason);
            return;
        }
        const activePlayer = this.setPlayerActive();
        if (activePlayer instanceof BotPlayer) {
            activePlayer.generateAction(this);
        }
        const timerEnd$ = this.timer.start(this.timePerTurn).pipe(mapTo(new PassTurn(activePlayer)));
        timerEnd$.pipe(first()).subscribe((action) => this.endOfTurn(action));
    }

    private setPlayerActive(player: Player = this.getActivePlayer()): Player {
        player.action$.pipe(first()).subscribe((action) => this.takeAction(action));
        return player;
    }

    private takeAction(action: Action) {
        if (!(action instanceof MagicCard)) {
            // If it isn't a magic cards, it is the last action of the turn
            this.endOfTurn(action);
            return;
        }
        // If it is a magic card, can take more action(s)
        action.execute(this);
        this.emitGameState();
        this.setPlayerActive();
    }

    private endOfTurn(action: Action | undefined) {
        this.timer.stop();
        if (!action) {
            this.onEndOfGame(EndOfGameReason.Forfeit);
            return;
        }

        if (this.endReason) {
            this.onEndOfGame(this.endReason);
            return;
        }

        action.end$.subscribe(() => {
            if (this.isEndOfGame()) {
                this.onEndOfGame(EndOfGameReason.GameEnded);
                return;
            }
            this.nextPlayer();
            this.emitGameState();
            this.startTurn();
        });

        action.execute(this);
    }

    private emitGameState() {
        const gameState = this.gameCompiler.compile(this);
        const gameStateToken: GameStateToken = { gameState, gameToken: this.gameToken };
        this.newGameStateSubject.next(gameStateToken);
    }

    private displayLettersLeft() {
        let message = 'Fin de partie - lettres restantes';
        this.messagesService.sendGlobal(this.gameToken, message);
        for (const player of this.players) {
            message = `${player.name}: ${player.printLetterRack()}`;
            this.messagesService.sendGlobal(this.gameToken, message);
        }
    }
}
