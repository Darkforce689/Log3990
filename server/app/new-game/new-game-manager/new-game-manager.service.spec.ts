/* eslint-disable no-unused-expressions */
/* eslint-disable @typescript-eslint/no-unused-expressions */
import { BotDifficulty } from '@app/game/game-logic/player/bot/bot-difficulty';
import { DEFAULT_DICTIONARY_TITLE } from '@app/game/game-logic/constants';
import { GameManagerService, PlayersAndToken } from '@app/game/game-manager/game-manager.services';
import { GameMode } from '@app/game/game-mode.enum';
import { NameAndToken } from '@app/game/game-socket-handler/game-socket-handler.service';
import { ConversationService } from '@app/messages-service/services/conversation.service';
import { NewGameManagerService } from '@app/new-game/new-game-manager/new-game-manager.service';
import { OnlineGameSettings } from '@app/new-game/online-game.interface';
import { createSinonStubInstance, StubbedClass } from '@app/test.util';
import { expect } from 'chai';
import { Subject } from 'rxjs';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager/bot-manager.service';

describe('NewGameManagerService', () => {
    let gameManagerStub: StubbedClass<GameManagerService>;
    let conversationStub: StubbedClass<ConversationService>;
    let botManagerStub: StubbedClass<BotManager>;
    let service: NewGameManagerService;
    const tmpPlayerNames: string[] = [];
    const password = undefined;

    before(() => {
        gameManagerStub = createSinonStubInstance<GameManagerService>(GameManagerService);
        conversationStub = createSinonStubInstance<ConversationService>(ConversationService);
        botManagerStub = createSinonStubInstance<BotManager>(BotManager);
        gameManagerStub.gameDeleted$ = new Subject<string>();
        gameManagerStub.playerLeft$ = new Subject<PlayersAndToken>();
        gameManagerStub.observerLeft$ = new Subject<NameAndToken>();
        service = new NewGameManagerService(gameManagerStub, conversationStub, botManagerStub);
    });

    it('should createGame', () => {
        const gameSettings = {
            playerNames: ['Max'],
            privateGame: false,
            randomBonus: true,
            timePerTurn: 60000,
            gameMode: GameMode.Classic,
            dictTitle: DEFAULT_DICTIONARY_TITLE,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames,
            password,
            botNames: [],
        };
        service.createPendingGame(gameSettings);
        expect(service.pendingGames.size).to.equal(1);
    });

    it('on JoinGame should update gameSetting', () => {
        gameManagerStub.activeGames = new Map();
        const id = service.getPendingGames()[0].id;
        const playerName = 'Sim';
        service.joinPendingGame(id, playerName);
        const pendingGamePlayerNames = service.pendingGames.get(id)?.playerNames;
        expect(pendingGamePlayerNames?.find((name) => name === playerName)).to.be.equal(playerName);
    });

    it('on JoinGame should not delete pending game if player join not existing game', () => {
        service.pendingGames.clear();
        const id = 'abc';
        const playerName = 'Sim';
        const confirmedId = service.joinPendingGame(id, playerName);
        expect(confirmedId).to.be.undefined;
    });

    it('on JoinGame should not delete pending game if gameSetting of game are not defined', () => {
        service.pendingGames.clear();
        const gameSetting = undefined as unknown;
        service.pendingGames.set('abc', gameSetting as OnlineGameSettings);
        const id = 'abc';
        const playerName = 'Sim';
        const confirmedId = service.joinPendingGame(id, playerName);
        expect(confirmedId).to.be.undefined;
    });

    it('getPendingGame should return correct pending game', () => {
        service.pendingGames.clear();
        const gameSettings = {
            playerNames: ['Max'],
            privateGame: false,
            randomBonus: true,
            timePerTurn: 60000,
            gameMode: GameMode.Classic,
            dictTitle: DEFAULT_DICTIONARY_TITLE,
            botDifficulty: BotDifficulty.Easy,
            numberOfPlayers: 2,
            magicCardIds: [],
            tmpPlayerNames,
            password,
            botNames: [],
        };
        service.pendingGames.set('abc', gameSettings);
        const id = 'abc';
        expect(service.getPendingGame(id)).to.deep.equal(gameSettings);
    });

    it('getPendingGame should throw if game does not exist', () => {
        service.pendingGames.clear();
        const id = 'abc';
        try {
            service.getPendingGame(id);
        } catch (e) {
            expect(e.message).to.equal('This game does not exist.');
        }
    });
});
