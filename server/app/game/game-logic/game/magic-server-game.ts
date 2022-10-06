import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { EXCHANGEALETTER_ID, SPLITPOINTS_ID } from '@app/game/game-logic/actions/magic-card/magic-card-constants';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken, IMagicCard } from '@app/game/game-logic/interface/game-state.interface';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { Subject } from 'rxjs';

export class MagicServerGame extends ServerGame {
    drawableMagicCards: IMagicCard[];
    drawnMagicCards: IMagicCard[][];

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
    }

    start(): void {
        this.initiateDrawnMagicCards();
        super.start();
    }

    initiateDrawnMagicCards() {
        this.drawnMagicCards = [];
        this.players.forEach(() => {
            // Keep this until we add the way to get the card in-game
            this.drawnMagicCards.push([{ id: SPLITPOINTS_ID } as IMagicCard, { id: EXCHANGEALETTER_ID } as IMagicCard]);
            // this.drawnMagicCards.push([]);
        });
    }
}
