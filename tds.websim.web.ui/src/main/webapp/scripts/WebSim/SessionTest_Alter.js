YUI.add('sessiontest_alter', function (Y) {
    Y.SessionTest_Alter = function () {
        // id's of input controls
        var _strWinAlterTest = "#alterSimTest",
            _strDivModalContainer = "#modalContainer",
            _strLblSimTestKey = "#alter-test-testID",
            _strBtnSimIteration = "#alter-test-simIteration",
            _strBtnSimOpps = "#alter-test-simOpps",
            _strBtnSimDeviation = "#alter-test-simDeviation",
            _strBtnSimStrand = "#alter-test-simStrand",
            _strBtnSimHandScoreItemTypes = "#alter-test-simHandScoreItemTypes",
            _strBtnSimMean = "#alter-test-simMean",
            _strDdlSimGrade = "#alter-test-simGrade",
            _strDdlSimSubject = "#alter-test-simSubject",
        //submit and cancel buttons
            _strBtnSubmit = "#alter-test-submit",
            _strBtnCancel = "#alter-test-cancel",
            _loadingPanel,

        open = function (sessionKey, clientname, simTableRowData, loadingPanel) {
            //show
            Y.one(_strWinAlterTest).setStyle('display', 'block');
            Y.one(_strDivModalContainer).removeAttribute('style');

            _loadingPanel = loadingPanel;

            Y.one(_strLblSimTestKey).setContent(simTableRowData.AdminSubject);

            //set defaults
            Y.one(_strBtnSimIteration).set('value', simTableRowData.Iterations);
            Y.one(_strBtnSimOpps).set('value', simTableRowData.Opportunities);    //this is now an offset, so this will always be set to 0
            Y.one(_strBtnSimMean).set('value', simTableRowData.MeanProficiency);
            Y.one(_strBtnSimDeviation).set('value', simTableRowData.SdProficiency);
            Y.one(_strBtnSimStrand).set('value', simTableRowData.StrandCorrelation);
            initializeHandScoreItemTypes(simTableRowData.AdminSubject, simTableRowData.HandScoreItemTypes);
            
            //events
            Y.on('click', btnSubmit_Click, _strBtnSubmit, null, sessionKey, simTableRowData.AdminSubject);
            Y.on('click', btnCancel_Click, _strBtnCancel, null, _strWinAlterTest);
        },
            
        initializeHandScoreItemTypes = function (adminsubject, handscoreitemtypes) {
            var callback = {
                on: {
                    success: function (id, xhr, arguments) {
                        var selectedItemTypes = handscoreitemtypes ? handscoreitemtypes.split(",") : [];
                        var itemTypes = [];
                        try {
                            itemTypes = Y.JSON.parse(xhr.responseText);
                            Y.one(_strBtnSimHandScoreItemTypes).empty();
                            for (var i = 0, len = itemTypes.length; i < len; i++) {
                                var itemType = itemTypes[i].toString();
                                var valuestring =  ' value=\"' + itemType +  '\"' ;
                                var selectedstring = selectedItemTypes.indexOf(itemType) > -1 ? ' selected=\"selected\"' : "";
                                var optn = Y.Node.create('<option' + valuestring + selectedstring + '>' + itemType + '</option>');								
                                Y.one(_strBtnSimHandScoreItemTypes).appendChild(optn);
                            }							
                        }
                        catch (e) {
                            alert("Failed to parse the JSON data." + e.toString());
                            return;
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to get item type list!");
                    }
                }
            };
            Y.io("Services/WebSimXHR.ashx/GetItemTypes?adminsubject=" + adminsubject, callback);
        },

        validateAlterTestParams = function (iterations, opportunities, meanProficiency, sdProficiency, strandCorrelation, handScoreItemTypes) {
            var validationError,
                regExInteger = /^\s*(\+|-)?\d+\s*$/,
                regExDecimal = /^\s*(\+|-)?((\d+(\.\d+)?)|(\.\d+))\s*$/;
            if (iterations == null || iterations.length == 0
                    || opportunities == null || opportunities.length == 0
                    || meanProficiency == null || meanProficiency.length == 0
                    || sdProficiency == null || sdProficiency.length == 0
                    || strandCorrelation == null || strandCorrelation.length == 0) {
                validationError = "All fields are required.";
            } else if (String(iterations).search(regExInteger) == -1 || String(opportunities).search(regExInteger) == -1
                    || String(meanProficiency).search(regExDecimal) == -1 || String(sdProficiency).search(regExDecimal) == -1
                    || String(strandCorrelation).search(regExDecimal) == -1) {
                validationError = "An invalid value was found. Please check your input values.";
            }

            return validationError;
        },

        submitAlterTest = function (sessionKey, testKey, iterations, opportunities, meanProficiency, sdProficiency, strandCorrelation, handScoreItemTypes) {
            // notify user that all opportunity data will be cleared if test properties are altered.  Deletion of session opportunity data is done on the server
            if (!confirm("All opportunity data will be cleared for the test.  Continue?")) {
                return;
            }

            //config obj for POST transaction
            cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey + "&testkey=" + testKey + "&iterations=" + iterations + "&opportunities=" + opportunities
                    + "&meanproficiency=" + meanProficiency + "&sdproficiency=" + sdProficiency + "&strandcorrelation=" + strandCorrelation
                    + "&handscoreitemtypes=" + handScoreItemTypes,
                on: {
                    complete: function (id, xhr, arguments) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status === "success") {
                            close();
                            alert("Session Test updated successfully.");
                        }
                        else {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to alter the session test.");
                    }
                }
            };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/AlterSessionTest", cfg);
        },

        close = function () {
            Y.one(_strWinAlterTest).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');

            // detach events
            Y.one(_strBtnSubmit).detach();
            Y.one(_strBtnCancel).detach();

            // fire update edit event
            Y.fire('Session_Edit:refresh', {
            //pass addt'l data here
        });
    },

    //event handlers to increase/decrease the opportunity offset (this control will always have a leading +/- sign)
        btnCancel_Click = function (e) {
            e.preventDefault();
            close();
        },

        btnSubmit_Click = function (e, sessionKey, testKey) {
            e.preventDefault();
            
            //retrieve input values
            var iterations = Y.one(_strBtnSimIteration).get('value'),
                opportunities = Y.one(_strBtnSimOpps).get('value'),
                meanProficiency = Y.one(_strBtnSimMean).get('value'),
                sdProficiency = Y.one(_strBtnSimDeviation).get('value'),
                strandCorrelation = Y.one(_strBtnSimStrand).get('value'),				
                handScoreItemTypes = "",
                validationErrorMsg;							
			
                Y.one(_strBtnSimHandScoreItemTypes).get('options').each(function () {
                    var selected = this.get('selected');
                    if (selected){
                        if (handScoreItemTypes === "") {
                            handScoreItemTypes = this.get('value');
                        }
                        else {
                            handScoreItemTypes += ",";
                            handScoreItemTypes += this.get('value');
                        }
                    }
                });
			validationErrorMsg = validateAlterTestParams(iterations, opportunities, meanProficiency, sdProficiency, strandCorrelation, handScoreItemTypes);
            if (validationErrorMsg != null && validateAlterTestParams != '') {                
				alert(validationErrorMsg);
            } else {
                submitAlterTest(sessionKey, testKey, iterations, opportunities, meanProficiency, sdProficiency, strandCorrelation, handScoreItemTypes);
            }
        };

    return {
        open: open
    };
} ();
}, '0.0.1', { requires: ["node", "io"] });