export interface GameCreationParams {
    timePerTurn: number;
}
export interface OnlineGameCreationParams extends GameCreationParams {
    id: string;
}
