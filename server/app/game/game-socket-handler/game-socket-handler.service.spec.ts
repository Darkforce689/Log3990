/* eslint-disable @typescript-eslint/no-magic-numbers */
/* eslint-disable no-unused-expressions */
/* eslint-disable @typescript-eslint/no-unused-expressions */
import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { ForfeitPlayerInfo, GameState, GameStateToken, PlayerInfoToken } from '@app/game/game-logic/interface/game-state.interface';
import { TimerStartingTime, TimerTimeLeft } from '@app/game/game-logic/timer/timer-game-control.interface';
import { GameManagerService } from '@app/game/game-manager/game-manager.services';
import { GameSocketsHandler } from '@app/game/game-socket-handler/game-socket-handler.service';
import { UserAuth } from '@app/game/game-socket-handler/user-auth.interface';
import { OnlineAction, OnlineActionType } from '@app/game/online-action.interface';
import { createSinonStubInstance, StubbedClass } from '@app/test.util';
import { User } from '@app/user/interfaces/user.interface';
import { UserService } from '@app/user/user.service';
import { expect } from 'chai';
import { createServer, Server } from 'http';
import { AddressInfo } from 'net';
import { Subject } from 'rxjs';
import * as sinon from 'sinon';
import { Socket } from 'socket.io';
import { io as Client, Socket as ClientSocket } from 'socket.io-client';

describe('GameSocketHandler', () => {
    let handler: GameSocketsHandler;
    let httpServer: Server;
    let clientSocket: ClientSocket;
    let serverSocket: Socket;
    let port: number;
    let sandbox: sinon.SinonSandbox;
    let stubGameManager: StubbedClass<GameManagerService>;
    const mockPlayerInfo$ = new Subject<PlayerInfoToken>();
    const mockNewGameState$ = new Subject<GameStateToken>();
    const mockTimerStartingTime$ = new Subject<TimerStartingTime>();
    const mockTimeUpdate$ = new Subject<TimerTimeLeft>();
    const user: User = {
        name: 'Max',
        _id: '',
        email: '',
        avatar: '',
    };
    before((done) => {
        httpServer = createServer();
        httpServer.listen(() => {
            port = (httpServer.address() as AddressInfo).port;

            stubGameManager = createSinonStubInstance<GameManagerService>(GameManagerService);
            const sessionMiddleware = createSinonStubInstance(SessionMiddlewareService);
            const authService = createSinonStubInstance(AuthService);
            const userService = createSinonStubInstance(UserService);
            userService.getUser.returns(Promise.resolve(user));

            sinon.stub(stubGameManager, 'newGameState$').value(mockNewGameState$);
            sinon.stub(stubGameManager, 'forfeitedGameState$').value(mockPlayerInfo$);
            sinon.stub(stubGameManager, 'timerStartingTime$').value(mockTimerStartingTime$);
            sinon.stub(stubGameManager, 'timeUpdate$').value(mockTimeUpdate$);
            handler = new GameSocketsHandler(httpServer, stubGameManager, sessionMiddleware, authService, userService);
            handler.handleSockets();
            handler.sio.on('connection', (socket) => {
                serverSocket = socket;
            });
            done();
        });
    });

    beforeEach((done) => {
        sandbox = sinon.createSandbox();
        clientSocket = Client(`http://localhost:${port}`, { path: '/game' });
        clientSocket.on('connect', done);
    });

    afterEach(() => {
        sandbox.restore();
        clientSocket.disconnect();
    });

    after(() => {
        httpServer.close();
    });

    it('should be able to join a game', (done) => {
        const userAuth: UserAuth = {
            playerName: 'test',
            gameToken: 'abc',
        };
        serverSocket.on('joinGame', (receivedUserAuth: UserAuth) => {
            expect(receivedUserAuth).to.deep.equal(userAuth);
            done();
        });

        clientSocket.emit('joinGame', userAuth);
    });

    it('should disconnect client when game manager throws', (done) => {
        const userAuth: UserAuth = {
            playerName: 'test',
            gameToken: 'abc',
        };
        serverSocket.on('joinGame', (receivedUserAuth: UserAuth) => {
            expect(receivedUserAuth).to.deep.equal(userAuth);
        });
        stubGameManager.addPlayerToGame.throws(Error);
        clientSocket.emit('joinGame', userAuth);
        clientSocket.on('disconnect', () => {
            done();
        });
    });

    it('should be able to send user action', (done) => {
        const userAction: OnlineAction = {
            type: OnlineActionType.Pass,
        };
        serverSocket.on('nextAction', (receivedUserAction: OnlineAction) => {
            expect(receivedUserAction).to.deep.equal(userAction);
            done();
        });
        clientSocket.emit('nextAction', userAction);
    });

    it('should disconnect client when there is an error on send action', (done) => {
        const userAction: OnlineAction = {
            type: OnlineActionType.Pass,
        };
        serverSocket.on('nextAction', (receivedUserAction: OnlineAction) => {
            expect(receivedUserAction).to.deep.equal(userAction);
        });
        stubGameManager.receivePlayerAction.throws(Error);
        clientSocket.emit('nextAction', userAction);
        clientSocket.on('disconnect', () => {
            done();
        });
    });

    it('should emit gametate to client', (done) => {
        stubGameManager.addPlayerToGame.returns();
        const gameState: GameState = {
            players: [],
            activePlayerIndex: 0,
            grid: [],
            lettersRemaining: 0,
            isEndOfGame: false,
            winnerIndex: [],
        };
        const gameToken = 'abc';
        const gameStateToken: GameStateToken = {
            gameToken,
            gameState,
        };
        clientSocket.on('gameState', (receivedGameState: GameState) => {
            expect(receivedGameState).to.deep.equal(gameState);
            done();
        });

        const userAuth: UserAuth = {
            playerName: 'test',
            gameToken,
        };
        clientSocket.emit('joinGame', userAuth);
        serverSocket.on('joinGame', () => {
            mockNewGameState$.next(gameStateToken);
        });
    });

    it('should emit forfeited gamestate to client with valid game state', (done) => {
        stubGameManager.addPlayerToGame.returns();
        const forfeitedGameState: ForfeitPlayerInfo = {
            name: 'Name',
            previousPlayerName: 'OtherName',
        };

        const gameToken = 'abc';
        const gameStateToken: PlayerInfoToken = {
            gameToken,
            playerInfo: forfeitedGameState,
        };
        clientSocket.on('transitionGameState', (lastGameState: ForfeitPlayerInfo) => {
            expect(lastGameState).to.deep.equal(forfeitedGameState);
            done();
        });

        const userAuth: UserAuth = {
            playerName: 'test',
            gameToken,
        };

        clientSocket.emit('joinGame', userAuth);
        serverSocket.on('joinGame', () => {
            mockPlayerInfo$.next(gameStateToken);
        });
    });

    it('should send timer controls to client', (done) => {
        stubGameManager.addPlayerToGame.returns();
        const gameToken = 'abc';
        const initialTime = 300;
        const timerGameControl: TimerStartingTime = {
            gameToken,
            initialTime,
        };
        clientSocket.on('timerStartingTime', (receivedtimerStartingTime: TimerStartingTime) => {
            expect(receivedtimerStartingTime).to.deep.equal(initialTime);
            done();
        });

        const userAuth: UserAuth = {
            playerName: 'test',
            gameToken,
        };
        clientSocket.emit('joinGame', userAuth);
        serverSocket.on('joinGame', () => {
            mockTimerStartingTime$.next(timerGameControl);
        });
    });

    it('should send timer new time to client', (done) => {
        stubGameManager.addPlayerToGame.returns();
        const gameToken = 'abc';
        const timeLeft = 300;
        const timerNewTime: TimerTimeLeft = {
            gameToken,
            timeLeft,
        };
        clientSocket.on('timeUpdate', (receivedTimerTimeLeft: TimerTimeLeft) => {
            expect(receivedTimerTimeLeft).to.deep.equal(timeLeft);
            done();
        });

        const userAuth: UserAuth = {
            playerName: 'test',
            gameToken,
        };
        clientSocket.emit('joinGame', userAuth);
        serverSocket.on('joinGame', () => {
            mockTimeUpdate$.next(timerNewTime);
        });
    });
});
