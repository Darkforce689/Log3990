export interface InvitationDTO extends BaseInvitation {
    date: Date;
}

export interface BaseInvitation {
    from: string;
    to: string;
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
