import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppMaterialModule } from '@app/modules/material.module';

import { ProfilComponent } from './profil.component';

describe('ProfilComponent', () => {
    let component: ProfilComponent;
    let fixture: ComponentFixture<ProfilComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [ProfilComponent],
            imports: [AppMaterialModule, HttpClientModule, BrowserAnimationsModule],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProfilComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
