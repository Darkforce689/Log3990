import { GameActionNotification, GameActionNotifierService } from '@app/game/game-action-notifier/game-action-notifier.service';
import { GlobalSystemMessage, IndividualSystemMessage } from '@app/messages-service/system-message.interface';
import { Observable, Subject } from 'rxjs';
import { Service } from 'typedi';

@Service()
export class SystemMessagesService {
    private globalSystemMessagesSubject = new Subject<GlobalSystemMessage>();
    private individualSystemMessagesSubject = new Subject<IndividualSystemMessage>();

    get globalSystemMessages$(): Observable<GlobalSystemMessage> {
        return this.globalSystemMessagesSubject;
    }

    get individualSystemMessages$(): Observable<IndividualSystemMessage> {
        return this.individualSystemMessagesSubject;
    }

    constructor(private gameActionNotifier: GameActionNotifierService) {
        this.gameActionNotifier.notification$.subscribe((notification: GameActionNotification) => {
            const { gameToken, content } = notification;
            this.sendGlobal(gameToken, content);
        });
    }

    sendIndividual(playerName: string, gameToken: string, content: string) {
        const message: IndividualSystemMessage = { playerName, gameToken, content };
        this.individualSystemMessagesSubject.next(message);
    }

    sendGlobal(gameToken: string, content: string) {
        const message: GlobalSystemMessage = { gameToken, content };
        this.globalSystemMessagesSubject.next(message);
    }
}
