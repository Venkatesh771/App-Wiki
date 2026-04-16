/**
 * Dynamic Dropdown Manager
 * Handles adding new options to existing dropdowns
 */

class DynamicDropdown {
    constructor() {
        console.log('DynamicDropdown Manager initialized');
        this.createInputModal();
    }    /**
     * Create the input modal for adding new dropdown options
     */
    createInputModal() {
        // Check if modal already exists
        if (document.getElementById('inputModal')) {
            return;
        }

        // Create modal HTML - iPhone style with clean design
        const modalHTML = `
            <div id="inputModal" style="display: none !important; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); z-index: 10001; justify-content: center; align-items: center;">
                <div style="background: white; border-radius: 14px; width: 85%; max-width: 300px; box-shadow: 0 10px 35px rgba(0, 0, 0, 0.2); overflow: hidden;">
                    <div style="padding: 20px 20px 15px; border-bottom: 1px solid #f0f0f0;">
                        <h4 style="margin: 0; font-size: 0.95rem; color: #333; font-weight: 600;">Add New Option</h4>
                    </div>
                    <div style="padding: 15px 20px;">
                        <input 
                            type="text" 
                            id="inputModalValue" 
                            placeholder="Enter new option" 
                            style="width: 100%; padding: 8px 10px; border: 1px solid #d0d0d0; border-radius: 6px; font-size: 0.9rem; box-sizing: border-box; height: 36px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;"
                        />
                    </div>
                    <div style="padding: 0 20px 15px; display: flex; gap: 8px;">
                        <button id="inputModalCancel" style="flex: 1; padding: 8px; border: 1px solid #e0e0e0; background: white; color: #666; border-radius: 6px; font-size: 0.9rem; font-weight: 600; cursor: pointer; height: 36px; transition: background 0.2s;">Cancel</button>
                        <button id="inputModalAdd" style="flex: 1; padding: 8px; background: #007AFF; color: white; border: none; border-radius: 6px; font-size: 0.9rem; font-weight: 600; cursor: pointer; height: 36px; transition: background 0.2s;">Add</button>
                    </div>
                </div>
            </div>
            
            <style>
                #inputModal[style*="display: flex"] {
                    display: flex !important;
                }
                
                #inputModalCancel:hover {
                    background: #f5f5f5;
                }
                
                #inputModalCancel:active {
                    background: #e8e8e8;
                }
                
                #inputModalAdd:hover {
                    background: #0051D5;
                }
                
                #inputModalAdd:active {
                    background: #003da3;
                    transform: scale(0.98);
                }
            </style>
        `;

        // Insert modal into body
        document.body.insertAdjacentHTML('beforeend', modalHTML);

        // Attach event listeners
        this.attachModalListeners();
    }

    /**
     * Attach event listeners to modal buttons
     */
    attachModalListeners() {
        const cancelBtn = document.getElementById('inputModalCancel');
        const addBtn = document.getElementById('inputModalAdd');
        const inputField = document.getElementById('inputModalValue');
        const modal = document.getElementById('inputModal');

        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => this.closeInput());
        }

        if (addBtn) {
            addBtn.addEventListener('click', () => this.addOption());
        }

        if (inputField) {
            inputField.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.addOption();
                }
            });

            // Close on Escape key
            inputField.addEventListener('keydown', (e) => {
                if (e.key === 'Escape') {
                    this.closeInput();
                }
            });
        }

        // Close when clicking outside the modal
        if (modal) {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.closeInput();
                }
            });
        }
    }

    /**
     * Open input modal to add new dropdown option
     * Called when user clicks "+" button next to dropdown
     */
    openInput(button) {
        console.log('Opening input modal...');

        // Find the associated dropdown
        const dropdown = button.closest('.field-input')?.querySelector('select');
        if (!dropdown) {
            console.error('Could not find associated dropdown');
            return;
        }

        // Store reference to current dropdown
        this.currentDropdown = dropdown;

        // Show modal
        const modal = document.getElementById('inputModal');
        const inputField = document.getElementById('inputModalValue');

        if (modal && inputField) {
            modal.style.display = 'flex';
            inputField.value = '';
            inputField.focus();
        }
    }

    /**
     * Add the new option to dropdown
     */
    addOption() {
        const inputField = document.getElementById('inputModalValue');
        const newValue = (inputField?.value || '').trim();        if (!newValue) {
            // Show validation popup if empty
            if (window.ValidationManager) {
                window.ValidationManager.showModal('Please enter an option');
            } else {
                alert('Please enter an option');
            }
            return;
        }

        // Get current dropdown
        const dropdown = this.currentDropdown;
        if (!dropdown) {
            console.error('No dropdown selected');
            return;
        }

        // Check if option already exists
        const exists = Array.from(dropdown.options).some(opt => 
            opt.value.toLowerCase() === newValue.toLowerCase()
        );        if (exists) {
            if (window.ValidationManager) {
                window.ValidationManager.showModal('This option already exists');
            } else {
                alert('This option already exists');
            }
            return;
        }

        // Add new option to dropdown
        const newOption = document.createElement('option');
        newOption.value = newValue;
        newOption.textContent = newValue;
        dropdown.appendChild(newOption);

        // Select the new option
        dropdown.value = newValue;

        console.log('Option added successfully:', newValue);

        // Close modal
        this.closeInput();        // Show success message
        if (window.ValidationManager) {
            window.ValidationManager.showModal(`"${newValue}" added successfully!`);
        }
    }

    /**
     * Close the input modal
     */
    closeInput() {
        const modal = document.getElementById('inputModal');
        if (modal) {
            modal.style.display = 'none';
        }
        this.currentDropdown = null;
    }
}

// Initialize globally
window.DynamicDropdown = new DynamicDropdown();
console.log('Dynamic Dropdown Manager loaded');
