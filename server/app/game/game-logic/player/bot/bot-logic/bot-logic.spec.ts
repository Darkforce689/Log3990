/* eslint-disable dot-notation */

// TODO GL3A22107-35 : Remove or Adapt server-side tests
import { BotDifficulty } from '@app/database/bot-info/bot-difficulty';
import { ActionCreatorService } from '@app/game/game-logic/actions/action-creator/action-creator.service';
import { PassTurn } from '@app/game/game-logic/actions/pass-turn';
import { TIME_BEFORE_PASS, TIME_BEFORE_PICKING_ACTION } from '@app/game/game-logic/constants';
import { BotCalculatorService } from '@app/game/game-logic/player/bot-calculator/bot-calculator.service';
import { BotLogic } from '@app/game/game-logic/player/bot/bot-logic/bot-logic';
import { EasyBotLogic } from '@app/game/game-logic/player/bot/bot-logic/easy-bot-logic/easy-bot-logic';
import { BotDictionaryService } from '@app/game/game-logic/validator/dictionary/bot-dictionary/bot-dictionary';
import { WordSearcher } from '@app/game/game-logic/validator/word-search/word-searcher.service';
import { createSinonStubInstance } from '@app/test.util';
import { expect } from 'chai';

describe('Bot', () => {
    let bot: EasyBotLogic;
    // const dict = new DictionaryService(dictHttpServiceMock);
    // const obs = of(['Test1', 'Test2', 'Test3']);
    // mockBotHttpService.getDataInfo.and.returnValue(obs);

    const stubBotDictionaryService = createSinonStubInstance(BotDictionaryService);
    const stubBotCalculatorService = createSinonStubInstance(BotCalculatorService);
    const stubWordValidator = createSinonStubInstance(WordSearcher);
    const stubActionCreator = createSinonStubInstance(ActionCreatorService);

    beforeEach(() => {
        bot = new EasyBotLogic(stubBotDictionaryService, stubBotCalculatorService, stubWordValidator, stubActionCreator, BotDifficulty.Easy);
    });

    it('should create an instance', () => {
        expect(bot).to.be.instanceOf(BotLogic);
    });

    it('should generate a different name', () => {
        const numberOfTime = 1000;
        const opponentName = 'Jimmy';
        for (let i = 0; i < numberOfTime; i++) {
            const botName = bot['generateBotName'](opponentName);
            const sameName: boolean = botName === opponentName;
            expect(sameName).to.be.false;
        }
    });

    it('should play before 3 seconds', fakeAsync(() => {
        bot.startTimerAction();
        bot.chooseAction(new PassTurn(bot));
        tick(TIME_BEFORE_PICKING_ACTION);
        expect(botMessageMock.sendAction.calls.argsFor(0)[0]).to.be.instanceOf(PassTurn);
        tick(TIME_BEFORE_PASS);
    }));

    it('should play after 3 seconds', fakeAsync(() => {
        bot.startTimerAction();
        tick(TIME_BEFORE_PICKING_ACTION);
        bot.chooseAction(new PassTurn(bot));
        expect(botMessageMock.sendAction.calls.argsFor(0)[0]).to.be.instanceOf(PassTurn);
        tick(TIME_BEFORE_PASS);
    }));

    it('should pass turn after 20 seconds', fakeAsync(() => {
        bot.startTimerAction();
        tick(TIME_BEFORE_PICKING_ACTION);
        tick(TIME_BEFORE_PASS);
        expect(botMessageMock.sendAction.calls.argsFor(0)[0]).to.be.instanceOf(PassTurn);
    }));
});
