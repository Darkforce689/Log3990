export interface Invitation {
    from: string;
    to: string;
    date: Date;
    type: InvitationType;
    args: InvitationArgs;
}

type InvitationArgs = GameInviteArgs; // or other args

export enum InvitationType {
    Game = 'Game',
}

export interface GameInviteArgs {
    id: string;
    password?: string;
}
