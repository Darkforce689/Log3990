import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrototypePageComponent } from './prototype-page.component';

describe('PrototypePageComponent', () => {
    let component: PrototypePageComponent;
    let fixture: ComponentFixture<PrototypePageComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [PrototypePageComponent],
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(PrototypePageComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
