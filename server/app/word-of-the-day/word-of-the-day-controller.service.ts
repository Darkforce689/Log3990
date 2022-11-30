import { LetterCreator } from '@app/game/game-logic/board/letter-creator';
import { ListOfWordOfTheDay, WordOfTheDay } from '@app/word-of-the-day/interface/word-of-the-day.interface';
import { Router } from 'express';
import * as fs from 'fs';
import { Service } from 'typedi';

@Service()
export class WordOfTheDayController {
    router: Router;
    allWords: WordOfTheDay[] = [];

    constructor() {
        this.configureRouter();
    }

    private configureRouter() {
        this.router = Router();
        this.loadFromFile();

        this.router.get('', async (req, res) => {
            return res.send(this.getTodayWord());
        });
    }

    private getTodayWord(): WordOfTheDay {
        const today = new Date();
        const day = today.getDate();
        const wordIndex = day % this.allWords.length;
        return this.allWords[wordIndex];
    }

    private loadFromFile() {
        this.allWords = [];
        const folderPath = 'app/word-of-the-day/list-of-words/';

        fs.readdirSync(folderPath).forEach((file) => {
            if (!file.includes('.json')) {
                return;
            }
            const dict = JSON.parse(fs.readFileSync(folderPath + file, 'utf8'));
            this.allWords = (dict as ListOfWordOfTheDay).listOfWords;
        });
        this.calculatePoints();
    }

    private calculatePoints() {
        const letterCreator = new LetterCreator();

        this.allWords.forEach((wordOfTheDay) => {
            wordOfTheDay.points = 0;
            const wordToCalculate = wordOfTheDay.word.split('');
            const letterWithPoints = letterCreator.createLetters(wordToCalculate);
            letterWithPoints.forEach((character) => {
                wordOfTheDay.points += character.value;
            });
        });
    }
}
