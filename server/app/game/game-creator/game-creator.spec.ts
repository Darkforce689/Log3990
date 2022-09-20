import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { GameCompiler } from '@app/game/game-compiler/game-compiler.service';
import { GameCreator } from '@app/game/game-creator/game-creator';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { DEFAULT_DICTIONARY_TITLE } from '@app/game/game-logic/constants';
import { SpecialServerGame } from '@app/game/game-logic/game/special-server-game';
import { EndOfGame } from '@app/game/game-logic/interface/end-of-game.interface';
import { GameStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { ObjectiveCreator } from '@app/game/game-logic/objectives/objective-creator/objective-creator.service';
import { BotManager } from '@app/game/game-logic/player/bot/bot-manager/bot-manager.service';
import { Player } from '@app/game/game-logic/player/player';
import { PointCalculatorService } from '@app/game/game-logic/point-calculator/point-calculator.service';
import { TimerController } from '@app/game/game-logic/timer/timer-controller.service';
import { getRandomInt } from '@app/game/game-logic/utils';
import { GameMode } from '@app/game/game-mode.enum';
import { SystemMessagesService } from '@app/messages-service/system-messages-service/system-messages.service';
import { OnlineGameSettings } from '@app/new-game/online-game.interface';
import { createSinonStubInstance } from '@app/test.util';
import { expect } from 'chai';
import { Subject } from 'rxjs';

describe('GameCreator', () => {
    let gameCreator: GameCreator;
    let onlineGameSettings: OnlineGameSettings;
    let id: string;
    let timePerTurn: number;
    let playerNames: string[];
    let randomBonus: boolean;
    let gameMode: GameMode;
    let gameToken: string;
    let dictTitle: string;
    let botDifficulty: BotDifficulty;
    let numberOfPlayers: number;
    const botInfo = {
        name: 'BotA',
        type: BotDifficulty.Easy,
        canEdit: true,
    };

    const pointCalculatorStub = createSinonStubInstance<PointCalculatorService>(PointCalculatorService);
    const gameCompilerStub = createSinonStubInstance<GameCompiler>(GameCompiler);
    const systemMessagesServiceStub = createSinonStubInstance<SystemMessagesService>(SystemMessagesService);
    const timerControllerStub = createSinonStubInstance<TimerController>(TimerController);
    const objectiveCreatorStub = createSinonStubInstance<ObjectiveCreator>(ObjectiveCreator);
    const botInfoServiceStub = createSinonStubInstance<BotInfoService>(BotInfoService);
    botInfoServiceStub.getBotInfoList.resolves([botInfo]);
    // TODO GL3A22107-35 : BotManager has no methods. Might not be worth of a class
    const botManagerStub = {} as BotManager;
    const actionCreatorStub = createSinonStubInstance<ActionCreatorService>(ActionCreatorService);

    const newGameStateSubject = new Subject<GameStateToken>();
    const endGameSubject = new Subject<EndOfGame>();
    beforeEach(() => {
        id = getRandomInt(Number.MAX_SAFE_INTEGER).toString();
        gameToken = id + 'token';
        timePerTurn = getRandomInt(Number.MAX_SAFE_INTEGER);
        playerNames = ['p1', 'p2'];
        randomBonus = getRandomInt(1) === 0;
        dictTitle = DEFAULT_DICTIONARY_TITLE;
        botDifficulty = BotDifficulty.Easy;
        gameMode = GameMode.Classic;
        numberOfPlayers = playerNames.length;
        onlineGameSettings = { id, playerNames, randomBonus, timePerTurn, gameMode, dictTitle, botDifficulty, numberOfPlayers };
        gameCreator = new GameCreator(
            pointCalculatorStub,
            gameCompilerStub,
            systemMessagesServiceStub,
            newGameStateSubject,
            endGameSubject,
            timerControllerStub,
            objectiveCreatorStub,
            botInfoServiceStub,
            botManagerStub,
            actionCreatorStub,
        );
    });

    it('should create a server game with requested parameters', async () => {
        const createdGame = await gameCreator.createGame(onlineGameSettings, gameToken);
        expect(createdGame.gameToken).to.be.equal(gameToken);
        expect(createdGame.players).to.be.deep.equal(playerNames.map((playerName) => new Player(playerName)));
        expect(createdGame.timePerTurn).to.be.equal(timePerTurn);
        expect(createdGame.randomBonus).to.be.equal(randomBonus);
    });

    it('should create a server game with requested parameters and default opponent name', async () => {
        const singlePlayerOnlineGameSettings = { ...onlineGameSettings };
        singlePlayerOnlineGameSettings.playerNames = ['p1'];
        const createdGame = await gameCreator.createGame(singlePlayerOnlineGameSettings, gameToken);
        expect(createdGame.gameToken).to.be.equal(gameToken);
        expect(createdGame.players.map((p) => p.name)).to.be.deep.equal([singlePlayerOnlineGameSettings.playerNames[0], botInfo.name]);
        expect(createdGame.timePerTurn).to.be.equal(timePerTurn);
        expect(createdGame.randomBonus).to.be.equal(randomBonus);
    });

    it('should create a special server game with requested parameters and default opponent name', async () => {
        const specialSinglePlayerOnlineGameSettings = { ...onlineGameSettings };
        specialSinglePlayerOnlineGameSettings.gameMode = GameMode.Special;
        specialSinglePlayerOnlineGameSettings.playerNames = ['p1'];
        const createdGame = await gameCreator.createGame(specialSinglePlayerOnlineGameSettings, gameToken);
        expect(createdGame.gameToken).to.be.equal(gameToken);
        expect(createdGame.players.map((p) => p.name)).to.be.deep.equal([specialSinglePlayerOnlineGameSettings.playerNames[0], botInfo.name]);
        expect(createdGame.timePerTurn).to.be.equal(timePerTurn);
        expect(createdGame.randomBonus).to.be.equal(randomBonus);
        expect(createdGame as SpecialServerGame).instanceof(SpecialServerGame);
    });
});
