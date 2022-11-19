import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
    selector: 'app-game-action-button',
    templateUrl: './game-action-button.component.html',
    styleUrls: ['./game-action-button.component.scss'],
})
export class GameActionButtonComponent {
    @Output() clickButton = new EventEmitter();
    @Input() label: string;
    @Input() icon: string;
    @Input() isDisabled: boolean = false;
}
