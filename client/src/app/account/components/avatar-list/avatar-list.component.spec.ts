import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AvatarListComponent } from '@app/account/components/avatar-list/avatar-list.component';
import { AppMaterialModule } from '@app/modules/material.module';

describe('AvatarComponent', () => {
    let component: AvatarListComponent;
    let fixture: ComponentFixture<AvatarListComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [AvatarListComponent],
            imports: [AppMaterialModule, HttpClientTestingModule],
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
