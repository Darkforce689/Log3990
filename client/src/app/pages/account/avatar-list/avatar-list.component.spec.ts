import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AvatarListComponent } from '@app/pages/account/avatar-list/avatar-list.component';

describe('AvatarComponent', () => {
    let component: AvatarListComponent;
    let fixture: ComponentFixture<AvatarListComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [AvatarListComponent],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(AvatarListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
