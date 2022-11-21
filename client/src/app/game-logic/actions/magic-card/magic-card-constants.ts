export interface UIMagicCard {
    id: string;
    name: string;
    description: string;
    icon: string;
}

export const EXCHANGEALETTER_ID = 'MC_EXCHANGE_LETTER';
export const EXCHANGEALETTER_NAME = 'Échanger une lettre';
export const EXCHANGEALETTER_DESCRIPTION =
    'Échanger la lettre sélectionnée (sélectionner une lettre avec un clic gauche) pour une lettre aléatoire de la réserve';

export const SPLITPOINTS_ID = 'MC_SPLIT_POINTS';
export const SPLITPOINTS_NAME = 'Vole des points';
export const SPLITPOINTS_DESCRIPTION = 'Voler des points du joueur en tête et les séparer entre tous les joueurs';

export const PLACERANDOMBONUS_ID = 'MC_PLACE_RANDOM_BONUS';
export const PLACERANDOMBONUS_NAME = 'Place un bonus';
export const PLACERANDOMBONUS_DESCRIPTION = 'Place un bonus aléatoire sur une tuile';

export const EXCHANGEHORSE_ID = 'MC_EXCHANGE_HORSE';
export const EXCHANGEHORSE_NAME = 'Échanger son chevalet';
export const EXCHANGEHORSE_DESCRIPTION = "Échanger son chevalet avec celui d'un autre joueur aléatoire";

export const EXCHANGEHORSEALL_ID = 'MC_EXCHANGE_HORSE_ALL';
export const EXCHANGEHORSEALL_NAME = 'Échanger tous les chevalets';
export const EXCHANGEHORSEALL_DESCRIPTION = 'Chaque joueur échange son chevalet avec celui du joueur suivant';

export const SKIPNEXTTURN_ID = 'MC_SKIP_NEXT_TURN';
export const SKIPNEXTTURN_NAME = 'Passe le prochain tour';
export const SKIPNEXTTURN_DESCRIPTION = 'Le prochain joueur perd son tour';

export const EXTRATURN_ID = 'MC_EXTRA_TURN';
export const EXTRATURN_NAME = 'Prend un tour supplémentaire';
export const EXTRATURN_DESCRIPTION = 'Prendre un tour supplémentaire avant de donner le contrôle à une autre joueur';

export const REDUCETIMER_ID = 'MC_REDUCE_TIMER';
export const REDUCETIMER_NAME = 'Réduit le temps des autres joueurs';
export const REDUCETIMER_DESCRIPTION = 'Réduit de moitié le temps de jeu des autres joueurs pour un tour complet';

export const UI_MAGIC_CARD_ARRAY: UIMagicCard[] = [
    { id: EXCHANGEALETTER_ID, name: EXCHANGEALETTER_NAME, description: EXCHANGEALETTER_DESCRIPTION, icon: 'font_download' },
    { id: SPLITPOINTS_ID, name: SPLITPOINTS_NAME, description: SPLITPOINTS_DESCRIPTION, icon: 'open_with' },
    { id: PLACERANDOMBONUS_ID, name: PLACERANDOMBONUS_NAME, description: PLACERANDOMBONUS_DESCRIPTION, icon: 'add_box' },
    { id: EXCHANGEHORSE_ID, name: EXCHANGEHORSE_NAME, description: EXCHANGEHORSE_DESCRIPTION, icon: 'swap_horizontal_circle' },
    { id: EXCHANGEHORSEALL_ID, name: EXCHANGEHORSEALL_NAME, description: EXCHANGEHORSEALL_DESCRIPTION, icon: 'shuffle' },
    { id: SKIPNEXTTURN_ID, name: SKIPNEXTTURN_NAME, description: SKIPNEXTTURN_DESCRIPTION, icon: 'hide_source' },
    { id: EXTRATURN_ID, name: EXTRATURN_NAME, description: EXTRATURN_DESCRIPTION, icon: 'exposure_plus_1' },
    { id: REDUCETIMER_ID, name: REDUCETIMER_NAME, description: REDUCETIMER_DESCRIPTION, icon: 'timelapse' },
];

export const UI_MAGIC_CARD_MAP: Map<string, UIMagicCard> = new Map(UI_MAGIC_CARD_ARRAY.map((magicCard) => [magicCard.id, magicCard]));
