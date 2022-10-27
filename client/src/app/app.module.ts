import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ChatBoxComponent } from '@app/chat/components/chat-box/chat-box.component';
import { HeaderBarComponent } from '@app/components/header-bar/header-bar.component';
import { AbandonDialogComponent } from '@app/components/modals/abandon-dialog/abandon-dialog.component';
import { DisconnectedFromServerComponent } from '@app/components/modals/disconnected-from-server/disconnected-from-server.component';
import { ErrorDialogComponent } from '@app/components/modals/error-dialog/error-dialog.component';
import { JoinOnlineGameComponent } from '@app/components/modals/join-online-game/join-online-game.component';
import { LeaderboardComponent } from '@app/components/modals/leaderboard/leaderboard.component';
import { NewOnlineGameFormComponent } from '@app/components/modals/new-online-game-form/new-online-game-form.component';
import { PendingGamesComponent } from '@app/components/modals/pending-games/pending-games.component';
import { WaitingForOtherPlayersComponent } from '@app/components/modals/waiting-for-other-players/waiting-for-other-players.component';
import { ThemeSelectorComponent } from '@app/components/theme-selector/theme-selector.component';
import { ClickAndClickoutDirective } from '@app/directives/click-and-clickout.directive';
import { MouseRollDirective } from '@app/directives/mouse-roll.directive';
import { PreventContextMenuDirective } from '@app/directives/prevent-context-menu.directive';
import { AuthInterceptor } from '@app/interceptors/auth.interceptor';
import { AppRoutingModule } from '@app/modules/app-routing.module';
import { AppMaterialModule } from '@app/modules/material.module';
import { AccountPageComponent } from '@app/pages/account/account-page/account-page.component';
import { AvatarListComponent } from '@app/pages/account/avatar-list/avatar-list.component';
import { AvatarComponent } from '@app/pages/account/avatar/avatar.component';
import { AppComponent } from '@app/pages/app/app.component';
import { BoardComponent } from '@app/pages/game-page/board/board.component';
import { GamePageComponent } from '@app/pages/game-page/game-page.component';
import { HorseComponent } from '@app/pages/game-page/horse/horse.component';
import { InfoBoxComponent } from '@app/pages/game-page/info-box/info-box.component';
import { PlayerInfoComponent } from '@app/pages/game-page/player-info/player-info.component';
import { HomepageComponent } from '@app/pages/homepage/homepage.component';
import { NewGamePageComponent } from '@app/pages/new-game-page/new-game-page.component';
import { BoldPipe } from '@app/pipes/bold-pipe/bold.pipe';
import { AppSocketHandlerService } from '@app/socket-handler/app-socket-handler.service';
import { ConversationPickerComponent } from './chat/components/conversation-picker/conversation-picker.component';
import { AlertDialogComponent } from './components/modals/alert-dialog/alert-dialog.component';
import { LoadingGameComponent } from './components/modals/loading-game/loading-game.component';
import { WinnerDialogComponent } from './components/modals/winner-dialog/winner-dialog.component';
import { MagicCardListComponent } from './pages/game-page/magic-cards/magic-card-list.component';
import { MagicCardComponent } from './pages/game-page/magic-cards/magic-card/magic-card.component';
import { LoginPageComponent } from './pages/login-page/login-page.component';
import { PrototypePageComponent } from './pages/prototype-page/prototype-page.component';
import { RegisterPageComponent } from './pages/register-page/register-page.component';
import { JoinConversationComponent } from './chat/components/join-conversation/join-conversation.component';
import { CreateConversationComponent } from './chat/components/create-conversation/create-conversation.component';
import { MessageComponent } from './chat/components/message/message.component';

/**
 * Main module that is used in main.ts.
 * All automatically generated components will appear in this module.
 * Please do not move this module in the module folder.
 * Otherwise Angular Cli will not know in which module to put new component
 */
@NgModule({
    declarations: [
        AppComponent,
        GamePageComponent,
        PlayerInfoComponent,
        InfoBoxComponent,
        ChatBoxComponent,
        BoardComponent,
        HorseComponent,
        NewOnlineGameFormComponent,
        HomepageComponent,
        NewGamePageComponent,
        HeaderBarComponent,
        BoldPipe,
        PreventContextMenuDirective,
        ClickAndClickoutDirective,
        MouseRollDirective,
        WaitingForOtherPlayersComponent,
        PendingGamesComponent,
        JoinOnlineGameComponent,
        DisconnectedFromServerComponent,
        ErrorDialogComponent,
        LeaderboardComponent,
        PreventContextMenuDirective,
        ClickAndClickoutDirective,
        MouseRollDirective,
        AlertDialogComponent,
        AbandonDialogComponent,
        LoadingGameComponent,
        WinnerDialogComponent,
        PrototypePageComponent,
        LoginPageComponent,
        RegisterPageComponent,
        ThemeSelectorComponent,
        MagicCardListComponent,
        MagicCardComponent,
        AccountPageComponent,
        AvatarListComponent,
        AvatarComponent,
        ConversationPickerComponent,
        JoinConversationComponent,
        CreateConversationComponent,
        MessageComponent,
    ],
    imports: [
        AppMaterialModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        BrowserModule,
        FormsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatTableModule,
    ],
    providers: [
        {
            provide: APP_INITIALIZER,
            useFactory: () => {
                return () => {
                    return;
                };
            },
            deps: [AppSocketHandlerService],
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
        },
    ],
    bootstrap: [AppComponent],
})
export class AppModule {}
