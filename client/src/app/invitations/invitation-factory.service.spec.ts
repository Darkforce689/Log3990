import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { InvitationFactoryService } from './invitation-factory.service';

describe('InvitationFactoryService', () => {
    let service: InvitationFactoryService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
        });
        service = TestBed.inject(InvitationFactoryService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
