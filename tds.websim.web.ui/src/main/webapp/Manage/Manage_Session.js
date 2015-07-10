YUI.add('manage_session', function (Y) {
    Y.Manage_Session = function () {
        var _simTableOddRow,
            _simTableEvenRow,
            _divNav = "#navigation",
            _strDdlClientPicker = "#ddlClientPicker",
            _strDivSimManageDetails = "#simManageDetails",
            _strTbDescription = '#tbDesc',
            _strDdlLang = '#ddlLang',
            _strTblSimManageDetails = '#tblSimManageDetails',
            _strBtnAddTests = '#edit-sim-addTest',
            _strBtnUpdateTests = '#edit-sim-modifyAll',
            _strBtnEditSave = '#edit-sim-save',
            _strServerPath = "#serverpath",
        //hf controls to retrieve post data;
            _strHfSessionKey = "#ctl00_MainContent_hfsk",
            _strHfClientName = "#ctl00_MainContent_hfcn",
            _strHfSessionType = "#ctl00_MainContent_hfst",
            _strHfSessionDescription = "#ctl00_MainContent_hfdn",
            _strHfSessionLanguage = "#ctl00_MainContent_hflg",
        //data
            _clientName,
            _sessionKey,
            _sessionType,
            _sessionDescription,
            _sessionLanguage,
        //local copy
            _dataSessionTests,

        init = function () {
            //retrieve values from post
            getPostVars();

            // we must have a sessionkey!
            if (_sessionKey === null || _sessionKey === '') {
                alert('Session Key required');
                return;
            }

            //update ddl
            Y.one(_strDdlClientPicker).appendChild(Y.Node.create('<option>' + Y.one(_strHfClientName).get('value') + '</option>'));
            //update nav links
            updateNavLinks(_clientName);

            var simTableTBody = Y.one(_strTblSimManageDetails).one('tbody');
            if (_simTableOddRow == null)
                _simTableOddRow = simTableTBody.one('tr.oddRow').cloneNode(true);
            if (_simTableEvenRow == null)
                _simTableEvenRow = simTableTBody.one('tr.evenRow').cloneNode(true);
            simTableTBody.empty();

            // populate the description control
            Y.one(_strTbDescription).set('value', _sessionDescription);
            // populate language control
            Y.one(_strDdlLang).appendChild(Y.Node.create('<option value=\"' + _sessionLanguage + '\">' + _sessionLanguage + '</option>'));

            // retrieve session tests data
            getSessionTests(_sessionKey);

            // show the table
            Y.one(_strDivSimManageDetails).removeAttribute('style');
        },

        getPostVars = function () {
            _sessionKey = Y.one(_strHfSessionKey).get('value');
            _clientName = Y.one(_strHfClientName).get('value');
            _sessionType = Y.one(_strHfSessionType).get('value');
            _sessionDescription = Y.one(_strHfSessionDescription).get('value');
            _sessionLanguage = Y.one(_strHfSessionLanguage).get('value');
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

                        // populate the table
                        updateSimTable(sessionTests);

                        // begin to poll the database to update the simulation progress every 30 seconds
                        Y.io.poll(30000, "Services/WebSimXHR.ashx/GetSessionTests?sessionkey=" + sessionKey, {
                            method: 'POST',
                            on: {
                                modified: function (txId, r, args) {
                                    var sessionTests;

                                    try {
                                        sessionTests = Y.JSON.parse(r.responseText);
                                    } catch (e) {
                                        Y.log("Call to retrieve session tests during polling failed.");
                                    }

                                    _dataSessionTests = Y.clone(sessionTests);
                                    updateSimProgress(sessionTests);
                                }
                            }
                        }).start();
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

            // clear table
            tableTbody.empty();
            // alert if no tests found for this session
            if (sessionTests.length == 0) {
                alert("No Tests found for this Session.");
            }
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

                // assign appropriate link
                selectedReport = tableTcols.item(8).one('select').get('value');
                // show in new window if set to one of the following on page load
                if ((selectedReport === "Summary Stats") || (selectedReport == "Field Test Distribution") || (selectedReport == "Item Distribution") || (selectedReport == "Scores")) {
                    tableTcols.item(8).one('a.view').set('target', '_blank');
                }
                tableTcols.item(8).one('a.view').set('href', getNewHref(tableTcols.item(8).one('select').get('value'), sessionTests[i].AdminSubject));

                tableTcols.item(8).one('select').on('change', function (e) {
                    e.preventDefault();

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
                        case 'Field Test Distribution':
                        case 'Item Distribution':
                        case 'Scores':
                        case 'Blueprint Summary':
                        case 'Summary Stats':
                        case 'Items Report':
                        case 'Form Distributions':
                        case 'Opportunities':
                        case 'Test Package':
                            btnView.set('target', '_blank');
                            break;
                    }
                });

                tableTbody.appendChild(tableTrow);
            }
        },

        updateSimProgress = function (sessionTests) {
            var tableTbody = Y.one(_strTblSimManageDetails).one('tbody'),
                lenSessionTests = sessionTests.length,
                i;

            tableTbody.all('tr').each(function () {
                var testId = this.one('td').getContent(),
                    testProgressCol = this.one('td').next().next().next().next().next().next().next(), //get the progress column - should be refactored (possibly create a table module?)
                    totSims;
                for (i = 0; i < lenSessionTests; i++) {
                    if (testId == sessionTests[i].TestID) {
                        totSims = parseInt(sessionTests[i].Iterations) * parseInt(sessionTests[i].Opportunities);
                        testProgressCol.setContent(sessionTests[i].Simulations + '/' + totSims);
                        break;
                    }
                }
            });
        },

        // utility function to create the new urls to retrieve reports
        getNewHref = function (reportType, testKey) {
            var serverPath = Y.one(_strServerPath).get('value'), newUrl;
            if (serverPath != null && serverPath != '') {
                // trim the trailing '/' character
                if (serverPath.charAt(serverPath.length - 1) == '/') {
                    serverPath = serverPath.substr(0, serverPath.length - 1);
                }

                switch (reportType) {
                    case 'Field Test Distribution':
                        newUrl = serverPath + "/Reports/FieldTestDistribution.aspx?&sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    case 'Item Distribution':
                        newUrl = serverPath + "/Reports/ItemDistribution.aspx?&sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    case 'Summary Stats':
                        newUrl = serverPath + "/Reports/SummaryStats.aspx?&sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    case 'Blueprint Summary':
                        newUrl = serverPath + "/Reports/BPSummary.aspx?&sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    case 'Scores':
                        newUrl = serverPath + "/Reports/Scores.aspx?&sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    case 'Opportunities':
                        newUrl = serverPath + "/Reports/Services/WebSimXHR.ashx/GetReportOpportunities?sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    case 'Items Report':
                        newUrl = serverPath + "/Reports/Services/WebSimXHR.ashx/GetReportItems?sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    case 'Form Distributions':
                        newUrl = serverPath + "/Reports/Services/WebSimXHR.ashx/GetReportFormDistributions?sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    case 'Test Package':
                        newUrl = serverPath + "/Reports/Services/WebSimXHR.ashx/GetTestPackage?sessionkey=" + _sessionKey + "&testkey=" + testKey;
                        break;
                    default:
                        newUrl = serverPath;
                        break;
                }
            }
            return newUrl;
        },

        // this will update the clientname querystring value for navigation and breadcrumbs
        updateNavLinks = function (clientname) {
            Y.one(_divNav + ' .leftNav').all('li a').each(function () {
                var href = this.get('href');
                href = href.split('?', 1)[0];
                this.set('href', href + '?&clientname=' + clientname);
            });

            // probably needs to be revisited (separate module?)
            Y.one(_strDivSimManageDetails).all('.breadcrumbs a').each(function () {
                var href = this.get('href');
                href = href.split('?', 1)[0];
                this.set('href', href + '?&clientname=' + clientname);
            });
        };

        return {
            init: init
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "json-parse"] });

YUI({
    modules: {
        manage_session: {
            fullpath: '../Scripts/WebSim/Manage_Session.js',
            requires: ['node', 'io', 'json-parse', 'gallery-io-poller']
        }
    }
}).use("node", "io", "dump", "manage_session", function (Y) {
    var manageSession = Y.Manage_Session;
    manageSession.init();
});