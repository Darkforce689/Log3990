/* eslint-disable dot-notation */
/* eslint-disable no-unused-expressions */
/* eslint-disable @typescript-eslint/no-unused-expressions */
/* eslint-disable no-unused-vars */
import { AuthService } from '@app/auth/services/auth.service';
import { SessionMiddlewareService } from '@app/auth/services/session-middleware.service';
import { Session } from '@app/auth/services/session.interface';
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { WAIT_STATUS } from '@app/game/game-logic/constants';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { GameMode } from '@app/game/game-mode.enum';
import { NewGameManagerService } from '@app/new-game/new-game-manager/new-game-manager.service';
import { OnlineGameSettings, OnlineGameSettingsUI } from '@app/new-game/online-game.interface';
import { createSinonStubInstance, StubbedClass } from '@app/test.util';
import { User } from '@app/user/interfaces/user.interface';
import { UserService } from '@app/user/user.service';
import { expect } from 'chai';
import { createServer, Server } from 'http';
import { beforeEach } from 'mocha';
import { AddressInfo } from 'net';
import { Subject } from 'rxjs';
import * as sinon from 'sinon';
import { Socket } from 'socket.io';
import { io as Client, Socket as ClientSocket } from 'socket.io-client';
import { ExtendedError } from 'socket.io/dist/namespace';
import { NewGameSocketHandler } from './new-game-socket-handler';

describe('New Online Game Service', () => {
    const timeout = 20;
    let handler: NewGameSocketHandler;
    let clientSocket: ClientSocket;
    let serverSocket: Socket;
    let port: number;
    let httpServer: Server;
    let newGameManagerService: StubbedClass<NewGameManagerService>;
    let dictionaryService: StubbedClass<DictionaryService>;
    const tmpPlayerNames: string[] = [];
    const password = undefined;

    const user: User = {
        name: 'Max',
        _id: '1',
        email: '',
        avatar: '',
    };

    before((done) => {
        httpServer = createServer();
        httpServer.listen(() => {
            process.setMaxListeners(0);
            port = (httpServer.address() as AddressInfo).port;
            newGameManagerService = createSinonStubInstance<NewGameManagerService>(NewGameManagerService);
            newGameManagerService.refreshPendingGame$ = new Subject<void>();
            dictionaryService = createSinonStubInstance<DictionaryService>(DictionaryService);
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

            handler = new NewGameSocketHandler(httpServer, newGameManagerService, dictionaryService, sessionMiddleware, authService, userService);
            handler.newGameHandler();
            handler.ioServer.on('connection', (socket: Socket) => {
                serverSocket = socket;
                (socket.request as unknown as { session: Session }).session = { userId: '1' };
            });
            done();
        });
    });
    beforeEach((done) => {
        clientSocket = Client(`http://localhost:${port}`, { path: '/newGame' });
        clientSocket.on('connect', done);
    });
    afterEach(() => {
        clientSocket.close();
    });

    after(() => {
        httpServer.close();
    });

    it('should create pendingGame', (done) => {
        const playerNames: string[] = [];
        const gameId = '1';

        newGameManagerService.createPendingGame.returns(gameId);
        const gameSettingsOnline: OnlineGameSettingsUI = {
            gameMode: GameMode.Classic,
            timePerTurn: 60000,
            playerNames,
            privateGame: false,
            randomBonus: true,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames: [],
            password: '',
        };
        serverSocket.on('createGame', (gameSettings) => {
            setTimeout(() => {
                gameSettingsOnline.playerNames.push(user.name);
                (gameSettingsOnline as OnlineGameSettings).id = gameId;
                expect(newGameManagerService.createPendingGame.calledOnce).to.be.true;
                // expect(gameSettings).to.deep.equal(gameSettingsOnline);
                done();
            }, 3 * timeout);
        });
        clientSocket.emit('createGame', gameSettingsOnline);
    });

    it('should receive pendingGameId on create', (done) => {
        const id = 'abc';
        newGameManagerService.createPendingGame.returns(id);
        const gameSettings = {
            gameMode: GameMode.Classic,
            timePerTurn: 60000,
            playerNames: ['Max'],
            privateGame: false,
            randomBonus: true,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames: [],
            password: '',
        };

        clientSocket.on('pendingGameId', (pendingId: string) => {
            expect(pendingId).to.deep.equal(id);
            done();
        });
        clientSocket.emit('createGame', gameSettings);
    });

    it('should throw error if game settings are invalid', (done) => {
        const gameSettings = { playerName: [false], randomBonus: 3, timePerTurn: 'uyyu' };
        clientSocket.on('error', (errorContent: string) => {
            setTimeout(() => {
                expect(errorContent).to.equal('Impossible de rejoindre la partie, les paramètres de partie sont invalides.');
                done();
            }, timeout);
        });
        clientSocket.emit('createGame', gameSettings);
    });

    it('should delete pending game if client disconnect', (done) => {
        const gameSettings = {
            playerNames: ['Max'],
            privateGame: false,
            gameStatus: WAIT_STATUS,
            id: '1',
            randomBonus: true,
            timePerTurn: 60000,
            tmpPlayerNames,
            password,
        };
        newGameManagerService.getPendingGames.returns([gameSettings as OnlineGameSettings]);
        clientSocket.emit('createGame', gameSettings);
        serverSocket.on('disconnect', () => {
            setTimeout(() => {
                expect(newGameManagerService.deletePendingGame.called).to.be.true;
                done();
            }, timeout);
        });
        clientSocket.close();
    });

    it('should throw error if parameters are invalid', (done) => {
        const id = true;
        const playerName = 'abc';
        clientSocket.on('error', (errorContent: string) => {
            setTimeout(() => {
                expect(errorContent).to.equal('Impossible de rejoindre la partie, les paramètres sont invalides.');
                done();
            }, timeout);
        });
        clientSocket.emit('joinGame', id, playerName);
    });

    it('should throw error if player try to join a game not active', (done) => {
        newGameManagerService.joinPendingGame.returns(undefined);
        const id = 'aa';
        const playerName = 'abc';
        clientSocket.on('error', (errorContent: string) => {
            setTimeout(() => {
                expect(errorContent).to.equal("Impossible de rejoindre la partie, elle n'existe pas.");
                done();
            }, timeout);
        });
        clientSocket.emit('joinGame', id, playerName);
    });

    it('should send gameSettings to players on joinGame', (done) => {
        const gameSettingsUI = {
            playerNames: [],
            randomBonus: true,
            privateGame: false,
            timePerTurn: 60000,
            gameMode: GameMode.Classic,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames,
            password,
        } as OnlineGameSettingsUI;

        const gameSettings = {
            id: 'a',
            playerNames: [user.name],
            randomBonus: true,
            privateGame: false,
            timePerTurn: 60000,
            gameMode: GameMode.Classic,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames,
            password,
            gameStatus: WAIT_STATUS,
        } as OnlineGameSettings;

        newGameManagerService.createPendingGame.returns('a');
        newGameManagerService.joinPendingGame.returns('a');
        newGameManagerService.getPendingGame.returns(gameSettings);

        const clientSocket2 = Client(`http://localhost:${port}`, { path: '/newGame', multiplex: false });
        const playerName = 'abc';

        clientSocket2.on('gameJoined', (gameSettingServer: OnlineGameSettings) => {
            expect(gameSettingServer).to.deep.equal(gameSettings);
            clientSocket2.close();
            done();
        });
        clientSocket2.emit('createGame', gameSettingsUI);
        clientSocket2.on('pendingGameId', (idGame: string) => {
            clientSocket.emit('joinGame', idGame, playerName);
        });
    });

    it('should delete pending game on game launch', (done) => {
        const gameSettingsUI = {
            playerNames: [user.name],
            randomBonus: true,
            privateGame: true,
            timePerTurn: 60000,
            gameMode: GameMode.Classic,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames: [],
            password: '',
        };
        const gameSettings = {
            id: 'a',
            playerNames: [user.name],
            randomBonus: true,
            privateGame: true,
            timePerTurn: 60000,
            gameMode: GameMode.Classic,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames: [],
            password: '',
            gameStatus: WAIT_STATUS,
        };
        newGameManagerService.activeGameSettingMap = new Map<string, OnlineGameSettings>();
        newGameManagerService.launchPendingGame.returns(Promise.resolve('a'));
        newGameManagerService.createPendingGame.returns('a');
        newGameManagerService.joinPendingGame.returns('a');
        newGameManagerService.getPendingGame.returns(gameSettings as OnlineGameSettings);

        const clientSocket2 = Client(`http://localhost:${port}`, { path: '/newGame', multiplex: false });
        clientSocket2.on('gameStarted', (clientGameSettings: OnlineGameSettings) => {
            expect(newGameManagerService.deletePendingGame.called).to.be.true;
            done();
        });
        clientSocket2.emit('createGame', gameSettingsUI);
        clientSocket2.emit('launchGame', gameSettings.id);
    });
});
