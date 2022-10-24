import { Component } from '@angular/core';
import { BrowserWindow } from 'electron';
@Component({
    selector: 'app-integrated-chat',
    templateUrl: './integrated-chat.component.html',
    styleUrls: ['./integrated-chat.component.scss'],
})
export class IntegratedChatComponent {
    opened: boolean;
    content = 'fdhfdg hfghfhf  dghfgdh  \n fgd g gfdhdfgh fdghg fhdfhdf dfsdfsdfsdfsdfsggccccccccccfdsdfsdfsdfsdfsdfsfds \n';

    initWindow() {
        const chatWindow = new BrowserWindow({
            // fullscreen: true,
            height: 800,
            width: 1000,
            webPreferences: {
                nodeIntegration: true,
            },
        });

        // Electron Build Path
        const path = 'file://' + __dirname + '/integrated-chat.component.html';
        chatWindow.loadURL(path);

        chatWindow.setMenuBarVisibility(false);

        // Initialize the DevTools.
        chatWindow.webContents.openDevTools();

        chatWindow.on('closed', () => {
            chatWindow.close();
        });
    }
}
