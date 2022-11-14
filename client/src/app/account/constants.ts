import {
    EXCHANGEALETTER_DESCRIPTION,
    EXCHANGEALETTER_ID,
    EXCHANGEALETTER_NAME,
    EXCHANGEHORSEALL_DESCRIPTION,
    EXCHANGEHORSEALL_ID,
    EXCHANGEHORSEALL_NAME,
    EXCHANGEHORSE_DESCRIPTION,
    EXCHANGEHORSE_ID,
    EXCHANGEHORSE_NAME,
    EXTRATURN_DESCRIPTION,
    EXTRATURN_ID,
    EXTRATURN_NAME,
    PLACERANDOMBONUS_DESCRIPTION,
    PLACERANDOMBONUS_ID,
    PLACERANDOMBONUS_NAME,
    REDUCETIMER_DESCRIPTION,
    REDUCETIMER_ID,
    REDUCETIMER_NAME,
    SKIPNEXTTURN_DESCRIPTION,
    SKIPNEXTTURN_ID,
    SKIPNEXTTURN_NAME,
    SPLITPOINTS_DESCRIPTION,
    SPLITPOINTS_ID,
    SPLITPOINTS_NAME,
    UIMagicCard,
} from '@app/game-logic/actions/magic-card/magic-card-constants';

export const SECONDS_TO_HOUR = 3600;
export const MILLI_TO_SECONDS = 1000;
export const MIN_IN_HOUR = 60;
export const SEC_IN_MIN = 60;
export const TIME_BASE = 10;

export const UI_MAGIC_CARD_ARRAY_REPLAY: { icon: string; info: UIMagicCard }[] = [
    { info: { id: EXCHANGEALETTER_ID, name: EXCHANGEALETTER_NAME, description: EXCHANGEALETTER_DESCRIPTION }, icon: 'font_download' },
    { info: { id: SPLITPOINTS_ID, name: SPLITPOINTS_NAME, description: SPLITPOINTS_DESCRIPTION }, icon: 'open_with' },
    { info: { id: PLACERANDOMBONUS_ID, name: PLACERANDOMBONUS_NAME, description: PLACERANDOMBONUS_DESCRIPTION }, icon: 'add_box' },
    { info: { id: EXCHANGEHORSE_ID, name: EXCHANGEHORSE_NAME, description: EXCHANGEHORSE_DESCRIPTION }, icon: 'swap_horizontal_circle' },
    { info: { id: EXCHANGEHORSEALL_ID, name: EXCHANGEHORSEALL_NAME, description: EXCHANGEHORSEALL_DESCRIPTION }, icon: 'shuffle' },
    { info: { id: SKIPNEXTTURN_ID, name: SKIPNEXTTURN_NAME, description: SKIPNEXTTURN_DESCRIPTION }, icon: 'hide_source' },
    { info: { id: EXTRATURN_ID, name: EXTRATURN_NAME, description: EXTRATURN_DESCRIPTION }, icon: 'exposure_plus_1' },
    { info: { id: REDUCETIMER_ID, name: REDUCETIMER_NAME, description: REDUCETIMER_DESCRIPTION }, icon: 'timelapse' },
];
