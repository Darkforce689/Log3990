import { Component } from '@angular/core';
import { ThemeService } from '@app/services/theme/theme.service';
@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss'],
})
export class AppComponent {
    constructor(private themeService: ThemeService) {}

    get currentTheme() {
        return this.themeService.theme;
    }
}
