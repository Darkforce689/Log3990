import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { JoinConversationComponent } from './join-conversation.component';

describe('JoinConversationComponent', () => {
    let component: JoinConversationComponent;
    let fixture: ComponentFixture<JoinConversationComponent>;
    const mockDialogRef = {
        close: jasmine.createSpy('close'),
    };
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            providers: [
                { provide: MatDialogRef, useValue: mockDialogRef },
                { provide: MatSnackBar, useValue: {} },
            ],
            declarations: [JoinConversationComponent],
            imports: [HttpClientTestingModule, MatMenuModule, MatPaginatorModule, BrowserAnimationsModule],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(JoinConversationComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
