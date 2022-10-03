import { ObjectId } from 'mongodb';

export interface UserQuery {
    email?: string;
    name?: string;
    _id?: string | ObjectId;
}
