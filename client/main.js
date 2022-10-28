const { app, BrowserWindow, session } = require('electron');

let appWindow;

function initWindow() {
    appWindow = new BrowserWindow({
        // fullscreen: true,
        height: 800,
        width: 1000,
        webPreferences: {
            nodeIntegration: true,
        },
    });

    // Electron Build Path
    const path = `file://${__dirname}/dist/client/index.html`;
    appWindow.loadURL(path);

    appWindow.setMenuBarVisibility(false);

    // Initialize the DevTools.
    // appWindow.webContents.openDevTools()

    appWindow.on('closed', function () {
        appWindow = null;
    });
}

app.on('ready', () => {
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
