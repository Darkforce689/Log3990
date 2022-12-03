import { OverlayContainer } from '@angular/cdk/overlay';
import { Injectable } from '@angular/core';
import { Themes } from '@app/services/theme/themes.enum';

export interface CustomColors {
    tileFontColor: string;
    tileBackgroundColor: string;
}
@Injectable({
    providedIn: 'root',
})
export class ThemeService {
    static darkThemeColors = {
        tileFontColor: 'white',
        tileBackgroundColor: '#55342b',
    } as CustomColors;

    static lightThemeColors = {
        tileFontColor: 'black',
        tileBackgroundColor: '#d7c096',
    } as CustomColors;

    private static readonly defaultTheme = Themes.Light;

    private currentTheme: Themes;

    constructor(private overlayContainer: OverlayContainer) {
        this.updateTheme(ThemeService.defaultTheme);
    }

    updateTheme(newTheme: Themes) {
        this.currentTheme = newTheme;
        this.updateOverlayContainerThemes(newTheme);
    }

    get colors(): CustomColors {
        return this.isDark ? ThemeService.darkThemeColors : ThemeService.lightThemeColors;
    }

    private updateOverlayContainerThemes(newTheme: Themes) {
        // From https://stackoverflow.com/questions/48431227/angular-w-angular-material-dialog-theme-broken
        const overlayContainerClasses = this.overlayContainer.getContainerElement().classList;
        const themeClassesToRemove = Array.from(overlayContainerClasses).filter((item: string) => item.includes('theme'));
        if (themeClassesToRemove.length > 0) {
            overlayContainerClasses.remove(...themeClassesToRemove);
        }
        overlayContainerClasses.add(newTheme);
    }

    get theme(): Themes {
        return this.currentTheme;
    }

    get isDark(): boolean {
        return this.currentTheme === Themes.Dark;
    }
}
