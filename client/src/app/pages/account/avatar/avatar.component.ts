import { Component, Input } from '@angular/core';

@Component({
    selector: 'app-avatar',
    templateUrl: './avatar.component.html',
    styleUrls: ['./avatar.component.scss'],
})
export class AvatarComponent {
    @Input() src: string;
    @Input() size: number;

    get imgSrc() {
        if (!this.src) {
            return 'assets/img/avatar/default.png';
        }
        return 'assets/img/avatar/' + this.src + '.png';
    }
}
