import { Letter } from '@app/game/game-logic/board/letter.interface';
import { MAX_WORD_LENGTH, NOT_FOUND, RACK_LETTER_COUNT, RESET, START_OF_STRING } from '@app/game/game-logic/constants';
import { ValidWord } from '@app/game/game-logic/player/bot/valid-word';
import {
    DictInitialSearchSettings,
    DictRegexSettings,
    DictSubSearchSettings,
    DictWholeSearchSettings
} from '@app/game/game-logic/validator/dictionary/dict-settings';
import { DictionaryHelper } from '@app/game/game-logic/validator/dictionary/dictionary-helper';
import { DictionaryService } from '@app/game/game-logic/validator/dictionary/dictionary.service';
import { Service } from 'typedi';

@Service()
export class BotDictionaryService {
    static dictionaryHelper = new DictionaryHelper();

    constructor(private dictionaryService: DictionaryService) {}

    // fetchDictionary(dictTitle: string): BehaviorSubject<boolean> {
    // if (dictTitle === DEFAULT_DICTIONARY_TITLE && this.isDefaultDict) {
    //     this.ready();
    //     return this.dictReady$;
    // }
    // if (dictTitle === DEFAULT_DICTIONARY_TITLE && !this.isDefaultDict) {
    //     // this.addDefault();
    //     this.ready();
    //     return this.dictReady$;
    // }
    // this.dictReady$.next(false);
    // this.dictHttpService.getDict(dictTitle).subscribe((response) => {
    //     const dictionary = response as Dictionary;
    //     this.addWords(dictionary);
    //     this.isDefaultDict = false;
    //     this.ready();
    // });
    // return this.dictReady$;
    // }

    isWordInDict(word: string, gameToken: string): boolean {
        const dict = this.getDynamicWordList(gameToken);
        return dict[word.length].has(word.toLowerCase());
    }

    wordGen(partWord: ValidWord, gameToken: string): ValidWord[] {
        const wordList: ValidWord[] = [];
        const tmpWordList: ValidWord[] = [];

        let letterCountOfPartWord = 0;
        letterCountOfPartWord = BotDictionaryService.dictionaryHelper.countNumberOfLetters(partWord, letterCountOfPartWord);

        let maxDictWordLength = 0;
        const missingLetters = partWord.word.length - letterCountOfPartWord + partWord.leftCount + partWord.rightCount;
        if (missingLetters === 0) {
            return wordList;
        }
        if (missingLetters > RACK_LETTER_COUNT) {
            maxDictWordLength = letterCountOfPartWord + RACK_LETTER_COUNT;
        } else {
            maxDictWordLength = letterCountOfPartWord + missingLetters;
        }
        if (maxDictWordLength > MAX_WORD_LENGTH) {
            maxDictWordLength = MAX_WORD_LENGTH;
        }

        const dict = this.getDynamicWordList(gameToken);
        const dictWords = dict[maxDictWordLength];

        if (partWord.word.includes('-')) {
            BotDictionaryService.dictionaryHelper.getSubWordsOfPartWord(partWord, tmpWordList);

            const tmpDict: ValidWord[] = [];
            const tmpDict2: ValidWord[] = [];
            const foundIndex: number = START_OF_STRING;
            let oldSubWordLength: number = RESET;
            const initialSettings: DictInitialSearchSettings = { partWord, dictWords, tmpWordList, letterCountOfPartWord, tmpDict, foundIndex };
            oldSubWordLength = BotDictionaryService.dictionaryHelper.initialDictionarySearch(initialSettings);
            const subSettings: DictSubSearchSettings = { tmpWordList, tmpDict2, oldSubWordLength, wordList };
            BotDictionaryService.dictionaryHelper.subDictionarySearch(initialSettings, subSettings);
        } else {
            const wholeSettings: DictWholeSearchSettings = { partWord, dictWords, letterCountOfPartWord, wordList };
            BotDictionaryService.dictionaryHelper.wholePartWordDictionarySearch(wholeSettings);
        }
        return wordList;
    }

    regexValidation(dictWord: ValidWord, placedLetters: string, botLetterRack: Letter[]): string {
        const letterRack = botLetterRack;
        const mapRack = new Map<string, number>();
        const wordLength = dictWord.word.length;

        let placedWord = BotDictionaryService.dictionaryHelper.placedWordReformat(placedLetters);
        BotDictionaryService.dictionaryHelper.addLetterRackToMap(letterRack, mapRack);

        const regex = new RegExp(placedWord.toLowerCase());
        const index = dictWord.word.search(regex);
        if (index === NOT_FOUND) {
            return 'false';
        }
        const regexSettings: DictRegexSettings = { dictWord, placedWord, mapRack };
        placedWord = BotDictionaryService.dictionaryHelper.validateLeftOfPlacedWord(regexSettings);
        placedWord = BotDictionaryService.dictionaryHelper.validateMiddleOfPlacedWord(regexSettings);
        placedWord = BotDictionaryService.dictionaryHelper.validateRightOfPlacedWord(regexSettings, wordLength);

        return dictWord.word === placedWord.toLowerCase() ? placedWord : 'false';
    }

    private getDynamicWordList(gameToken: string): Set<string>[] {
        const dictName = this.dictionaryService.liveGamesMap.get(gameToken);
        if (!dictName) {
            return [];
        }
        const dict = this.dictionaryService.liveDictMap.get(dictName);
        return dict ? dict.dynamicWordList : [];
    }
}
