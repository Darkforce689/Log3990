import { ObjectId } from 'mongodb';

export const parseNumWithDefault = (s: string, def: number) => {
    const num = parseInt(s, 10);
    if (isNaN(num)) {
        return def;
    }

    if (num < 0) {
        return def;
    }
    return num;
};

export const parseBooleanQueryParam = (s: string | undefined, def: boolean) => {
    return s === undefined ? def : s === 'true';
};

export const isObjectId = (id: string) => {
    try {
        new ObjectId(id);
        return true;
    } catch (e) {
        return false;
    }
};
