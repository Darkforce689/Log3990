import { Tile } from '@app/game/game-logic/board/tile';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { MagicServerGame } from '@app/game/game-logic/game/magic-server-game';
import { ForfeitPlayerInfo, GameState, LightPlayer, MagicGameState } from '@app/game/game-logic/interface/game-state.interface';
import { Player } from '@app/game/game-logic/player/player';
import { Service } from 'typedi';

@Service()
export class GameCompiler {
    compile(game: ServerGame): GameState | MagicGameState {
        const gameState = this.compileGameState(game);
        if (game instanceof MagicServerGame) {
            return this.compileMagicGameState(game, gameState);
        }
        return gameState;
    }

    compilePlayerInfo(playerInfo: Player, previousPlayerName: string): ForfeitPlayerInfo {
        return { name: playerInfo.name, previousPlayerName };
    }

    private compileGameState(game: ServerGame): GameState {
        const lightPlayers: LightPlayer[] = this.fillPlayer(game.players);
        const activePlayer = game.activePlayerIndex;
        const lightGrid: Tile[][] = game.board.grid;
        let lightEndOfGame = false;
        let lightWinnerIndex: number[] = [];
        if (game.isEndOfGame()) {
            lightEndOfGame = true;
            if (game.getWinner().length === 2) {
                lightWinnerIndex = [0, 1];
            } else if (game.getWinner()[0].name === game.players[0].name) {
                lightWinnerIndex = [0];
            } else {
                lightWinnerIndex = [1];
            }
        } else {
            lightEndOfGame = false;
        }
        return {
            players: lightPlayers,
            activePlayerIndex: activePlayer,
            grid: lightGrid,
            isEndOfGame: lightEndOfGame,
            lettersRemaining: game.letterBag.lettersLeft,
            winnerIndex: lightWinnerIndex,
        };
    }

    private compileMagicGameState(game: MagicServerGame, gameState: GameState): MagicGameState {
        const magicGameState: MagicGameState = {
            players: gameState.players,
            activePlayerIndex: gameState.activePlayerIndex,
            grid: gameState.grid,
            isEndOfGame: gameState.isEndOfGame,
            lettersRemaining: gameState.lettersRemaining,
            winnerIndex: gameState.winnerIndex,
            drawableMagicCards: game.drawableMagicCards,
        };
        return magicGameState;
    }

    private fillPlayer(players: Player[]): LightPlayer[] {
        return [
            { name: players[0].name, points: players[0].points, letterRack: players[0].letterRack },
            { name: players[1].name, points: players[1].points, letterRack: players[1].letterRack },
        ];
    }
}
