/**
*   The Simulation Error module displays error messages in simulations
*/
YUI.add('simulationerror', function (Y) {
    Y.SimulationError = function (sessionKey) {
        var _strDivSimError = "#simulationError",
            _strDivModalContainer = "#modalContainer",
            _strTblSimError = "#tblSimError",
            _strBtnSimErrorClose = "#btnSimErrorClose",
        // local copy of table row
            _trSimError,
        // loading panel
            _loadingPanelConfigErrors,

        open = function (sessionKey, loadingPanel) {
            if (_trSimError == null)
                _trSimError = Y.one(_strTblSimError).one('tbody tr').cloneNode(true);
            Y.one(_strTblSimError).one('tbody').empty();

            // loading panel setup
            _loadingPanelConfigErrors = loadingPanel;

            getSimError(sessionKey);

            Y.on('click', btnSimErrorClose_Click, _strBtnSimErrorClose);
        },

        getSimError = function (sessionKey) {
            //Configuration object for POST transaction 
            var cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey,
                on: {
                    complete: function (id, response) {
                        // loadingpanel hide
                        _loadingPanelConfigErrors.hide();
                    },
                    success: function (id, xhr, arguments) {
                        Y.log("RAW JSON DATA: " + xhr.responseText);

                        Y.one(_strDivSimError).removeAttribute('style');
                        Y.one(_strDivModalContainer).removeAttribute('style');

                        try {
                            simErrorData = Y.JSON.parse(xhr.responseText);
                        }
                        catch (e) {
                            alert("Error parsing response data");
                            return;
                        }

                        Y.log("PARSED DATA: " + Y.Lang.dump(simErrorData));

                        if (simErrorData.length > 0) {
                            updateSimulationErrorTable(simErrorData);
                        } else {
                            alert("Simulation error messages not found.");
                        }
                    },
                    failure: function (id, response) {
                        alert("Unable to retrieve simulation error.");
                    }
                }
            };

            // show the loading panel
            _loadingPanelConfigErrors.show();

            Y.io("Services/WebSimXHR.ashx/GetSimulationErrors", cfg);
        },

        updateSimulationErrorTable = function (simErrorData) {
            var tableTbody = Y.one(_strTblSimError).one('tbody'),
                lenSimErrors = simErrorData.length,
                i, tableTrow, tableTcols;

            tableTbody.empty();
            for (i = 0; i < lenSimErrors; i++) {
                tableTrow = _trSimError.cloneNode(true);
                if (i % 2 != 0)
                    tableTrow.addClass('oddRow');
                else
                    tableTrow.addClass('evenRow');

                tableTcols = tableTrow.all('td');

                tableTcols.item(0).setContent(simErrorData[i].ProcName);
                tableTcols.item(1).setContent(simErrorData[i].NumErrors);

                tableTbody.appendChild(tableTrow);
            }
        },

        close = function () {
            Y.one(_strDivSimError).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');
        },

        /*****
        ******  Event Handlers
        *****/

        btnSimErrorClose_Click = function (e) {
            e.preventDefault();

            Y.one(_strBtnSimErrorClose).detach();

            close();
        };

        return {
            open: open
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "json-parse"] });