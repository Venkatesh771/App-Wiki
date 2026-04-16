/**
 * App Dictionary - Storage Manager V4 - WITH VALIDATION
 * 
 * This version includes:
 * - Integrated validation with modal popups
 * - Loading animations during save
 * - Mandatory field validation (BEAT ID, Application Name)
 * - Clean data handling (null for empty fields)
 * - iPhone-style modal dialogs
 */

class AppDictionaryStorageManager {
    constructor() {
        console.log('Creating Storage Manager with Validation...');
        this.isRestoring = false;
        this.isSubmitting = false;
        this.STORAGE_KEYS = {
            mainDetails: 'app_mainDetails_form',
            appServer: 'app_appServer_form',
            cloudDetails: 'app_cloudDetails_form',
            dbServer: 'app_dbServer_form'
        };
        this.API_ENDPOINTS = {
            basicIdentity: '/api/basic-identity',
            descriptionImpact: '/api/description-impact',
            authenticationVendor: '/api/authentication-vendor',
            technicalDetails: '/api/technical-details',
            resourceContacts: '/api/resource-contacts',
            appServer: '/api/application-server-details',
            cloudDetails: '/api/cloud-details',
            dbServer: '/api/database-server-details'
        };
        console.log('Storage Manager Ready');
    }    // ===== PAGE 1: MAIN FILE =====
    initMainFilePage() {
        console.log('Initializing Main File Page');
        this.restoreMainFileData();
        // Auto-save removed - only save on explicit "Save & Continue" button click
        this.attachSaveAndContinueButton('main', '/applicationserverdetails');
    }

    collectMainFileData() {
        const data = {};
        document.querySelectorAll('[data-field]').forEach(field => {
            const fieldName = field.getAttribute('data-field');
            if (field.type === 'checkbox') {
                data[fieldName] = field.checked;
            } else {
                data[fieldName] = field.value || '';
            }
        });
        return data;
    }

    restoreMainFileData() {
        console.log('Restoring Main File data...');
        const savedData = this.getFromLocalStorage(this.STORAGE_KEYS.mainDetails);
        if (savedData && Object.keys(savedData).length > 0) {
            console.log('Found saved data, restoring...');
            Object.keys(savedData).forEach(fieldName => {
                const field = document.querySelector('[data-field="' + fieldName + '"]');
                if (field) {
                    if (field.type === 'checkbox') {
                        field.checked = savedData[fieldName];
                    } else {
                        field.value = savedData[fieldName] || '';
                    }
                }
            });
            console.log('Main File data restored');
        }
    }

    // ===== PAGE 2: APP SERVER DETAILS =====
    initAppServerPage() {
        console.log('Initializing App Server Page');
        this.restoreAppServerData();
        this.attachAutoSaveListeners('appServer');
        this.attachSaveAndContinueButton('appServer', '/clouddetails');
    } collectAppServerData() {
        const data = [];
        const sections = ['dev-section', 'test-section', 'qa-section', 'prod-section'];
        const fieldNames = ['environment', 'serverName', 'serverOsVersion', 'deployedServer', 'domain', 'cluster', 'serviceName', 'ipAddress'];

        sections.forEach(sectionId => {
            const section = document.getElementById(sectionId);
            if (section) {
                const tbody = section.querySelector('.row-container');
                if (tbody) {
                    tbody.querySelectorAll('tr').forEach(row => {
                        const rowData = {};
                        const fields = row.querySelectorAll('input, select, textarea');

                        // Map environment from section ID
                        if (sectionId === 'dev-section') rowData['environment'] = 'DEV';
                        else if (sectionId === 'test-section') rowData['environment'] = 'TEST';
                        else if (sectionId === 'qa-section') rowData['environment'] = 'QA';
                        else if (sectionId === 'prod-section') rowData['environment'] = 'PROD';

                        // Map fields
                        fields.forEach((field, idx) => {
                            if (idx < fieldNames.length - 1) { // Skip environment since we set it above
                                const fieldName = fieldNames[idx + 1]; // Offset by 1 because first is environment
                                if (field.type === 'checkbox') {
                                    rowData[fieldName] = field.checked;
                                } else {
                                    const value = field.value ? field.value.trim() : '';
                                    rowData[fieldName] = value || null;
                                }
                            }
                        });

                        // Only add if has data
                        const hasData = Object.values(rowData).some(v => v && v !== false && v !== null);
                        if (hasData) {
                            data.push(rowData);
                        }
                    });
                }
            }
        });
        return data;
    } restoreAppServerData() {
        console.log('Restoring App Server data...');
        this.isRestoring = true;
        const savedData = this.getFromLocalStorage(this.STORAGE_KEYS.appServer);

        if (savedData && Array.isArray(savedData) && savedData.length > 0) {
            console.log('Found saved data array, restoring table rows...');
            const envToSectionMap = { 'DEV': 'dev-section', 'TEST': 'test-section', 'QA': 'qa-section', 'PROD': 'prod-section' };
            const fieldNames = ['serverName', 'serverOsVersion', 'deployedServer', 'domain', 'cluster', 'serviceName', 'ipAddress'];

            const sections = ['dev-section', 'test-section', 'qa-section', 'prod-section'];
            sections.forEach(sectionId => {
                const section = document.getElementById(sectionId);
                if (section) {
                    const tbody = section.querySelector('.row-container');
                    if (tbody) {
                        const sectionRows = savedData.filter(r => {
                            const env = sectionId === 'dev-section' ? 'DEV' : sectionId === 'test-section' ? 'TEST' : sectionId === 'qa-section' ? 'QA' : 'PROD';
                            return r.environment === env;
                        });

                        if (sectionRows.length > 0) {
                            const templateRow = tbody.querySelector('tr');
                            tbody.innerHTML = '';
                            if (templateRow) tbody.appendChild(templateRow);

                            sectionRows.forEach((rowData, idx) => {
                                let targetRow;
                                if (idx === 0) {
                                    targetRow = tbody.querySelector('tr');
                                } else {
                                    targetRow = templateRow.cloneNode(true);
                                    tbody.appendChild(targetRow);
                                }

                                if (targetRow) {
                                    const fields = targetRow.querySelectorAll('input, select, textarea');
                                    fields.forEach((field, fieldIdx) => {
                                        if (fieldIdx < fieldNames.length) {
                                            const value = rowData[fieldNames[fieldIdx]];
                                            if (field.type === 'checkbox') {
                                                field.checked = value || false;
                                            } else {
                                                field.value = value || '';
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
            console.log('App Server data restored');
        }
        this.isRestoring = false;
    }

    // ===== PAGE 3: CLOUD DETAILS =====
    initCloudDetailsPage() {
        console.log('Initializing Cloud Details Page');
        this.restoreCloudDetailsData();
        this.attachAutoSaveListeners('cloud');
        this.attachSaveAndContinueButton('cloud', '/databaseserverdetails');
    } collectCloudDetailsData() {
        const data = [];
        const sections = ['non-prod', 'prod'];
        const fieldNames = ['environment', 'accountId', 'hostType', 'serviceName', 'lambdaNames', 's3Bucket', 'sqsNames', 'iamUser', 'comments'];

        sections.forEach(sectionId => {
            const section = document.getElementById(sectionId);
            if (section) {
                const tbody = section.querySelector('.row-container');
                if (tbody) {
                    tbody.querySelectorAll('tr').forEach(row => {
                        const rowData = {};
                        const fields = row.querySelectorAll('input, select, textarea');

                        // Map environment from section ID
                        rowData['environment'] = sectionId === 'non-prod' ? 'NON_PROD' : 'PROD';

                        // Map fields
                        fields.forEach((field, idx) => {
                            if (idx < fieldNames.length - 1) { // Skip environment since we set it above
                                const fieldName = fieldNames[idx + 1]; // Offset by 1 because first is environment
                                if (field.type === 'checkbox') {
                                    rowData[fieldName] = field.checked;
                                } else {
                                    const value = field.value ? field.value.trim() : '';
                                    rowData[fieldName] = value || null;
                                }
                            }
                        });

                        // Only add if has data
                        const hasData = Object.values(rowData).some(v => v && v !== false && v !== null);
                        if (hasData) {
                            data.push(rowData);
                        }
                    });
                }
            }
        });
        return data;
    } restoreCloudDetailsData() {
        console.log('Restoring Cloud Details data...');
        this.isRestoring = true;
        const savedData = this.getFromLocalStorage(this.STORAGE_KEYS.cloudDetails);

        if (savedData && Array.isArray(savedData) && savedData.length > 0) {
            console.log('Found saved data array, restoring table rows...');
            const fieldNames = ['accountId', 'hostType', 'serviceName', 'lambdaNames', 's3Bucket', 'sqsNames', 'iamUser', 'comments'];

            const sections = ['non-prod', 'prod'];
            sections.forEach(sectionId => {
                const section = document.getElementById(sectionId);
                if (section) {
                    const tbody = section.querySelector('.row-container');
                    if (tbody) {
                        const env = sectionId === 'non-prod' ? 'NON_PROD' : 'PROD';
                        const sectionRows = savedData.filter(r => r.environment === env);

                        if (sectionRows.length > 0) {
                            const templateRow = tbody.querySelector('tr');
                            while (tbody.rows.length > 1) tbody.deleteRow(1);

                            sectionRows.forEach((rowData, idx) => {
                                let targetRow;
                                if (idx === 0) {
                                    targetRow = tbody.querySelector('tr');
                                } else {
                                    targetRow = templateRow.cloneNode(true);
                                    tbody.appendChild(targetRow);
                                }

                                if (targetRow) {
                                    const fields = targetRow.querySelectorAll('input, select, textarea');
                                    fields.forEach((field, fieldIdx) => {
                                        if (fieldIdx < fieldNames.length) {
                                            const value = rowData[fieldNames[fieldIdx]];
                                            if (field.type === 'checkbox') {
                                                field.checked = value || false;
                                            } else {
                                                field.value = value || '';
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
            console.log('Cloud Details data restored');
        }
        this.isRestoring = false;
    }

    // ===== PAGE 4: DB SERVER DETAILS =====
    initDBServerPage() {
        console.log('Initializing DB Server Page (FINAL SAVE PAGE)');
        this.restoreDBServerData();
        this.attachAutoSaveListeners('dbServer');
        this.attachFinalSaveButton();
    } collectDBServerData() {
        const data = [];
        const sections = ['dev-section', 'test-section', 'qa-section', 'prod-section'];
        const fieldNames = ['environment', 'databaseType', 'databaseVersion', 'databaseHostingType', 'databaseName', 'accountName', 'hostName', 'serviceName', 'port', 'accountId', 'ip'];

        sections.forEach(sectionId => {
            const section = document.getElementById(sectionId);
            if (section) {
                const tbody = section.querySelector('.row-container');
                if (tbody) {
                    const allRows = tbody.querySelectorAll('tr');
                    console.log(`Collecting ${sectionId}: Found ${allRows.length} rows`);
                    allRows.forEach((row, rowIdx) => {
                        const rowData = {};
                        const fields = row.querySelectorAll('input, select, textarea');

                        // Map environment from section ID
                        if (sectionId === 'dev-section') rowData['environment'] = 'DEV';
                        else if (sectionId === 'test-section') rowData['environment'] = 'TEST';
                        else if (sectionId === 'qa-section') rowData['environment'] = 'QA';
                        else if (sectionId === 'prod-section') rowData['environment'] = 'PROD';

                        // Map fields
                        fields.forEach((field, idx) => {
                            if (idx < fieldNames.length - 1) { // Skip environment
                                const fieldName = fieldNames[idx + 1]; // Offset by 1
                                if (field.type === 'checkbox') {
                                    rowData[fieldName] = field.checked;
                                } else {
                                    const value = field.value ? field.value.trim() : '';
                                    rowData[fieldName] = value || null;
                                }
                            }
                        });

                        // Only add if has data
                        const hasData = Object.values(rowData).some(v => v && v !== false && v !== null);
                        if (hasData) {
                            console.log(`  Row ${rowIdx}: Added (has data)`);
                            data.push(rowData);
                        } else {
                            console.log(`  Row ${rowIdx}: Skipped (empty)`);
                        }
                    });
                }
                console.log(`${sectionId} FINAL COUNT: ${data.length} rows`);
            }
        });
        return data;
    } restoreDBServerData() {
        console.log('Restoring DB Server data...');
        this.isRestoring = true;
        const savedData = this.getFromLocalStorage(this.STORAGE_KEYS.dbServer);

        if (savedData && Array.isArray(savedData) && savedData.length > 0) {
            console.log('Found saved data array, restoring table rows...');
            const fieldNames = ['databaseType', 'databaseVersion', 'databaseHostingType', 'databaseName', 'accountName', 'hostName', 'serviceName', 'port', 'accountId', 'ip'];

            const sections = ['dev-section', 'test-section', 'qa-section', 'prod-section'];
            sections.forEach(sectionId => {
                const section = document.getElementById(sectionId);
                if (section) {
                    const tbody = section.querySelector('.row-container');
                    if (tbody) {
                        const env = sectionId === 'dev-section' ? 'DEV' : sectionId === 'test-section' ? 'TEST' : sectionId === 'qa-section' ? 'QA' : 'PROD';
                        const sectionRows = savedData.filter(r => r.environment === env);

                        if (sectionRows.length > 0) {
                            const templateRow = tbody.querySelector('tr');
                            while (tbody.rows.length > 1) tbody.deleteRow(1);

                            sectionRows.forEach((rowData, idx) => {
                                let targetRow;
                                if (idx === 0) {
                                    targetRow = tbody.querySelector('tr');
                                } else {
                                    targetRow = templateRow.cloneNode(true);
                                    tbody.appendChild(targetRow);
                                }

                                if (targetRow) {
                                    const fields = targetRow.querySelectorAll('input, select, textarea');
                                    fields.forEach((field, fieldIdx) => {
                                        if (fieldIdx < fieldNames.length) {
                                            const value = rowData[fieldNames[fieldIdx]];
                                            if (field.type === 'checkbox') {
                                                field.checked = value || false;
                                            } else {
                                                field.value = value || '';
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
            console.log('DB Server data restored');
        }
        this.isRestoring = false;
    }

    // ===== AUTO-SAVE MECHANISM =====
    attachAutoSaveListeners(pageType) {
        console.log('Attaching auto-save listeners for ' + pageType);
        const storageKey = this.STORAGE_KEYS[pageType === 'main' ? 'mainDetails' : pageType === 'appServer' ? 'appServer' : pageType === 'cloud' ? 'cloudDetails' : 'dbServer'];
        const collectFn = pageType === 'main' ? this.collectMainFileData.bind(this) : pageType === 'appServer' ? this.collectAppServerData.bind(this) : pageType === 'cloud' ? this.collectCloudDetailsData.bind(this) : this.collectDBServerData.bind(this);
        const self = this;

        document.addEventListener('input', function (e) {
            if (self.isRestoring) return;
            if ((pageType === 'main' && e.target.hasAttribute('data-field')) || (pageType !== 'main' && e.target.closest('table'))) {
                const data = collectFn();
                self.saveToLocalStorage(storageKey, data);
            }
        });
        document.addEventListener('change', function (e) {
            if (self.isRestoring) return;
            if ((pageType === 'main' && e.target.hasAttribute('data-field')) || (pageType !== 'main' && e.target.closest('table'))) {
                const data = collectFn();
                self.saveToLocalStorage(storageKey, data);
            }
        }); console.log('Auto-save listeners attached for ' + pageType);
    }

    // ===== VALIDATION =====
    validateMainFilePageFields() {
        console.log('Validating main file fields...');

        const beatIdField = document.querySelector('[data-field="beatId"]');
        const appNameField = document.querySelector('[data-field="applicationName"]');
        const beatId = beatIdField ? (beatIdField.value || '').trim() : '';
        const appName = appNameField ? (appNameField.value || '').trim() : '';
        // Check BEAT ID - MANDATORY
        if (!beatId) {
            if (window.ValidationManager) {
                window.ValidationManager.showModal('Please enter BEAT ID');
            }
            beatIdField?.focus();
            return false;
        }

        // Check Application Name - MANDATORY
        if (!appName) {
            if (window.ValidationManager) {
                window.ValidationManager.showModal('Please enter Application Name');
            }
            appNameField?.focus();
            return false;
        }

        console.log('Main file validation passed');
        return true;
    }

    // ===== BUTTON HANDLERS =====
    attachSaveAndContinueButton(pageType, nextPageUrl) {
        console.log('Attaching Save and Continue button for pageType: ' + pageType);
        // Use more specific selectors for different pages
        let saveBtn;
        if (pageType === 'main') {
            saveBtn = document.querySelector('.btn-save');
        } else if (pageType === 'appServer') {
            saveBtn = document.querySelector('.btn-save-app-server');
        } else if (pageType === 'cloud') {
            saveBtn = document.querySelector('.btn-save-cloud');
        } else if (pageType === 'dbServer') {
            saveBtn = document.querySelector('.btn-save-db');
        }

        if (saveBtn) {
            const self = this;
            saveBtn.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('=== SAVE AND CONTINUE - ' + pageType.toUpperCase() + ' ===');

                // Validate mandatory fields for main page
                if (pageType === 'main' && !self.validateMainFilePageFields()) {
                    return;
                }                // Show loading modal
                if (window.ValidationManager) {
                    window.ValidationManager.showLoadingModal('Saving Data...');
                }

                const storageKey = self.STORAGE_KEYS[pageType === 'main' ? 'mainDetails' : pageType === 'appServer' ? 'appServer' : pageType === 'cloud' ? 'cloudDetails' : 'dbServer'];
                const collectFn = pageType === 'main' ? self.collectMainFileData.bind(self) : pageType === 'appServer' ? self.collectAppServerData.bind(self) : pageType === 'cloud' ? self.collectCloudDetailsData.bind(self) : self.collectDBServerData.bind(self);
                const data = collectFn();
                self.saveToLocalStorage(storageKey, data);
                // Use validation manager's random delay (3-5 seconds)
                const delay = window.ValidationManager ? window.ValidationManager.getRandomLoadingDelay() : (3000 + Math.random() * 2000);
                console.log(`Loading modal will display for ${(delay / 1000).toFixed(1)} seconds`);

                setTimeout(function () {
                    if (window.ValidationManager) {
                        window.ValidationManager.hideLoadingModal();
                        window.ValidationManager.showSuccessModal('Data saved successfully!');
                        setTimeout(() => {
                            window.ValidationManager.closeSuccessModal();
                            if (nextPageUrl) window.location.href = nextPageUrl;
                        }, 800);
                    } else {
                        window.location.href = nextPageUrl;
                    }
                }, delay);
            });
        }
    }

    attachFinalSaveButton() {
        console.log('Attaching FINAL SAVE button');
        // Only target the final save button on database server details page
        const finalSaveBtn = document.querySelector('.btn-save-db');
        if (finalSaveBtn) {
            const self = this;
            finalSaveBtn.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('=== FINAL SAVE BUTTON CLICKED ===');
                self.sendAllDataToBackend();
            });
        }
    }

    // ===== BACKEND SYNC =====
    sendAllDataToBackend() {
        if (this.isSubmitting) return;
        this.isSubmitting = true;

        const self = this;
        console.log('=== FINAL SAVE: COLLECTING ALL DATA ===');
        const mainDetails = this.getFromLocalStorage(this.STORAGE_KEYS.mainDetails) || {};
        const appServerArray = this.collectAppServerData() || [];
        const cloudDetailsArray = this.collectCloudDetailsData() || [];
        const dbServerArray = this.collectDBServerData() || [];

        console.log('=== FINAL DATA FOR SUBMISSION ===');
        console.log('Main Details:', mainDetails);
        console.log('App Server Rows:', appServerArray);
        console.log('Cloud Details Rows:', cloudDetailsArray);
        console.log('DB Server Rows:', dbServerArray);

        // Show loading popup
        if (window.ValidationManager) {
            window.ValidationManager.showLoadingModal('Submitting all data...');
        }

        const finalSaveBtn = document.querySelector('.btn-save, .btn-save-db, button[type="submit"], .save-btn');
        if (finalSaveBtn) {
            finalSaveBtn.disabled = true;
            finalSaveBtn.textContent = 'Saving...';
        }

        // Collect all promises
        const promises = [];

        // SEND DATA TO BACKEND - Main Details
        if (mainDetails && Object.keys(mainDetails).length > 0) {
            console.log('Sending main details...');
            promises.push(this.sendToBackend('/api/basic-identity', mainDetails));
        }

        // SEND DATA TO BACKEND - App Server Details (multiple rows)
        if (appServerArray && appServerArray.length > 0) {
            console.log(`Sending ${appServerArray.length} app server rows...`);
            appServerArray.forEach(row => {
                promises.push(this.sendToBackend('/api/application-server-details', row));
            });
        }

        // SEND DATA TO BACKEND - Cloud Details (multiple rows)
        if (cloudDetailsArray && cloudDetailsArray.length > 0) {
            console.log(`Sending ${cloudDetailsArray.length} cloud detail rows...`);
            cloudDetailsArray.forEach(row => {
                promises.push(this.sendToBackend('/api/cloud-details', row));
            });
        }

        // SEND DATA TO BACKEND - DB Server Details (multiple rows)
        if (dbServerArray && dbServerArray.length > 0) {
            console.log(`Sending ${dbServerArray.length} database server rows...`);
            dbServerArray.forEach(row => {
                promises.push(this.sendToBackend('/api/database-server-details', row));
            });
        }

        // If no data at all, show error
        if (promises.length === 0) {
            console.warn('No data to submit!');
            if (window.ValidationManager) {
                window.ValidationManager.hideLoadingModal();
                window.ValidationManager.showModal('No data to submit. Please fill in at least one form.');
            }
            self.isSubmitting = false;
            if (finalSaveBtn) {
                finalSaveBtn.disabled = false;
                finalSaveBtn.textContent = 'Save & Finish';
            }
            return;
        }

        // Wait for all API calls to complete
        Promise.all(promises)
            .then(() => {
                console.log('All data successfully sent to backend');
                if (window.ValidationManager) {
                    window.ValidationManager.hideLoadingModal();
                    window.ValidationManager.showSuccessModal('All data saved successfully!');
                    setTimeout(() => {
                        window.ValidationManager.closeSuccessModal();
                        // Clear storage AFTER successful submission
                        self.clearAllStorage();
                        self.isSubmitting = false;
                        window.location.href = '/home';
                    }, 800);
                }

                if (finalSaveBtn) {
                    finalSaveBtn.disabled = false;
                    finalSaveBtn.textContent = 'Save & Finish';
                }
            })
            .catch(error => {
                console.error('Error sending data to backend:', error);
                if (window.ValidationManager) {
                    window.ValidationManager.hideLoadingModal();
                    window.ValidationManager.showModal('Error saving data to backend. Please try again.');
                }

                self.isSubmitting = false;
                if (finalSaveBtn) {
                    finalSaveBtn.disabled = false;
                    finalSaveBtn.textContent = 'Save & Finish';
                }
            });
    }

    // Helper method to send data to backend
    sendToBackend(endpoint, data) {
        console.log(`Sending data to ${endpoint}:`, data);
        return fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            }).then(data => {
                console.log(`Successfully saved to ${endpoint}:`, data);
                return data;
            });
    }

    // ===== STORAGE METHODS =====
    saveToLocalStorage(key, data) {
        try {
            localStorage.setItem(key, JSON.stringify({ data: data, savedAt: new Date().toISOString(), version: 1 }));
            return true;
        } catch (error) {
            console.error('Error saving:', error);
            return false;
        }
    }

    getFromLocalStorage(key) {
        try {
            const raw = localStorage.getItem(key);
            if (!raw) return null;
            return JSON.parse(raw).data;
        } catch (error) {
            console.error('Error reading:', error);
            return null;
        }
    }

    clearAllStorage() {
        try {
            Object.values(this.STORAGE_KEYS).forEach(key => localStorage.removeItem(key));
        } catch (error) {
            console.error('Error clearing:', error);
        }
    }
}

window.StorageManager = new AppDictionaryStorageManager();
console.log('Storage Manager with Validation initialized');
