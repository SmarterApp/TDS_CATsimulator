YUI.add('deletesessoppdata', function (Y) {
    Y.DeleteSessOppData = function () {
        var _strDivDeleteSessData = "#deleteSessionOppData",
            _strDivModalContainer = "#modalContainer",
            _strBtnSubmit = "#delete-opp-submit",
            _strBtnCancel = "#delete-opp-cancel",
            _sessionKey,
            _loadingPanel,

        // takes in session key and loading panel
        open = function (sessionKey, loadingPanel) {

            _sessionKey = sessionKey;
            _loadingPanel = loadingPanel;

            Y.one(_strDivDeleteSessData).setStyle('display', 'block');
            Y.one(_strDivModalContainer).removeAttribute('style');

            //attach event handlers
            Y.on('click', btnSubmit_Click, _strBtnSubmit);
            Y.on('click', btnCancel_Click, _strBtnCancel);
        },

        close = function () {
            Y.one(_strDivDeleteSessData).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');
            //detach events
            Y.one(_strBtnCancel).detach();
            Y.one(_strBtnSubmit).detach();

            //fire event to refresh manage dashboard
            Y.fire('ManageSim:refresh', {
            });
        },

        submitDeleteSessionData = function () {
            var cfg = {
                method: "POST",
                data: "sessionkey=" + _sessionKey,
                on: {
                    complete: function (id, xhr, arguments) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status == "success") {
                            close();
                            alert("Session Opportunity data has been cleared.");
                        } else {
                            alert("Response status: " + response.status + " with reason: " + response.reason);
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionOppData", cfg);
        },

    //event handlers
        btnSubmit_Click = function (e) {
            submitDeleteSessionData();

            //moved from close function
            Y.one(_strDivDeleteSessData).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');
            //detach events
            Y.one(_strBtnCancel).detach();
            Y.one(_strBtnSubmit).detach();
        },
        btnCancel_Click = function (e) {
            close();
        };

    return {
        open: open
    };
} ();
}, '0.0.1', { requires: ["node", "io"] });