import { TestBed } from '@angular/core/testing';
import { CommandExecuterService } from '@app/game-logic/commands/command-executer/command-executer.service';
import { DEFAULT_TIME_PER_TURN } from '@app/game-logic/constants';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { Letter } from '@app/game-logic/game/board/letter.interface';
import { GameInfoService } from '@app/game-logic/game/game-info/game-info.service';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { MessagesService } from '@app/game-logic/messages/messages.service';
import { Player } from '@app/game-logic/player/player';
import { MockGame } from '@app/game-logic/point-calculator/mock-game';
import { BotHttpService } from '@app/services/bot-http.service';

describe('Player', () => {
    let boardService: BoardService;
    let timer: TimerService;
    let messagesService: MessagesService;
    let gameInfo: GameInfoService;
    let player: Player;
    const commandExecuterMock = jasmine.createSpyObj('CommandExecuterService', ['execute']);
    const mockBotHttpService = jasmine.createSpyObj('BotHttpService', ['getDataInfo']);

    //     const obs = of(['Test1', 'Test2', 'Test3']);
    //     mockBotHttpService.getDataInfo.and.returnValue(obs);

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                { provide: CommandExecuterService, useValue: commandExecuterMock },
                { provide: BotHttpService, useValue: mockBotHttpService },
            ],
        });
        boardService = TestBed.inject(BoardService);
        timer = TestBed.inject(TimerService);
        messagesService = TestBed.inject(MessagesService);
        gameInfo = TestBed.inject(GameInfoService);
        player = new Player('test');
        gameInfo.receiveGame(new MockGame(DEFAULT_TIME_PER_TURN, timer, boardService, messagesService));
    });

    it('should create an instance', () => {
        expect(player).toBeTruthy();
    });

    it('should have a full letterRack', () => {
        const emptyNumberOfLetters = 0;
        expect(player.letterRack.length === emptyNumberOfLetters).toBeTruthy();
    });

    it('should have a full letterRack', () => {
        const letterRack: Letter[] = [
            { char: 'E', value: 1 },
            { char: 'K', value: 1 },
            { char: 'O', value: 1 },
            { char: 'I', value: 1 },
            { char: 'N', value: 1 },
            { char: 'J', value: 1 },
            { char: 'L', value: 1 },
        ];
        player.letterRack = letterRack;
        const nLetters = 7;
        expect(player.letterRack.length === nLetters).toBeTruthy();
    });
});
