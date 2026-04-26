

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

    initMainFilePage() {
        console.log('Initializing Main File Page');
        const doRestore = () => {
            this.restoreMainFileData();
            this.attachAutoSaveListeners('main');
        };
        (window.DynamicDropdown ? window.DynamicDropdown.waitForOptionsLoaded() : Promise.resolve()).then(doRestore);
        this.attachSaveAndContinueButton('main', '/applicationserverdetails');
        this.attachDuplicateBlurValidation();
    }

    attachDuplicateBlurValidation() {
        const showInlineWarning = (field, msg) => {
            let warn = field.parentElement?.querySelector('.duplicate-field-warning');
            if (!warn) {
                warn = document.createElement('div');
                warn.className = 'duplicate-field-warning';
                field.parentElement?.appendChild(warn);
            }
            warn.innerHTML = '<i class="fa-solid fa-circle-xmark"></i> ' + msg;
            warn.style.cssText = 'color:#c0392b;font-size:0.8rem;margin-top:5px;display:flex;align-items:center;gap:6px;background:#fff0f0;border:1px solid #f5c6c6;border-radius:6px;padding:5px 9px;font-weight:500;';
            field.style.border = '2px solid #e74c3c';
            field.style.boxShadow = '0 0 0 3px rgba(231,76,60,0.15)';
        };
        const clearInlineWarning = (field) => {
            const warn = field.parentElement?.querySelector('.duplicate-field-warning');
            if (warn) warn.remove();
            field.style.border = '';
            field.style.boxShadow = '';
        };

        const checkField = (field, paramName, label) => {
            const val = field.value.trim();
            if (!val) { clearInlineWarning(field); return; }
            fetch('/api/basic-identity/check?' + paramName + '=' + encodeURIComponent(val))
                .then(r => r.json())
                .then(result => {
                    const exists = paramName === 'beatId' ? result.beatIdExists : result.applicationNameExists;
                    if (exists) {
                        showInlineWarning(field, label + ' already exists!');
                    } else {
                        clearInlineWarning(field);
                    }
                }).catch(() => clearInlineWarning(field));
        };

        const debounce = (fn, delay) => {
            let timer;
            return function (...args) { clearTimeout(timer); timer = setTimeout(() => fn.apply(this, args), delay); };
        };

        const beatIdField = document.querySelector('[data-field="beatId"]');
        const appNameField = document.querySelector('[data-field="applicationName"]');

        if (beatIdField) {
            const check = () => checkField(beatIdField, 'beatId', 'This Beat ID');
            beatIdField.addEventListener('blur', check);
            beatIdField.addEventListener('input', debounce(check, 800));
            beatIdField.addEventListener('input', function () {
                if (!this.value.trim()) clearInlineWarning(this);
            });
        }

        if (appNameField) {
            const check = () => checkField(appNameField, 'applicationName', 'This Application');
            appNameField.addEventListener('blur', check);
            appNameField.addEventListener('input', debounce(check, 800));
            appNameField.addEventListener('input', function () {
                if (!this.value.trim()) clearInlineWarning(this);
            });
        }
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
                if (!field) return;
                if (field.type === 'checkbox') {
                    field.checked = savedData[fieldName];
                } else if (field.tagName === 'SELECT') {
                    const val = savedData[fieldName] || '';
                    if (val && val !== 'Select') {
                        const exists = Array.from(field.options).some(o => o.value === val);
                        if (!exists) {
                            const opt = document.createElement('option');
                            opt.value = val;
                            opt.textContent = val;
                            field.appendChild(opt);
                        }
                    }
                    field.value = val;
                } else {
                    field.value = savedData[fieldName] || '';
                }
            });
            console.log('Main File data restored');
        }
    }

    initAppServerPage() {
        console.log('Initializing App Server Page');
        const doRestore = () => {
            this.restoreAppServerData();
            this.attachAutoSaveListeners('appServer');
        };
        (window.DynamicDropdown ? window.DynamicDropdown.waitForOptionsLoaded() : Promise.resolve()).then(doRestore);
        this.attachSaveAndContinueButton('appServer', '/clouddetails');
    }

    appServerFieldMap = {
        'deployed-server-field': 'deployedServer',
        'server-name-field': 'serverName',
        'server-os-field': 'serverOsVersion',
        'domain-field': 'domain',
        'cluster-field': 'cluster',
        'service-name-field': 'serviceName',
        'ip-address-field': 'ipAddress'
    };

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
                    } else if (input.tagName === 'SELECT') {
                        const val = rowData[fieldName] || '';
                        if (val && val !== 'Select') {
                            const exists = Array.from(input.options).some(o => o.value === val);
                            if (!exists) {
                                const opt = document.createElement('option');
                                opt.value = val;
                                opt.textContent = val;
                                input.appendChild(opt);
                            }
                        }
                        input.value = val;
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
    }
    initCloudDetailsPage() {
        console.log('Initializing Cloud Details Page');
        const doRestore = () => {
            this.restoreCloudDetailsData();
            this.attachAutoSaveListeners('cloud');
        };
        (window.DynamicDropdown ? window.DynamicDropdown.waitForOptionsLoaded() : Promise.resolve()).then(doRestore);
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

    initDBServerPage() {
        console.log('Initializing DB Server Page (FINAL SAVE PAGE)');
        const doRestore = () => {
            this.restoreDBServerData();
            this.attachAutoSaveListeners('dbServer');
        };
        (window.DynamicDropdown ? window.DynamicDropdown.waitForOptionsLoaded() : Promise.resolve()).then(doRestore);
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
        console.log('Attaching Save and Continue button for page:', pageType);
        const saveBtn = document.querySelector('.btn-save, .btn-save-app-server, .btn-save-cloud, button[type="submit"], .save-btn');

        if (saveBtn) {
            const self = this;
            saveBtn.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('=== SAVE AND CONTINUE CLICKED for page: ' + pageType + ' ===');

                if (pageType === 'main') {
                    const beatId = (document.querySelector('[data-field="beatId"]')?.value || '').trim();
                    const appName = (document.querySelector('[data-field="applicationName"]')?.value || '').trim();
                    const assignmentGroup = (document.querySelector('[data-field="assignmentGroup"]')?.value || '').trim();
                    if (!beatId) {
                        if (window.ValidationManager) window.ValidationManager.showModal('Beat ID is required. Please enter a Beat ID to continue.');
                        return;
                    }
                    if (!appName) {
                        if (window.ValidationManager) window.ValidationManager.showModal('Application Name is required. Please enter an Application Name to continue.');
                        return;
                    }
                    if (!assignmentGroup || assignmentGroup === 'Select') {
                        if (window.ValidationManager) window.ValidationManager.showModal('Assignment Group is required. Please select or add an Assignment Group to continue.');
                        return;
                    }
                } else {
                    const sectionInputs = document.querySelectorAll(
                        '.fields-grid input[type="text"], .fields-grid input[type="number"], .fields-grid select, ' +
                        '.row-container-grid input[type="text"], .row-container-grid input[type="number"], .row-container-grid select'
                    );
                    const hasData = Array.from(sectionInputs).some(function (el) {
                        const v = (el.value || '').trim();
                        return v !== '' && v !== 'Select';
                    });
                    if (!hasData) {
                        const label = pageType === 'appServer' ? 'Server Details' : pageType === 'cloud' ? 'Cloud Details' : 'DB Server Details';
                        if (window.ValidationManager) window.ValidationManager.showModal('At least one ' + label + ' entry is required. Please fill in at least one environment section to continue.');
                        return;
                    }
                }

                function proceedToSave() {
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

                    const pageMessages = {
                        main: { title: 'Data Saved Successfully!', subtitle: 'Please continue to add Server Details.' },
                        appServer: { title: 'Data Saved Successfully!', subtitle: 'Please continue to add Cloud Details.' },
                        cloud: { title: 'Data Saved Successfully!', subtitle: 'Please continue to add DB Server Details.' }
                    };
                    const msg = pageMessages[pageType] || { title: 'Data Saved!', subtitle: 'Proceeding to next step...' };

                    setTimeout(function () {
                        if (window.ValidationManager) {
                            window.ValidationManager.hideLoadingModal();
                            window.ValidationManager.showSuccessModal(msg.title, msg.subtitle, function () {
                                window.location.href = nextPageUrl;
                            });
                        } else {
                            window.location.href = nextPageUrl;
                        }
                    }, delay);
                }

                if (pageType === 'main') {
                    const beatId = (document.querySelector('[data-field="beatId"]')?.value || '').trim();
                    const appName = (document.querySelector('[data-field="applicationName"]')?.value || '').trim();
                    if (window.ValidationManager) window.ValidationManager.showLoadingModal('Checking for duplicates...');
                    fetch('/api/basic-identity/check?beatId=' + encodeURIComponent(beatId) + '&applicationName=' + encodeURIComponent(appName))
                        .then(function (r) { return r.json(); })
                        .then(function (result) {
                            if (result.beatIdExists) {
                                if (window.ValidationManager) {
                                    window.ValidationManager.hideLoadingModal();
                                    window.ValidationManager.showModal('Application Already Exists! A record with Beat ID "' + beatId + '" is already registered. Please use a different Beat ID.');
                                } else {
                                    alert('Application Already Exists! Beat ID "' + beatId + '" is already registered.');
                                }
                                return;
                            }
                            if (result.applicationNameExists) {
                                if (window.ValidationManager) {
                                    window.ValidationManager.hideLoadingModal();
                                    window.ValidationManager.showModal('Application Already Exists! "' + appName + '" is already registered. Please use a different Application Name.');
                                } else {
                                    alert('Application Already Exists! "' + appName + '" is already registered.');
                                }
                                return;
                            }
                            proceedToSave();
                        })
                        .catch(function () {
                            if (window.ValidationManager) window.ValidationManager.hideLoadingModal();
                            proceedToSave();
                        });
                } else {
                    proceedToSave();
                }
            });
        }
    } attachFinalSaveButton() {
        console.log('Attaching FINAL SAVE button');
        const finalSaveBtn = document.querySelector('.btn-save, .btn-save-cloud, .btn-save-db, button[type="submit"], .save-btn');
        if (finalSaveBtn) {
            const self = this;
            finalSaveBtn.addEventListener('click', function (e) {
                e.preventDefault();
                console.log('=== FINAL SAVE BUTTON CLICKED ===');

                const sectionInputs = document.querySelectorAll(
                    '.fields-grid input[type="text"], .fields-grid input[type="number"], .fields-grid select, ' +
                    '.row-container-grid input[type="text"], .row-container-grid input[type="number"], .row-container-grid select'
                );
                const hasData = Array.from(sectionInputs).some(function (el) {
                    const v = (el.value || '').trim();
                    return v !== '' && v !== 'Select';
                });
                if (!hasData) {
                    if (window.ValidationManager) window.ValidationManager.showModal('At least one DB Server Details entry is required. Please fill in at least one environment section to continue.');
                    return;
                }

                self.sendAllDataToBackend();
            });
        }
    }
    sendAllDataToBackend() {
        const self = this;
        console.log('=== FINAL SAVE: COLLECTING ALL DATA ===');

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

        const basicIdentityData = allData.mainDetails;
        fetch(this.API_ENDPOINTS.basicIdentity, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(basicIdentityData)
        })
            .then(r => {
                if (r.status === 409) {
                    return r.json().then(body => {
                        const msg = body.error || 'Duplicate entry detected.';
                        if (window.ValidationManager) {
                            window.ValidationManager.hideLoadingModal();
                            window.ValidationManager.showModal(msg);
                        } else {
                            alert(msg);
                        }
                        const finalSaveBtn = document.querySelector('.btn-save, .btn-save-db, button[type="submit"], .save-btn');
                        if (finalSaveBtn) finalSaveBtn.disabled = false;
                        throw new Error('duplicate');
                    });
                }
                return r.json();
            })
            .then(basicIdentityResponse => {
                if (!basicIdentityResponse || !basicIdentityResponse.id) return;
                console.log('✅ BasicIdentity response:', basicIdentityResponse);
                const basicIdentityId = basicIdentityResponse.id;
                const beatId = basicIdentityData.beatId;

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
                        window.ValidationManager.showSuccessModal(
                            'Application Added Successfully!',
                            'Your application has been saved. You can now view it on the home page.',
                            function () {
                                self.clearAllStorage();
                                window.location.href = '/home';
                            }
                        );
                    } else {
                        self.showNotification('New Application added successfully!', 'success');
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
                if (error.message === 'duplicate') return;
                console.error('ERROR:', error);
                if (window.ValidationManager) {
                    window.ValidationManager.hideLoadingModal();
                    window.ValidationManager.showModal('An unexpected error occurred. Please try again.');
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
