

class ValidationManager {
    constructor() {
        console.log('ValidationManager - Simple Popup initialized');
        this.isSubmitting = false;
        this.LOADING_DELAY_MIN = 3000;
        this.LOADING_DELAY_MAX = 5000;
        this.setupPopups();
    }

    setupPopups() {

        document.getElementById('validationPopup')?.remove();
        document.getElementById('loadingPopup')?.remove();

        const container = document.createElement('div');
        container.innerHTML = `
            <!-- Validation Popup -->
            <div id="validationPopup" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 99999;">
                <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; border-radius: 12px; width: 85%; max-width: 300px; padding: 24px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.25);">
                    <p id="validationMessage" style="margin: 0 0 20px 0; font-size: 15px; color: #000; line-height: 1.5; text-align: center; font-weight: 500;"></p>
                    <button id="validationOkBtn" style="width: 100%; background: #007AFF; color: white; border: none; padding: 12px; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer;">OK</button>
                </div>
            </div>

            <!-- Loading Popup -->
            <div id="loadingPopup" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 99999;">
                <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: white; border-radius: 12px; width: 85%; max-width: 300px; padding: 30px 24px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.25);">
                    <div id="loadingSpinner" style="width: 50px; height: 50px; border: 4px solid #f0f0f0; border-top: 4px solid #007AFF; border-radius: 50%; margin: 0 auto 20px; animation: spin 1s linear infinite;"></div>
                    <p id="loadingText" style="margin: 0; font-size: 15px; color: #000; text-align: center; font-weight: 500;">Saving Data...</p>
                </div>
            </div>

            <style>
                @keyframes spin {
                    to { transform: rotate(360deg); }
                }
            </style>
        `;

        document.body.appendChild(container);

        const okBtn = document.getElementById('validationOkBtn');
        if (okBtn) {
            okBtn.addEventListener('click', () => this.closeValidationPopup());
        }
    }

    showValidationPopup(message) {
        console.log('Showing validation popup:', message);
        const popup = document.getElementById('validationPopup');
        const msgEl = document.getElementById('validationMessage');

        if (msgEl) msgEl.textContent = message;
        if (popup) popup.style.display = 'block';

        const okBtn = document.getElementById('validationOkBtn');
        if (okBtn) okBtn.focus();
    }

    closeValidationPopup() {
        const popup = document.getElementById('validationPopup');
        if (popup) popup.style.display = 'none';
    }

    showLoadingPopup(message = 'Saving Data...') {
        console.log('Showing loading popup:', message);
        const popup = document.getElementById('loadingPopup');
        const msgEl = document.getElementById('loadingText');

        if (msgEl) msgEl.textContent = message;
        if (popup) popup.style.display = 'block';
    }

    closeLoadingPopup() {
        const popup = document.getElementById('loadingPopup');
        if (popup) popup.style.display = 'none';
    }

    getRandomDelay() {
        return this.LOADING_DELAY_MIN + Math.random() * (this.LOADING_DELAY_MAX - this.LOADING_DELAY_MIN);
    }

    validateMainFileFields() {
        console.log('Validating main file fields...');

        const beatIdInput = document.querySelector('[data-field="beatId"]');
        const appNameInput = document.querySelector('[data-field="applicationName"]');

        const beatId = (beatIdInput?.value || '').trim();
        const appName = (appNameInput?.value || '').trim();

        if (!beatId) {
            this.showValidationPopup('Please enter BEAT ID');
            beatIdInput?.focus();
            return false;
        }

        if (!appName) {
            this.showValidationPopup('Please enter Application Name');
            appNameInput?.focus();
            return false;
        }

        return true;
    }

    cleanFieldValue(value, fieldType = 'text') {
        const cleanVal = (value || '').trim();

        if (fieldType === 'dropdown' || fieldType === 'select') {

            if (!cleanVal || cleanVal.toLowerCase() === 'select') {
                return null;
            }
            return cleanVal;
        }

        return cleanVal.length > 0 ? cleanVal : null;
    }

    collectCleanFormData() {
        console.log('Collecting clean form data...');
        const data = {};

        document.querySelectorAll('[data-field]').forEach(field => {
            const fieldName = field.getAttribute('data-field');
            let value = null;

            if (field.type === 'checkbox') {
                value = field.checked;
            } else if (field.tagName === 'SELECT') {
                value = this.cleanFieldValue(field.value, 'dropdown');
            } else {
                value = this.cleanFieldValue(field.value, 'text');
            }

            data[fieldName] = value;
        });

        console.log('Clean form data collected:', data);
        return data;
    }

    handleSaveAndContinue(pageType, nextPageUrl, validationFunc = null) {
        console.log('Handle save and continue for:', pageType);

        if (validationFunc && !validationFunc()) {
            return;
        }

        if (this.isSubmitting) {
            return;
        }

        this.isSubmitting = true;

        this.showLoadingPopup('Saving Data...');

        const delay = this.getRandomDelay();
        console.log(`Loading popup will show for ${(delay / 1000).toFixed(1)} seconds`);

        setTimeout(() => {
            this.closeLoadingPopup();
            this.isSubmitting = false;

            this.showValidationPopup('Data saved successfully!');

            setTimeout(() => {
                this.closeValidationPopup();
                if (nextPageUrl) {
                    window.location.href = nextPageUrl;
                }
            }, 800);
        }, delay);
    }
}

window.ValidationManager = new ValidationManager();
console.log('ValidationManager initialized - Simple iPhone Popup Ready');
