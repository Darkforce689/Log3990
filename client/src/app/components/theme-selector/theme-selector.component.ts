import { Component } from '@angular/core';
import { ThemeService } from '@app/services/theme/theme.service';
import { Themes } from '@app/services/theme/themes.enum';

@Component({
    selector: 'app-theme-selector',
    templateUrl: './theme-selector.component.html',
    styleUrls: ['./theme-selector.component.scss'],
})
export class ThemeSelectorComponent {
    constructor(private themeService: ThemeService) {}

    set isDarkMode(newValue: boolean) {
        this.themeService.updateTheme(newValue ? Themes.Dark : Themes.Light);
    }

    get isDarkMode(): boolean {
        return this.themeService.isDark;
    }
}
