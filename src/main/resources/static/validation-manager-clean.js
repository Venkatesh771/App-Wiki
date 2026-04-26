

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

    createModalElements() {

        document.getElementById('validationModal')?.remove();
        document.getElementById('loadingModal')?.remove();
        document.getElementById('successModal')?.remove();
        document.getElementById('vm-styles')?.remove();

        const cssHTML = `
            <style id="vm-styles">
                @keyframes vm-spin {
                    0%   { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
                @keyframes vm-pop {
                    0%   { transform: scale(0.82) translateY(18px); opacity: 0; }
                    100% { transform: scale(1) translateY(0); opacity: 1; }
                }
                @keyframes vm-success-pop {
                    0%   { transform: scale(0.78) translateY(24px); opacity: 0; }
                    65%  { transform: scale(1.04) translateY(-3px); opacity: 1; }
                    100% { transform: scale(1) translateY(0); opacity: 1; }
                }
                @keyframes vm-check-draw {
                    0%   { stroke-dashoffset: 60; }
                    100% { stroke-dashoffset: 0; }
                }
                @keyframes vm-circle-draw {
                    0%   { stroke-dashoffset: 200; opacity: 0; }
                    30%  { opacity: 1; }
                    100% { stroke-dashoffset: 0; }
                }
                @keyframes vm-ring-pulse {
                    0%   { box-shadow: 0 0 0 0 rgba(0,128,128,0.35); }
                    70%  { box-shadow: 0 0 0 14px rgba(0,128,128,0); }
                    100% { box-shadow: 0 0 0 0 rgba(0,128,128,0); }
                }
                .vm-overlay {
                    display: none;
                    position: fixed;
                    inset: 0;
                    background: rgba(0,0,0,0.48);
                    backdrop-filter: blur(4px);
                    -webkit-backdrop-filter: blur(4px);
                    z-index: 19999;
                    justify-content: center;
                    align-items: center;
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                }
                .vm-overlay.vm-open { display: flex !important; }
                .vm-card {
                    background: #fff;
                    border-radius: 20px;
                    width: 88%;
                    max-width: 320px;
                    box-shadow: 0 20px 60px rgba(0,0,0,0.22);
                    overflow: hidden;
                    animation: vm-pop 0.3s cubic-bezier(0.34,1.56,0.64,1);
                }
                .vm-card.vm-success-card {
                    animation: vm-success-pop 0.42s cubic-bezier(0.34,1.3,0.64,1);
                }
                .vm-body { padding: 30px 22px 18px; text-align: center; }
                .vm-icon {
                    width: 56px; height: 56px;
                    border-radius: 50%;
                    display: flex; align-items: center; justify-content: center;
                    margin: 0 auto 16px;
                    font-size: 1.4rem;
                }
                .vm-icon.load {
                    border: 4px solid #e0f0f0;
                    border-top-color: #008080;
                    animation: vm-spin 0.9s linear infinite;
                    background: transparent;
                }
                .vm-icon.success {
                    background: #e6f7f7;
                    animation: vm-ring-pulse 1s ease-out 0.3s 1;
                }
                .vm-icon.warn { background: #fff3e0; color: #e67e22; }
                .vm-check-svg { width: 30px; height: 30px; }
                .vm-check-circle {
                    fill: none; stroke: #008080; stroke-width: 3;
                    stroke-dasharray: 200;
                    stroke-dashoffset: 0;
                    animation: vm-circle-draw 0.5s ease-out 0.1s both;
                }
                .vm-check-tick {
                    fill: none; stroke: #008080; stroke-width: 3;
                    stroke-linecap: round; stroke-linejoin: round;
                    stroke-dasharray: 60; stroke-dashoffset: 0;
                    animation: vm-check-draw 0.35s ease-out 0.45s both;
                }
                .vm-title {
                    margin: 0 0 6px;
                    font-size: 16px;
                    color: #111;
                    font-weight: 700;
                    line-height: 1.3;
                }
                .vm-subtitle {
                    margin: 0;
                    font-size: 13.5px;
                    color: #666;
                    line-height: 1.55;
                    font-weight: 400;
                }
                .vm-msg {
                    margin: 0;
                    font-size: 15px;
                    color: #111;
                    line-height: 1.55;
                    font-weight: 500;
                }
                .vm-footer {
                    border-top: 1px solid #f0f0f0;
                    padding: 14px 22px 18px;
                }
                .vm-btn {
                    width: 100%;
                    background: #008080;
                    color: #fff;
                    border: none;
                    border-radius: 12px;
                    padding: 11px;
                    font-size: 15px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s, transform 0.15s;
                }
                .vm-btn:hover { background: #006666; transform: scale(1.02); }
                .vm-btn:active { transform: scale(0.98); }
            </style>`;

        const modalHTML = `
            <div id="validationModal" class="vm-overlay">
                <div class="vm-card">
                    <div class="vm-body">
                        <div class="vm-icon warn">&#9888;</div>
                        <p id="modalMessage" class="vm-msg"></p>
                    </div>
                    <div class="vm-footer">
                        <button id="modalOkButton" class="vm-btn">OK</button>
                    </div>
                </div>
            </div>`;

        const loadingHTML = `
            <div id="loadingModal" class="vm-overlay">
                <div class="vm-card">
                    <div class="vm-body">
                        <div class="vm-icon load"></div>
                        <p id="loadingMessage" class="vm-msg">Saving...</p>
                    </div>
                </div>
            </div>`;

        const successHTML = `
            <div id="successModal" class="vm-overlay">
                <div class="vm-card vm-success-card">
                    <div class="vm-body">
                        <div class="vm-icon success">
                            <svg class="vm-check-svg" viewBox="0 0 36 36">
                                <circle class="vm-check-circle" cx="18" cy="18" r="16"/>
                                <polyline class="vm-check-tick" points="10,18 15.5,24 26,12"/>
                            </svg>
                        </div>
                        <p id="successTitle" class="vm-title"></p>
                        <p id="successMessage" class="vm-subtitle"></p>
                    </div>
                    <div class="vm-footer">
                        <button id="successOkButton" class="vm-btn">OK, Continue</button>
                    </div>
                </div>
            </div>`;

        document.head.insertAdjacentHTML('beforeend', cssHTML);
        document.body.insertAdjacentHTML('beforeend', modalHTML);
        document.body.insertAdjacentHTML('beforeend', loadingHTML);
        document.body.insertAdjacentHTML('beforeend', successHTML);

        document.getElementById('modalOkButton')?.addEventListener('click', () => this.closeModal());
        document.getElementById('successOkButton')?.addEventListener('click', () => this.closeSuccessModal());
    }
    showModal(message) {
        const modal = document.getElementById('validationModal');
        const messageEl = document.getElementById('modalMessage');

        if (!modal) {
            console.error('Modal not found, recreating...');
            this.createModalElements();
            return this.showModal(message);
        }

        if (messageEl) messageEl.textContent = message;
        modal.classList.add('vm-open');
        document.getElementById('modalOkButton')?.focus();
    }

    closeModal() {
        document.getElementById('validationModal')?.classList.remove('vm-open');
    }

    showSuccessModal(title = 'Success!', subtitle = '', onOk = null) {
        const modal = document.getElementById('successModal');
        if (!modal) { this.createModalElements(); return this.showSuccessModal(title, subtitle, onOk); }

        const titleEl = document.getElementById('successTitle');
        const subtitleEl = document.getElementById('successMessage');
        if (titleEl) titleEl.textContent = title;
        if (subtitleEl) subtitleEl.textContent = subtitle;

        const card = modal.querySelector('.vm-card');
        if (card) { card.style.animation = 'none'; card.offsetHeight; card.style.animation = ''; }
        const circle = modal.querySelector('.vm-check-circle');
        const tick = modal.querySelector('.vm-check-tick');
        if (circle) { circle.style.animation = 'none'; circle.offsetHeight; circle.style.animation = ''; }
        if (tick) { tick.style.animation = 'none'; tick.offsetHeight; tick.style.animation = ''; }

        const okBtn = document.getElementById('successOkButton');
        if (okBtn) {
            const newBtn = okBtn.cloneNode(true);
            okBtn.parentNode.replaceChild(newBtn, okBtn);
            newBtn.addEventListener('click', () => {
                this.closeSuccessModal();
                if (typeof onOk === 'function') onOk();
            }, { once: true });
        }

        modal.classList.add('vm-open');
        document.getElementById('successOkButton')?.focus();
    }

    closeSuccessModal() {
        document.getElementById('successModal')?.classList.remove('vm-open');
    }

    showLoadingModal(message = 'Saving...') {
        const modal = document.getElementById('loadingModal');
        const messageEl = document.getElementById('loadingMessage');
        if (!modal) { this.createModalElements(); return this.showLoadingModal(message); }

        if (messageEl) messageEl.textContent = message;
        modal.classList.add('vm-open');
    }

    hideLoadingModal() {
        document.getElementById('loadingModal')?.classList.remove('vm-open');
    }

    getRandomLoadingDelay() {
        return this.LOADING_DELAY_MIN + Math.random() * (this.LOADING_DELAY_MAX - this.LOADING_DELAY_MIN);
    }
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
    }
    handleSaveAndContinue(_pageType, nextPageUrl, validationFn = null) {
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
    }
    showLoadingPopup(message = 'Processing...') {
        this.showLoadingModal(message);
    }

    closeLoadingPopup() {
        this.hideLoadingModal();
    }

    showValidationPopup(message) {
        this.showModal(message);
    }

    closeValidationPopup() {
        this.closeModal();
    }
}

window.ValidationManager = new ValidationManager();
console.log('Validation Manager loaded - Simple Black & White Popups');
