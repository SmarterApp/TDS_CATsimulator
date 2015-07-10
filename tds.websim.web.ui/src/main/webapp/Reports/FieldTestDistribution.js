YUI.add('reportfiedtestdistribution', function (Y) {
    Y.ReportFieldTestDistribution = function () {
            // report tables
        var _strReportTable1 = "#table-report-1",
            //button
            _strButtonExport = "#btnExport",
            // data
            _sessionKey,
            _testKey,

        init = function () {
            getKeys();

            getReport(_sessionKey, _testKey);
            
            // listen for export button click
            Y.one(_strButtonExport).set('target', '_blank');
            Y.one(_strButtonExport).set('href', 'Services/WebSimXHR.ashx/GetReportFieldTestDistributionCSV?sessionkey=' + _sessionKey + '&testkey=' + _testKey);
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
                        
                        _dataBiasTable = summaryStatsReport.Tables[0];
                        _dataCorrelationsTable = summaryStatsReport.Tables[1];
                        _dataBPSatisfactionsTable = summaryStatsReport.Tables[2];
                        _dataOPItemDistributionsTable = summaryStatsReport.Tables[3];
                        _dataRecycledsTable = summaryStatsReport.Tables[4];

                        updateReportTables();
                    },
                    failure: function (id, response) {
                        alert("Error retrieving Field Test Distribution Report. Please make sure the session simulation is complete.");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetReportFieldTestDistribution", cfg);
        },

        updateReportTables = function () {
            updateTable1();
        },

        updateTable1 = function () {
            populateTable(_strReportTable1, _dataBiasTable);
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
        };

        return {
            init: init
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "json-parse", "querystring-parse-simple"] });

YUI().use("node", "io", "dump", "reportfiedtestdistribution", function (Y) {
    var fieldTestDistribution = Y.ReportFieldTestDistribution;
    fieldTestDistribution.init();
});