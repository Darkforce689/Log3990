import { Injectable } from '@angular/core';
// TODO RELEASE : UNCOMMENT FILE
// import { ipcRenderer } from 'electron';
import { BehaviorSubject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ElectronIpcService {
    externalWindow$ = new BehaviorSubject<boolean>(false);
    // ipcRenderer: typeof ipcRenderer;
    // constructor() {
    //     this.ipcRenderer = ipcRenderer;
    //     this.ipcRenderer.on('open-chat-box', (event, args) => {
    //         this.externalWindow$.next(args);
    //     });
    // }

    openPage(route: string) {
        // this.ipcRenderer.send('open-external-window', route);
        // La fonction n'a pas a return, c'est pour la compilation
        return route;
    }

    closePage() {
        // this.ipcRenderer.send('close-external-window');
    }

    sendGameToken(gameToken: string) {
        // this.ipcRenderer.send('game-token', gameToken);
        // La fonction n'a pas a return, c'est pour la compilation
        return gameToken;
    }
}
