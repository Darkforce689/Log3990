import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppRoutingModule } from '@app/modules/app-routing.module';
import { AppMaterialModule } from '@app/modules/material.module';
import { HeaderBarComponent } from './header-bar.component';

describe('HeaderBarComponent', () => {
    let component: HeaderBarComponent;
    let fixture: ComponentFixture<HeaderBarComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [AppRoutingModule, HttpClientTestingModule, AppMaterialModule],
        }).compileComponents();
        fixture = TestBed.createComponent(HeaderBarComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
