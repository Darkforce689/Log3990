import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppMaterialModule } from '@app/modules/material.module';

import { GameStatsPageComponent } from './game-stats-page.component';

describe('GameStatsPageComponent', () => {
    let component: GameStatsPageComponent;
    let fixture: ComponentFixture<GameStatsPageComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [GameStatsPageComponent],
            imports: [HttpClientTestingModule, AppMaterialModule, BrowserAnimationsModule],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(GameStatsPageComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
