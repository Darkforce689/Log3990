import { MAX_FILE_LENGTH } from '@app/constants';
import { DictionaryServer } from '@app/dictionary-manager/default-dictionary';
import * as fs from 'fs';
import { Service } from 'typedi';

@Service()
export class DictionaryServerService {
    allDictionary: DictionaryServer[] = [];

    constructor(private folderPath = 'assets/') {
        this.loadFromFile();
    }

    getDictsList(): DictionaryServer[] {
        const dictsList: DictionaryServer[] = [];

        for (const dict of this.allDictionary) {
            const tmpDict: DictionaryServer = { title: dict.title, description: dict.description, canEdit: dict.canEdit };
            dictsList.push(tmpDict);
        }
        return dictsList;
    }

    getUniqueName(dictTitle: string): string {
        for (const dict of this.allDictionary) {
            if (dict.title !== dictTitle) {
                continue;
            }
            const uniqueName = dictTitle + '_' + dict.date;
            return uniqueName;
        }
        return '';
    }

    getDictByTitle(dictTitle: string): DictionaryServer | undefined {
        return this.allDictionary.find(
            (dictionary) => dictionary.title.replace(/\s/g, '').slice(0, MAX_FILE_LENGTH) === dictTitle.replace(/\s/g, '').slice(0, MAX_FILE_LENGTH),
        );
    }

    private loadFromFile() {
        this.allDictionary = [];
        fs.readdirSync(this.folderPath).forEach((file) => {
            if (!file.includes('.json')) {
                return;
            }
            const dict = JSON.parse(fs.readFileSync(this.folderPath + file, 'utf8'));
            this.allDictionary.push(dict);
        });
    }
}
