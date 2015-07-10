YUI.add('sessiontest_delete', function (Y) {
    Y.SessionTest_Delete = function () {
        var _strBtnSubmit = "#delete-submit",
            _strBtnCancel = "#delete-cancel",
            _strWinDeleteSim = "#deleteSim",
            _strDivModalContainer = "#modalContainer",
        //data
            _sessionKey,
            _testKey,
            _loadingPanel,

        open = function (sessionKey, testKey, loadingPanel) {
            var winDeleteSim = Y.one(_strWinDeleteSim);

            _sessionKey = sessionKey;
            _testKey = testKey;
            _loadingPanel = loadingPanel;

            winDeleteSim.setStyle('display', 'block');
            Y.one(_strDivModalContainer).removeAttribute('style');

            //attach event handlers
            Y.on('click', btnSubmit_Click, _strBtnSubmit);
            Y.on('click', btnCancel_Click, _strBtnCancel);
        },

        close = function () {
            Y.one(_strWinDeleteSim).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');

            //detach events
            Y.one(_strBtnSubmit).detach();
            Y.one(_strBtnCancel).detach();

            //fire update setup dashboard event
            Y.fire('Session_Edit:refresh', {
            //pass data here if needed
        });
    },

        btnSubmit_Click = function (e) {
            //should de-register the buttonclick to prevent multiple posts?

            //Configuration object for POST transaction 
            var cfg = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status === "success") {
                            close(null, _strWinDeleteSim);
                            alert("Session Test deleted successfully.");
                        }
                        else {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Async call to add Session Tests failed!");
                    }
                }
            };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionTest", cfg);
        },

        btnCancel_Click = function (e) {
            close();
        };

    return {
        open: open
    };
} ();
}, '0.0.1', { requires: ["node", "io", "event"] });