/* eslint-disable no-console */
export class ServerLogger {
    static logInfo(...logs: unknown[]) {
        console.log('< - INFO - > : ', ...logs);
    }

    static logDebug(...logs: unknown[]) {
        console.debug('< - DEBUG - > : ', ...logs);
    }

    static logError(...logs: unknown[]) {
        console.error('< - ERROR - > : ', ...logs);
    }
}
