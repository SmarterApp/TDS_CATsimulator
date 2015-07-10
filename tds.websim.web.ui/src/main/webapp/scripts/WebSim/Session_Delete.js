YUI.add('session_delete', function (Y) {
    Y.DeleteSession = function () {
        var _strBtnSubmit = "#delete-submit",
            _strBtnCancel = "#delete-cancel",
            _strWinDeleteSim = "#deleteSim",
            _strDivModalContainer = "#modalContainer",
            _loadingPanel,
        //data
            _sessionKey,

        // take in the session key to delete and loading panel object
        open = function (sessionKey, loadingPanel) {
            var winDeleteSim = Y.one(_strWinDeleteSim);

            _sessionKey = sessionKey;
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
            Y.fire('SetupDash:refresh', {
            //pass data here if needed
        });
    },

        btnSubmit_Click = function (e) {
            //Configuration object for POST transaction 
            var cfg = {
                method: "POST",
                data: "sessionkey=" + _sessionKey,
                on: {
                    complete: function (id, xhr, arguments) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status === "success") {
                            close(null, _strWinDeleteSim);
                            alert("Session was deleted successfully.");
                        }
                        else {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to delete session");
                    }
                }
            };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSession", cfg);

            //should de-register the buttonclick to prevent multiple posts?
            Y.one(_strWinDeleteSim).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');

            //detach events
            Y.one(_strBtnSubmit).detach();
            Y.one(_strBtnCancel).detach();
        },

        btnCancel_Click = function (e) {
            close();
        };

    return {
        open: open
    };
} ();
}, '0.0.1', { requires: ["node", "io", "event"] });