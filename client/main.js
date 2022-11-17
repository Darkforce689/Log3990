const { app, BrowserWindow, session, ipcMain } = require('electron');

let appWindow;
let externalWindow;
let gameToken;
function initWindow() {
    appWindow = new BrowserWindow({
        height: 800,
        width: 1200,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
        },
    });

    // Electron Build Path
    const path = `file://${__dirname}/dist/client/index.html`;
    appWindow.loadURL(path);

    appWindow.setMenuBarVisibility(false);

    // Initialize the DevTools.
    appWindow.webContents.openDevTools()

    appWindow.on('closed', function () {
        appWindow = null;
        try {
            app.quit();
        } catch (error) {

        }
    });
}

function openChatWindow(route, eventSender) {
    externalWindow = new BrowserWindow({
        height: 600,
        width: 600,
        resizable: false,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
        },
    });
    // Electron Build Path
    const path = `file://${__dirname}/dist/client/index.html#/${route}?gameToken=${gameToken}`;
    externalWindow.loadURL(path);

    
    if (gameToken!==undefined) {
        externalWindow.webContents.send('game-token',gameToken);
    }
    externalWindow.setMenuBarVisibility(false);
    eventSender.send(`open-${route}`, true);

    externalWindow.on('close', (event, arg) => {
        eventSender.send(`open-${route}`, false);
    });

    externalWindow.on('closed', function () {
        externalWindow = null;
    });
}

app.on('ready', () => {
    ipcMain.on('open-external-window', (event, args) => {
        openChatWindow(args, event.sender);
    });

    ipcMain.on('close-external-window', (event, args) => {
        externalWindow.close();
    });

    ipcMain.on('game-token',(event,args)=>{
        gameToken = args;
     });

    session.defaultSession.webRequest.onHeadersReceived(
        { urls: ['http://localhost:3000/*', 'https://d2niwfi3hp97su.cloudfront.net/*'] },
        (details, callback) => {
            if (details.responseHeaders && details.responseHeaders['set-cookie'] && details.responseHeaders['set-cookie'].length) {
                details.responseHeaders['set-cookie'][0] = details.responseHeaders['set-cookie'][0] + '; SameSite=none; Secure';
            }

            if (
                details.responseHeaders &&
                details.responseHeaders['Set-Cookie'] &&
                details.responseHeaders['Set-Cookie'].length &&
                !details.responseHeaders['Set-Cookie'][0].includes('SameSite=none')
            ) {
                details.responseHeaders['Set-Cookie'][0] = details.responseHeaders['Set-Cookie'][0] + '; SameSite=none; Secure';
            }
            callback({ cancel: false, responseHeaders: details.responseHeaders });
        },
    );
    initWindow();
});

// Close when all windows are closed.
app.on('window-all-closed', function () {
    // On macOS specific close process
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

app.on('activate', function () {
    if (appWindow === null) {
        initWindow();
    }
});
