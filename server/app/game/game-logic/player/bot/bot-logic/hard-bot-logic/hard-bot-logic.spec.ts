/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-magic-numbers */

// TODO GL3A22107-35 : Remove or Adapt server-side tests
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { LiveDict } from '@app/dictionary-manager/default-dictionary';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { PlaceLetter } from '@app/game/game-logic/actions/place-letter';
import { Letter } from '@app/game/game-logic/board/letter.interface';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { BotMessagesService } from '@app/game/game-logic/player/bot-message/bot-messages.service';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { HardBotLogic } from '@app/game/game-logic/player/bot/bot-logic/hard-bot-logic/hard-bot-logic';
import { BotRepository } from '@app/game/game-logic/player/bot/bot-repository/bot-repository.service';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { BotDictionaryService } from '@app/game/game-logic/validator/dictionary/bot-dictionary/bot-dictionary';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { createSinonStubInstance } from '@app/test.util';
import { expect } from 'chai';
import { Subject } from 'rxjs';
import * as sinon from 'sinon';

describe.only('HardBotLogic', () => {
    let hardBotLogic: HardBotLogic;
    let botPlayer: BotPlayer;
    let clock: sinon.SinonFakeTimers;
    let game: ServerGame;

    // let boardService: Board;
    // let timer: TimerService;
    // let pointCalculator: PointCalculatorService;
    // let messagesService: MessagesService;
    // let gameInfo: GameInfoService;
    // let newGame: OfflineGame;
    // const randomBonus = false;
    // const commandExecuterMock = jasmine.createSpyObj('CommandExecuterService', ['execute']);
    // const botMessageMock = jasmine.createSpyObj('BotMessageService', ['sendAction']);
    // const dictHttpServiceMock = jasmine.createSpyObj('DictHttpService', ['getDictionary']);
    // const dict = new DictionaryService(dictHttpServiceMock);

    // const mockBotHttpService = jasmine.createSpyObj('BotHttpService', ['getDataInfo']);
    // const obs = of(['Test1', 'Test2', 'Test3']);
    // mockBotHttpService.getDataInfo.and.returnValue(obs);

    let stubDictionaryService: DictionaryService;
    let botDictionaryService: BotDictionaryService;
    let stubBotCalculatorService: BotCalculatorService;
    let stubWordSearcher: WordSearcher;
    let stubActionCreatorService: ActionCreatorService;
    let stubBotInfoService: BotInfoService;
    let stubBotRepository: BotRepository;
    let stubBotMessage: BotMessagesService;
    let stubActionCreator: ActionCreatorService;
    const botDifficulty = BotDifficulty.Expert;
    let stubTimerController: TimerController;
    const randomBonus = false;
    const timePerTurn = 60000;
    const gameToken = 'gameToken';
    let stubPointCalculator: PointCalculatorService;
    let stubGameCompiler: GameCompiler;
    let stubMessagesService: SystemMessagesService;
    let stubNewGameStateSubject: Subject<GameStateToken>;
    let stubEndGameSubject: Subject<EndOfGame>;
    const liveDict: LiveDict = {
        currentUsage: 1,
        dynamicWordList: [],
    };

    beforeEach(() => {
        stubDictionaryService = createSinonStubInstance(DictionaryService);
        botDictionaryService = new BotDictionaryService(stubDictionaryService);
        stubBotCalculatorService = createSinonStubInstance(BotCalculatorService);
        stubWordSearcher = createSinonStubInstance(WordSearcher);
        stubActionCreatorService = createSinonStubInstance(ActionCreatorService);
        hardBotLogic = new HardBotLogic(botDictionaryService, stubBotCalculatorService, stubWordSearcher, stubActionCreatorService, botDifficulty);
        botPlayer = new BotPlayer(stubBotInfoService, stubBotRepository, stubBotMessage, stubActionCreator, botDifficulty);
        stubBotInfoService = createSinonStubInstance(BotInfoService);
        stubBotRepository = { hardBot: hardBotLogic } as BotRepository;
        stubBotMessage = createSinonStubInstance(BotMessagesService);
        stubActionCreator = createSinonStubInstance(ActionCreatorService);
        clock = sinon.useFakeTimers();
        stubTimerController = createSinonStubInstance(TimerController);
        stubPointCalculator = createSinonStubInstance(PointCalculatorService);
        stubGameCompiler = createSinonStubInstance(GameCompiler);
        stubMessagesService = createSinonStubInstance(SystemMessagesService);
        stubNewGameStateSubject = new Subject<GameStateToken>();
        stubEndGameSubject = new Subject<EndOfGame>();
        game = new ServerGame(
            stubTimerController,
            randomBonus,
            timePerTurn,
            gameToken,
            stubPointCalculator,
            stubGameCompiler,
            stubMessagesService,
            stubNewGameStateSubject,
            stubEndGameSubject,
        );

        stubDictionaryService.liveGamesMap = new Map();
        stubDictionaryService.liveDictMap = new Map();
        stubDictionaryService.liveGamesMap.set(gameToken, 'dictUniqueName');
        stubDictionaryService.liveDictMap.set('dictUniqueName', liveDict);

        // TestBed.configureTestingModule({
        //     providers: [
        //         { provide: DictionaryService, useValue: dict },
        //         { provide: CommandExecuterService, useValue: commandExecuterMock },
        //         { provide: BotMessagesService, useValue: botMessageMock },
        //         { provide: BotHttpService, useValue: mockBotHttpService },
        //     ],
        // });
        // boardService = TestBed.inject(BoardService);
        // timer = TestBed.inject(TimerService);
        // pointCalculator = TestBed.inject(PointCalculatorService);
        // messagesService = TestBed.inject(MessagesService);
        // gameInfo = TestBed.inject(GameInfoService);
        // newGame = new OfflineGame(randomBonus, DEFAULT_TIME_PER_TURN, timer, pointCalculator, boardService, messagesService);
        // gameInfo.receiveGame(newGame);
        // hardBot = new HardBot(
        //     'test',
        //     boardService,
        //     dict,
        //     TestBed.inject(BotCalculatorService),
        //     TestBed.inject(WordSearcher),
        //     TestBed.inject(BotMessagesService),
        //     gameInfo,
        //     TestBed.inject(CommandExecuterService),
        //     TestBed.inject(ActionCreatorService),
        //     TestBed.inject(BotHttpService),
        //     BotType.Expert,
        // );
    });

    afterEach(() => {
        clock.restore();
    });

    it('should create an instance', () => {
        expect(hardBotLogic).to.be.instanceOf(HardBotLogic);
    });

    it('should return the best word it can place (piano) (horizontal))', () => {
        const letters: Letter[] = [
            { char: 'A', value: 1 },
            { char: 'N', value: 1 },
            { char: 'I', value: 1 },
            { char: 'P', value: 1 },
            { char: 'O', value: 1 },
        ];
        botPlayer.letterRack = letters;
        const spy = sinon.spy(hardBotLogic, 'actionPicker');
        const result = 'piano';
        liveDict.dynamicWordList[8] = new Set([result]);
        hardBotLogic.actionPicker(botPlayer, game);
        // spyOn<any>(hardBotLogic, 'getRandomInt').and.returnValue(0);

        // botPlayer.startTimerAction();
        // clock.tick(TIME_BUFFER_BEFORE_ACTION);

        const expected: PlaceLetter = spy.getCall(0).returnValue as PlaceLetter;
        // console.log(spy.getCall(0).returnValue);
        // console.log(spy.callCount);
        // clock.tick(TIME_BEFORE_PICKING_ACTION);
        // clock.tick(TIME_BEFORE_PASS);
        expect(expected.word).to.be.equal(result);
    });

    // it('should return the best word it can place (vertical))', async () => {
    //     const letters: Letter[] = [
    //         { char: 'A', value: 1 },
    //         { char: 'A', value: 1 },
    //     ];
    //     hardBotLogic.letterRack = letters;
    //     const actionSpy = spyOn<any>(hardBotLogic, 'actionPicker').and.callThrough();
    //     spyOn<any>(hardBotLogic, 'getRandomInt').and.returnValue(1);

    //     hardBotLogic.setActive();
    //     clock.tick(TIME_BUFFER_BEFORE_ACTION);

    //     const result = 'aa';
    //     const expected: PlaceLetter = actionSpy.calls.first().returnValue as PlaceLetter;
    //     expect(expected.word).to.be.equal(result);
    //     clock.tick(TIME_BEFORE_PICKING_ACTION);
    //     clock.tick(TIME_BEFORE_PASS);
    // });

    // it('should exchange letters because it cant play)', async () => {
    //     const letters: Letter[] = [{ char: 'A', value: 1 }];
    //     hardBotLogic.letterRack = letters;
    //     const exchangeSpy = spyOn<any>(hardBotLogic, 'exchangeAction').and.callThrough();

    //     hardBotLogic.setActive();
    //     clock.tick(TIME_BUFFER_BEFORE_ACTION);

    //     expect(exchangeSpy).toHaveBeenCalled();
    //     clock.tick(TIME_BEFORE_PICKING_ACTION);
    //     clock.tick(TIME_BEFORE_PASS);
    // });

    // it('should exchange letters because it cant play and >0 <7 letters left)', async () => {
    //     const letters: Letter[] = [{ char: 'A', value: 1 }];
    //     hardBotLogic.letterRack = letters;
    //     const exchangeSpy = spyOn<any>(hardBotLogic, 'exchangeAction').and.callThrough();
    //     spyOnProperty(gameInfo, 'numberOfLettersRemaining').and.returnValue(5);

    //     hardBotLogic.setActive();
    //     clock.tick(TIME_BUFFER_BEFORE_ACTION);

    //     expect(exchangeSpy).toHaveBeenCalled();
    //     clock.tick(TIME_BEFORE_PICKING_ACTION);
    //     clock.tick(TIME_BEFORE_PASS);
    // });

    // it('should pass because it cant play and there is no letters to draw)', async () => {
    //     const letters: Letter[] = [{ char: 'A', value: 1 }];
    //     hardBotLogic.letterRack = letters;
    //     const passSpy = spyOn<any>(hardBotLogic, 'passAction').and.callThrough();
    //     spyOnProperty(gameInfo, 'numberOfLettersRemaining').and.returnValue(0);

    //     hardBotLogic.setActive();
    //     clock.tick(TIME_BUFFER_BEFORE_ACTION);

    //     expect(passSpy).toHaveBeenCalled();
    //     clock.tick(TIME_BEFORE_PICKING_ACTION);
    //     clock.tick(TIME_BEFORE_PASS);
    // });
});
