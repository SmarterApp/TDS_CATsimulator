YUI.add('reportscores', function (Y) {
    Y.ReportScores = function () {
            //ids
        var _strDivReport = "#reportSummaryStats",
            _strBtnClose = "#button-close",
            //report tables
            _strReportTable3 = "#table-report-2",
            //button
            _strButtonExport = "#btnExport",
            //loading panel
            _loadingPanel,
            //data
            _sessionKey,
            _testKey,
            _dataScores,

        init = function () {
            getKeys();

            _loadingPanel = new Y.Panel({
                srcNode: '#loadingPanel',
                zIndex: 5,
                centered: true,
                modal: true,
                visible: false,
                render: true,
                buttons: []
            });
            _loadingPanel.hide();
            Y.one('#loadingPanel').setStyle('display', 'inherit');

            getReport(_sessionKey, _testKey);

            // listen for export button click
            Y.one(_strButtonExport).set('target', '_blank');
            Y.one(_strButtonExport).set('href', 'Services/WebSimXHR.ashx/GetReportScoresCSV?sessionkey=' + _sessionKey + '&testkey=' + _testKey);
        },

        getKeys = function () {
            _sessionKey = Y.QueryString.parse(window.location.href).sessionkey;
            _testKey = Y.QueryString.parse(window.location.href).testkey;
        },

        getReport = function () {
            //Configuration object for POST transaction 
            var cfg = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
                on: {
                    complete: function(id, xhr, arguments) {
                        _loadingPanel.hide();
                    },
                    success: function(id, xhr, arguments) {
                        Y.log("RAW JSON DATA: " + xhr.responseText);

                        try {
                            summaryStatsReport = Y.JSON.parse(xhr.responseText);
                        }
                        catch (e) {
                            alert("Error parsing response data");
                            return;
                        }

                        Y.log("PARSED DATA: " + Y.Lang.dump(summaryStatsReport));

                        // TODO: check for errors
                        if (summaryStatsReport.Tables.length < 0) {
                            alert('Failed: Reports not ready yet. Has the simulation finished?');
                            return;
                        }
                        if (summaryStatsReport.Tables[0].TableRows[0] != null && summaryStatsReport.Tables[0].TableRows[0].ColVals[0] == 'failed') {
                            alert('Failed: ' + summaryStatsReport.Tables[0].TableRows[0].ColVals[1]);
                            return;
                        }
                        
                        _dataScores = summaryStatsReport.Tables[0];

                        updateScoresTable();
                    },
                    failure: function (id, response) {
                        alert("Error retrieving Summary Stats Report. Please make sure the simulation is complete.");
                    }
                }
            };

            _loadingPanel.show();

            Y.io("Services/WebSimXHR.ashx/GetReportScores", cfg);
        },

        updateScoresTable = function () {
            populateTable(_strReportTable3, _dataScores);
        },

        // method to populate tables with the data container object
        populateTable = function (tableID, dataSet) {
            var table = Y.one(tableID),
                len, len2, i, j, tableRow;
            
            // clear table
            table.empty();
            // add table headers
            tableRow = "<thead>";
            len = dataSet.TableHeaders.length;
            for (i = 0; i < len; i++) {
                tableRow += "<th>" + dataSet.TableHeaders[i] + "</th>";
            }
            tableRow += "</thead>";
            table.appendChild(tableRow);
            tableRow = "<tbody>";
            len = dataSet.TableRows.length;
            for (i = 0; i < len; i++) {
                len2 = dataSet.TableRows[i].ColVals.length;
                tableRow += "<tr>";
                for (j = 0; j < len2; j++) {
                    tableRow += "<td>" + dataSet.TableRows[i].ColVals[j] + "</td>";
                }
                tableRow += "</tr>";
            }
            tableRow += "</tbody>";
            table.appendChild(tableRow);
        },

        close = function () {
            Y.one(_strDivReport).setStyle('display', 'none');

            //detach events
            Y.one(_strBtnClose).detach();
        };

        return {
            init: init
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "json-parse", "querystring-parse-simple", "panel"] });

YUI().use("node", "io", "dump", "reportscores", function (Y) {
    var reportScores = Y.ReportScores;
    reportScores.init();
});