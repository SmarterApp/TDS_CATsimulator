/** 
* The Session Dashboard module manages the Dashboard for the Web-based Simulator's Setup page 
*/
YUI.add('session_dash', function (Y) {
    /** 
    * The SetupDash class populates all elements related to the Web-based Simulator Setup Page's dashboard.
    */
    Y.Session_Dash = function () {
        var _simTableRow = null,
        // id's that will be used in this module
            _strClientDdl = "#ctl00_MainContent_ddlClientPicker",
            _strSimSetupDiv = "#simSetup",
            _strNewSessionBtn = "#new-session-button",
            _strSimSetupTable = "#tblSimSetup",
            _strBtnRefresh = "#btnRefresh",
            _loadingPanelRefresh,
        // info to send posts
            _strPostForm = "#postForm",
            _strServerPath = "#serverpath",
        // div id for navigation
            _divNav = "#navigation",
        // hf variables
            _strHfClientName = "#ctl00_MainContent_hfcn",
        // data
            _clientName,
            _tooltip,

        // initialize the "module"
        init = function () {
            // retrieve value from hidden variables that are pulled from http posts / session data
            getHiddenVars();

            // grab a deep copy of the simulation table rows before we clear it
            if (_simTableRow == null)
                _simTableRow = Y.one(_strSimSetupTable + ' tbody tr').cloneNode(true);
            Y.one(_strSimSetupTable).one('tbody').empty();

            // update the table and navigation links with the selected clientname
            clientVal = Y.one(_strClientDdl).get('value');
            //don't update if clientvalue is null or blank
            if (clientVal != null || clientVal != '') {
                updateSimTable(clientVal);
                updateNavLinks(clientVal);
            }

            _loadingPanelRefresh = new Y.Panel({
                srcNode: '#refreshLoadingPanel',
                zIndex: 10000,
                centered: true,
                modal: true,
                visible: false,
                render: true,
                buttons: []
            });
            _loadingPanelRefresh.hide();
            Y.one('#refreshLoadingPanel').setStyle('display', 'inherit');

            // show the div display
            Y.one(_strSimSetupDiv).removeAttribute('style');

            //            // initialize and render tooltips http://yuilibrary.com/gallery/show/yui-tooltip
            //            _tooltip = new Y.Tooltip();
            //            _tooltip.render();

            // make sure controls are enabled/visible
            Y.one(_strClientDdl).removeAttribute('disabled');
            Y.one(_strNewSessionBtn).removeAttribute('style');

            // listen for change in dropdown list
            Y.on('change', ddlClient_Change, _strClientDdl);
            // listen for new session button click
            Y.on('click', btnNewSim_Click, _strNewSessionBtn);
            // listen for refresh button click
            Y.on('click', btnRefresh_Click, _strBtnRefresh);

            // listen for refreshing the simtable
            Y.on('SetupDash:refresh', function (data) {
                updateSimTable(Y.one(_strClientDdl).get('value'));
            });
        },

        getHiddenVars = function () {
            _clientName = Y.one(_strHfClientName).get('value');
        },

        // update main dashboard table which lists all of the sessions
        updateSimTable = function updateSimTable(client) {
            var callback = {
                on: {
                    complete: function (id, response) {
                        // wait .5 sec before hiding loading panel
                        var msToWait = 500;
                        setTimeout(function () {
                            _loadingPanelRefresh.hide();
                        }, msToWait);
                    },
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.ResponseText);
                        var simTableTBody = Y.one(_strSimSetupTable + ' tbody'),
                            simTableRow,
                            sessions = [];

                        try {
                            sessions = Y.JSON.parse(response.responseText);
                        }
                        catch (e) {
                            alert("Failed to parse JSON data.");
                            return;
                        }

                        Y.log("PARSED DATA: " + Y.Lang.dump(sessions));

                        //display errors
                        if (sessions.errormsg) {
                            alert(sessions.errormsg);
                            return;
                        }

                        // clear table
                        simTableTBody.empty();
                        // notify if no sessions found
                        if (sessions.length == 0) {
                            // alert if no sessions retrieved
                            alert("No Sessions found for this client.");
                        }
                        // populate table
                        for (var i = 0; i < sessions.length; i++) {
                            simTableRow = _simTableRow.cloneNode(true);
                            if (i % 2 != 0)
                                simTableRow.addClass('oddRow');
                            else
                                simTableRow.addClass('evenRow');

                            populateSimTableRow(simTableRow, sessions[i], client);
                            simTableTBody.appendChild(simTableRow);
                        }
                    },
                    failure: function (id, response) {
                        alert('Failed to retrieve Sessions.');
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetSessions?clientname=" + client, callback);
        },

        // populate the each simulation table row
        populateSimTableRow = function (simTableRow, simTableRowData, clientname) {
            var simTableCols = simTableRow.all('td');

            simTableCols.item(0).setContent(simTableRowData.SessionID);
            simTableCols.item(1).setContent(simTableRowData.Description);
            simTableCols.item(2).setContent(simTableRowData.Sim_Language);
            simTableCols.item(3).setContent(simTableRowData.DateCreated);
            //            simTableCols.item(4).setContent(simTableRowData.Status);
            simTableCols.item(4).setContent(simTableRowData.Sim_Status);
            simTableCols.item(5).setContent(simTableRowData.Sim_Start);
            simTableCols.item(6).setContent(simTableRowData.Sim_Stop);

            simTableCols.item(7).one('a.button.copy').on('click', function (e) {
                e.preventDefault();
                // open a copySim module - pass in sim key, sim name, and the loading panel
                Y.CopySession.open(simTableRowData.Key, simTableRowData.Name,
                    _loadingPanelRefresh);
            });

            simTableCols.item(7).one('a.button.delete').on('click', function (e) {
                e.preventDefault();
                // open a copySim module - pass in key, and loading panel
                Y.DeleteSession.open(simTableRowData.Key, _loadingPanelRefresh);
            });

            simTableCols.item(7).one('a.button.edit').on('click', function (e) {
                e.preventDefault();

                // post to next page
                postToEditSession(simTableRowData.Key, clientname, simTableRowData.SessionType, simTableRowData.Description, simTableRowData.Sim_Language, simTableRowData.SessionID);
            });
        },

        // redirect to the edit page and send required POST data 
        postToEditSession = function (sessionKey, clientName, sessionType, description, language, sessionID) {
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
                postForm.set('action', serverPath + "/Setup/Session_Edit.xhtml");

                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfsk" value="' + sessionKey + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfcn" value="' + clientName + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfst" value="' + sessionType + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfdn" value="' + description + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hflg" value="' + language + '" />'));
                postForm.appendChild(Y.Node.create('<input type="hidden" name="hfsid" value="' + sessionID + '" />'));

                postForm.submit();
            } else {
                alert('Unable to view Edit Session page. Server Path unknown.');
            }
        },

        // this will update the clientname querystring value for navigation and breadcrumbs
        updateNavLinks = function (clientname) {
            Y.one(_divNav + ' .leftNav').all('li a').each(function () {
                var href = this.get('href');
                href = href.split('?', 1)[0];
                this.set('href', href + '?&clientname=' + clientname);
            });
        },


        /*
        *       EVENT HANDLERS
        */

        // handle dropdown client change events
        ddlClient_Change = function (e) {
            e.preventDefault();
            var clientName = Y.one(_strClientDdl).get('value');

            // don't update the table because it will already be done via init method (since dropdown posts back on a change event)
            //            updateSimTable(clientName);

            //update clientname on nav links
            updateNavLinks(clientName);
        },

        // handle new simulation button click event
        btnNewSim_Click = function (e) {
            e.preventDefault();
            var ddlClient = Y.one(_strClientDdl);

            // if client is selected, open the create session popup
            if (ddlClient.get('selectedIndex') === 0)
                alert("You must select a client first!");
            else
                Y.CreateSession.open(ddlClient.get('value'), '#MainContent_hfId');
        },
        btnRefresh_Click = function (e) {
            e.preventDefault();

            // show loading panel
            _loadingPanelRefresh.show();

            updateSimTable(Y.one(_strClientDdl).get('value'));
        };

        return {
            init: init
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "querystring-parse-simple", "panel", "session_copy", "session_delete", "session_create", "gallery-yui-tooltip"] });

YUI({
    modules: {
        session_create: {
            fullpath: '../scripts/WebSim/Session_Create.js',
            requires: ['node', 'io']
        },
        session_copy: {
            fullpath: '../scripts/WebSim/Session_Copy.js',
            requires: ['node', 'io', 'event']
        },
        session_delete: {
            fullpath: '../scripts/WebSim/Session_Delete.js',
            requires: ['node', 'io', 'event']
        }
    }
}).use("node", "io", "dump", "json-parse", "session_dash", function (Y) {
    var sessionDash = Y.Session_Dash;
    sessionDash.init();
});