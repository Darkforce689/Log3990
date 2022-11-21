import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { JoinOnlineGameComponent } from '@app/components/modals/join-online-game/join-online-game.component';
import { AppMaterialModule } from '@app/modules/material.module';
import { of } from 'rxjs';

describe('JoinOnlineGameComponent', () => {
    let component: JoinOnlineGameComponent;
    let fixture: ComponentFixture<JoinOnlineGameComponent>;

    const mockDialogRef = {
        close: jasmine.createSpy('close').and.returnValue(() => {
            return;
        }),
    };
    const mockDialog = {
        open: jasmine.createSpy('open').and.returnValue({ afterClosed: () => of(true) }),
    };

    beforeEach(
        waitForAsync(() => {
            TestBed.configureTestingModule({
                imports: [FormsModule, ReactiveFormsModule, BrowserAnimationsModule, AppMaterialModule, HttpClientTestingModule, RouterTestingModule],
                providers: [
                    { provide: MAT_DIALOG_DATA, useValue: {} },
                    { provide: MatDialogRef, useValue: mockDialogRef },
                    { provide: MatDialog, useValue: mockDialog },
                ],
                declarations: [JoinOnlineGameComponent, DatePipe],
                schemas: [CUSTOM_ELEMENTS_SCHEMA],
            }).compileComponents();
        }),
    );

    beforeEach(() => {
        fixture = TestBed.createComponent(JoinOnlineGameComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('cancel', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const cancelButton = dom.querySelectorAll('button')[0];
        spyOn(component, 'cancel');
        cancelButton.click();
        expect(component.cancel).toHaveBeenCalled();
    });

    it('startGame should not call sendParameter if no password is entered', () => {
        const dom = fixture.nativeElement as HTMLElement;
        const startGameButton = dom.querySelectorAll('button')[1];
        fixture.detectChanges();
        spyOn(component, 'sendParameter');
        startGameButton.click();
        fixture.detectChanges();
        expect(component.sendParameter).not.toHaveBeenCalled();
    });

    it('startGame should close the dialog', () => {
        component.sendParameter();
        expect(mockDialogRef.close).toHaveBeenCalled();
    });
});
