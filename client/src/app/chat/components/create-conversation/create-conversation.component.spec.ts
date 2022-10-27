import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';

import { CreateConversationComponent } from './create-conversation.component';

describe('CreateConversationComponent', () => {
    let component: CreateConversationComponent;
    let fixture: ComponentFixture<CreateConversationComponent>;
    const mockDialogRef = {
        close: jasmine.createSpy('close'),
    };
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [CreateConversationComponent],
            providers: [{ provide: MatDialogRef, useValue: mockDialogRef }],
            imports: [HttpClientTestingModule],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(CreateConversationComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
