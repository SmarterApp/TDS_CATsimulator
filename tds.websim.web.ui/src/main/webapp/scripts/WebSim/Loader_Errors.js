YUI.add('loadererrors', function (Y) {
    Y.Loader_Errors = function () {
        var _strDivWindow = "#divLoaderErrors",
            _strTblLoaderErrors = "#tblLoaderErrors",
            _strBtnCloseWindow = "#loaderErrors-close",
        // data
            _configsToLoad,
        // loading panel
            _loadConfigLoadingPanel,

        open = function (configsToLoad, loadingPanel) {
            var regexInteger = /^[0-9]+(,[0-9]+)*$/,
                tblLoaderErrors;

            // validate configID
            if (configsToLoad == null || configsToLoad == '' || !regexInteger.test(configsToLoad)) {
                alert("Config IDs must be non-negative integers.");
                return;
            }

            _configsToLoad = configsToLoad;

            // clear table
            tblLoaderErrors = Y.one(_strTblLoaderErrors);
            tblLoaderErrors.one('tbody').empty();
            tblLoaderErrors.scrollIntoView();

            // loading panel setup
            _loadConfigLoadingPanel = loadingPanel;

            // retrieve the loader errors
            getLoaderErrors(configsToLoad);

            // show the window
            Y.one(_strDivWindow).removeAttribute('style');

            // event handler
            Y.on('click', btnCloseWindow_Click, _strBtnCloseWindow);
        },

        getLoaderErrors = function (configsToLoad) {
            var callback = {
                on: {
                    completed: function (id, response) {
                        // loadingpanel hide
                        _loadConfigLoadingPanel.hide();
                    },
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.responseText);
                        var parsedResponse,
                            loaderErrors;

                        try {
                            parsedResponse = Y.JSON.parse(response.responseText);
                        } catch (e) {
                            alert("Failed to parse response.");
                            return;
                        }

                        // check to make sure no issues encountered
                        if (parsedResponse.status === "failed") {
                            alert(parsedResponse.reason);
                            return;
                        } else {
                            loaderErrors = parsedResponse;
                        }

                        updateTblLoaderErrors(loaderErrors);
                    },
                    failure: function (id, response) {
                        alert("Failed to retrieve Loader_Error messages.");
                    }
                }
            };

            // show the loading panel
            _loadConfigLoadingPanel.show();

            Y.io("Services/WebSimXHR.ashx/GetLoaderErrors?configstoload=" + configsToLoad, callback);
        },

        updateTblLoaderErrors = function (loaderErrors) {
            var tblLoaderErrorsTBody = Y.one(_strTblLoaderErrors + ' tbody'),
                lenLoaderErrors = loaderErrors.length,
                i, tableRow;

            tblLoaderErrorsTBody.empty();
            for (i = 0; i < lenLoaderErrors; i++) {
                tableRow = "<tr><td>" + loaderErrors[i].ConfigID
                    + "</td><td>" + loaderErrors[i].Severity
                    + "</td><td>" + loaderErrors[i].Test
                    + "</td><td>" + loaderErrors[i].Error + "</td></tr>";

                tblLoaderErrorsTBody.appendChild(tableRow);
            }
        },

        close = function () {
            _loadConfigLoadingPanel.hide();

            Y.one(_strDivWindow).setStyle('display', 'none');

            // detach events
            Y.one(_strBtnCloseWindow).detach();
        },

        // EVENT HANDLERS
        btnCloseWindow_Click = function (e) {
            e.preventDefault();
            close();
        };

        return {
            open: open
        };
    } ();
}, '0.0.1', { requires: ["node", "io"] });