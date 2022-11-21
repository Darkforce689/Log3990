import { Injectable } from '@angular/core';
import { ElectronIpcService } from '@app/services/electron-ipc.service';

@Injectable({
    providedIn: 'root',
})
export class PopChatService {
    isPopOut = false;
    isOpen = true;
    constructor(private electron: ElectronIpcService) {
        this.windowed$.subscribe((isOpened: boolean) => {
            this.isPopOut = isOpened;
        });
    }

    get windowed$() {
        return this.electron.externalWindow$.asObservable();
    }

    get windowed() {
        return this.electron.externalWindow$.value;
    }

    toggleExternalWindow() {
        this.isPopOut = true;
        if (this.windowed) {
            this.electron.closePage();
            return;
        }
        this.electron.openPage('chat-box');
    }

    setOpen() {
        this.isOpen = true;
    }

    setClose() {
        this.isOpen = false;
    }

    get isOpenInWindow() {
        return !this.isPopOut && this.isOpen;
    }
}
