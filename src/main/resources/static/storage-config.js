/**
 * Storage Manager Configuration
 * 
 * Customize auto-reset behavior here
 */

const STORAGE_CONFIG = {
    // Enable or disable auto-reset feature
    AUTO_RESET_ENABLED: true,
    
    // Time in minutes before auto-resetting stored data
    // Options: 5, 10, 15, 30, 60, 120
    AUTO_RESET_TIMEOUT_MINUTES: 30,
    
    // Show console logs (helpful for debugging)
    DEBUG_MODE: false,
    
    // Show user notification when data is reset
    SHOW_RESET_NOTIFICATION: true,
    
    // Custom notification message (leave blank for default)
    CUSTOM_RESET_MESSAGE: '',
    
    // Auto-clear on browser close (additional security)
    CLEAR_ON_BROWSER_CLOSE: false
};

/**
 * Apply custom configuration to Storage Manager
 * Call this after the page loads but before initializing pages
 */
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

// Example: Apply configuration when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    applyStorageConfig();
});
