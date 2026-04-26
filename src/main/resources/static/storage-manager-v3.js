

class AppDictionaryStorageManager {
    constructor() {
        console.log('Creating Storage Manager...');
        this.isRestoring = false; this.STORAGE_KEYS = {
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
    initMainFilePage() {
        console.log('Initializing Main File Page');
        this.restoreMainFileData();
        this.attachAutoSaveListeners('main');
        this.attachSaveAndContinueButton('main', '/applicationserverdetails');
    }

    collectMainFileDataClean() {
        const data = {};
        document.querySelectorAll('[data-field]').forEach(field => {
            const fieldName = field.getAttribute('data-field');
            let value;

            if (field.type === 'checkbox') {
                value = field.checked;
            } else if (field.tagName === 'SELECT') {

                const val = field.value || '';
                value = (!val || val === '' || val === 'null') ? null : val.trim();
            } else {

                const val = field.value || '';
                value = !val || val.trim() === '' ? null : val.trim();
            }

            data[fieldName] = value;
        });
        return data;
    } collectMainFileData() {
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

    initAppServerPage() {
        console.log('Initializing App Server Page');
        this.restoreAppServerData();
        this.attachAutoSaveListeners('appServer');
        this.attachSaveAndContinueButton('appServer', '/clouddetails');
    }

    collectAppServerData() {
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
                            const fields = fieldsGrid.querySelectorAll('input, select, textarea');
                            fields.forEach((field, fieldIdx) => {
                                if (field.type === 'checkbox') {
                                    rowData['col_' + fieldIdx] = field.checked;
                                } else {
                                    rowData['col_' + fieldIdx] = field.value || '';
                                }
                            });
                        }

                        if (Object.values(rowData).some(v => v && v !== false)) {
                            rows.push(rowData);
                        }
                    });
                } else {

                    const tbody = section.querySelector('.row-container');
                    if (tbody) {
                        tbody.querySelectorAll('tr').forEach(row => {
                            const rowData = {};
                            row.querySelectorAll('input, select, textarea').forEach((field, idx) => {
                                if (field.type === 'checkbox') {
                                    rowData['col_' + idx] = field.checked;
                                } else {
                                    rowData['col_' + idx] = field.value || '';
                                }
                            });
                            if (Object.values(rowData).some(v => v)) {
                                rows.push(rowData);
                            }
                        });
                    }
                }
                data[sectionId] = rows;
            }
        });
        return data;
    } restoreAppServerData() {
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
                                    const fields = fieldsGrid.querySelectorAll('input, select, textarea');
                                    fields.forEach((field, fieldIdx) => {
                                        const value = rowData['col_' + fieldIdx];
                                        if (field.type === 'checkbox') {
                                            field.checked = value || false;
                                        } else {
                                            field.value = value || '';
                                        }
                                    });
                                }
                            });
                        } else {

                            const tbody = section.querySelector('.row-container');
                            if (tbody) {
                                const templateRow = tbody.querySelector('tr');
                                tbody.innerHTML = '';
                                if (templateRow) tbody.appendChild(templateRow);
                                const rows = tbody.querySelectorAll('tr');
                                savedData[sectionId].forEach((rowData, idx) => {
                                    let targetRow = rows[idx];
                                    if (!targetRow && idx > 0) {
                                        targetRow = rows[0].cloneNode(true);
                                        tbody.appendChild(targetRow);
                                    }
                                    if (targetRow) {
                                        const fields = targetRow.querySelectorAll('input, select, textarea');
                                        fields.forEach((field, fieldIdx) => {
                                            const value = rowData['col_' + fieldIdx];
                                            if (field.type === 'checkbox') {
                                                field.checked = value || false;
                                            } else {
                                                field.value = value || '';
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                }
            });
            console.log('App Server data restored');
        }
        this.isRestoring = false;
    }

    initCloudDetailsPage() {
        console.log('Initializing Cloud Details Page');
        this.restoreCloudDetailsData();
        this.attachAutoSaveListeners('cloud');
        this.attachSaveAndContinueButton('cloud', '/databaseserverdetails');
    } collectCloudDetailsData() {
        const data = {};
        const sections = ['non-prod', 'prod'];
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
                            const fields = fieldsGrid.querySelectorAll('input, select, textarea');
                            fields.forEach((field, fieldIdx) => {
                                if (field.type === 'checkbox') {
                                    rowData['col_' + fieldIdx] = field.checked;
                                } else {
                                    rowData['col_' + fieldIdx] = field.value || '';
                                }
                            });
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
    } restoreCloudDetailsData() {
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
                                    const fields = fieldsGrid.querySelectorAll('input, select, textarea');
                                    fields.forEach((field, fieldIdx) => {
                                        const value = rowData['col_' + fieldIdx];
                                        if (field.type === 'checkbox') {
                                            field.checked = value || false;
                                        } else {
                                            field.value = value || '';
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

    initDBServerPage() {
        console.log('Initializing DB Server Page (FINAL SAVE PAGE)');
        this.restoreDBServerData();
        this.attachAutoSaveListeners('dbServer');
        this.attachFinalSaveButton();
    } collectDBServerData() {
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
                            const fields = fieldsGrid.querySelectorAll('input, select, textarea');
                            fields.forEach((field, fieldIdx) => {
                                if (field.type === 'checkbox') {
                                    rowData['col_' + fieldIdx] = field.checked;
                                } else {
                                    rowData['col_' + fieldIdx] = field.value || '';
                                }
                            });
                        }

                        if (Object.values(rowData).some(v => v && v !== false)) {
                            rows.push(rowData);
                        }
                    });
                } else {

                    const tbody = section.querySelector('.row-container');
                    if (tbody) {
                        const allRows = tbody.querySelectorAll('tr');
                        console.log(`Collecting ${sectionId}: Found ${allRows.length} rows`);
                        allRows.forEach((row, rowIdx) => {
                            const rowData = {};
                            const fields = row.querySelectorAll('input, select, textarea');
                            fields.forEach((field, fieldIdx) => {
                                if (field.type === 'checkbox') {
                                    rowData['col_' + fieldIdx] = field.checked;
                                } else {
                                    rowData['col_' + fieldIdx] = field.value || '';
                                }
                            });
                            const hasData = Object.values(rowData).some(v => v && v !== false);
                            if (hasData) {
                                console.log(`  Row ${rowIdx}: Added (has data)`);
                                rows.push(rowData);
                            } else {
                                console.log(`  Row ${rowIdx}: Skipped (empty)`);
                            }
                        });
                    }
                }
                data[sectionId] = rows;
                console.log(`${sectionId} FINAL COUNT: ${rows.length} rows`);
            }
        });
        return data;
    } restoreDBServerData() {
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
                                    const fields = fieldsGrid.querySelectorAll('input, select, textarea');
                                    fields.forEach((field, fieldIdx) => {
                                        const value = rowData['col_' + fieldIdx];
                                        if (field.type === 'checkbox') {
                                            field.checked = value || false;
                                        } else {
                                            field.value = value || '';
                                        }
                                    });
                                }
                            });
                        } else {

                            const tbody = section.querySelector('.row-container');
                            if (tbody) {
                                const templateRow = tbody.querySelector('tr');
                                while (tbody.rows.length > 1) tbody.deleteRow(1);
                                savedData[sectionId].forEach((rowData, idx) => {
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
                                            const value = rowData['col_' + fieldIdx];
                                            if (field.type === 'checkbox') {
                                                field.checked = value || false;
                                            } else {
                                                field.value = value || '';
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                }
            });
            console.log('DB Server data restored');
        }
        this.isRestoring = false;
    }
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
    }
    attachSaveAndContinueButton(pageType, nextPageUrl) {
        console.log('Attaching Save and Continue button for page:', pageType, 'Redirect to:', nextPageUrl);
        const saveBtn = document.querySelector('.btn-save, .btn-save-app-server, .btn-save-cloud, button[type="submit"], .save-btn');
        console.log('Save button found:', !!saveBtn);
        console.log('Button element:', saveBtn);
        console.log('All buttons on page:', document.querySelectorAll('button').length);

        if (saveBtn) {
            const self = this;
            saveBtn.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('=== SAVE AND CONTINUE CLICKED ===');
                if (window.ValidationManager) {
                    window.ValidationManager.showLoadingModal('Saving Data...');
                }

                const storageKey = self.STORAGE_KEYS[pageType === 'main' ? 'mainDetails' : pageType === 'appServer' ? 'appServer' : pageType === 'cloud' ? 'cloudDetails' : 'dbServer'];
                const collectFn = pageType === 'main' ? self.collectMainFileData.bind(self) : pageType === 'appServer' ? self.collectAppServerData.bind(self) : pageType === 'cloud' ? self.collectCloudDetailsData.bind(self) : self.collectDBServerData.bind(self);
                const data = collectFn();
                self.saveToLocalStorage(storageKey, data);

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
    }

    attachFinalSaveButton() {
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
    }
    sendAllDataToBackend() {
        const self = this;
        console.log('=== FINAL SAVE: COLLECTING ALL DATA ===');
        const allData = {
            mainDetails: this.getFromLocalStorage(this.STORAGE_KEYS.mainDetails) || {},
            appServer: this.getFromLocalStorage(this.STORAGE_KEYS.appServer) || {},
            cloudDetails: this.getFromLocalStorage(this.STORAGE_KEYS.cloudDetails) || {},
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
        } const requests = [
            fetch(this.API_ENDPOINTS.basicIdentity, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(allData.mainDetails) }),
            fetch(this.API_ENDPOINTS.descriptionImpact, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(allData.mainDetails) }),
            fetch(this.API_ENDPOINTS.authenticationVendor, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(allData.mainDetails) }),
            fetch(this.API_ENDPOINTS.technicalDetails, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(allData.mainDetails) }),
            fetch(this.API_ENDPOINTS.resourceContacts, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(allData.mainDetails) }),
            fetch(this.API_ENDPOINTS.appServer + '/bulk', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(allData.appServer) }),
            fetch(this.API_ENDPOINTS.cloudDetails + '/bulk', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(allData.cloudDetails) }),
            fetch(this.API_ENDPOINTS.dbServer + '/bulk', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(allData.dbServer) })
        ];

        Promise.all(requests).then(function (responses) {
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
