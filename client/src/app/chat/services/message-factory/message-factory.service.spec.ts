import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { MessageFactoryService } from './message-factory.service';

describe('MessageFactoryService', () => {
    let service: MessageFactoryService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
        });
        service = TestBed.inject(MessageFactoryService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
