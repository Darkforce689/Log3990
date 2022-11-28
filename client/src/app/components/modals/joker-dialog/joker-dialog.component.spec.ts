import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppMaterialModule } from '@app/modules/material.module';
import { JokerDialogComponent } from './joker-dialog.component';

const mockDialogRef = {
    close: jasmine.createSpy('close').and.returnValue(() => {
        return;
    }),
};

describe('JokerDialogComponent', () => {
    let component: JokerDialogComponent;
    let fixture: ComponentFixture<JokerDialogComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [AppMaterialModule],
            providers: [
                { provide: MAT_DIALOG_DATA, useValue: {} },
                { provide: MatDialogRef, useValue: mockDialogRef },
            ],
            declarations: [JokerDialogComponent],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(JokerDialogComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
