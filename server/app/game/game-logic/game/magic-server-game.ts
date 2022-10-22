import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { Action } from '@app/game/game-logic/actions/action';
import { MagicCard } from '@app/game/game-logic/actions/magic-card/magic-card';
import {
    EXTRATURN_ID,
    MAX_NUMBER_OF_MAGIC_CARD,
    NUMBER_OF_WORDS_FOR_MAGIC_CARD,
    SKIPNEXTTURN_ID,
} from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken, IMagicCard } from '@app/game/game-logic/interface/game-state.interface';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { NOT_FOUND } from '@app/game/game-logic/constants';
import { getRandomInt } from '@app/game/game-logic/utils';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { Subject } from 'rxjs';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';

export class MagicServerGame extends ServerGame {
    drawableMagicCards: IMagicCard[];
    drawnMagicCards: IMagicCard[][];
    activeMagicCards: IMagicCard[];
    playersNumberOfWordsPlaced: number[];

    constructor(
        timerController: TimerController,
        public randomBonus: boolean,
        public timePerTurn: number,
        public gameToken: string,
        pointCalculator: PointCalculatorService,
        gameCompiler: GameCompiler,
        messagesService: SystemMessagesService,
        newGameStateSubject: Subject<GameStateToken>,
        endGameSubject: Subject<EndOfGame>,
        drawableMagicCardIds: string[],
    ) {
        super(
            timerController,
            randomBonus,
            timePerTurn,
            gameToken,
            pointCalculator,
            gameCompiler,
            messagesService,
            newGameStateSubject,
            endGameSubject,
        );
        this.drawableMagicCards = drawableMagicCardIds.map((id) => ({ id } as IMagicCard));
    }

    start(): void {
        this.initiateDrawnMagicCards();
        super.start();
    }

    addWordCount(wordsCount: number) {
        this.playersNumberOfWordsPlaced[this.activePlayerIndex] += wordsCount;
        while (this.playersNumberOfWordsPlaced[this.activePlayerIndex] >= NUMBER_OF_WORDS_FOR_MAGIC_CARD) {
            this.playersNumberOfWordsPlaced[this.activePlayerIndex] -= NUMBER_OF_WORDS_FOR_MAGIC_CARD;
            this.addMagicCard(this.activePlayerIndex);
        }
    }

    protected takeAction(action: Action) {
        if (!(action instanceof MagicCard)) {
            // If it isn't a magic cards, it is the last action of the turn
            super.takeAction(action);
            return;
        }
        // If it is a magic card, can take more action(s)
        this.executeMagicCard(action);
    }

    protected startTurn() {
        const indexSkipTurn = this.activeMagicCards.findIndex((value) => value.id === SKIPNEXTTURN_ID);
        if (indexSkipTurn !== NOT_FOUND) {
            this.skipTurn(indexSkipTurn);
            return;
        }
        super.startTurn();
    }

    protected endOfTurn(action: Action | undefined) {
        const indexExtraTurn = this.activeMagicCards.findIndex((value) => value.id === EXTRATURN_ID);
        if (indexExtraTurn !== NOT_FOUND) {
            this.activeMagicCards.splice(indexExtraTurn, 1);
            super.endOfTurn(action, 0);
            return;
        }
        super.endOfTurn(action);
    }

    private executeMagicCard(action: MagicCard) {
        action.execute(this);
        this.removeMagicCard(action);
        this.emitGameState();
        this.setPlayerActive();
    }

    private initiateDrawnMagicCards() {
        this.drawnMagicCards = [];
        this.playersNumberOfWordsPlaced = [];
        this.activeMagicCards = [];
        this.players.forEach(() => {
            this.drawnMagicCards.push([]);
            this.playersNumberOfWordsPlaced.push(0);
        });
    }

    private addMagicCard(playerIndex: number) {
        if (this.drawnMagicCards[playerIndex].length >= MAX_NUMBER_OF_MAGIC_CARD) return;
        this.drawnMagicCards[playerIndex].push(this.randomMagicCard());
    }

    private randomMagicCard(): IMagicCard {
        return this.drawableMagicCards[getRandomInt(this.drawableMagicCards.length)];
    }

    private removeMagicCard(action: MagicCard) {
        const index = this.drawnMagicCards[this.activePlayerIndex].findIndex((magicCard) => magicCard.id === action.id);
        if (index === NOT_FOUND) return;
        this.drawnMagicCards[this.activePlayerIndex].splice(index, 1);
    }

    private skipTurn(index: number) {
        this.activeMagicCards.splice(index, 1);
        super.endOfTurn(new PassTurn(this.getActivePlayer()));
        this.consecutivePass--;
    }
}
