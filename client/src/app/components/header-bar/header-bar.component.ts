import { Component } from '@angular/core';
import { ThemeService } from '@app/services/theme/theme.service';

@Component({
    selector: 'app-header-bar',
    templateUrl: './header-bar.component.html',
    styleUrls: ['./header-bar.component.scss'],
})
export class HeaderBarComponent {
    constructor(private themeService: ThemeService) {}

    get isDarkMode() {
        return this.themeService.isDark;
    }
}
