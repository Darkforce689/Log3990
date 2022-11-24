import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatSelectionListChange } from '@angular/material/list';
import { currentLevel } from '@app/game-logic/utils';
import { AccountService } from '@app/services/account.service';
interface Avatar {
    src: string;
    name: string;
    unlockLevel: number;
}
@Component({
    selector: 'app-avatar-list',
    templateUrl: './avatar-list.component.html',
    styleUrls: ['./avatar-list.component.scss'],
})
export class AvatarListComponent {
    @Output() srcChange = new EventEmitter<string>();
    @Input() hasClear = false;
    avatarDefault: Avatar[] = [
        { src: 'assets/img/avatar/elephant.png', name: 'elephant', unlockLevel: 0 },
        { src: 'assets/img/avatar/fox.png', name: 'fox', unlockLevel: 0 },
        { src: 'assets/img/avatar/hippopotamus.png', name: 'hippopotamus', unlockLevel: 0 },
        { src: 'assets/img/avatar/polarbear.png', name: 'polarbear', unlockLevel: 0 },
        { src: 'assets/img/avatar/stag.png', name: 'stag', unlockLevel: 0 },
        { src: 'assets/img/avatar/lynx.png', name: 'lynx', unlockLevel: 0 },
    ];
    avatarAdd: Avatar[] = [
        { src: 'assets/img/avatar/koala.png', name: 'koala', unlockLevel: 1 },
        { src: 'assets/img/avatar/bear.png', name: 'bear', unlockLevel: 1 },
        { src: 'assets/img/avatar/frog.png', name: 'frog', unlockLevel: 2 },
        { src: 'assets/img/avatar/giraffe.png', name: 'giraffe', unlockLevel: 3 },
        { src: 'assets/img/avatar/hippo.png', name: 'hippo', unlockLevel: 4 },
        { src: 'assets/img/avatar/lion.png', name: 'lion', unlockLevel: 5 },
        { src: 'assets/img/avatar/monkey.png', name: 'monkey', unlockLevel: 6 },
        { src: 'assets/img/avatar/owl.png', name: 'owl', unlockLevel: 7 },
        { src: 'assets/img/avatar/raccoon.png', name: 'raccoon', unlockLevel: 8 },
        { src: 'assets/img/avatar/eagle.png', name: 'eagle', unlockLevel: 9 },
        { src: 'assets/img/avatar/wolf.png', name: 'wolf', unlockLevel: 10 },
    ];

    constructor(private accountService: AccountService) {}

    updateAvatar(src: MatSelectionListChange) {
        const selection = src.options[0].value;
        this.srcChange.emit(selection);
    }

    isUnlocked(unlockLevel: number) {
        const totalExp = this.accountService.account?.totalExp;
        if (totalExp === undefined) {
            return unlockLevel === 0;
        }
        return currentLevel(totalExp) >= unlockLevel;
    }

    canSeeLocked() {
        const totalExp = this.accountService.account?.totalExp;
        return totalExp !== undefined;
    }
}
