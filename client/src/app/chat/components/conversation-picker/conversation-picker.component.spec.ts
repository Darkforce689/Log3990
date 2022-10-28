import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';

import { ConversationPickerComponent } from './conversation-picker.component';

describe('ConversationPickerComponent', () => {
    let component: ConversationPickerComponent;
    let fixture: ComponentFixture<ConversationPickerComponent>;
    const mockDialogRef = {
        close: jasmine.createSpy('close'),
    };
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            providers: [{ provide: MatDialog, useValue: mockDialogRef }],
            declarations: [ConversationPickerComponent],
            imports: [HttpClientTestingModule, MatMenuModule],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ConversationPickerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
