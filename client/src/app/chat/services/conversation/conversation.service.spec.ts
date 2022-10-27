import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ConversationService } from './conversation.service';

describe('ConversationService', () => {
    let service: ConversationService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
        });
        service = TestBed.inject(ConversationService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
