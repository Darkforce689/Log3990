/* eslint-disable no-underscore-dangle */
import { GameHistory, GameStateHistory } from '@app/account/user-game-history/game-history.interface';
import { Pagination } from '@app/common/interfaces/pagination.interface';
import { GAMES_COLLECTION, USER_COLLECTION } from '@app/constants';
import { MongoDBClientService } from '@app/database/mongodb-client.service';
import { GameStateToken } from '@app/game/game-logic/interface/game-state.interface';
import { ServerLogger } from '@app/logger/logger';
import { User } from '@app/user/interfaces/user.interface';
import { ObjectId } from 'mongodb';
import { Service } from 'typedi';

const filterDate = -1;
@Service()
export class GameHistoryService {
    constructor(private mongoService: MongoDBClientService) {}

    private get collection() {
        return this.mongoService.db.collection(GAMES_COLLECTION);
    }

    async insertGameState(gameStateToken: GameStateToken) {
        try {
            const { gameToken, gameState } = gameStateToken;
            const date = Date.now();
            await this.collection.insertOne({ gameToken, gameState, date });
            return [];
        } catch (error) {
            ServerLogger.logError(error);
            return ['UNEXPECTED_ERROR'];
        }
    }

    async insertGame(gameToken: string, userNames: string[], winnerNames: string[], date: number) {
        try {
            const users: User[] = [];
            for (const name of userNames) {
                const user = (await this.mongoService.db.collection(USER_COLLECTION).findOne({ name: { $eq: name } })) as unknown as User;
                users.push(user);
            }
            const winners = users.filter((user: User) => winnerNames.find((winnerName) => user.name === winnerName));
            await this.collection.insertOne({
                gameToken,
                userIds: users.map((user) => user._id),
                winnerUsers: winners.map((user) => user._id),
                date,
            });
            return [];
        } catch (error) {
            ServerLogger.logError(error);
            return ['UNEXPECTED_ERROR'];
        }
    }

    async getGameHistory(userId: string, pagination: Pagination): Promise<GameHistory[]> {
        const objectId = new ObjectId(userId);
        const { perPage, page } = pagination;
        const result = await this.collection
            .find({ userIds: objectId })
            .sort('date', filterDate)
            .skip(perPage * page)
            .limit(perPage)
            .toArray();
        return result as GameHistory[];
    }

    async getGameStates(gameToken: string): Promise<GameStateHistory[]> {
        const result = await this.collection.find({ gameToken }).sort('date', filterDate).toArray();
        return result as GameStateHistory[];
    }
}