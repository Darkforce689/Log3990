import { Injectable } from '@angular/core';
import { DictionaryHelper } from '@app/game-logic/validator/dictionary-helper';

// TODO GL3A22107-5 : whole class behavior methods -> to be removed / converted to be sent to server
@Injectable({
    providedIn: 'root',
})
export class DictionaryService {
    // dictReady$ = new BehaviorSubject<boolean>(false);
    // isDefaultDict: boolean;
    dynamicWordList: Set<string>[] = [];

    // TODO GL3A22107-3 : migrate, since necessary for Server-side BotCrawler
    dictionaryHelper = new DictionaryHelper();

    // constructor(private dictHttpService: DictHttpService) {
    //     this.addDefault();
    // }

    // fetchDictionary(dictTitle: string): BehaviorSubject<boolean> {
    //     if (dictTitle === DEFAULT_DICTIONARY_TITLE && this.isDefaultDict) {
    //         this.ready();
    //         return this.dictReady$;
    //     }
    //     if (dictTitle === DEFAULT_DICTIONARY_TITLE && !this.isDefaultDict) {
    //         this.addDefault();
    //         this.ready();
    //         return this.dictReady$;
    //     }
    //     this.dictReady$.next(false);
    //     this.dictHttpService.getDict(dictTitle).subscribe((response) => {
    //         const dictionary = response as Dictionary;
    //         this.addWords(dictionary);
    //         this.isDefaultDict = false;
    //         this.ready();
    //     });
    //     return this.dictReady$;
    // }

    // isWordInDict(word: string): boolean {
    //     return this.dynamicWordList[word.length].has(word.toLowerCase());
    // }

    // TODO GL3A22107-3 : migrate, since necessary for Server-side BotCrawler
    // wordGen(partWord: ValidWord): ValidWord[] {
    //     const wordList: ValidWord[] = [];
    //     const tmpWordList: ValidWord[] = [];

    //     let letterCountOfPartWord = 0;
    //     letterCountOfPartWord = this.dictionaryHelper.countNumberOfLetters(partWord, letterCountOfPartWord);

    //     let maxDictWordLength = 0;
    //     const missingLetters = partWord.word.length - letterCountOfPartWord + partWord.leftCount + partWord.rightCount;
    //     if (missingLetters === 0) {
    //         return wordList;
    //     }
    //     if (missingLetters > RACK_LETTER_COUNT) {
    //         maxDictWordLength = letterCountOfPartWord + RACK_LETTER_COUNT;
    //     } else {
    //         maxDictWordLength = letterCountOfPartWord + missingLetters;
    //     }
    //     if (maxDictWordLength > MAX_WORD_LENGTH) {
    //         maxDictWordLength = MAX_WORD_LENGTH;
    //     }
    //     const dictWords = this.dynamicWordList[maxDictWordLength];

    //     if (partWord.word.includes('-')) {
    //         this.dictionaryHelper.getSubWordsOfPartWord(partWord, tmpWordList);

    //         const tmpDict: ValidWord[] = [];
    //         const tmpDict2: ValidWord[] = [];
    //         const foundIndex: number = START_OF_STRING;
    //         let oldSubWordLength: number = RESET;
    //         const initialSettings: DictInitialSearchSettings = { partWord, dictWords, tmpWordList, letterCountOfPartWord, tmpDict, foundIndex };
    //         oldSubWordLength = this.dictionaryHelper.initialDictionarySearch(initialSettings);
    //         const subSettings: DictSubSearchSettings = { tmpWordList, tmpDict2, oldSubWordLength, wordList };
    //         this.dictionaryHelper.subDictionarySearch(initialSettings, subSettings);
    //     } else {
    //         const wholeSettings: DictWholeSearchSettings = { partWord, dictWords, letterCountOfPartWord, wordList };
    //         this.dictionaryHelper.wholePartWordDictionarySearch(wholeSettings);
    //     }
    //     return wordList;
    // }

    // // TODO GL3A22107-3 : migrate, since necessary for Server-side BotCrawler
    // regexValidation(dictWord: ValidWord, placedLetters: string, botLetterRack: Letter[]): string {
    //     const letterRack = botLetterRack;
    //     const mapRack = new Map<string, number>();
    //     const wordLength = dictWord.word.length;

    //     let placedWord = this.dictionaryHelper.placedWordReformat(placedLetters);
    //     this.dictionaryHelper.addLetterRackToMap(letterRack, mapRack);

    //     const regex = new RegExp(placedWord.toLowerCase());
    //     const index = dictWord.word.search(regex);
    //     if (index === NOT_FOUND) {
    //         return 'false';
    //     }
    //     const regexSettings: DictRegexSettings = { dictWord, placedWord, mapRack };
    //     placedWord = this.dictionaryHelper.validateLeftOfPlacedWord(regexSettings);
    //     placedWord = this.dictionaryHelper.validateMiddleOfPlacedWord(regexSettings);
    //     placedWord = this.dictionaryHelper.validateRightOfPlacedWord(regexSettings, wordLength);

    //     return dictWord.word === placedWord.toLowerCase() ? placedWord : 'false';
    // }

    // private addDefault() {
    //     const dict = data as Dictionary;
    //     this.addWords(dict);
    //     this.isDefaultDict = true;
    // }

    // private addWords(dictionary: Dictionary) {
    //     this.clearWords();
    //     dictionary.words.forEach((word) => {
    //         let wordLength = word.length;
    //         for (wordLength; wordLength <= MAX_WORD_LENGTH; wordLength++) {
    //             this.dynamicWordList[wordLength].add(word);
    //         }
    //     });
    // }

    // private ready() {
    //     this.dictReady$.next(true);
    // }

    // private clearWords() {
    //     this.dynamicWordList = [];
    //     for (let i = 0; i <= MAX_WORD_LENGTH; i++) {
    //         this.dynamicWordList.push(new Set());
    //     }
    // }
}
