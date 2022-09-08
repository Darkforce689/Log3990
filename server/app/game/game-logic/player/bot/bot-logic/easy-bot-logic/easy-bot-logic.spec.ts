/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable dot-notation */
/* eslint-disable @typescript-eslint/no-magic-numbers */

// TODO GL3A22107-35 : Remove or Adapt server-side tests

// import { BotDifficulty } from '@app/database/bot-info/bot-info';
// import { ExchangeLetter } from '@app/game/game-logic/actions/exchange-letter';
// import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
// import { PlaceLetter } from '@app/game/game-logic/actions/place-letter';
// import { Letter } from '@app/game/game-logic/board/letter.interface';
// import { DEFAULT_TIME_PER_TURN, TIME_BEFORE_PASS, TIME_BEFORE_PICKING_ACTION, TIME_BUFFER_BEFORE_ACTION } from '@app/game/game-logic/constants';
// import { ValidWord } from '@app/game/game-logic/interface/valid-word';
// import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
// import { BotMessagesService } from '@app/game/game-logic/player/bot-message/bot-messages.service';
// import { EasyBot } from '@app/game/game-logic/player/bot/easy-bot-brain';
// import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
// import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
// import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
// import { expect } from 'chai';
// import { of } from 'rxjs';
// import { Action } from 'rxjs/internal/scheduler/Action';

// describe('EasyBot', () => {
//     let easyBot: EasyBot;
//     let boardService: BoardService;
//     let timer: TimerService;
//     let pointCalculator: PointCalculatorService;
//     let messagesService: MessagesService;
//     let gameInfo: GameInfoService;
//     const dictHttpServiceMock = jasmine.createSpyObj('DictHttpService', ['getDictionary']);
//     const dict = new DictionaryService(dictHttpServiceMock);
//     let newGame: OfflineGame;
//     const randomBonus = false;
//     const commandExecuterMock = jasmine.createSpyObj('CommandExecuterService', ['execute']);
//     const botMessageMock = jasmine.createSpyObj('BotMessageService', ['sendAction']);
//     const mockBotHttpService = jasmine.createSpyObj('BotHttpService', ['getDataInfo']);

//     const obs = of(['Test1', 'Test2', 'Test3']);
//     mockBotHttpService.getDataInfo.and.returnValue(obs);
//     beforeEach(() => {
//         TestBed.configureTestingModule({
//             providers: [
//                 { provide: DictionaryService, useValue: dict },
//                 { provide: CommandExecuterService, useValue: commandExecuterMock },
//                 { provide: BotMessagesService, useValue: botMessageMock },
//                 { provide: BotHttpService, useValue: mockBotHttpService },
//             ],
//         });
//         boardService = TestBed.inject(BoardService);
//         timer = TestBed.inject(TimerService);
//         pointCalculator = TestBed.inject(PointCalculatorService);
//         messagesService = TestBed.inject(MessagesService);
//         gameInfo = TestBed.inject(GameInfoService);
//         newGame = new OfflineGame(randomBonus, DEFAULT_TIME_PER_TURN, timer, pointCalculator, boardService, messagesService);
//         gameInfo.receiveGame(newGame);
//         easyBot = new EasyBot(
//             'test',
//             boardService,
//             dict,
//             TestBed.inject(BotCalculatorService),
//             TestBed.inject(WordSearcher),
//             TestBed.inject(BotMessagesService),
//             gameInfo,
//             TestBed.inject(CommandExecuterService),
//             TestBed.inject(ActionCreatorService),
//             TestBed.inject(BotHttpService),
//             BotDifficulty.Easy,
//         );
//     });

//     it('should create an instance', () => {
//         expect(easyBot).toBeTruthy();
//     });

//     it('should call actions based on setting', () => {
//         const mul = 10;
//         const numberOfTime = 1000;
//         const spyPlay = spyOn<any>(easyBot, 'playAction');
//         const spyExchange = spyOn<any>(easyBot, 'exchangeAction');

//         for (let i = 0; i < numberOfTime; i++) {
//             gameInfo.receiveGame(newGame);
//             easyBot['randomActionPicker']();
//         }
//         let value;
//         value = Math.round((spyExchange.calls.count() / numberOfTime) * mul) / mul;
//         expect(value).toBeCloseTo(EasyBot.actionProbability.exchange);
//         value = Math.round((spyPlay.calls.count() / numberOfTime) * mul) / mul;
//         expect(value).toBeCloseTo(EasyBot.actionProbability.play);
//     });

//     it('should return a valid first turn action (empty board))', fakeAsync(() => {
//         const letters: Letter[] = [
//             { char: 'A', value: 1 },
//             { char: 'A', value: 1 },
//         ];
//         easyBot.letterRack = letters;
//         const test = spyOn<any>(easyBot, 'randomActionPicker').and.callThrough();
//         spyOn(Math, 'random').and.returnValue(0.2);
//         easyBot.setActive();
//         tick(TIME_BUFFER_BEFORE_ACTION);

//         const result: Action = test.calls.first().returnValue;
//         expect(result).toBeTruthy();
//         tick(TIME_BEFORE_PICKING_ACTION);
//         tick(TIME_BEFORE_PASS);
//     }));

//     it('should return a valid word 2-6 points', () => {
//         const letters: Letter[] = [
//             { char: 'A', value: 1 },
//             { char: 'A', value: 1 },
//         ];
//         easyBot.letterRack = letters;

//         spyOn(Math, 'random').and.returnValue(0.2);
//         easyBot.bruteForceStart();
//         const pickedWord: ValidWord = easyBot['randomWordPicker'](easyBot.validWordList);
//         let result = false;
//         if (pickedWord.value.totalPoints >= 2 && pickedWord.value.totalPoints <= 6) {
//             result = true;
//         }
//         expect(result).toBeTruthy();
//     });

//     it('should return a valid word 7-12 points', () => {
//         const letters: Letter[] = [
//             { char: 'U', value: 1 },
//             { char: 'B', value: 1 },
//         ];
//         easyBot.letterRack = letters;

//         spyOn(Math, 'random').and.returnValue(0.6);
//         easyBot.bruteForceStart();
//         const pickedWord: ValidWord = easyBot['randomWordPicker'](easyBot.validWordList);
//         let result = false;
//         if (pickedWord.value.totalPoints >= 7 && pickedWord.value.totalPoints <= 12) {
//             result = true;
//         }
//         expect(result).toBeTruthy();
//     });

//     it('should return a valid word 13-18 points', () => {
//         const letters: Letter[] = [
//             { char: 'A', value: 1 },
//             { char: 'B', value: 1 },
//             { char: 'C', value: 1 },
//         ];
//         easyBot.letterRack = letters;

//         spyOn(Math, 'random').and.returnValue(0.9);
//         easyBot.bruteForceStart();
//         const pickedWord: ValidWord = easyBot['randomWordPicker'](easyBot.validWordList);
//         let result = false;
//         if (pickedWord.value.totalPoints >= 13 && pickedWord.value.totalPoints <= 18) {
//             result = true;
//         }
//         expect(result).toBeTruthy();
//     });

//     it('should return a valid PlaceLetter action (playAction through spied randomAction)(vertical)', () => {
//         const letters: Letter[] = [
//             { char: 'A', value: 1 },
//             { char: 'A', value: 1 },
//         ];
//         easyBot.letterRack = letters;
//         const getRandomInt = spyOn<any>(easyBot, 'getRandomInt');
//         getRandomInt.withArgs(1).and.returnValue(1);
//         getRandomInt.and.callThrough();

//         spyOn(Math, 'random').and.returnValue(0.2);
//         const result = easyBot['randomActionPicker']();
//         expect(result).toBeInstanceOf(PlaceLetter);
//     });

//     it('should return a valid PlaceLetter action (playAction through spied randomAction)(horizontal)', () => {
//         const letters: Letter[] = [
//             { char: 'A', value: 1 },
//             { char: 'A', value: 1 },
//         ];
//         easyBot.letterRack = letters;
//         const getRandomInt = spyOn<any>(easyBot, 'getRandomInt');
//         getRandomInt.withArgs(1).and.returnValue(0);
//         getRandomInt.and.callThrough();

//         spyOn(Math, 'random').and.returnValue(0.2);
//         const result = easyBot['randomActionPicker']();
//         expect(result).toBeInstanceOf(PlaceLetter);
//     });

//     it('should a valid PassTurn because no words were found (playAction through spied randomAction)', () => {
//         const letters: Letter[] = [];
//         easyBot.letterRack = letters;
//         spyOn(Math, 'random').and.returnValue(0.5);
//         const result = easyBot['randomActionPicker']();
//         expect(result).toBeInstanceOf(PassTurn);
//     });

//     it('should return a valid ExchangeAction (exchangeAction)', () => {
//         const letters: Letter[] = [
//             { char: 'A', value: 1 },
//             { char: 'P', value: 1 },
//             { char: '*', value: 1 },
//             { char: 'C', value: 1 },
//             { char: 'U', value: 1 },
//             { char: 'E', value: 1 },
//             { char: 'V', value: 1 },
//         ];
//         easyBot.letterRack = letters;
//         spyOn(Math, 'random').and.returnValue(0.9);
//         const result = easyBot['randomActionPicker']();
//         expect(result).toBeInstanceOf(ExchangeLetter);
//     });
// });
