/* eslint-disable @typescript-eslint/no-require-imports */
import { DictionaryServer } from '@app/dictionary-manager/default-dictionary';
import { DictionaryServerService } from '@app/dictionary-manager/dictionary-server.service';
import { DEFAULT_DICTIONARY_TITLE } from '@app/game/game-logic/constants';
import { expect } from 'chai';
import fs = require('fs');

describe('DictionaryServerService', () => {
    const testPath = 'assets/testingEnvironnement/';
    let service: DictionaryServerService;

    before(() => {
        if (!fs.existsSync(testPath)) {
            fs.mkdirSync(testPath);
            fs.mkdirSync(testPath + 'someGarbageThatShouldBeIgnored');
        }
        if (!fs.existsSync(testPath + 'dictionary.json')) {
            fs.copyFileSync('assets/dictionary.json', testPath + 'dictionary.json');
        }
    });

    beforeEach(() => {
        service = new DictionaryServerService(testPath);
    });

    afterEach(() => {
        fs.readdirSync(testPath).forEach((file: string) => {
            if (file !== 'dictionary.json' && file !== 'someGarbageThatShouldBeIgnored') {
                const dictPath = testPath + file;
                fs.unlinkSync(dictPath);
            }
        });
    });

    after(() => {
        fs.unlinkSync(testPath + 'dictionary.json');
        fs.rmdirSync(testPath + 'someGarbageThatShouldBeIgnored');
        fs.rmdirSync(testPath);
    });

    it('should be created', () => {
        expect(service).to.equal(service);
    });

    it('should get the list of dict', () => {
        expect(service.getDictsList()).to.be.an('array');
    });

    it('should return the unique name of a dict', () => {
        expect(service.getUniqueName(DEFAULT_DICTIONARY_TITLE)).to.equal('Mon dictionnaire_0');
    });

    it('should return the unique name of a dict that doesnt exist', () => {
        expect(service.getUniqueName('test')).to.equal('');
    });

    it('should return the dict', () => {
        const result = service.getDictByTitle(DEFAULT_DICTIONARY_TITLE) as DictionaryServer;
        const expected = DEFAULT_DICTIONARY_TITLE;
        expect(result.title).to.equal(expected);
    });

    it('should return false if the dict doesnt exist', () => {
        expect(service.getDictByTitle('test')).to.equal(undefined);
    });
});
