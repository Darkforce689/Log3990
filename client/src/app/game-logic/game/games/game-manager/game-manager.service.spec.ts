/* eslint-disable @typescript-eslint/no-magic-numbers */
/* eslint-disable max-lines */
/* eslint-disable dot-notation */
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MessagesService } from '@app/chat/services/messages/messages.service';
import { BOARD_DIMENSION, DEFAULT_TIME_PER_TURN, MIDDLE_OF_BOARD, WAIT_STATUS } from '@app/game-logic/constants';
import { Tile } from '@app/game-logic/game/board/tile';
import { OnlineGame } from '@app/game-logic/game/games/online-game/online-game';
import { Player } from '@app/game-logic/player/player';
import { LeaderboardService } from '@app/leaderboard/leaderboard.service';
import { AccountService } from '@app/services/account.service';
import { BotDifficulty } from '@app/services/bot-difficulty';
import { GameSocketHandlerService } from '@app/socket-handler/game-socket-handler/game-socket-handler.service';
import { GameMode } from '@app/socket-handler/interfaces/game-mode.interface';
import { OnlineGameSettings } from '@app/socket-handler/interfaces/game-settings-multi.interface';
import { GameManagerService } from './game-manager.service';

describe('GameManagerService Online Edition', () => {
    let service: GameManagerService;
    let gameSocketHandler: GameSocketHandlerService;
    const leaderboardServiceMock = jasmine.createSpyObj('LeaderboardService', ['updateLeaderboard']);
    const messageServiceMock = jasmine.createSpyObj('MessageService', ['joinGameConversation', 'leaveGameConversation']);
    const accountServiceMock = jasmine.createSpyObj('AccountService', ['actualizeAccount'], {
        account: {
            name: 'p1',
            _id: '1',
            email: 'a@b.c',
        },
    });
    const grid: Tile[][] = [];
    for (let i = 0; i < BOARD_DIMENSION; i++) {
        const row: Tile[] = [];
        for (let j = 0; j < BOARD_DIMENSION; j++) {
            row.push(new Tile());
        }
        grid.push([...row]);
    }
    grid[MIDDLE_OF_BOARD][MIDDLE_OF_BOARD].letterObject.char = 'X';

    const onlineGameSettings: OnlineGameSettings = {
        timePerTurn: DEFAULT_TIME_PER_TURN,
        playerNames: ['p1', 'p2'],
        privateGame: false,
        gameStatus: WAIT_STATUS,
        randomBonus: false,
        id: '0',
        gameMode: GameMode.Classic,
        botDifficulty: BotDifficulty.Easy,
        numberOfPlayers: 2,
        magicCardIds: [],
        tmpPlayerNames: [],
        password: undefined,
    };

    const accountService = { account: { name: 'Tim' } };
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                { provide: AccountService, useValue: accountService },
                { provide: LeaderboardService, useValue: leaderboardServiceMock },
                { provide: AccountService, useValue: accountServiceMock },
                { provide: MessagesService, useValue: messageServiceMock },
            ],
            imports: [HttpClientTestingModule],
        });
        service = TestBed.inject(GameManagerService);
        gameSocketHandler = TestBed.inject(GameSocketHandlerService);
    });

    it('should join an online game', () => {
        const gameToken = '0';

        service.joinOnlineGame(gameToken, onlineGameSettings);
        const result = service['game'];
        expect(result).toBeInstanceOf(OnlineGame);
    });

    it('should stop game if online game exist on join an online game', () => {
        const gameToken = '0';
        const gameSpy = spyOn(service, 'stopGame').and.callThrough();
        service.joinOnlineGame(gameToken, onlineGameSettings);
        service.joinOnlineGame(gameToken, onlineGameSettings);
        expect(gameSpy).toHaveBeenCalled();
    });

    it('should test the disconnectedFromServerSubject subject', () => {
        const result = service.disconnectedFromServer$.subscribe();
        gameSocketHandler['disconnectedFromServerSubject'].next();
        expect(result).toBeTruthy();
    });

    it('should test the forfeitedGameState subject', () => {
        const result = service.forfeitGameState$.subscribe();
        gameSocketHandler['forfeitGameState$'].next();
        expect(result).toBeTruthy();
    });

    it('should join an online game when you are the opponent', () => {
        const gameToken = '0';

        service.joinOnlineGame(gameToken, onlineGameSettings);
        const result = service['game'];
        expect(result).toBeInstanceOf(OnlineGame);
    });

    it('should not allocate players if there is no game', () => {
        service['allocatePlayers']([new Player('p1'), new Player('p2')]);
        expect(service['game']?.players).toBe(undefined);
    });
});
