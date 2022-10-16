import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatSelectionListChange } from '@angular/material/list';
interface Avatar {
    src: string;
    name: string;
}
@Component({
    selector: 'app-avatar-list',
    templateUrl: './avatar-list.component.html',
    styleUrls: ['./avatar-list.component.scss'],
})
export class AvatarListComponent {
    @Output() srcChange = new EventEmitter<string>();
    @Input() hasClear = false;
    avatarTypes: Avatar[] = [
        { src: 'assets/img/avatar/koala.png', name: 'Koala' },
        { src: 'assets/img/avatar/fox.png', name: 'Renard' },
        { src: 'assets/img/avatar/elephant.png', name: 'Élephant' },
        { src: 'assets/img/avatar/lynx.png', name: 'Panthère' },
        { src: 'assets/img/avatar/polarbear.png', name: 'Ours polaire' },
        { src: 'assets/img/avatar/stag.png', name: 'Cerf' },
    ];

    updateAvatar(src: MatSelectionListChange) {
        const selection = src.options[0].value;
        this.srcChange.emit(selection);
    }
}
