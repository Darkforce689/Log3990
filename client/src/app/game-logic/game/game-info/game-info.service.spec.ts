+import { TestBed } from '@angular/core/testing';
import { OnlineActionCompilerService } from '@app/game-logic/actions/online-actions/online-action-compiler.service';
import { DEFAULT_TIME_PER_TURN, EMPTY_CHAR, NOT_FOUND } from '@app/game-logic/constants';
import { BoardService } from '@app/game-logic/game/board/board.service';
import { MockGame } from '@app/game-logic/game/games/mock-game';
import { OnlineGame } from '@app/game-logic/game/games/online-game/online-game';
import { SpecialOnlineGame } from '@app/game-logic/game/games/special-games/special-online-game';
import { ObjectiveCreator } from '@app/game-logic/game/objectives/objective-creator/objective-creator.service';
import { TimerService } from '@app/game-logic/game/timer/timer.service';
import { MessagesService } from '@app/game-logic/messages/messages.service';
import { Player } from '@app/game-logic/player/player';
import { LeaderboardService } from '@app/leaderboard/leaderboard.service';
import { GameSocketHandlerService } from '@app/socket-handler/game-socket-handler/game-socket-handler.service';
import { GameInfoService } from './game-info.service';

describe('GameInfoService', () => {
    let service: GameInfoService;
    let game: MockGame;
    let timer: TimerService;
    let board: BoardService;
    let messages: MessagesService;

    beforeEach(() => {
        service = TestBed.inject(GameInfoService);
        timer = TestBed.inject(TimerService);
        board = TestBed.inject(BoardService);
        messages = TestBed.inject(MessagesService);

        game = new MockGame(DEFAULT_TIME_PER_TURN, timer, board, messages);
        game.players = [new Player('p1'), new Player('p2')];
        game.start();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should throw Error for getPlayer() if no players were received', () => {
        for (let i = 0; i < game.players.length; i++) {
            expect(() => {
                service.getPlayer(i);
            }).toThrowError('No Players in GameInfo');
        }
    });

    it('should throw Error for getPlayerScore() if no players were received', () => {
        for (let i = 0; i < game.players.length; i++) {
            expect(() => {
                service.getPlayerScore(i);
            }).toThrowError('No Players in GameInfo');
        }
    });

    it('should throw Error for activePlayer if no players were received', () => {
        expect(() => {
            const p = service.activePlayer;
            p.toString();
        }).toThrowError('No Players in GameInfo');
    });

    it('should return -1 for numberOfPlayers if there are no players', () => {
        expect(service.numberOfPlayers).toBe(NOT_FOUND);
    });

    it('should return -1 for numberOfLettersRemaining if there is no game', () => {
        expect(service.numberOfLettersRemaining).toBe(NOT_FOUND);
    });

    it('should return the time left for a turn from the Timer', () => {
        expect(service.timeLeftForTurn).toBeTruthy();
    });

    it('should properly store the player', () => {
        const player = new Player('p1');
        service.receivePlayer(player);
        expect(service.player).toBeTruthy();
        expect(service.player.name).toBe(player.name);
    });

    it('should return the player with provided index', () => {
        service.receiveGame(game);
        expect(service.getPlayer(0)).toEqual(game.players[0]);
        expect(service.getPlayer(1)).toEqual(game.players[1]);
    });

    it('should return the player points with provided index', () => {
        service.receiveGame(game);
        game.players[0].points = Math.floor(Math.random() * 1000);
        game.players[1].points = Math.floor(Math.random() * 1000);
        expect(service.getPlayerScore(0)).toBe(game.players[0].points);
        expect(service.getPlayerScore(1)).toBe(game.players[1].points);
    });

    it('should return the number of players', () => {
        service.players = [new Player('p1'), new Player('p2')];
        expect(service.numberOfPlayers).toBe(service.players.length);
    });

    it('should return the end of the game flag', () => {
        service.receiveGame(game);
        expect(service.isEndOfGame).toBeFalsy();
        game.letterBag.gameLetters = [];
        service.players[0].letterRack = [];
        expect(service.isEndOfGame).toBeTruthy();
    });
    it('should return the number of players', () => {
        service.players = [new Player('p1'), new Player('p2')];
        expect(service.numberOfPlayers).toBe(service.players.length);
    });

    it('should return the end of the game flag', () => {
        it('should return the number of letters remaining', () => {
            game.lettersRemaining = 88;
            service.receiveGame(game);
            const result = service.numberOfLettersRemaining;
            const expected = 88;
            expect(result).toEqual(expected);
        });

        it('should get the active player', () => {
            game.activePlayerIndex = 0;
            service.receiveGame(game);
            const result = service.activePlayer;
            const expected = game.players[0];
            expect(result).toEqual(expected);
        });

        it('should return that the game is not online', () => {
            service.receiveGame(game);
            const result = service.isOnlineGame;
            expect(result).toBeFalsy();
        });

        it('should get the gameId offline', () => {
            service.receiveGame(game);
            const result = service.gameId;
            const expected = EMPTY_CHAR;
            expect(result).toEqual(expected);
        });

        it('should test the endTurn$ arrow function', () => {
            service.receiveGame(game);
            game['endTurnSubject'].next();
            const result = service.endTurn$.subscribe();
            expect(result).toBeTruthy();
        });


        it('should return empty string for gameID when there is no game', () => {
            expect(service.gameId).toBe(EMPTY_CHAR);
        });
    });

    describe('GameInfoService Online Edition', () => {
        let service: GameInfoService;
        let onlineGame: OnlineGame;
        let timer: TimerService;
        let board: BoardService;
        let specialOnlineGame: SpecialOnlineGame;
        const leaderboardServiceMock = jasmine.createSpyObj('LeaderboardService', ['updateLeaderboard']);
        const objectiveCreatorMock = jasmine.createSpyObj(ObjectiveCreator, ['createObjective']);
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [{ provide: LeaderboardService, useValue: leaderboardServiceMock }],
            });
            service = TestBed.inject(GameInfoService);
            timer = TestBed.inject(TimerService);
            board = TestBed.inject(BoardService);

            onlineGame = new OnlineGame(
                '0',
                DEFAULT_TIME_PER_TURN,
                'QWERTY',
                timer,
                new GameSocketHandlerService(),
                board,
                TestBed.inject(OnlineActionCompilerService),
            );


            specialOnlineGame = new SpecialOnlineGame(
                '0',
                DEFAULT_TIME_PER_TURN,
                'QWERTY',
                timer,
                new GameSocketHandlerService(),
                board,
                TestBed.inject(OnlineActionCompilerService),
                objectiveCreatorMock,
            );
            onlineGame.players = [new Player('p1'), new Player('p2')];
        });

        it('should return the number of letters remaining', () => {
            service.receiveGame(onlineGame);
            const result = service.numberOfLettersRemaining;
            const expected = 0;
            expect(result).toEqual(expected);
        });

        it('should get the active player', () => {
            onlineGame.players = [new Player('p1'), new Player('p2')];
            onlineGame.activePlayerIndex = 0;
            service.receiveGame(onlineGame);
            const result = service.activePlayer;
            const expected = onlineGame.players[0];
            expect(result).toEqual(expected);
        });

        it('should get the endOfGame status', () => {
            service.receiveGame(onlineGame);
            const result = service.isEndOfGame;
            const expected = false;
            expect(result).toEqual(expected);
        });

        it('should get the winner', () => {
            const p1 = new Player('p1');
            spyOn(onlineGame, 'getWinner').and.returnValue([p1]);
            service.receiveGame(onlineGame);
            const result = service.winner;
            const expected: Player[] = [p1];
            expect(result).toEqual(expected);
        });

        it('should return that the game is online', () => {
            service.receiveGame(onlineGame);
            const result = service.isOnlineGame;
            expect(result).toBeTruthy();
        });

        it('should get the gameId online', () => {
            service.receiveGame(onlineGame);
            const result = service.gameId;
            const expected = '0';
            expect(result).toEqual(expected);
        });

        it('should return false for isEndOfGame when there is no game', () => {
            expect(service.isEndOfGame).toBeFalsy();
        });

        it('#is special game should return false when there is no game', () => {
            expect(service.isSpecialGame).toBeFalse();
        });

        it('should throw when getting opponent when no players received', () => {
            expect(() => {
                /*eslint - disable - next - line no - unused - expressions*/
                service.opponent;
            }).toThrowError();
        });

        it('should get opponent properly', () => {
            const p1 = new Player('p1');
            const p2 = new Player('p2');
            service.players = [p1, p2];
            service.receivePlayer(p1);
            expect(service.opponent).toBe(p2);
            service.receivePlayer(p2);
            expect(service.opponent).toBe(p1);
        });

        it('should get is special game properly when online', () => {
            service.receiveGame(specialOnlineGame);
            expect(service.isSpecialGame).toBeTrue();
        });
    });