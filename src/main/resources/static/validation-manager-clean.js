/**
 * App Dictionary - Validation Manager - SIMPLE iPhone STYLE
 * Clean black & white iOS-style popups with NO ICONS
 * VERSION: 3.0 - SIMPLE & CLEAN
 * CACHE BUSTER: v3.0.0
 */

class ValidationManager {
    constructor() {
        console.log('Initializing Simple Validation Manager v3.0...');
        this.VERSION = '3.0.0';
        this.isSubmitting = false;
        this.LOADING_DELAY_MIN = 3000;
        this.LOADING_DELAY_MAX = 4000;
        this.createModalElements();
        console.log('Simple Validation Manager v' + this.VERSION + ' Ready!');
    }

    /**
     * Create simple iPhone-style modal elements - BLACK & WHITE, NO ICONS
     */
    createModalElements() {
        // Remove old modals if they exist
        document.getElementById('validationModal')?.remove();
        document.getElementById('loadingModal')?.remove();
        document.getElementById('successModal')?.remove();
          // Simple Validation Modal - BLACK & WHITE ONLY
        const modalHTML = `
            <div id="validationModal" style="display: none !important; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 10000; justify-content: center; align-items: center; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
                <div style="background: #FFFFFF; border-radius: 16px; width: 88%; max-width: 320px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2); text-align: center; padding: 0; overflow: hidden;">
                    <div style="padding: 24px 20px;">
                        <p id="modalMessage" style="margin: 0; font-size: 17px; color: #000000; line-height: 1.6; font-weight: 500;"></p>
                    </div>
                    <div style="padding: 16px 20px 20px; border-top: 1px solid #E8E8E8;">
                        <button id="modalOkButton" style="background: #000000; color: #FFFFFF; border: none; padding: 10px 24px; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; width: 100%;">OK</button>
                    </div>
                </div>
            </div>
        `;

        // Simple Loading Modal - BLACK & WHITE ONLY
        const loadingHTML = `
            <div id="loadingModal" style="display: none !important; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 10000; justify-content: center; align-items: center; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
                <div style="background: #FFFFFF; border-radius: 16px; width: 88%; max-width: 300px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2); text-align: center; padding: 32px 20px;">
                    <div id="loadingSpinner" style="width: 48px; height: 48px; margin: 0 auto 16px; border: 4px solid #E8E8E8; border-top-color: #000000; border-radius: 50%; animation: spin 1s linear infinite;"></div>
                    <p id="loadingMessage" style="margin: 0; font-size: 16px; color: #000000; font-weight: 500;">Saving...</p>
                </div>
            </div>
        `;

        // Simple Success Modal - BLACK & WHITE ONLY
        const successHTML = `
            <div id="successModal" style="display: none !important; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 10000; justify-content: center; align-items: center; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
                <div style="background: #FFFFFF; border-radius: 16px; width: 88%; max-width: 320px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2); text-align: center; padding: 0; overflow: hidden;">
                    <div style="padding: 24px 20px;">
                        <p id="successMessage" style="margin: 0; font-size: 17px; color: #000000; line-height: 1.6; font-weight: 500;"></p>
                    </div>
                    <div style="padding: 16px 20px 20px; border-top: 1px solid #E8E8E8;">
                        <button id="successOkButton" style="background: #000000; color: #FFFFFF; border: none; padding: 10px 24px; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; width: 100%;">OK</button>
                    </div>
                </div>
            </div>
        `;        
        
        // Add CSS animations
        const cssHTML = `
            <style>
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
                
                /* Show modals when needed */
                #validationModal[style*="display: flex"],
                #loadingModal[style*="display: flex"],
                #successModal[style*="display: flex"] {
                    display: flex !important;
                }
            </style>
        `;

        document.head.insertAdjacentHTML('beforeend', cssHTML);
        document.body.insertAdjacentHTML('beforeend', modalHTML);
        document.body.insertAdjacentHTML('beforeend', loadingHTML);
        document.body.insertAdjacentHTML('beforeend', successHTML);

        // Attach button listeners
        const okButton = document.getElementById('modalOkButton');
        if (okButton) {
            okButton.addEventListener('click', () => this.closeModal());
        }
        
        const successOkButton = document.getElementById('successOkButton');
        if (successOkButton) {
            successOkButton.addEventListener('click', () => this.closeSuccessModal());
        }
    }    /**
     * Show modal popup - SIMPLE BLACK & WHITE (NO ICONS)
     */
    showModal(message) {
        const modal = document.getElementById('validationModal');
        const messageEl = document.getElementById('modalMessage');
        
        if (!modal) {
            console.error('Modal not found, recreating...');
            this.createModalElements();
            return this.showModal(message);
        }
        
        if (messageEl) messageEl.textContent = message;
        modal.style.display = 'flex';
        
        const okButton = document.getElementById('modalOkButton');
        if (okButton) okButton.focus();
    }

    /**
     * Close modal
     */
    closeModal() {
        const modal = document.getElementById('validationModal');
        if (modal) modal.style.display = 'none';
    }

    /**
     * Show success modal with checkmark icon
     */
    showSuccessModal(message = 'Data Saved Successfully!') {
        const modal = document.getElementById('successModal');
        const messageEl = document.getElementById('successMessage');
        
        if (!modal) {
            console.error('Success modal not found, recreating...');
            this.createModalElements();
            return this.showSuccessModal(message);
        }
        
        if (messageEl) messageEl.textContent = message;
        modal.style.display = 'flex';
        
        const okButton = document.getElementById('successOkButton');
        if (okButton) okButton.focus();
    }

    /**
     * Close success modal
     */
    closeSuccessModal() {
        const modal = document.getElementById('successModal');
        if (modal) modal.style.display = 'none';
    }    /**
     * Show loading modal - SIMPLE BLACK & WHITE (NO ICONS)
     */
    showLoadingModal(message = 'Saving...') {
        const modal = document.getElementById('loadingModal');
        const messageEl = document.getElementById('loadingMessage');
        
        if (!modal) {
            console.error('Loading modal not found, recreating...');
            this.createModalElements();
            return this.showLoadingModal(message);
        }
        
        if (messageEl) messageEl.textContent = message;
        modal.style.display = 'flex';
    }

    /**
     * Hide loading modal
     */
    hideLoadingModal() {
        const modal = document.getElementById('loadingModal');
        if (modal) modal.style.display = 'none';
    }

    /**
     * Get random loading delay (3-5 seconds)
     */
    getRandomLoadingDelay() {
        return this.LOADING_DELAY_MIN + Math.random() * (this.LOADING_DELAY_MAX - this.LOADING_DELAY_MIN);
    }    /**
     * Validate mandatory fields for main file page
     */
    validateMainFileFields() {
        console.log('Validating main file fields...');
        
        const beatIdField = document.querySelector('[data-field="beatId"]');
        const appNameField = document.querySelector('[data-field="applicationName"]');
        
        const beatId = beatIdField ? (beatIdField.value || '').trim() : '';
        const appName = appNameField ? (appNameField.value || '').trim() : '';
        
        if (!beatId) {
            this.showModal('Please enter BEAT ID');
            beatIdField?.focus();
            return false;
        }
        
        if (!appName) {
            this.showModal('Please enter Application Name');
            appNameField?.focus();
            return false;
        }
        
        return true;
    }

    /**
     * Clean field value (empty = null)
     */
    cleanFieldValue(value, fieldType = 'input') {
        if (fieldType === 'dropdown' || fieldType === 'select') {
            const val = (value || '').trim();
            if (!val || val === '' || val === 'null' || val.toLowerCase() === 'select') {
                return null;
            }
            return val;
        }
        
        const val = (value || '').trim();
        return val === '' ? null : val;
    }

    /**
     * Collect clean form data
     */
    collectCleanMainData() {
        console.log('Collecting clean main data...');
        const data = {};
        
        document.querySelectorAll('[data-field]').forEach(field => {
            const fieldName = field.getAttribute('data-field');
            let value = null;
            
            if (field.type === 'checkbox') {
                value = field.checked;
            } else if (field.tagName === 'SELECT') {
                value = this.cleanFieldValue(field.value, 'dropdown');
            } else {
                value = this.cleanFieldValue(field.value, 'input');
            }
            
            data[fieldName] = value;
        });
        
        return data;
    }    /**
     * Handle save and continue with loading and success flow
     */
    handleSaveAndContinue(pageType, nextPageUrl, validationFn = null) {
        if (validationFn && !validationFn()) {
            return;
        }
        
        if (this.isSubmitting) return;
        this.isSubmitting = true;
        
        this.showLoadingModal('Saving Data...');
        
        const delay = this.getRandomLoadingDelay();
        
        setTimeout(() => {
            this.hideLoadingModal();
            this.isSubmitting = false;
            this.showSuccessModal('Data saved successfully!');
            
            setTimeout(() => {
                this.closeSuccessModal();
                if (nextPageUrl) window.location.href = nextPageUrl;
            }, 800);
        }, delay);
    }    /**
     * Show loading popup with custom message
     */
    showLoadingPopup(message = 'Processing...') {
        this.showLoadingModal(message);
    }

    /**
     * Close loading popup
     */
    closeLoadingPopup() {
        this.hideLoadingModal();
    }

    /**
     * Show validation popup (error message)
     */
    showValidationPopup(message) {
        this.showModal(message);
    }

    /**
     * Close validation popup
     */
    closeValidationPopup() {
        this.closeModal();
    }
}

// Initialize globally
window.ValidationManager = new ValidationManager();
console.log('Validation Manager loaded - Simple Black & White Popups');
