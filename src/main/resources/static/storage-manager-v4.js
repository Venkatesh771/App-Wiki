/**
 * App Dictionary - Storage Manager V4 - NAMED FIELD RESTORATION
 * 
 * This version includes:
 * - Named field collection and restoration (deployedServer, serverName, etc.)
 * - Proper restoration logic that clears old rows before restoring
 * - Auto-save prevention during restoration (isRestoring flag)
 * - Backend submission with basicIdentityId and beatId
 */

class AppDictionaryStorageManager {
    constructor() {
        console.log('Creating Storage Manager...');
        this.isRestoring = false;
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
    }

    // ===== PAGE 1: MAIN FILE =====
    initMainFilePage() {
        console.log('Initializing Main File Page');
        this.restoreMainFileData();
        this.attachAutoSaveListeners('main');
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
                        field.value = savedData[fieldName];
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
    }

    // Field name mapping for AppServer fields
    appServerFieldMap = {
        'deployed-server-field': 'deployedServer',
        'server-name-field': 'serverName',
        'server-os-field': 'serverOs',
        'domain-field': 'domain',
        'cluster-field': 'cluster',
        'service-name-field': 'serviceName',
        'ip-address-field': 'ipAddress'
    };

    // Field name mapping for CloudDetails fields
    cloudDetailsFieldMap = {
        'account-id-field': 'accountId',
        'host-type-field': 'hostType',
        'service-name-field': 'serviceName',
        'lambda-field': 'lambdaNames',
        's3-bucket-field': 's3Bucket',
        'sqs-field': 'sqsNames',
        'iam-user-field': 'iamUser',
        'comments-field': 'comments'
    };

    // Field name mapping for DatabaseServer fields
    dbServerFieldMap = {
        'db-type-field': 'databaseType',
        'db-version-field': 'databaseVersion',
        'db-hosting-field': 'databaseHostingType',
        'db-name-field': 'databaseName',
        'account-name-field': 'accountName',
        'host-name-field': 'hostName',
        'service-name-field': 'serviceName',
        'port-field': 'port',
        'account-id-field': 'accountId',
        'ip-field': 'ip'
    };

    // Helper method to extract named field values
    extractNamedFieldsFromRow(fieldsGrid, fieldMap) {
        const rowData = {};
        if (!fieldsGrid) return rowData;

        const fieldItems = fieldsGrid.querySelectorAll('.field-item');
        fieldItems.forEach(fieldItem => {
            let fieldName = null;
            for (const [className, mappedName] of Object.entries(fieldMap)) {
                if (fieldItem.classList.contains(className)) {
                    fieldName = mappedName;
                    break;
                }
            }

            if (fieldName) {
                const input = fieldItem.querySelector('input, select, textarea');
                if (input) {
                    if (input.type === 'checkbox') {
                        rowData[fieldName] = input.checked;
                    } else {
                        rowData[fieldName] = input.value || '';
                    }
                }
            }
        });

        return rowData;
    }

    // Helper method to restore named fields in a row
    restoreNamedFieldsInRow(fieldsGrid, rowData, fieldMap) {
        if (!fieldsGrid) return;

        const fieldItems = fieldsGrid.querySelectorAll('.field-item');
        fieldItems.forEach(fieldItem => {
            let fieldName = null;
            for (const [className, mappedName] of Object.entries(fieldMap)) {
                if (fieldItem.classList.contains(className)) {
                    fieldName = mappedName;
                    break;
                }
            }
            if (fieldName && rowData[fieldName] !== undefined) {
                const input = fieldItem.querySelector('input, select, textarea');
                if (input) {
                    if (input.type === 'checkbox') {
                        input.checked = rowData[fieldName] || false;
                    } else {
                        input.value = rowData[fieldName] || '';
                    }
                }
            }
        });
    }

    collectAppServerData() {
        const data = {};
        const sections = ['dev-section', 'test-section', 'qa-section', 'prod-section'];
        sections.forEach(sectionId => {
            const section = document.getElementById(sectionId);
            if (section) {
                const rows = [];
                const containerGrid = section.querySelector('.row-container-grid');
                console.log(`🔍 Section ${sectionId}: found container:`, containerGrid ? 'YES' : 'NO');
                if (containerGrid) {
                    const selectFields = containerGrid.querySelectorAll('.field-item.select-field');
                    console.log(`   Found ${selectFields.length} rows`);
                    selectFields.forEach((selectField, idx) => {
                        const rowData = {};

                        const checkbox = selectField.querySelector('input[type="checkbox"]');
                        if (checkbox) {
                            rowData['checkbox'] = checkbox.checked;
                        }

                        let fieldsGrid = selectField.nextElementSibling;
                        while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                            fieldsGrid = fieldsGrid.nextElementSibling;
                        }

                        if (fieldsGrid) {
                            const namedFields = this.extractNamedFieldsFromRow(fieldsGrid, this.appServerFieldMap);
                            Object.assign(rowData, namedFields);
                        }

                        if (Object.values(rowData).some(v => v && v !== false)) {
                            console.log(`   Row ${idx}:`, rowData);
                            rows.push(rowData);
                        }
                    });
                }
                data[sectionId] = rows;
            }
        });
        console.log('📊 AppServer collected:', data);
        return data;
    }

    restoreAppServerData() {
        console.log('Restoring App Server data...');
        this.isRestoring = true;
        const savedData = this.getFromLocalStorage(this.STORAGE_KEYS.appServer);
        if (savedData && Object.keys(savedData).length > 0) {
            console.log('Found saved data, restoring grid rows...');
            const sections = ['dev-section', 'test-section', 'qa-section', 'prod-section'];
            sections.forEach(sectionId => {
                if (savedData[sectionId] && savedData[sectionId].length > 0) {
                    const section = document.getElementById(sectionId);
                    if (section) {
                        const containerGrid = section.querySelector('.row-container-grid');
                        if (containerGrid) {
                            const templateSelectField = containerGrid.querySelector('.field-item.select-field');
                            let templateFieldsGrid = templateSelectField ? templateSelectField.nextElementSibling : null;
                            while (templateFieldsGrid && !templateFieldsGrid.classList.contains('fields-grid')) {
                                templateFieldsGrid = templateFieldsGrid.nextElementSibling;
                            }

                            const selectFields = Array.from(containerGrid.querySelectorAll('.field-item.select-field'));
                            for (let i = selectFields.length - 1; i > 0; i--) {
                                const field = selectFields[i];
                                let fieldsGrid = field.nextElementSibling;
                                while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                                    fieldsGrid = fieldsGrid.nextElementSibling;
                                }
                                field.remove();
                                if (fieldsGrid) fieldsGrid.remove();
                            }

                            savedData[sectionId].forEach((rowData, idx) => {
                                let selectField, fieldsGrid;

                                if (idx === 0) {
                                    selectField = containerGrid.querySelector('.field-item.select-field');
                                    fieldsGrid = selectField ? selectField.nextElementSibling : null;
                                    while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                                        fieldsGrid = fieldsGrid.nextElementSibling;
                                    }
                                } else {
                                    selectField = templateSelectField.cloneNode(true);
                                    fieldsGrid = templateFieldsGrid.cloneNode(true);
                                    containerGrid.appendChild(selectField);
                                    containerGrid.appendChild(fieldsGrid);
                                }

                                if (selectField) {
                                    const checkbox = selectField.querySelector('input[type="checkbox"]');
                                    if (checkbox) checkbox.checked = rowData['checkbox'] || false;
                                }

                                if (fieldsGrid) {
                                    this.restoreNamedFieldsInRow(fieldsGrid, rowData, this.appServerFieldMap);
                                }
                            });
                        }
                    }
                }
            });
            console.log('App Server data restored');
        }
        this.isRestoring = false;
    }    // ===== PAGE 3: CLOUD DETAILS =====
    initCloudDetailsPage() {
        console.log('Initializing Cloud Details Page');
        this.restoreCloudDetailsData();
        this.attachAutoSaveListeners('cloud');
        this.attachSaveAndContinueButton('cloud', '/databaseserverdetails');
    }

    collectCloudDetailsData() {
        const data = {};
        const sections = ['non-prod', 'prod'];
        sections.forEach(sectionId => {
            const section = document.getElementById(sectionId);
            if (section) {
                const rows = [];
                const containerGrid = section.querySelector('.row-container-grid');
                console.log(`🔍 Section ${sectionId}: found container:`, containerGrid ? 'YES' : 'NO');
                if (containerGrid) {
                    const selectFields = containerGrid.querySelectorAll('.field-item.select-field');
                    console.log(`   Found ${selectFields.length} rows`);
                    selectFields.forEach((selectField, idx) => {
                        const rowData = {};

                        const checkbox = selectField.querySelector('input[type="checkbox"]');
                        if (checkbox) {
                            rowData['checkbox'] = checkbox.checked;
                        }

                        let fieldsGrid = selectField.nextElementSibling;
                        while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                            fieldsGrid = fieldsGrid.nextElementSibling;
                        }

                        if (fieldsGrid) {
                            const namedFields = this.extractNamedFieldsFromRow(fieldsGrid, this.cloudDetailsFieldMap);
                            Object.assign(rowData, namedFields);
                        }

                        if (Object.values(rowData).some(v => v && v !== false)) {
                            console.log(`   Row ${idx}:`, rowData);
                            rows.push(rowData);
                        }
                    });
                }
                data[sectionId] = rows;
            }
        });
        console.log('📊 CloudDetails collected:', data);
        return data;
    }

    restoreCloudDetailsData() {
        console.log('Restoring Cloud Details data...');
        this.isRestoring = true;
        const savedData = this.getFromLocalStorage(this.STORAGE_KEYS.cloudDetails);
        if (savedData && Object.keys(savedData).length > 0) {
            console.log('Found saved data, restoring grid rows...');
            const sections = ['non-prod', 'prod'];
            sections.forEach(sectionId => {
                if (savedData[sectionId] && savedData[sectionId].length > 0) {
                    const section = document.getElementById(sectionId);
                    if (section) {
                        const containerGrid = section.querySelector('.row-container-grid');
                        if (containerGrid) {
                            const templateSelectField = containerGrid.querySelector('.field-item.select-field');
                            let templateFieldsGrid = templateSelectField ? templateSelectField.nextElementSibling : null;
                            while (templateFieldsGrid && !templateFieldsGrid.classList.contains('fields-grid')) {
                                templateFieldsGrid = templateFieldsGrid.nextElementSibling;
                            }

                            const selectFields = Array.from(containerGrid.querySelectorAll('.field-item.select-field'));
                            for (let i = selectFields.length - 1; i > 0; i--) {
                                const field = selectFields[i];
                                let fieldsGrid = field.nextElementSibling;
                                while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                                    fieldsGrid = fieldsGrid.nextElementSibling;
                                }
                                field.remove();
                                if (fieldsGrid) fieldsGrid.remove();
                            }

                            savedData[sectionId].forEach((rowData, idx) => {
                                let selectField, fieldsGrid;

                                if (idx === 0) {
                                    selectField = containerGrid.querySelector('.field-item.select-field');
                                    fieldsGrid = selectField ? selectField.nextElementSibling : null;
                                    while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                                        fieldsGrid = fieldsGrid.nextElementSibling;
                                    }
                                } else {
                                    selectField = templateSelectField.cloneNode(true);
                                    fieldsGrid = templateFieldsGrid.cloneNode(true);
                                    containerGrid.appendChild(selectField);
                                    containerGrid.appendChild(fieldsGrid);
                                }

                                if (selectField) {
                                    const checkbox = selectField.querySelector('input[type="checkbox"]');
                                    if (checkbox) checkbox.checked = rowData['checkbox'] || false;
                                }

                                if (fieldsGrid) {
                                    this.restoreNamedFieldsInRow(fieldsGrid, rowData, this.cloudDetailsFieldMap);
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
    }

    collectDBServerData() {
        const data = {};
        const sections = ['dev-section', 'test-section', 'qa-section', 'prod-section'];
        sections.forEach(sectionId => {
            const section = document.getElementById(sectionId);
            if (section) {
                const rows = [];
                const containerGrid = section.querySelector('.row-container-grid');
                if (containerGrid) {
                    const selectFields = containerGrid.querySelectorAll('.field-item.select-field');
                    selectFields.forEach((selectField, idx) => {
                        const rowData = {};

                        const checkbox = selectField.querySelector('input[type="checkbox"]');
                        if (checkbox) {
                            rowData['checkbox'] = checkbox.checked;
                        }

                        let fieldsGrid = selectField.nextElementSibling;
                        while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                            fieldsGrid = fieldsGrid.nextElementSibling;
                        }

                        if (fieldsGrid) {
                            const namedFields = this.extractNamedFieldsFromRow(fieldsGrid, this.dbServerFieldMap);
                            Object.assign(rowData, namedFields);
                        }

                        if (Object.values(rowData).some(v => v && v !== false)) {
                            rows.push(rowData);
                        }
                    });
                }
                data[sectionId] = rows;
            }
        });
        return data;
    }

    restoreDBServerData() {
        console.log('Restoring DB Server data...');
        this.isRestoring = true;
        const savedData = this.getFromLocalStorage(this.STORAGE_KEYS.dbServer);
        if (savedData && Object.keys(savedData).length > 0) {
            console.log('Found saved data, restoring grid rows...');
            const sections = ['dev-section', 'test-section', 'qa-section', 'prod-section'];
            sections.forEach(sectionId => {
                if (savedData[sectionId] && savedData[sectionId].length > 0) {
                    const section = document.getElementById(sectionId);
                    if (section) {
                        const containerGrid = section.querySelector('.row-container-grid');
                        if (containerGrid) {
                            const templateSelectField = containerGrid.querySelector('.field-item.select-field');
                            let templateFieldsGrid = templateSelectField ? templateSelectField.nextElementSibling : null;
                            while (templateFieldsGrid && !templateFieldsGrid.classList.contains('fields-grid')) {
                                templateFieldsGrid = templateFieldsGrid.nextElementSibling;
                            }

                            const selectFields = Array.from(containerGrid.querySelectorAll('.field-item.select-field'));
                            for (let i = selectFields.length - 1; i > 0; i--) {
                                const field = selectFields[i];
                                let fieldsGrid = field.nextElementSibling;
                                while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                                    fieldsGrid = fieldsGrid.nextElementSibling;
                                }
                                field.remove();
                                if (fieldsGrid) fieldsGrid.remove();
                            }

                            savedData[sectionId].forEach((rowData, idx) => {
                                let selectField, fieldsGrid;

                                if (idx === 0) {
                                    selectField = containerGrid.querySelector('.field-item.select-field');
                                    fieldsGrid = selectField ? selectField.nextElementSibling : null;
                                    while (fieldsGrid && !fieldsGrid.classList.contains('fields-grid')) {
                                        fieldsGrid = fieldsGrid.nextElementSibling;
                                    }
                                } else {
                                    selectField = templateSelectField.cloneNode(true);
                                    fieldsGrid = templateFieldsGrid.cloneNode(true);
                                    containerGrid.appendChild(selectField);
                                    containerGrid.appendChild(fieldsGrid);
                                }

                                if (selectField) {
                                    const checkbox = selectField.querySelector('input[type="checkbox"]');
                                    if (checkbox) checkbox.checked = rowData['checkbox'] || false;
                                }

                                if (fieldsGrid) {
                                    this.restoreNamedFieldsInRow(fieldsGrid, rowData, this.dbServerFieldMap);
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
            if ((pageType === 'main' && e.target.hasAttribute('data-field')) || (pageType !== 'main' && (e.target.closest('table') || e.target.closest('.row-container-grid') || e.target.closest('.fields-grid')))) {
                const data = collectFn();
                self.saveToLocalStorage(storageKey, data);
            }
        });
        document.addEventListener('change', function (e) {
            if (self.isRestoring) return;
            if ((pageType === 'main' && e.target.hasAttribute('data-field')) || (pageType !== 'main' && (e.target.closest('table') || e.target.closest('.row-container-grid') || e.target.closest('.fields-grid')))) {
                const data = collectFn();
                self.saveToLocalStorage(storageKey, data);
            }
        });
        console.log('Auto-save listeners attached for ' + pageType);
    }    // ===== BUTTON HANDLERS =====
    attachSaveAndContinueButton(pageType, nextPageUrl) {
        console.log('Attaching Save and Continue button for page:', pageType);
        const saveBtn = document.querySelector('.btn-save, .btn-save-app-server, .btn-save-cloud, button[type="submit"], .save-btn');

        if (saveBtn) {
            const self = this;
            saveBtn.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('=== SAVE AND CONTINUE CLICKED for page: ' + pageType + ' ===');

                if (window.ValidationManager) {
                    window.ValidationManager.showLoadingModal('Saving Data...');
                }

                const storageKey = self.STORAGE_KEYS[pageType === 'main' ? 'mainDetails' : pageType === 'appServer' ? 'appServer' : pageType === 'cloud' ? 'cloudDetails' : 'dbServer'];
                const collectFn = pageType === 'main' ? self.collectMainFileData.bind(self) : pageType === 'appServer' ? self.collectAppServerData.bind(self) : pageType === 'cloud' ? self.collectCloudDetailsData.bind(self) : self.collectDBServerData.bind(self);
                const data = collectFn();
                console.log('📊 Collected data for ' + pageType + ':', data);
                self.saveToLocalStorage(storageKey, data);
                console.log('💾 Saved to localStorage[' + storageKey + ']');

                const delay = window.ValidationManager ? window.ValidationManager.getRandomLoadingDelay() : (3000 + Math.random() * 2000);

                setTimeout(function () {
                    if (window.ValidationManager) {
                        window.ValidationManager.hideLoadingModal();
                        window.ValidationManager.showSuccessModal('Data saved successfully!');
                        setTimeout(() => {
                            window.ValidationManager.closeSuccessModal();
                            window.location.href = nextPageUrl;
                        }, 800);
                    } else {
                        window.location.href = nextPageUrl;
                    }
                }, delay);
            });
        }
    }    attachFinalSaveButton() {
        console.log('Attaching FINAL SAVE button');
        const finalSaveBtn = document.querySelector('.btn-save, .btn-save-cloud, .btn-save-db, button[type="submit"], .save-btn');
        if (finalSaveBtn) {
            const self = this;
            finalSaveBtn.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('=== FINAL SAVE BUTTON CLICKED ===');
                self.sendAllDataToBackend();
            });
        }
    }    // ===== BACKEND SYNC =====
    sendAllDataToBackend() {
        const self = this;
        console.log('=== FINAL SAVE: COLLECTING ALL DATA ===');

        // Check what's in localStorage
        const appServerFromStorage = this.getFromLocalStorage(this.STORAGE_KEYS.appServer);
        const cloudDetailsFromStorage = this.getFromLocalStorage(this.STORAGE_KEYS.cloudDetails);
        console.log('📦 AppServer from localStorage:', appServerFromStorage);
        console.log('📦 CloudDetails from localStorage:', cloudDetailsFromStorage);

        const allData = {
            mainDetails: this.getFromLocalStorage(this.STORAGE_KEYS.mainDetails) || {},
            appServer: appServerFromStorage || this.collectAppServerData(),
            cloudDetails: cloudDetailsFromStorage || this.collectCloudDetailsData(),
            dbServer: this.collectDBServerData()
        };

        console.log('=== FINAL DATA FOR SUBMISSION ===');
        console.log('Main Details:', allData.mainDetails);
        console.log('App Server:', allData.appServer);
        console.log('Cloud Details:', allData.cloudDetails);
        console.log('DB Server:', allData.dbServer);

        if (window.ValidationManager) {
            window.ValidationManager.showLoadingModal('Submitting all data...');
        }

        const finalSaveBtn = document.querySelector('.btn-save, .btn-save-db, button[type="submit"], .save-btn');
        if (finalSaveBtn) {
            finalSaveBtn.disabled = true;
        }

        // STEP 1: Save BasicIdentity first to get the ID
        const basicIdentityData = allData.mainDetails;
        fetch(this.API_ENDPOINTS.basicIdentity, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(basicIdentityData)
        })
            .then(r => r.json())
            .then(basicIdentityResponse => {
                console.log('✅ BasicIdentity response:', basicIdentityResponse);
                const basicIdentityId = basicIdentityResponse.id;
                const beatId = basicIdentityData.beatId;

                // STEP 2: Send other details and bulk data with basicIdentityId
                const mainDetailsWithId = { basicIdentityId: basicIdentityId, ...allData.mainDetails };
                console.log('📤 Sending to descriptionImpact:', mainDetailsWithId);
                const requests = [
                    fetch(self.API_ENDPOINTS.descriptionImpact, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(mainDetailsWithId) }),
                    fetch(self.API_ENDPOINTS.authenticationVendor, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(mainDetailsWithId) }),
                    fetch(self.API_ENDPOINTS.technicalDetails, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(mainDetailsWithId) }),
                    fetch(self.API_ENDPOINTS.resourceContacts, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(mainDetailsWithId) }),
                    fetch(self.API_ENDPOINTS.appServer + '/bulk', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ basicIdentityId: basicIdentityId, beatId: beatId, gridData: allData.appServer })
                    }),
                    fetch(self.API_ENDPOINTS.cloudDetails + '/bulk', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ basicIdentityId: basicIdentityId, beatId: beatId, gridData: allData.cloudDetails })
                    }),
                    fetch(self.API_ENDPOINTS.dbServer + '/bulk', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ basicIdentityId: basicIdentityId, beatId: beatId, gridData: allData.dbServer })
                    })
                ];

                return Promise.all(requests);
            })
            .then(function (responses) {
                const allSuccess = responses.every(r => r.ok);
                if (allSuccess) {
                    console.log('SUCCESS: All requests successful');
                    if (window.ValidationManager) {
                        window.ValidationManager.hideLoadingModal();
                        window.ValidationManager.showSuccessModal('All data saved successfully!');
                        setTimeout(() => {
                            window.ValidationManager.closeSuccessModal();
                            self.clearAllStorage();
                            window.location.href = '/home';
                        }, 800);
                    } else {
                        self.showNotification('All data saved successfully!', 'success');
                        self.clearAllStorage();
                        setTimeout(() => window.location.href = '/home', 2000);
                    }
                } else {
                    console.error('FAILED: Some requests failed');
                    if (window.ValidationManager) {
                        window.ValidationManager.hideLoadingModal();
                        window.ValidationManager.showModal('Save failed! Please try again.');
                    } else {
                        self.showNotification('Save failed!', 'error');
                    }

                    if (finalSaveBtn) {
                        finalSaveBtn.disabled = false;
                    }
                }
            }).catch(error => {
                console.error('ERROR:', error);
                if (window.ValidationManager) {
                    window.ValidationManager.hideLoadingModal();
                    window.ValidationManager.showModal(`Error: ${error.message}`);
                } else {
                    self.showNotification('Error: ' + error.message, 'error');
                }

                if (finalSaveBtn) {
                    finalSaveBtn.disabled = false;
                }
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

    showNotification(message, type) {
        const div = document.createElement('div');
        div.className = 'toast-notification ' + type;
        div.textContent = message;
        div.style.cssText = 'position: fixed; top: 80px; right: 20px; padding: 15px 20px; border-radius: 6px; font-size: 14px; z-index: 9999;';
        document.body.appendChild(div);
        setTimeout(() => div.remove(), 5000);
    }
}

window.StorageManager = new AppDictionaryStorageManager();
