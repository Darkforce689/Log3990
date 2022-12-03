import { Injectable } from '@angular/core';
import { ipcRenderer } from 'electron';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ElectronIpcService {
    externalWindow$ = new BehaviorSubject<boolean>(false);
    leaveGameConvo$ = new Subject<void>();
    joinGameConvo$ = new Subject<string>();
    ipcRenderer: typeof ipcRenderer;
    constructor() {
        this.ipcRenderer = ipcRenderer;
        this.ipcRenderer.on('open-chat-box', (event, args) => {
            this.externalWindow$.next(args);
        });
        this.ipcRenderer.on('game-token', (event, args) => {
            this.joinGameConvo$.next(args);
        });
        this.ipcRenderer.on('leave-game-convo', (event, args) => {
            this.leaveGameConvo$.next();
        });
    }

    leaveGameConvo() {
        this.ipcRenderer.send('leave-game-convo');
    }

    openPage(route: string) {
        this.ipcRenderer.send('open-external-window', route);
        // La fonction n'a pas a return, c'est pour la compilation
        return route;
    }

    closePage() {
        this.ipcRenderer.send('close-external-window');
    }

    sendGameToken(gameToken: string) {
        this.ipcRenderer.send('game-token', gameToken);
        // La fonction n'a pas a return, c'est pour la compilation
        return gameToken;
    }
}
