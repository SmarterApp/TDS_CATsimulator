YUI.add('sessiontests_add', function (Y) {
    Y.SessionTests_Add = function () {
        var _strWinAddSimTests = "#addSimTests",
            _strDivModalContainer = "#modalContainer",
            _strDdlSimTests = "#add-test-simTests",
            _strDdlSimGrade = "#add-test-simGrade",
            _strDdlSimSubject = "#add-test-simSubject",
            _strTblTestsToAdd = "#add-test-table",
            _strBtnSubmit = "#add-test-submit",
            _strBtnCancel = "#add-test-cancel",
        //copy of tablerow
            _addTestTblRow,
        //module data
            _clientName,
            _sessionType,
            _sessionLanguage,
            _sessionKey,
        //copy of data
            _dataSessionTests,
        //loading panel
            _loadingPanel,

        // get session key, client name, session type, session language, and loading panel object
        open = function (sessionKey, clientName, sessionType, sessionLanguage, loadingPanel) {
            var winAddSimTests = Y.one(_strWinAddSimTests),
                tempTableTbody,
                simIteration,
//                simOpps,
//                simMean,
                simDeviation,
                simStrand;

            //show
            winAddSimTests.setStyle('display', 'block');
            Y.one(_strDivModalContainer).removeAttribute('style');

            _clientName = clientName;
            _sessionType = sessionType;
            _sessionLanguage = sessionLanguage;
            _sessionKey = sessionKey;
            _loadingPanel = loadingPanel;

            tempTableTbody = Y.one(_strTblTestsToAdd).one('tbody');
            if (_addTestTblRow == null) {
                _addTestTblRow = tempTableTbody.one('tr').cloneNode(true);
            }
            tempTableTbody.empty();

            //retrieve the session tests from the server
            getSessionTestsToAdd();

            //events
            Y.on('click', btnSubmit_Click, _strBtnSubmit);
            Y.on('click', btnCancel_Click, _strBtnCancel);
            // filter events
            Y.on('change', ddlSimGrade_Change , _strDdlSimGrade);
            Y.on('change', ddlSimSubject_Change, _strDdlSimSubject);
            //populate the addsimTestTable when selected tests change
            Y.on('change', ddlSimTests_Change, _strDdlSimTests);
        },

        getSessionTestsToAdd = function () {
            callback = {
                on: {
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.responseText);
                        var sessionTests;

                        try {
                            sessionTests = Y.JSON.parse(response.responseText);
                        }
                        catch (e) {
                            alert("Failed to parse Session Tests JSON data.");
                            return;
                        }

                        // check if tests are available
                        if (sessionTests.length == 0) {
                            alert("Did not find any Session Tests to be added.");
                        }

                        //clone the session tests data to local copy
                        _dataSessionTests = Y.clone(sessionTests);

                        Y.log("PARSED DATA: " + Y.Lang.dump(sessionTests));

                        //populate the controls
                        updateGradeDropdownControl();

                        updateSubjectDropdownControl();

                        updateTestListControl();
                    },
                    failure: function (id, response) {
                        alert("Failed to retrieve list of Session Tests.");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetAddSessionTestList?clientname=" + _clientName + "&sessiontype=" + _sessionType + "&sessionkey=" + _sessionKey + "&sessionlanguage=" + _sessionLanguage, callback);
        },

        //populate controls
        updateTestListControl = function () {
            var testListControl = Y.one(_strDdlSimTests),
                selectedGrade = Y.one(_strDdlSimGrade).get('value'),
                selectedSubject = Y.one(_strDdlSimSubject).get('value'),
                len = _dataSessionTests.length,
                optn;

            testListControl.empty();

            for (var i = 0; i < len; i++) {
                if (((selectedGrade === null || selectedGrade === '' || selectedGrade === 'All') || _dataSessionTests[i].GradeCode == selectedGrade)
                        && ((selectedSubject === null || selectedSubject === '' || selectedSubject === 'All') || _dataSessionTests[i].Subject == selectedSubject)) {
                    optn = Y.Node.create('<option value=\"' + _dataSessionTests[i].AdminSubject + '\">' + _dataSessionTests[i].TestID + '</option>');
                    testListControl.appendChild(optn);
                }
            }
        },

        updateGradeDropdownControl = function () {
            var ddlGrade = Y.one(_strDdlSimGrade),
                len = _dataSessionTests.length, 
                optn, tmp = {}, i;

            //clear the dropdownlist
            ddlGrade.empty();

            for (i = 0; i < len; i++) {
                tmp[_dataSessionTests[i].GradeCode] = _dataSessionTests[i].GradeCode;
            }

            optn = Y.Node.create('<option value=\"All\">All</option>');
            ddlGrade.appendChild(optn);
            for (i in tmp) {
                optn = Y.Node.create('<option value=\"' + tmp[i] + '\">' + tmp[i] + '</option>');
                ddlGrade.appendChild(optn);
            }
        },

        updateSubjectDropdownControl = function () {
            var ddlSubject = Y.one(_strDdlSimSubject),
                len = _dataSessionTests.length, 
                optn, tmp = {}, distinctGrades = [], i;

            //clear the dropdown
            ddlSubject.empty();

            for (i = 0; i < len; i++) {
                tmp[_dataSessionTests[i].Subject] = _dataSessionTests[i].Subject;
            }

            optn = Y.Node.create('<option value=\"All\">All</option>');
            ddlSubject.appendChild(optn);
            for (i in tmp) {
                optn = Y.Node.create('<option value=\"' + tmp[i] + '\">' + tmp[i] + '</option>');
                ddlSubject.appendChild(optn);
            }
        },

        updateTestListTable = function () {
            var ddlTestListControl = Y.one(_strDdlSimTests),
                tbodyTestListTable = Y.one(_strTblTestsToAdd).one('tbody'),
                testRowToAdd, testRowToAddCols;

            tbodyTestListTable.empty();

            ddlTestListControl.get("options").each( function () {
                var selected = this.get("selected"),
                    testKey = this.get("value"),
                    len, i;

                if (selected) {
                    len = _dataSessionTests.length;
                    for (i = 0; i < len; i++) {
                        if (testKey == _dataSessionTests[i].AdminSubject) {
                            testRowToAdd = _addTestTblRow.cloneNode(true);
                            testRowToAddCols = testRowToAdd.all('td');
                            testRowToAddCols.item(0).setContent(_dataSessionTests[i].AdminSubject);
                            testRowToAddCols.item(1).one('input').set('value', '10');
                            testRowToAddCols.item(2).one('input').set('value', _dataSessionTests[i].Opportunities);
                            testRowToAddCols.item(3).one('input').set('value', _dataSessionTests[i].MeanProficiency);
                            testRowToAddCols.item(4).one('input').set('value', '1.0');
                            testRowToAddCols.item(5).one('input').set('value', '1.0');
                            testRowToAddCols.item(6).setContent(_dataSessionTests[i].HandScoreItemTypes);
                            tbodyTestListTable.appendChild(testRowToAdd);
                        }
                    }
                }
            });
        },

        submitAddSessionTests = function () {
            var tbodyTestListTableRows = Y.one(_strTblTestsToAdd).one('tbody').all('tr'),
                tbodyTestListTableRow, tbodyTestListTableCols,
                testKey, iterations, opportunities, meanProficiency, sdProficiency, strandCorrelation, handScoreItemTypes,
                sessionTestsToAdd = [], SessionTest, validationErrorMsg;
            
            tbodyTestListTableRows.each( function () {
                tbodyTestListTableCols = this.all('td');
                testKey = tbodyTestListTableCols.item(0).getContent();
                iterations = tbodyTestListTableCols.item(1).one('input').get('value');
                opportunities = tbodyTestListTableCols.item(2).one('input').get('value');
                meanProficiency = tbodyTestListTableCols.item(3).one('input').get('value');
                sdProficiency = tbodyTestListTableCols.item(4).one('input').get('value');
                strandCorrelation = tbodyTestListTableCols.item(5).one('input').get('value');
                handScoreItemTypes = tbodyTestListTableCols.item(6).getContent();
                SessionTest = {
                    TestID : testKey,  // TODO - There is some ambiguity
                    AdminSubject: testKey,
                    Iterations: iterations, 
                    Opportunities: opportunities, 
                    MeanProficiency: meanProficiency, 
                    SdProficiency: sdProficiency, 
                    StrandCorrelation: strandCorrelation,
                    HandScoreItemTypes: handScoreItemTypes
                };

                // validate the parameters here
                var regExInteger = /^\s*(\+|-)?\d+\s*$/,
                    regExDecimal = /^\s*(\+|-)?((\d+(\.\d+)?)|(\.\d+))\s*$/;

                if (iterations == null || iterations.length == 0 || opportunities == null || opportunities.length == 0 
                    || meanProficiency == null || meanProficiency.length == 0 || sdProficiency == null || sdProficiency.length == 0 
                    || strandCorrelation == null || strandCorrelation == 0 ) {

                    validationErrorMsg = "All fields are required.";
                } else if (String(iterations).search(regExInteger) == -1 || String(opportunities).search(regExInteger) == -1
                    || String(meanProficiency).search(regExDecimal) == -1 || String(sdProficiency).search(regExDecimal) == -1
                    || String(strandCorrelation).search(regExDecimal) == -1) {

                    validationErrorMsg = "An invalid value was found. Please check your input values.";
                }

                // add the test before validating so we don't get a 'test not selected' validation error.  Validation for the whole thing needs to be re-designed.
                sessionTestsToAdd.push(SessionTest);
            });

            if (validationErrorMsg != null && validationErrorMsg.length > 0) {
                alert(validationErrorMsg);
            } else if (sessionTestsToAdd.length > 0) {
                var cfg = {
			            method: "POST",
			            data: "sessionkey=" + _sessionKey + "&sessiontests=" + Y.JSON.stringify(sessionTestsToAdd),
                        on: {
                            complete: function (id, xhr, args) {
                                _loadingPanel.hide();
                            },
                            success: function(id, xhr, arguments) {
                                var responseList = Y.JSON.parse(xhr.responseText),
                                    lenResponseList = responseList.length,
                                    response, reasons = [], i, strFTestKeys, allPass = true;

                                for (i = 0; i < lenResponseList; i++) {
                                    if (responseList[i].status != "success") {
                                        allPass = false;
                                        reasons.push(responseList[i].reason);
                                    }
                                }

                                if (allPass) {
                                    close();
                                    alert("Session Tests were added successfully.");
                                }
                                else {
                                    strFTestKeys = reasons.join("\n");
                                    alert(strFTestKeys);
                                }
                            },
                            failure: function (id, response) {
                                alert("Failed to add session tests.");
                            }
                        }
		    };

                    _loadingPanel.show();
                    Y.io("Services/WebSimXHR.ashx/AddSessionTests", cfg);
            } else {
                alert('You must select at least one test to add!');
            }
        },

        close = function () {

            Y.one(_strWinAddSimTests).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');

            //detach events
            Y.one(_strBtnSubmit).detach();
            Y.one(_strBtnCancel).detach();

            //fire update edit event
            Y.fire('Session_Edit:refresh', {
                //pass data here if needed
            });
        },

        updateTestList = function (e) {
            updateTestListControl();
        },

        ddlSimTests_Change = function (e) {
            e.preventDefault();
            updateTestListTable();
        },

        ddlSimGrade_Change = function (e) {
            e.preventDefault();
            updateTestListControl();
        },

        ddlSimSubject_Change = function (e) {
            e.preventDefault();
            updateTestListControl();
        },

        btnCancel_Click = function (e) {
            e.preventDefault();
            close();
        },

        btnSubmit_Click = function (e) {
            e.preventDefault();
            submitAddSessionTests();
        };

        return {
            open: open
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "json-stringify"] });