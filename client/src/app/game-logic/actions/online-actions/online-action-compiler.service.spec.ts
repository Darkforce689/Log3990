import { TestBed } from '@angular/core/testing';
import { MessagesService } from '@app/chat/services/messages/messages.service';
import { Action } from '@app/game-logic/actions/action';
import { ExchangeLetter } from '@app/game-logic/actions/exchange-letter';
import { OnlineActionCompilerService } from '@app/game-logic/actions/online-actions/online-action-compiler.service';
import { PassTurn } from '@app/game-logic/actions/pass-turn';
import { PlaceLetter } from '@app/game-logic/actions/place-letter';
import { DEFAULT_TIME_PER_TURN } from '@app/game-logic/constants';
import { Direction } from '@app/game-logic/direction.enum';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { Letter } from '@app/game-logic/game/board/letter.interface';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { MockGame } from '@app/game-logic/game/games/mock-game';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { PlacementSetting } from '@app/game-logic/interfaces/placement-setting.interface';
import { Player } from '@app/game-logic/player/player';
import { OnlineAction, OnlineActionType } from '@app/socket-handler/interfaces/online-action.interface';

class UnknownAction extends Action {
    id: number;
    constructor(readonly player: Player) {
        super(player);
    }
    execute(): void {
        throw new Error('Method not implemented.');
    }
    protected perform(): void {
        throw new Error('Method not implemented.');
    }
}
describe('Service: OnlineActionCompiler', () => {
    let service: OnlineActionCompilerService;
    let placement: PlacementSetting;
    let game: MockGame;
    let p1: Player;
    let p2: Player;
    let timer: TimerService;
    let board: BoardService;
    let info: GameInfoService;
    let messagesSpy: MessagesService;
    let letters: Letter[];

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [{ provide: MessagesService, useValue: messagesSpy }],
        });
        service = TestBed.inject(OnlineActionCompilerService);
        timer = TestBed.inject(TimerService);
        board = TestBed.inject(BoardService);
        info = TestBed.inject(GameInfoService);
        game = new MockGame(DEFAULT_TIME_PER_TURN, timer, board, messagesSpy);
        p1 = new Player('p1');
        p2 = new Player('p2');
        game.players.push(p1);
        game.players.push(p2);
        info.receiveGame(game);
        game.start();
        placement = { x: 1, y: 1, direction: Direction.Horizontal };
        letters = [
            { char: 'a', value: 1 },
            { char: 'b', value: 1 },
            { char: 'c', value: 1 },
        ];
    });

    it('should create OnlineActionCompiler', () => {
        expect(service).toBeTruthy();
    });

    it('should only call compilePlaceLetter', () => {
        const placeLetter = new PlaceLetter(p1, 'abc', placement);
        const onlinePlaceLetterTest: OnlineAction = {
            type: OnlineActionType.Place,
            placementSettings: placeLetter.placement,
            letters: placeLetter.word,
            letterRack: p1.letterRack,
        };
        expect(service.compileActionOnline(placeLetter) as OnlineAction).toEqual(onlinePlaceLetterTest);
    });

    it('should only call compileExchangeLetter', () => {
        const exchangeLetter = new ExchangeLetter(p1, letters);

        const onlineExchangeLetterTest: OnlineAction = {
            type: OnlineActionType.Exchange,
            letters: 'abc',
            letterRack: p1.letterRack,
        };
        expect(service.compileActionOnline(exchangeLetter) as OnlineAction).toEqual(onlineExchangeLetterTest);
    });

    it('should only call compilePassTurn', () => {
        const passTurn = new PassTurn(p1);
        const passTurnTest: OnlineAction = {
            type: OnlineActionType.Pass,
            letterRack: p1.letterRack,
        };
        expect(service.compileActionOnline(passTurn) as OnlineAction).toEqual(passTurnTest);
    });

    it('should only call compilePassTurn', () => {
        const unknownActionTest = new UnknownAction(p1);

        expect(service.compileActionOnline(unknownActionTest)).toEqual(undefined);
    });
});
