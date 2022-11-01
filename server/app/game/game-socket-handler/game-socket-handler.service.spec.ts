/* eslint-disable @typescript-eslint/no-magic-numbers */
/* eslint-disable no-unused-expressions */
/* eslint-disable @typescript-eslint/no-unused-expressions */
import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
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
import { ExtendedError } from 'socket.io/dist/namespace';

describe('GameSocketHandler', () => {
    let handler: GameSocketsHandler;
    let httpServer: Server;
    let clientSocket: ClientSocket;
    let serverSocket: Socket;
    let port: number;
    let stubGameManager: StubbedClass<GameManagerService>;
    const mockPlayerInfo$ = new Subject<PlayerInfoToken>();
    const mockNewGameState$ = new Subject<GameStateToken>();
    const mockTimerStartingTime$ = new Subject<TimerStartingTime>();
    const mockTimeUpdate$ = new Subject<TimerTimeLeft>();
    const user: User = {
        name: 'Max',
        _id: '1',
        email: '',
        avatar: '',
        averagePoints: 0,
        nGamePlayed: 0,
        nGameWinned: 0,
        averageTimePerGame: 0,
    } as User;

    before((done) => {
        httpServer = createServer();
        httpServer.listen(() => {
            port = (httpServer.address() as AddressInfo).port;

            stubGameManager = createSinonStubInstance<GameManagerService>(GameManagerService);
            const sessionMiddleware = createSinonStubInstance(SessionMiddlewareService);
            sessionMiddleware.getSocketSessionMiddleware.returns((socket: unknown, next: (err?: ExtendedError | undefined) => void) => {
                next();
                return;
            });
            const authService = createSinonStubInstance(AuthService);
            sinon.stub(authService, 'socketAuthGuard').value((socket: unknown, next: (err?: ExtendedError | undefined) => void) => {
                next();
            });
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

                (serverSocket.request as unknown as { session: Session }).session = { userId: '1' };
            });
            done();
        });
    });

    beforeEach((done) => {
        clientSocket = Client(`http://localhost:${port}`, { path: '/game' });
        clientSocket.on('connect', done);
    });

    afterEach(() => {
        clientSocket.close();
    });

    after(() => {
        httpServer.close();
    });

    // it('should be able to join a game', (done) => {
    //     const gameToken = 'abc';
    //
    //     serverSocket.on('joinGame', (receivedUserAuth: UserAuth) => {
    //         expect(receivedUserAuth).to.deep.equal(userAuth);
    //         done();
    //     });
    //
    //     clientSocket.emit('joinGame', gameToken);
    // });

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

    it('should emit gameState to client', (done) => {
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

        clientSocket.emit('joinGame', gameToken);
        serverSocket.on('joinGame', () => {
            setTimeout(() => mockNewGameState$.next(gameStateToken), 20);
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

        clientSocket.emit('joinGame', gameToken);
        serverSocket.on('joinGame', () => {
            setTimeout(() => mockPlayerInfo$.next(gameStateToken), 20);
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

        clientSocket.emit('joinGame', gameToken);
        serverSocket.on('joinGame', () => {
            setTimeout(() => mockTimerStartingTime$.next(timerGameControl), 20);
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

        clientSocket.emit('joinGame', gameToken);
        serverSocket.on('joinGame', () => {
            setTimeout(() => mockTimeUpdate$.next(timerNewTime), 20);
        });
    });
});
