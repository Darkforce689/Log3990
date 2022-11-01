import { Tile } from '@app/game/game-logic/board/tile';
import { MagicServerGame } from '@app/game/game-logic/game/magic-server-game';
import { ServerGame } from '@app/game/game-logic/game/server-game';
import { ForfeitPlayerInfo, GameState, LightPlayer, MagicGameState } from '@app/game/game-logic/interface/game-state.interface';
import { BotPlayer } from '@app/game/game-logic/player/bot-player';
import { Player } from '@app/game/game-logic/player/player';
import { GameStats } from '@app/user/interfaces/game-stats.interface';
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

    compileGameStatistics(players: Player[], winners: Player[], totalTime: number): Map<string, GameStats> {
        const gameStats = new Map<string, GameStats>();
        winners
            .filter((player) => !(player instanceof BotPlayer))
            .forEach((winner) => gameStats.set(winner.name, { isWinner: true, points: winner.points, totalTime }));
        players
            .filter((player) => !(player instanceof BotPlayer))
            .filter((player) => !gameStats.has(player.name))
            .forEach((player) => gameStats.set(player.name, { isWinner: false, points: player.points, totalTime }));
        return gameStats;
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
            drawnMagicCards: game.drawnMagicCards,
        };
        return magicGameState;
    }

    private fillPlayer(players: Player[]): LightPlayer[] {
        const playersInfo: LightPlayer[] = [];
        players.forEach((player) => {
            playersInfo.push({ name: player.name, points: player.points, letterRack: player.letterRack });
        });
        return playersInfo;
    }
}
