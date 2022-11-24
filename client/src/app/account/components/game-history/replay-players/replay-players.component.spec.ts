import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppMaterialModule } from '@app/modules/material.module';

import { ReplayPlayersComponent } from './replay-players.component';

describe('ReplayPlayersComponent', () => {
    let component: ReplayPlayersComponent;
    let fixture: ComponentFixture<ReplayPlayersComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [ReplayPlayersComponent],
            imports: [AppMaterialModule, HttpClientTestingModule],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ReplayPlayersComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
