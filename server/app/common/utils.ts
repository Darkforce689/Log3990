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
