/** 
 * The WebSim.Setup module manages the Dashboard for the Web-based Simulator's Setup page 
 */
YUI.add('manage_sessions', function (Y) {
    /** 
    * The SetupDash class populates all elements related to the Web-based Simulator Setup Page's dashboard.
    */
    Y.ManageSessions = function () {
        var _simTableOddRow,
            _simTableEvenRow,
            _divNav = "#navigation",
            _strDdlClientPicker = "#ctl00_MainContent_ddlClientPicker",
            _strSimTableTBody = "#simManage tbody",
            _strDivManage = "#simManage",
            _strDivManageDetails = "#simManageDetails",
            _strBtnRefresh = "#btnRefresh",
            _strDivSimulationError = "#simulationError",
        // form to post
            _strPostForm = "#postForm",
            _strServerPath = "#serverpath",
        // hf variables
            _strHfClientName = "#ctl00_MainContent_hfcn",
        // data
            _clientName,
            _dataSessions,
        //loadingPanel
            _loadingPanel,

        init = function () {
            // retrieve value from hidden variables that are pulled from http posts / session data
            getHiddenVars();

            var simTableTBody = Y.one(_strSimTableTBody);
            // grab a deep copy of the simulation table rows before we clear it
            if (_simTableOddRow == null)
                _simTableOddRow = simTableTBody.one('tr.oddRow').cloneNode(true);
            if (_simTableEvenRow == null)
                _simTableEvenRow = simTableTBody.one('tr.evenRow').cloneNode(true);
            simTableTBody.empty();

            getSessions(_clientName);
            updateNavLinks(_clientName);

            // remove the display:none styling for the loading panel
            _loadingPanel = new Y.Panel({
                srcNode: '#simLoadingPanel',
                zIndex: 10000,
                centered: true,
                modal: true,
                visible: false,
                render: true,
                buttons: []
            });
            _loadingPanel.hide();
            Y.one('#simLoadingPanel').setStyle('display', 'inherit');

            // show the div display
            Y.one(_strDivManage).removeAttribute('style');

            // listen for refresh button click
            Y.on('click', btnRefresh_Click, _strBtnRefresh);
            // listen for change in client dropdown
            Y.on('change', ddlClientPicker_Change, _strDdlClientPicker);

            // listen for refreshing the table
            Y.on('Manage_Sessions:refresh', function (data) {
                getSessions(Y.one(_strDdlClientPicker).get('value'));
            });
        },

        getHiddenVars = function () {
            _clientName = Y.one(_strHfClientName).get('value');
        },

        ddlClientPicker_Change = function (e) {
            _clientName = e.currentTarget.get('value');
            // update clientname on nav links
            updateNavLinks(_clientName);
            // don't get sessions again since init method will handle updating the sessions table when ddl onchange event is fired from the postback
            //            getSessions(_clientName);
        },

        getSessions = function (clientName) {
            var callback = {
                on: {
                    complete: function (id, response) {
                        // wait .5 sec before hiding loading panel
                        var msToWait = 500;
                        setTimeout(function () {
                            _loadingPanel.hide();
                        }, msToWait);
                    },
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.ResponseText);
                        var sessions = [];

                        try {
                            sessions = Y.JSON.parse(response.responseText);
                        }
                        catch (e) {
                            alert("JSON parse failed!");
                            return;
                        }

                        Y.log("PARSED DATA: " + Y.Lang.dump(sessions));

                        //display errors
                        if (sessions.errormsg) {
                            alert(sessions.errormsg);
                            return;
                        }

                        //copy to mem
                        _dataSessions = Y.clone(sessions);

                        //else, populate table after clearing
                        updateSimTable(sessions);
                    },
                    failure: function (id, response) {
                        alert('Failed to retrieve Sessions.');
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetSessions?clientname=" + clientName, callback);
        },

        updateSimTable = function (sessionsData) {
            var simTableTBody = Y.one(_strSimTableTBody),
                len = sessionsData.length,
                i, simTableRow,
                runButton, cancelButton, detailButton, clearSimButton;

            // clear table
            simTableTBody.empty();
            // display alert if no Sessions found
            if (len == 0) {
                alert("No Sessions found for this client.");
            }

            for (i = 0; i < len; i++) {
                if (i % 2 != 0)
                    simTableRow = _simTableOddRow.cloneNode(true);
                else
                    simTableRow = _simTableEvenRow.cloneNode(true);

                simTableRow = updateSimTableRow(simTableRow, sessionsData[i]);

                simTableTBody.appendChild(simTableRow);
            }
        },

        updateSimTableRow = function (simTableRow, rowData) {
            var simTableCols = simTableRow.all('td'),
            runButton, cancelButton, detailButton, clearSimButton, publishButton, publishPackagesButton;
            simTableCols.item(0).setContent(rowData.SessionID);
            simTableCols.item(1).setContent(rowData.Description);
            simTableCols.item(2).setContent(rowData.Sim_Language);
            simTableCols.item(3).setContent(rowData.DateCreated);
            //                simTableCols.item(4).setContent(sessionsData[i].Status);
            // hack: change the status to "aborting" if the sim_status is "running", but the abort flag is set
            if (rowData.Sim_Status == "running" && rowData.Sim_Abort == "True") {
                simTableCols.item(4).setContent("aborting...refresh to update status");
            } else if (rowData.Sim_Status == "error") {
                simTableCols.item(4).setContent('<a href="#">error</a>');
                simTableCols.item(4).one('a').on('click', function (e) {
                    Y.SimulationError.open(rowData.Key, _loadingPanel);
                });
            } else {
                simTableCols.item(4).setContent(rowData.Sim_Status);
            }
            simTableCols.item(5).setContent(rowData.Sim_Start);
            simTableCols.item(6).setContent(rowData.Sim_Stop);

            //show and hide buttons based on status
            runButton = simTableCols.item(7).one('a.button.run');
            cancelButton = simTableCols.item(7).one('a.button.cancel');
            detailButton = simTableCols.item(8).one('a.button.view');
            clearSimButton = simTableCols.item(8).one('a.button.delete');
            publishButton = simTableCols.item(8).one('a.button.publish');
            publishPackagesButton = simTableCols.item(8).one('a.button.publishpackages');

            // if currently running, and the simulation has been aborted
            if (rowData.Sim_Status == "running" && rowData.Sim_Abort == "True") {
                runButton.setStyle('display', 'none');
                cancelButton.setStyle('display', 'none');
                clearSimButton.setStyle('display', 'none');
                publishButton.setStyle('display', 'none');
                publishPackagesButton.setStyle('display', 'none');
            }
            // else if currently running and simulation has not been aborted
            else if (rowData.Sim_Status == "running" && rowData.Sim_Abort == "False") {
                runButton.setStyle('display', 'none');
                cancelButton.removeAttribute('style');
                clearSimButton.setStyle('display', 'none');
                publishButton.setStyle('display', 'none');
                publishPackagesButton.setStyle('display', 'none');
            }
            else {
                runButton.removeAttribute('style');
                cancelButton.setStyle('display', 'none');
                clearSimButton.removeAttribute('style');
                publishButton.removeAttribute('style');
                publishPackagesButton.removeAttribute('style');
            }

            Y.on('click', runSimulation_Click, runButton);
            Y.on('click', cancelSimulation_Click, cancelButton);
            Y.on('click', simDetails_Click, detailButton);
            Y.on('click', clearSimulation_Click, clearSimButton);
            Y.on('click', publishButton_Click, publishButton);
            Y.on('click', publishPackagesButton_Click, publishPackagesButton);


            return simTableRow;
        },

        // this will update the clientname querystring value for navigation and breadcrumbs
        updateNavLinks = function (clientname) {
            Y.one(_divNav + ' .leftNav').all('li a').each(function () {
                var href = this.get('href');
                href = href.split('?', 1)[0];
                this.set('href', href + '?&clientname=' + clientname);
            });
        },

        postToDetails = function (sessionKey, clientName, sessionType, sessionDescription, sessionLanguage, sessionID) {
            var postForm = Y.one(_strPostForm),
                serverPath = Y.one(_strServerPath).get('value');

            if (serverPath != null && serverPath != '') {
                // trim the trailing '/' character
                if (serverPath.charAt(serverPath.length - 1) == '/') {
                    serverPath = serverPath.substr(0, serverPath.length - 1);
                }

                //clear
                postForm.empty();

                postForm.set('method', 'post');
                postForm.set('target', '_self');
                postForm.set('action', serverPath + "/Manage/Manage_Session.aspx");

                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfsk" value="' + sessionKey + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfcn" value="' + clientName + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfst" value="' + sessionType + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfdn" value="' + sessionDescription + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hflg" value="' + sessionLanguage + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfsid" value="' + sessionID + '" />'));

                postForm.submit();
            }
        },

        startSimulation = function (sessionKey) {
            // should de-register the buttonclick to prevent multiple posts?

            // Configuration object for POST transaction 
            var cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey,
                on: {
                    complete: function (id, xhr, arguments) {
                        var msToWait = 2500;
                        setTimeout(function () {
                            _loadingPanel.hide();
                            Y.fire("Manage_Sessions:refresh", {});
                        }, msToWait);
                    },
                    success: function (id, xhr, arguments) {
                        //hide the panel and refresh no matter what the response
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status !== "success") {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to start the simulation!");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/RunSimulation", cfg);
        },

        publishSession = function (sessionKey) {
            // possibly need to de-register the buttonclick to prevent multiple clicks, but the loading panel should be up and running...

            var cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey,
                on: {
                    complete: function (id, xhr, arguments) {
                        var msToWait = 2500;
                        setTimeout(function () {
                            _loadingPanel.hide();
                            Y.fire("Manage_Sessions:refresh", {});
                        }, msToWait);
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status !== "success") {
                            alert("Failed: " + response.reason);
                        } else {
                            alert("Session published successfully!");
                        }
                    },
                    failure: function (id, xhr, arguments) {
                        alert("Failed to publish the session!");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/PublishSession", cfg);
        },
	
	publishSessionTestPackages = function (sessionKey) {
            // psossibly need to de-register the buttonclick to prevent multiple clicks, but the loading panel should be up and running...

            var cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey,
                on: {
                    complete: function (id, xhr, arguments) {
                        var msToWait = 2500;
                        setTimeout(function () {
                            _loadingPanel.hide();
                            Y.fire("Manage_Sessions:refresh", {});
                        }, msToWait);
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status !== "success") {
                            alert("Failed: " + response.reason);
                        } else {
                            alert("Session published successfully!");
                        }
                    },
                    failure: function (id, xhr, arguments) {
                        alert("Failed to publish the session!");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/PublishSessionTestPackages", cfg);
        },	
        cancelSimulation = function (sessionKey) {
            // should de-register the buttonclick to prevent multiple posts?

            // Configuration object for POST transaction 
            var cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey,
                on: {
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status === "success") {
                            alert("Simulation cancelled");
                            Y.fire("Manage_Sessions:refresh", {});
                        }
                        else {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to start the simulation!");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/CancelSimulation", cfg);
        },

        ValidateBlueprint_StartSim = function (sessionKey) {
            var cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey,
                on: {
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.Status == "failed" && parseInt(response.NumFatals, 10) > 0) {
                            // hide the loading panel (close it since simulation will not run)
                            _loadingPanel.hide();

                            alert("Blueprint is invalid.\nSimulation will not run until all errors are fixed!");

                            var serverPath = Y.one(_strServerPath).get('value'), newPathName;
                            if (serverPath != null && serverPath != '') {
                                // trim the trailing '/' character
                                if (serverPath.charAt(serverPath.length - 1) == '/') {
                                    serverPath = serverPath.substr(0, serverPath.length - 1);
                                }

                                newPathName = serverPath + "/Reports/BlueprintValidation.aspx?&sessionkey=" + sessionKey;

                                window.open(newPathName);
                            } else {
                                alert("Unable to view Blueprint Validation Report - ServerPath not found.");
                            }
                        } else {
                            startSimulation(sessionKey);
                        }
                    },
                    failure: function (id, xhr) {
                        _loadingPanel.hide();
                        alert("Failed to run Blueprint Validation procedure.");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/ValidateBlueprint", cfg);
        },

        ValidateBlueprint_PublishSession = function (sessionKey, sessionID) {
            var cfg = {
                method: "POST",
                data: "sessionKey=" + sessionKey,
                on: {
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.Status == "failed" && parseInt(response.NumFatals, 10) > 0) {
                            //hide the loading panel (close since we will not continue)
                            _loadingPanel.hide();

                            alert("Blueprint is invalid. \nSimulation will not run until all errors are fixed!");

                            var serverPath = Y.one(_strServerPath).get('value'), newPathName;
                            if (serverPath != null && serverPath != '') {
                                // trim the trailing '/' character
                                if (serverPath.charAt(serverPath.length - 1) == '/') {
                                    serverPath = serverPath.substr(0, serverPath.length - 1);
                                }

                                newPathName = serverPath + "/Reports/BlueprintValidation.aspx?&sessionkey=" + sessionKey;

                                window.open(newPathName);
                            } else {
                                alert("Unable to view the Blueprint Validation Report - ServerPath not found.");
                            }
                        } else {
                            publishSession(sessionKey);
                        }
                    },
                    failure: function (id, xhr) {
                        _loadingPanel.hide();
                        alert("Failed to run Blueprint Validation Procedure.");
                    }
                }
            };

            if (confirm("Are you sure you want to publish Session: " + sessionID + " to ITS?\nNOTE: If you have published this session before, it will be overwritten.")) {
                Y.io("Services/WebSimXHR.ashx/ValidateBlueprint", cfg);
            }
        },
	
	ValidateBlueprint_PublishSessionTestPackages = function (sessionKey, sessionID) {
            var cfg = {
                method: "POST",
                data: "sessionKey=" + sessionKey,
                on: {
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.Status == "failed" && parseInt(response.NumFatals, 10) > 0) {
                            //hide the loading panel (close since we will not continue)
                            _loadingPanel.hide();

                            alert("Blueprint is invalid. \nSimulation will not run until all errors are fixed!");

                            var serverPath = Y.one(_strServerPath).get('value'), newPathName;
                            if (serverPath != null && serverPath != '') {
                                // trim the trailing '/' character
                                if (serverPath.charAt(serverPath.length - 1) == '/') {
                                    serverPath = serverPath.substr(0, serverPath.length - 1);
                                }

                                newPathName = serverPath + "/Reports/BlueprintValidation.aspx?&sessionkey=" + sessionKey;

                                window.open(newPathName);
                            } else {
                                alert("Unable to view the Blueprint Validation Report - ServerPath not found.");
                            }
                        } else {
                            publishSessionTestPackages(sessionKey);
                        }
                    },
                    failure: function (id, xhr) {
                        _loadingPanel.hide();
                        alert("Failed to run Blueprint Validation Procedure.");
                    }
                }
            };

            if (confirm("Are you sure you want to publish test packages for the session: " + sessionID + "\nNOTE: If you have published this session before, it will be overwritten.")) {
                Y.io("Services/WebSimXHR.ashx/ValidateBlueprint", cfg);
            }
        },


        /*
        *       EVENT HANDLERS
        */
        runSimulation_Click = function (e) {
            e.preventDefault();

            _loadingPanel.show();

            var sessionID = e.currentTarget.ancestor('tr').one('*').getContent(),
            i, len = _dataSessions.length, sessionKey;

            for (i = 0; i < len; i++) {
                if (_dataSessions[i].SessionID == sessionID) {
                    sessionKey = _dataSessions[i].Key;
                    break;
                }
            }

            ValidateBlueprint_StartSim(sessionKey);
        },
        cancelSimulation_Click = function (e) {
            e.preventDefault();

            var sessionID = e.currentTarget.ancestor('tr').one('*').getContent(),
                i, len = _dataSessions.length, sessionKey;

            for (i = 0; i < len; i++) {
                if (_dataSessions[i].SessionID == sessionID) {
                    sessionKey = _dataSessions[i].Key;
                    break;
                }
            }

            cancelSimulation(sessionKey);
        },
        clearSimulation_Click = function (e) {
            e.preventDefault();

            var sessionID = e.currentTarget.ancestor('tr').one('*').getContent(),
                i, len = _dataSessions.length, sessionKey;

            for (i = 0; i < len; i++) {
                if (_dataSessions[i].SessionID == sessionID) {
                    sessionKey = _dataSessions[i].Key;
                    break;
                }
            }
            Y.DeleteSessOppData.open(sessionKey, _loadingPanel);
        },
        publishButton_Click = function (e) {
            e.preventDefault();

            _loadingPanel.show();

            var sessionID = e.currentTarget.ancestor('tr').one('*').getContent(),
                i, len = _dataSessions.length, sessionKey;

            for (i = 0; i < len; i++) {
                if (_dataSessions[i].SessionID == sessionID) {
                    sessionKey = _dataSessions[i].Key;
                    break;
                }
            }

            ValidateBlueprint_PublishSession(sessionKey, sessionID);
        },
	publishPackagesButton_Click = function (e) {
            e.preventDefault();

            _loadingPanel.show();

            var sessionID = e.currentTarget.ancestor('tr').one('*').getContent(),
                i, len = _dataSessions.length, sessionKey;

            for (i = 0; i < len; i++) {
                if (_dataSessions[i].SessionID == sessionID) {
                    sessionKey = _dataSessions[i].Key;
                    break;
                }
            }

            ValidateBlueprint_PublishSessionTestPackages(sessionKey, sessionID);
        },
	
        simDetails_Click = function (e) {
            e.preventDefault();

            var sessionID = e.currentTarget.ancestor('tr').one('*').getContent(),
                i, len = _dataSessions.length, sessionKey, sessionType, sessionDescription, sessionLanguage;

            for (i = 0; i < len; i++) {
                if (_dataSessions[i].SessionID == sessionID) {
                    sessionKey = _dataSessions[i].Key;
                    sessionType = _dataSessions[i].SessionType;
                    sessionDescription = _dataSessions[i].Description;
                    sessionLanguage = _dataSessions[i].Sim_Language;
                    break;
                }
            }

            // redirect to manage session details page with the accompanying data via http post
            postToDetails(sessionKey, _clientName, sessionType, sessionDescription, sessionLanguage, sessionID);
        },
        btnRefresh_Click = function (e) {
            e.preventDefault();

            // show loading panel
            _loadingPanel.show();

            getSessions(Y.one(_strDdlClientPicker).get('value'));
        };

        return {
            init: init
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "querystring-parse-simple", "json-parse", "panel", "managedetails", "deletesessoppdata"] });


YUI({
    modules: {
        managesession: {
            fullpath: '../scripts/WebSim/Manage_Session.js',
            requires: ['node', 'io', 'json-parse']
        },
        deletesessoppdata: {
            fullpath: '../scripts/WebSim/DeleteSessionData.js',
            requires: ['node', 'io', 'json-parse']
        },
        simulationerror: {
            fullpath: '../scripts/WebSim/SimulationError.js',
            requires: ['node', 'io', 'json-parse']
        }
    }
}).use("node", "io", "dump", "manage_sessions", "simulationerror", function (Y) {
    var manageSessions = Y.ManageSessions;
    manageSessions.init();
});