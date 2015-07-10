YUI.add('managedetails', function (Y) {
    Y.ManageDetails = function () {
        var _simTableOddRow,
            _simTableEvenRow,
            _strDivSimManageDetails = "#simManageDetails",
            _strDivModalContainer = "#modalContainer",
            _strTbDescription = '#tbDesc',
            _strDdlLang = '#ddlLang',
            _strTblSimManageDetails = '#tblSimManageDetails',
            _strBtnAddTests = '#edit-sim-addTest',
            _strBtnUpdateTests = '#edit-sim-modifyAll',
            _strBtnEditSave = '#edit-sim-save',
        //data
            _clientName,
            _sessionKey,
            _sessionType,
            _sessionDescription,
            _sessionLanguage,
        //local copy
            _dataSessionTests,

        open = function (sessionKey, clientName, sessionType, sessionDescription, sessionLanguage) {

            // we must have a sessionkey!
            if (sessionKey === null || sessionKey === '') {
                alert('Session Key required');
                return;
            }

            _clientName = clientName;
            _sessionKey = sessionKey;
            _sessionType = sessionType;
            _sessionDescription = sessionDescription;
            _sessionLanguage = sessionLanguage;

            // show the edit sim section
            Y.one(_strDivSimManageDetails).removeAttribute('style');
            Y.one(_strDivModalContainer).removeAttribute('style');

            var simTableTBody = Y.one(_strTblSimManageDetails).one('tbody');
            if (_simTableOddRow == null)
                _simTableOddRow = simTableTBody.one('tr.oddRow').cloneNode(true);
            if (_simTableEvenRow == null)
                _simTableEvenRow = simTableTBody.one('tr.evenRow').cloneNode(true);
            simTableTBody.empty();

            // populate the description control
            Y.one(_strTbDescription).set('value', sessionDescription);
            // populate language control
            Y.one(_strDdlLang).appendChild(Y.Node.create('<option value=\"' + sessionLanguage + '\">' + sessionLanguage + '</option>'));

            // retrieve session tests data
            getSessionTests(sessionKey);

        },

        getSessionTests = function (sessionKey) {
            var callback = {
                on: {
                    success: function (id, xhr) {
                        Y.log("RAW JSON DATA: " + xhr.responseText);
                        var sessionTests;

                        try {
                            sessionTests = Y.JSON.parse(xhr.responseText);
                        } catch (e) {
                            alert("JSON parse failed!");
                            return;
                        }

                        //display errors
                        if (sessionTests.errormsg) {
                            alert(sessionTests.errormsg);
                            return;
                        }

                        _dataSessionTests = Y.clone(sessionTests);

                        //populate the table
                        updateSimTable(sessionTests);
                    },
                    failure: function (id, xhr) {
                        alert('Failed to retrieve Session Tests');
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetSessionTests?sessionkey=" + sessionKey, callback);
        },


        updateSimTable = function (sessionTests) {
            var tableTbody = Y.one(_strTblSimManageDetails).one('tbody'),
                lenSessionTests = sessionTests.length,
                i, tableTrow, tableTcols, totSims;

            tableTbody.empty();
            for (i = 0; i < lenSessionTests; i++) {
                if (i % 2 != 0)
                    tableTrow = _simTableOddRow.cloneNode(true);
                else
                    tableTrow = _simTableEvenRow.cloneNode(true);

                tableTcols = tableTrow.all('td');

                tableTcols.item(0).setContent(sessionTests[i].TestID);
                tableTcols.item(1).setContent(sessionTests[i].Iterations);
                tableTcols.item(2).setContent(sessionTests[i].Opportunities);
                tableTcols.item(3).setContent(sessionTests[i].MeanProficiency);
                tableTcols.item(4).setContent(sessionTests[i].SdProficiency);
                tableTcols.item(5).setContent(sessionTests[i].StrandCorrelation);
                tableTcols.item(6).setContent(sessionTests[i].HandScoreItemTypes);

                totSims = parseInt(sessionTests[i].Iterations) * parseInt(sessionTests[i].Opportunities);
                tableTcols.item(7).setContent(sessionTests[i].Simulations + '/' + totSims);

                tableTcols.item(8).one('select').on('change', function (e) {
                    var btnView = e.currentTarget.next(),
                        reportType = e.currentTarget.get('value'),
                        testID = e.currentTarget.ancestor('tr').one('*').getContent(),
                        testKey, i, len = _dataSessionTests.length, newHref;

                    //get testKey
                    for (i = 0; i < len; i++) {
                        if (_dataSessionTests[i].TestID == testID) {
                            testKey = _dataSessionTests[i].AdminSubject;
                            break;
                        }
                    }

                    //determine what the new href should be
                    newHref = getNewHref(reportType, testKey);
                    btnView.set('href', newHref);

                    switch (reportType) {
                        case 'Summary Stats':
                            btnView.set('target', '_blank');
                            break;
                        case 'Opportunities':
                            break;
                    }
                });

                //eventhandler for reports
                //                tableTcols.item(7).one('a.button.view').on('click', function (e) {
                //                    //run reports on whatever is selected
                //                    var reportType = e.currentTarget.previous().get('value'),
                //                        testID = e.currentTarget.ancestor('tr').one('*').getContent(),
                //                        testKey, i, len = _dataSessionTests.length;

                //                    for (i = 0; i < len; i++) {
                //                        if (_dataSessionTests[i].TestID == testID) {
                //                            testKey = _dataSessionTests[i].AdminSubject;
                //                            break;
                //                        }
                //                    }
                //                    showReport(reportType, testKey);
                //                });

                tableTbody.appendChild(tableTrow);
            }
        },

        getNewHref = function (reportType, testKey) {
            var url = document.URL, newUrl,
                lenPathName, pathNames, newPathName = '';

            //let's rebuild the correct url!
            pathNames = window.location.pathname.substring(1).split('/');
            lenPathName = pathNames.length;

            switch (reportType) {
                case 'Summary Stats':
                    newPathName = '/' + pathNames[0] + '/Reports/SummaryStats.aspx';

                    newUrl = window.location.protocol + "//" + window.location.host + newPathName;
                    break;
                case 'Opportunities':
                    newPathName = '/' + pathNames[0] + "/Services/WebSimXHR.ashx/GetReportOpportunities?sessionkey=" + _sessionKey + "&testkey=" + testKey;
                    newUrl = window.location.protocol + "//" + window.location.host + newPathName;
                    break;
                default:
                    newUrl = window.location.url;
                    break;
            }

            return newUrl;
        };

        //        showReport = function (reportType, testKey) {
        //            switch (reportType) {
        //                case 'Summary Stats':
        //                    var url = document.URL, newUrl,
        //                        lenPathName, pathNames, i, newPathName = '';

        //                    //let's rebuild the correct url!
        //                    pathNames = window.location.pathname.substring(1).split('/');
        //                    lenPathName = pathNames.length;
        //                    pathNames[lenPathName - 2] = "Reports";
        //                    pathNames[lenPathName - 1] = "SummaryStats.aspx";
        //                    for (i in pathNames) {
        //                        newPathName += '/' + pathNames[i];
        //                    }

        //                    newUrl = window.location.protocol + "//" + window.location.host + newPathName;

        //                    //show summary stats report
        //                    window.open(newUrl);
        //                    break;
        //                case 'Opportunities':
        //                    //show opportunities report
        //                    getReportOpportunities(testKey);
        //                    break;
        //            }
        //        },

        //        getReportOpportunities = function (testKey) {
        //            var callback = {
        //                on: {
        //                    success: function (id, xhr) {
        //                        Y.log("RAW JSON DATA: " + xhr.responseText);
        //                        var sessionTests;

        //                        try {
        //                            sessionTests = Y.JSON.parse(xhr.responseText);
        //                        } catch (e) {
        //                            alert("JSON parse failed!");
        //                            return;
        //                        }

        //                        //populate the table
        //                        updateSimTable(sessionTests);
        //                    },
        //                    failure: function (id, xhr) {
        //                        alert('Failed to retrieve Session Tests');
        //                    }
        //                }
        //            }

        //            //            Y.io("Services/WebSimXHR.ashx/GetReportOpportunities?sessionkey=" + _sessionKey + "&testkey=" + testKey, callback);
        //            Y.io("Services/WebSimXHR.ashx/GetReportOpportunities?sessionkey=" + _sessionKey + "&testkey=" + testKey);
        //        };

        return {
            open: open
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "json-parse"] });