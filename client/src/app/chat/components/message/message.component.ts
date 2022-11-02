import { Component, Input } from '@angular/core';
import { Message } from '@app/chat/interfaces/message.interface';
// import { BoldPipe } from '@app/pipes/bold-pipe/bold.pipe';
// import { NewlinePipe } from '@app/pipes/newline-pipe/newline.pipe';

@Component({
    selector: 'app-message',
    templateUrl: './message.component.html',
    styleUrls: ['./message.component.scss'],
})
export class MessageComponent {
    @Input() message: Message;
    // TODO: OLI
    //  private boldPipe = new BoldPipe();
    //  private newlinePipe = new NewlinePipe();

    generateMessageContentHTML(content: string) {
        // let transformedContent = this.boldPipe.transform(content);
        // transformedContent = this.newlinePipe.transform(transformedContent);
        return content;
    }
}
