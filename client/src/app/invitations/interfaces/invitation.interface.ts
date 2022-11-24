export interface InvitationDTO extends BaseInvitation {
    from: string;
    to: string;
    date: Date;
}

export interface BaseInvitation {
    type: InvitationType;
    args: InvitationArgs;
}

export type Invitation = InvitationDTO;
export type InvitationArgs = GameInviteArgs; // or other args

export enum InvitationType {
    Game = 'Game',
}

export interface GameInviteArgs {
    id: string;
    password?: string;
}
