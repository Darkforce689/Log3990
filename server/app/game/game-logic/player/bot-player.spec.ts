/* eslint-disable dot-notation */

// TODO GL3A22107-35 : Remove or Adapt server-side tests
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { BotInfoService } from '@app/database/bot-info/bot-info.service';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
import { TIME_BEFORE_PASS, TIME_BEFORE_PICKING_ACTION } from '@app/game/game-logic/constants';
import { BotMessagesService } from '@app/game/game-logic/player/bot-message/bot-messages.service';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { BotLogic } from '@app/game/game-logic/player/bot/bot-logic/bot-logic';
import { BotRepository } from '@app/game/game-logic/player/bot/bot-repository/bot-repository.service';
import { createSinonStubInstance } from '@app/test.util';
import { expect } from 'chai';
import { SinonFakeTimers, useFakeTimers } from 'sinon';

describe('BotPlayer', () => {
    let botPlayer: BotPlayer;
    const stubActionCreator = createSinonStubInstance(ActionCreatorService);
    const stubBotInfoService = createSinonStubInstance(BotInfoService);
    const stubBotMessagesService = createSinonStubInstance(BotMessagesService);
    // TODO GL3A22107-35 : BotRepository has no methods. Might not be worth of a class
    const stubBotRepository = {} as BotRepository;
    let clock: SinonFakeTimers;

    afterEach(() => {
        clock.restore();
    });

    beforeEach(() => {
        botPlayer = new BotPlayer(stubBotInfoService, stubBotRepository, stubBotMessagesService, stubActionCreator, BotDifficulty.Easy);
        clock = useFakeTimers();
    });

    it('should create an instance', () => {
        expect(botPlayer).to.be.instanceOf(BotLogic);
    });

    it('should generate a different name', () => {
        const numberOfTime = 1000;
        const opponentName = 'Jimmy';
        for (let i = 0; i < numberOfTime; i++) {
            const botName = botPlayer['generateBotName'](opponentName);
            const sameName: boolean = botName === opponentName;
            expect(sameName).to.be.equal(false);
        }
    });

    it('should play before 3 seconds', () => {
        botPlayer.startTimerAction();
        botPlayer.chooseAction(new PassTurn(botPlayer));
        clock.tick(TIME_BEFORE_PICKING_ACTION);
        expect(stubBotMessagesService.sendAction.callsArg(0)[0]).to.be.instanceOf(PassTurn);
        clock.tick(TIME_BEFORE_PASS);
    });

    it('should play after 3 seconds', () => {
        botPlayer.startTimerAction();
        clock.tick(TIME_BEFORE_PICKING_ACTION);
        botPlayer.chooseAction(new PassTurn(botPlayer));
        expect(stubBotMessagesService.sendAction.callsArg(0)[0]).to.be.instanceOf(PassTurn);
        clock.tick(TIME_BEFORE_PASS);
    });

    it('should pass turn after 20 seconds', () => {
        botPlayer.startTimerAction();
        clock.tick(TIME_BEFORE_PICKING_ACTION);
        clock.tick(TIME_BEFORE_PASS);
        expect(stubBotMessagesService.sendAction.callsArg(0)[0]).to.be.instanceOf(PassTurn);
    });
});
