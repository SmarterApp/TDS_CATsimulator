/**
*   The Session Edit module allows users to manage an individual session properties, add/remove tests to the session, or edit session test properties.
*/
YUI.add('session_edit', function (Y) {
    Y.Session_Edit = function () {
        var _simTableRow,
        // div id for navigation
            _divNav = "#navigation",

        // controls
            _strEditSim = "#simEdit",
            _strClientDdl = "#ddlClientPicker",
            _strTbDescription = "#tbDesc",
            _strDdlLang = "#ddlLang",
            _strSimTbl = "#tblSimEdit",
            _strBtnAddTests = "#edit-sim-addTest",
            _strBtnUpdateTests = "#edit-sim-modifyAll",
            _strBtnEditSave = "#edit-sim-save",
        //hf controls to retrieve post data;
            _strHfSessionKey = "#ctl00_MainContent_hfsk",
            _strHfClientName = "#ctl00_MainContent_hfcn",
            _strHfSessionType = "#ctl00_MainContent_hfst",
            _strHfSessionDescription = "#ctl00_MainContent_hfdn",
            _strHfSessionLanguage = "#ctl00_MainContent_hflg",
            _strHfSessionID = "#ctl00_MainContent_hfsid",
            _strHfIsNew = "#ctl00_MainContent_hfnew",
        //form to post
            _strPostForm = "#postForm",
            _strServerPath = "#serverpath",
        //data
            _sessionKey,
            _clientName,
            _sessionType,
            _sessionDescription,
            _sessionLanguage,
            _sessionID,
            _isNewSession,
        //loading panel
            _loadingPanel,

        init = function () {
            //retrieve values from post data / session data
            getPostVars();

            //update nav links with clientname
            updateNavLinks(_clientName);

            // we must have a sessionkey!
            if (_sessionKey === null || _sessionKey === '') {
                alert('Session Key required');
                return;
            }

            // set up the loading panel
            _loadingPanel = new Y.Panel({
                srcNode: '#divLoadingPanel',
                zIndex: 10000,  // this is to get it on top of all panels...
                centered: true,
                modal: true,
                visible: false,
                render: true,
                buttons: []
            });
            _loadingPanel.hide();
            Y.one('#divLoadingPanel').setStyle('display', 'inherit');

            var simTableTBody = Y.one(_strEditSim).one('tbody');

            // populate the controls
            Y.one(_strClientDdl).appendChild(Y.Node.create('<option>' + Y.one(_strHfClientName).get('value') + '</option>'));
            Y.one(_strTbDescription).set('value', _sessionDescription);
            Y.one(_strDdlLang).appendChild(Y.Node.create('<option value=\"' + _sessionLanguage + '\">' + _sessionLanguage + '</option>'));

            if (_simTableRow == null)
                _simTableRow = simTableTBody.one('tr').cloneNode(true);
            simTableTBody.empty();

            // if empty string, it is not a new session
            if (_isNewSession == '') {
                // populate the simTable
                updateSimTable();
            }

            // display the table now (after it is populated)
            Y.one(_strEditSim).removeAttribute('style');

            // listeners
            Y.on('click', btnAddTests_Click, _strBtnAddTests, _loadingPanel);
            Y.on('click', btnEditSave_Click, _strBtnEditSave);

            //add eventhandler to refresh the simtable
            Y.on('Session_Edit:refresh', function (data) {
                updateSimTable();
            });
        },

        // retrieve variables from hiddenfields
        getPostVars = function () {
            _sessionKey = Y.one(_strHfSessionKey).get('value');
            _clientName = Y.one(_strHfClientName).get('value');
            _sessionType = Y.one(_strHfSessionType).get('value');
            if (_sessionDescription == null)
                _sessionDescription = Y.one(_strHfSessionDescription).get('value');
            _sessionLanguage = Y.one(_strHfSessionLanguage).get('value');
            _sessionID = Y.one(_strHfSessionID).get('value');
            _isNewSession = Y.one(_strHfIsNew).get('value');
        },

        // retrieve data to update the main table
        updateSimTable = function updateSimTable() {
            var callback = {
                on: {
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.responseText);
                        var simTableTBody = Y.one(_strSimTbl + ' tbody'),
                            simTableRow,
                            sessionTests = [], i;

                        try {
                            sessionTests = Y.JSON.parse(response.responseText);
                        }
                        catch (e) {
                            alert("JSON parse failed!");
                            return;
                        }

                        Y.log("PARSED DATA: " + Y.Lang.dump(sessionTests));

                        // display errors
                        if (sessionTests.errormsg) {
                            alert(sessionTests.errormsg);
                            return;
                        }

                        // clear table
                        simTableTBody.empty();
                        // display alert if no sessionTests found
                        if (sessionTests.length == 0) {
                            alert("No Tests found for this Session.");
                        }
                        // populate table
                        for (i = 0; i < sessionTests.length; i++) {
                            simTableRow = _simTableRow.cloneNode(true);
                            if (i % 2 != 0)
                                simTableRow.addClass('oddRow');
                            else
                                simTableRow.addClass('evenRow');

                            populateSimTableRow(simTableRow, sessionTests[i]);
                            simTableTBody.appendChild(simTableRow);
                        }
                    },
                    failure: function (id, response) {
                        alert('Failed to retrieve Session Tests.');
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetSessionTests?sessionkey=" + _sessionKey, callback);
        },

        // populate each row of the main table
        populateSimTableRow = function (simTableRow, simTableRowData) {
            var simTableCols = simTableRow.all('td');

            simTableCols.item(0).setContent(simTableRowData.TestID);
            simTableCols.item(1).setContent(simTableRowData.Iterations);
            simTableCols.item(2).setContent(simTableRowData.Opportunities);
            simTableCols.item(3).setContent(simTableRowData.MeanProficiency);
            simTableCols.item(4).setContent(simTableRowData.SdProficiency);
            simTableCols.item(5).setContent(simTableRowData.StrandCorrelation);
            simTableCols.item(6).setContent(simTableRowData.HandScoreItemTypes);

            simTableCols.item(7).one('a.button.edit').on('click', function (e) {
                e.preventDefault();
                Y.SessionTest_Alter.open(_sessionKey, _clientName, simTableRowData, _loadingPanel);
            });

            simTableCols.item(7).one('a.button.delete').on('click', function (e) {
                e.preventDefault();
                Y.SessionTest_Delete.open(_sessionKey, simTableRowData.AdminSubject, _loadingPanel);
            });

            simTableCols.item(8).one('a.button.edit').on('click', function (e) {
                e.preventDefault();
                postToBlueprint(simTableRowData.AdminSubject);
            });
        },

        // redirect user to the session blueprint page with accompanying post data
        postToBlueprint = function (testKey) {
            var postForm = Y.one(_strPostForm),
                serverPath = Y.one(_strServerPath).get('value');

            if (serverPath != null && serverPath != '') {
                // trim the trailing '/' character
                if (serverPath.charAt(serverPath.length - 1) == '/') {
                    serverPath = serverPath.substr(0, serverPath.length - 1);
                }
                //clear it first
                postForm.empty();

                postForm.set('method', 'post');
                postForm.set('target', '_self');
                postForm.set('action', serverPath + "/Setup/Session_Blueprint.xhtml");

                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfsk" value="' + _sessionKey + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hftk" value="' + testKey + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfcn" value="' + _clientName + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfsid" value="' + _sessionID + '" />'));

                postForm.submit();
            }
        },

        // this will update the clientname querystring value for navigation and breadcrumbs
        updateNavLinks = function (clientname) {
            Y.one(_divNav + ' .leftNav').all('li a').each(function () {
                var href = this.get('href');
                href = href.split('?', 1)[0];
                this.set('href', href + '?&clientname=' + clientname);
            });

            // probably needs to be revisited (separate module?)
            Y.one(_strEditSim).all('.breadcrumbs a').each(function () {
                var href = this.get('href');
                href = href.split('?', 1)[0];
                this.set('href', href + '?&clientname=' + clientname);
            });
        },

        saveSessionDescription = function (description) {
            var cfg = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&description=" + description,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status === "success") {
                            _sessionDescription = description;
                            alert("Session description update successful.");
                        }
                        else {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to update session description!");
                    }
                }
            };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/SetSessionDescription", cfg);
        },

        // handle click event for the "Add New Tests" button
        btnAddTests_Click = function (e) {
            e.preventDefault();
            // pass session key, clientname, session type, session language, and loading panel object
            Y.SessionTests_Add.open(_sessionKey, _clientName, _sessionType, _sessionLanguage, _loadingPanel);
        },

        // handle click event for the "Save" button
        btnEditSave_Click = function (e) {
            e.preventDefault();

            var description = Y.one(_strTbDescription).get('value');

            // validate
            if (description != null && description != '') {
                saveSessionDescription(description);
            } else {
                alert("Description cannot be blank.");
            }
        };

        return {
            init: init
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "querystring-parse-simple", "panel", "sessiontests_add", "sessiontest_alter", "sessiontest_delete"] });

YUI({
    modules: {
        sessiontests_add: {
            fullpath: '../scripts/WebSim/SessionTests_Add.js',
            requires: ['node', 'io']
        },
        sessiontest_alter: {
            fullpath: '../scripts/WebSim/SessionTest_Alter.js',
            requires: ['node', 'io']
        },
        sessiontest_delete: {
            fullpath: '../scripts/WebSim/SessionTest_Delete.js',
            requires: ['node', 'io']
        }
    }
}).use("node", "io", "dump", "json-parse", "session_edit", function (Y) {
    var sessionEdit = Y.Session_Edit;
    sessionEdit.init();
});