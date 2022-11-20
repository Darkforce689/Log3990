import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { UserCacheService } from './user-cache.service';

describe('UserCacheService', () => {
    let service: UserCacheService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
        });
        service = TestBed.inject(UserCacheService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
