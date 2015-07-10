/**
 *  Blueprint Validation module displays validation errors in a new window whenever validation fails
*/
YUI.add('blueprintvalidation', function (Y) {
    Y.BlueprintValidation = function () {
        var _strNumFatals = "#numFatals",
            _strNumWarnings = "#numWarnings",
            _strTblBpSummary = "#tblBpSummary",
        //report table rows
            _trBpSummary,
        //data
            _sessionKey,

        init = function () {
            //retrieve data
            getHiddenData();

            if (_trBpSummary == null) {
                _trBpSummary = Y.one(_strTblBpSummary).one('tbody tr').cloneNode(true);
            }
            Y.one(_strTblBpSummary).one('tbody').empty();

            getBlueprintValidation();
        },

        // retrieve data from hidden fields
        getHiddenData = function () {
            _sessionKey = Y.QueryString.parse(window.location.href).sessionkey;
        },

        // retrieve blueprint validation data from web server
        getBlueprintValidation = function () {
            //Configuration object for POST transaction 
            var blueprintValidationStatus,
            cfg = {
                method: "POST",
                data: "sessionkey=" + _sessionKey,
                on: {
                    success: function (id, xhr, arguments) {
                        Y.log("RAW JSON DATA: " + xhr.responseText);

                        try {
                            blueprintValidationStatus = Y.JSON.parse(xhr.responseText);
                        }
                        catch (e) {
                            alert("Error parsing response data");
                            return;
                        }

                        Y.log("PARSED DATA: " + Y.Lang.dump(blueprintValidationStatus));

                        updateControls(blueprintValidationStatus);
                    },
                    failure: function (id, response) {
                        alert("Async call to add Session Tests failed!");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/ValidateBlueprint", cfg);
        },

        updateControls = function (blueprintValidationStatus) {
            Y.one(_strNumFatals).set('value', blueprintValidationStatus.NumFatals);
            Y.one(_strNumWarnings).set('value', blueprintValidationStatus.NumWarnings);
            updateTable(blueprintValidationStatus);
        },

        updateTable = function (blueprintValidationStatus) {
            var tableTBody = Y.one(_strTblBpSummary).one('tbody'),
                len = blueprintValidationStatus.Errors.length,
                i, tableRow, tableCols;

            tableTBody.empty();
            for (i = 0; i < len; i++) {
                tableRow = _trBpSummary.cloneNode(true);
                if (i % 2 != 0) {
                    tableRow.addClass('oddRow');
                } else {
                    tableRow.addClass('evenRow');
                }

                tableCols = tableRow.all('td');
                tableCols.item(0).setContent(blueprintValidationStatus.Errors[i].Severity);
                tableCols.item(1).setContent(blueprintValidationStatus.Errors[i].Test);
                tableCols.item(2).setContent(blueprintValidationStatus.Errors[i].Error);

                tableTBody.appendChild(tableRow);
            }
        };

        return {
            init: init
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "json-parse", "querystring-parse-simple"] });

YUI().use("node", "io", "dump", "blueprintvalidation", function (Y) {
    var blueprintValidation = Y.BlueprintValidation;
    blueprintValidation.init();
});