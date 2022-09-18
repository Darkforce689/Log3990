/* eslint-disable no-console */
export class ServerLogger {
    static logInfo(...logs: unknown[]) {
        ServerLogger.logInternal(console.log, 'INFO', ...logs);
    }

    static logDebug(...logs: unknown[]) {
        ServerLogger.logInternal(console.debug, 'DEBUG', ...logs);
    }

    static logError(...logs: unknown[]) {
        ServerLogger.logInternal(console.error, 'ERROR', ...logs);
    }

    private static logInternal(consolePrinter: (...params: unknown[]) => void, severity: string, ...logs: unknown[]) {
        consolePrinter(' [ ', new Date(Date.now()).toISOString(), ' - ', severity, ' ] ', ...logs);
    }
}
