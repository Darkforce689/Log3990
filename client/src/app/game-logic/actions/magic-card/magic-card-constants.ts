export interface UIMagicCard {
    name: string;
    description: string;
}

export const NO_CARD_ID = 'MC_NO_CARD';

export const EXCHANGEALETTER_ID = 'MC_EXCHANGE_LETTER';
export const EXCHANGEALETTER_NAME = 'Échanger une lettre';
export const EXCHANGEALETTER_DESCRIPTION =
    'Échanger la lettre sélectionnée (sélectionner une lettre avec un clic gauche) pour une lettre aléatoire de la réserve';

export const SPLITPOINTS_ID = 'MC_SPLIT_POINTS';
export const SPLITPOINTS_NAME = 'Vole des points';
export const SPLITPOINTS_DESCRIPTION = 'Voler des points du joueur en tête et les séparer entre tous les joueurs';

export const UI_MAGIC_CARD_MAP: Map<string, UIMagicCard> = new Map<string, UIMagicCard>([
    [EXCHANGEALETTER_ID, { name: EXCHANGEALETTER_NAME, description: EXCHANGEALETTER_DESCRIPTION }],
    [SPLITPOINTS_ID, { name: SPLITPOINTS_NAME, description: SPLITPOINTS_DESCRIPTION }],
    [NO_CARD_ID, { name: '', description: '' }],
]);
