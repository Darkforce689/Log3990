/* tslint:disable:no-unused-variable */
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IntegratedChatComponent } from './integrated-chat.component';

describe('IntegratedChatComponent', () => {
    let component: IntegratedChatComponent;
    let fixture: ComponentFixture<IntegratedChatComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [IntegratedChatComponent],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(IntegratedChatComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
