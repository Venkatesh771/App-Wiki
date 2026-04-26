

class DynamicDropdown {
    constructor() {
        console.log('DynamicDropdown Manager initialized');
        this.createInputModal();
        this._loadAllCustomOptions();
    }

    createInputModal() {
        if (document.getElementById('inputModal')) {
            this.attachModalListeners();
            return;
        }

        if (!document.getElementById('dd-modal-styles')) {
            document.head.insertAdjacentHTML('beforeend', `
                <style id="dd-modal-styles">
                    .input-modal-overlay {
                        display: none;
                        position: fixed;
                        top: 0; left: 0; right: 0; bottom: 0;
                        background: rgba(0,0,0,0.3);
                        z-index: 10000;
                        align-items: center;
                        justify-content: center;
                    }
                    .input-modal-overlay.active { display: flex; }
                    .input-modal-content {
                        background: white;
                        border-radius: 16px;
                        padding: 0;
                        box-shadow: 0 15px 50px rgba(0,0,0,0.15);
                        max-width: 320px;
                        width: 90%;
                        overflow: hidden;
                    }
                    .input-modal-header {
                        padding: 20px 20px 0;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }
                    .input-modal-header h4 {
                        margin: 0;
                        color: #000;
                        font-weight: 600;
                        font-size: 1rem;
                    }
                    .input-modal-close {
                        background: none;
                        border: none;
                        font-size: 1.4rem;
                        color: #999;
                        cursor: pointer;
                        padding: 0;
                        width: 30px; height: 30px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        line-height: 1;
                        transition: color 0.2s ease;
                    }
                    .input-modal-close:hover { color: #333; }
                    .input-modal-body { padding: 20px; }
                    .input-modal-body .dd-input {
                        width: 100%;
                        background-color: #f5f5f5;
                        border: 1px solid #e0e0e0;
                        border-radius: 8px;
                        padding: 8px 12px;
                        font-size: 0.9rem;
                        height: 36px;
                        box-sizing: border-box;
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        transition: border-color 0.2s, box-shadow 0.2s;
                        outline: none;
                    }
                    .input-modal-body .dd-input:focus {
                        background-color: #fff;
                        border-color: #1b98e0;
                        box-shadow: 0 0 0 3px rgba(27,152,224,0.1);
                    }
                    .input-modal-footer {
                        padding: 0 20px 20px;
                        display: flex;
                        gap: 10px;
                        justify-content: flex-end;
                        border: none;
                        border-top: none;
                    }
                    .input-modal-footer button {
                        padding: 8px 20px;
                        height: 36px;
                        border-radius: 8px;
                        border: none;
                        font-size: 0.9rem;
                        font-weight: 500;
                        cursor: pointer;
                        transition: background 0.2s ease;
                    }
                    .input-modal-footer .dd-btn-primary { background: #1b98e0; color: white; }
                    .input-modal-footer .dd-btn-primary:hover { background: #1585c5; }
                    .input-modal-footer .dd-btn-secondary { background: #f0f0f0; color: #333; }
                    .input-modal-footer .dd-btn-secondary:hover { background: #e0e0e0; }
                </style>`);
        }

        document.body.insertAdjacentHTML('beforeend', `
            <div id="inputModal" class="input-modal-overlay">
                <div class="input-modal-content">
                    <div class="input-modal-header">
                        <h4>Add New Option</h4>
                        <button class="input-modal-close" id="inputModalClose">&times;</button>
                    </div>
                    <div class="input-modal-body">
                        <input type="text" class="dd-input" id="inputModalValue"
                            placeholder="Enter new value..." autocomplete="off">
                    </div>
                    <div class="input-modal-footer">
                        <button class="dd-btn-secondary" id="inputModalCancel">Cancel</button>
                        <button class="dd-btn-primary" id="inputModalAdd">Add</button>
                    </div>
                </div>
            </div>`);

        this.attachModalListeners();
    }

    attachModalListeners() {
        const cancelBtn = document.getElementById('inputModalCancel');
        const addBtn = document.getElementById('inputModalAdd');
        const closeBtn = document.getElementById('inputModalClose');
        const inputField = document.getElementById('inputModalValue') || document.getElementById('inputModalInput');
        const modal = document.getElementById('inputModal');

        cancelBtn?.addEventListener('click', () => this.closeInput());
        closeBtn?.addEventListener('click', () => this.closeInput());
        addBtn?.addEventListener('click', () => this.addOption());

        if (inputField) {
            inputField.addEventListener('keypress', (e) => { if (e.key === 'Enter') this.addOption(); });
            inputField.addEventListener('keydown', (e) => { if (e.key === 'Escape') this.closeInput(); });
        }

        modal?.addEventListener('click', (e) => { if (e.target === modal) this.closeInput(); });
    }

    openInput(button) {
        const dropdown = button.closest('.field-input')?.querySelector('select');
        if (!dropdown) { console.error('Could not find associated dropdown'); return; }

        this.currentDropdown = dropdown;

        const modal = document.getElementById('inputModal');
        const inputField = document.getElementById('inputModalValue') || document.getElementById('inputModalInput');
        if (modal) {
            modal.classList.add('active');
            if (inputField) { inputField.value = ''; inputField.focus(); }
        }
    }

    cancelInput() { this.closeInput(); }
    confirmInput() { this.addOption(); }

    _fieldNameFor(dropdown) {
        return dropdown.dataset.field || dropdown.id || null;
    }

    addOption() {
        const inputField = document.getElementById('inputModalValue') || document.getElementById('inputModalInput');
        const newValue = (inputField?.value || '').trim();

        if (!newValue) {
            this._showError('Please enter an option.');
            return;
        }

        const dropdown = this.currentDropdown;
        if (!dropdown) { console.error('No dropdown selected'); return; }

        const existsInDom = Array.from(dropdown.options).some(opt =>
            opt.value.toLowerCase() === newValue.toLowerCase()
        );
        if (existsInDom) {
            this._showError(`"${newValue}" already exists in this dropdown.`);
            return;
        }

        const fieldName = this._fieldNameFor(dropdown);

        if (fieldName) {

            fetch('/api/dropdown-options', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fieldName, value: newValue })
            })
                .then(r => {
                    if (r.status === 409) {
                        return r.json().then(body => {
                            this._showError(body.error || `"${newValue}" already exists in this dropdown.`);
                        });
                    }
                    if (!r.ok) {
                        this._showError('Failed to save option. Please try again.');
                        return;
                    }

                    this._injectOption(fieldName, newValue, dropdown);
                    this.closeInput();
                    if (window.ValidationManager) {
                        window.ValidationManager.showSuccessModal(`"${newValue}" added successfully!`);
                    }
                })
                .catch(err => {
                    console.error('Failed to persist dropdown option:', err);
                    this._showError('Failed to save option. Please try again.');
                });
        } else {

            this._injectOption(null, newValue, dropdown);
            this.closeInput();
            if (window.ValidationManager) {
                window.ValidationManager.showSuccessModal(`"${newValue}" added successfully!`);
            }
        }
    }

    _injectOption(fieldName, value, primaryDropdown) {
        const addTo = (sel) => {
            if (!Array.from(sel.options).some(o => o.value.toLowerCase() === value.toLowerCase())) {
                const opt = document.createElement('option');
                opt.value = value;
                opt.textContent = value;
                sel.appendChild(opt);
            }
        };
        addTo(primaryDropdown);
        primaryDropdown.value = value;

        if (fieldName) {
            document.querySelectorAll(`select[data-field="${fieldName}"], select#${fieldName}`)
                .forEach(sel => { if (sel !== primaryDropdown) addTo(sel); });
        }
    }

    _showError(msg) {
        if (window.ValidationManager) {
            window.ValidationManager.showModal(msg);
        } else {
            alert(msg);
        }
    }

    closeInput() {
        const modal = document.getElementById('inputModal');
        if (modal) modal.classList.remove('active');
        this.currentDropdown = null;
    }

    _loadAllCustomOptions() {
        const load = () => {
            const seen = new Set();
            const fetches = [];
            document.querySelectorAll('.btn-add-option').forEach(btn => {
                const select = btn.closest('.field-input')?.querySelector('select');
                if (!select) return;
                const fieldName = this._fieldNameFor(select);
                if (!fieldName || seen.has(fieldName)) return;
                seen.add(fieldName);

                const p = fetch(`/api/dropdown-options/${encodeURIComponent(fieldName)}`)
                    .then(r => r.json())
                    .then(values => {
                        values.forEach(val => {

                            document.querySelectorAll(`select[data-field="${fieldName}"], select#${fieldName}`)
                                .forEach(sel => {
                                    if (!Array.from(sel.options).some(o => o.value.toLowerCase() === val.toLowerCase())) {
                                        const opt = document.createElement('option');
                                        opt.value = val;
                                        opt.textContent = val;
                                        sel.appendChild(opt);
                                    }
                                });
                        });
                    })
                    .catch(err => console.error(`Failed to load options for ${fieldName}:`, err));
                fetches.push(p);
            });
            return Promise.all(fetches);
        };

        if (document.readyState === 'loading') {
            this._optionsLoaded = new Promise(resolve => {
                document.addEventListener('DOMContentLoaded', () => load().then(resolve));
            });
        } else {
            this._optionsLoaded = load();
        }
    }

    waitForOptionsLoaded() {
        return this._optionsLoaded || Promise.resolve();
    }
}

window.DynamicDropdown = new DynamicDropdown();
console.log('Dynamic Dropdown Manager loaded');
