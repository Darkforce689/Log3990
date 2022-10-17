export interface UIMagicCard {
    id: string;
    name: string;
    description: string;
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

export const UI_MAGIC_CARD_ARRAY: UIMagicCard[] = [
    { id: EXCHANGEALETTER_ID, name: EXCHANGEALETTER_NAME, description: EXCHANGEALETTER_DESCRIPTION },
    { id: SPLITPOINTS_ID, name: SPLITPOINTS_NAME, description: SPLITPOINTS_DESCRIPTION },
    { id: PLACERANDOMBONUS_ID, name: PLACERANDOMBONUS_NAME, description: PLACERANDOMBONUS_DESCRIPTION },
    { id: EXCHANGEHORSE_ID, name: EXCHANGEHORSE_NAME, description: EXCHANGEHORSE_DESCRIPTION },
];

export const UI_MAGIC_CARD_MAP: Map<string, UIMagicCard> = new Map(UI_MAGIC_CARD_ARRAY.map((magicCard) => [magicCard.id, magicCard]));
