export interface ObjectCrudResult<T> {
    errors: string[];
    object: T | undefined;
}
