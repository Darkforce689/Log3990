import { Component, EventEmitter, Output } from '@angular/core';
import { UIInput } from '@app/game-logic/interfaces/ui-input';
import { ElectronIpcService } from '@app/services/electron-ipc.service';
@Component({
    selector: 'app-integrated-chat',
    templateUrl: './integrated-chat.component.html',
    styleUrls: ['./integrated-chat.component.scss'],
})
export class IntegratedChatComponent {
    @Output() clickChatbox = new EventEmitter();
    opened: boolean = false;

    constructor(private electron: ElectronIpcService) {
        this.windowed$.subscribe((opened) => {
            this.opened = !opened;
        });
    }

    receiveInputAndRetransmit(input: UIInput) {
        this.clickChatbox.emit(input);
    }

    get windowed$() {
        return this.electron.externalWindow$.asObservable();
    }

    get windowed() {
        return this.electron.externalWindow$.value;
    }

    toggleExternalWindow() {
        if (this.windowed) {
            this.electron.closePage();
            return;
        }
        this.electron.openPage('chat-box');
        this.opened = false;
    }
}
