import { Injectable } from '@angular/core';
import { ElectronIpcService } from '@app/services/electron-ipc.service';

@Injectable({
    providedIn: 'root',
})
export class PopChatService {
    openedInPopOut = false;
    constructor(private electron: ElectronIpcService) {
        this.windowed$.subscribe((isOpened: boolean) => {
            this.openedInPopOut = isOpened;
        });
    }

    get windowed$() {
        return this.electron.externalWindow$.asObservable();
    }

    get windowed() {
        return this.electron.externalWindow$.value;
    }

    toggleExternalWindow() {
        this.openedInPopOut = true;
        if (this.windowed) {
            this.electron.closePage();
            return;
        }
        this.electron.openPage('chat-box');
    }
}
