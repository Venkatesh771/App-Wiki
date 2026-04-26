

const STORAGE_CONFIG = {

    AUTO_RESET_ENABLED: true,

    AUTO_RESET_TIMEOUT_MINUTES: 30,

    DEBUG_MODE: false,

    SHOW_RESET_NOTIFICATION: true,

    CUSTOM_RESET_MESSAGE: '',

    CLEAR_ON_BROWSER_CLOSE: false
};

function applyStorageConfig() {
    if (!window.StorageManager) {
        console.warn('StorageManager not loaded yet');
        return;
    }

    window.StorageManager.AUTO_RESET_ENABLED = STORAGE_CONFIG.AUTO_RESET_ENABLED;
    window.StorageManager.AUTO_RESET_TIMEOUT_MINUTES = STORAGE_CONFIG.AUTO_RESET_TIMEOUT_MINUTES;
    window.StorageManager.AUTO_RESET_TIMEOUT_MS = STORAGE_CONFIG.AUTO_RESET_TIMEOUT_MINUTES * 60 * 1000;
    window.StorageManager.DEBUG_MODE = STORAGE_CONFIG.DEBUG_MODE;

    if (STORAGE_CONFIG.DEBUG_MODE) {
        console.log('Storage Config Applied:', STORAGE_CONFIG);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    applyStorageConfig();
});
