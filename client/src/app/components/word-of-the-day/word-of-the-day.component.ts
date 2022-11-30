import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component } from '@angular/core';
import { WordOfTheDay } from '@app/socket-handler/interfaces/word-of-the-day.interface';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
    selector: 'app-word-of-the-day',
    templateUrl: './word-of-the-day.component.html',
    styleUrls: ['./word-of-the-day.component.scss'],
})
export class WordOfTheDayComponent implements AfterViewInit {
    static noWordDisplay: WordOfTheDay = { word: '', definition: 'Pas de définition disponible', points: 0 };
    title: string = 'Le mot du jour est:';
    wordOfTheDayHTTP: WordOfTheDay;
    constructor(private http: HttpClient) {}

    ngAfterViewInit(): void {
        this.fetchWordOfTheDay();
    }

    fetchWordOfTheDay() {
        const res = this.http.get(`${environment.serverUrl}/home`) as Observable<WordOfTheDay>;
        res.subscribe((wordOfTheDay: WordOfTheDay) => {
            this.wordOfTheDayHTTP = wordOfTheDay;
        });
    }

    get wordOfTheDay(): WordOfTheDay {
        return this.wordOfTheDayHTTP === undefined ? WordOfTheDayComponent.noWordDisplay : this.wordOfTheDayHTTP;
    }

    get tiles(): string[] {
        return this.wordOfTheDay.word.toUpperCase().split('');
    }
}
