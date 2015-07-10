/** 
* The WebSim.Setup module manages the Dashboard for the Web-based Simulator's Setup page 
*/
YUI.add('session_copy', function (Y) {
    Y.CopySession = function () {
        
        // id's that will be used for the module
        var _strWinCopySim = "#copySim",
            _strDivModalContainer = "#modalContainer",
            _strTbSessionName = "#copy-session_name",
            _strTbSessionDesc = "#copy-session_desc",
            _strBtnSubmit = "#copy-submit",
            _strBtnCancel = "#copy-cancel",
            // data
            _fromSessionKey,
            _sessionName,
            _loadingPanel,
        
        // take in session key, session name, loading panel object
        open = function (fromSessionKey, sessionName, loadingPanelRefresh) {
            var sessionDescription = "Copy of " + sessionName,
                sessionName = sessionName + " - Copy";

            _fromSessionKey = fromSessionKey;
            _sessionName = sessionName;
            _loadingPanel = loadingPanelRefresh;

            // show the window
            Y.one(_strWinCopySim).setStyle('display', 'block');
            Y.one(_strDivModalContainer).removeAttribute('style');

            // populate the new copied sessionName field with the session name
            Y.one(_strTbSessionName).set('value', _sessionName);
            // populate the description field with the session name + 'copy of'
            Y.one(_strTbSessionDesc).set('value', sessionDescription);

            // attach event to Copy button
            Y.on("click", btnSubmit_Click, _strBtnSubmit);
            Y.on("click", btnCancel_Click, _strBtnCancel);
        },
        
        close = function () {
            Y.one(_strWinCopySim).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');

            // detach the events
            Y.one(_strBtnSubmit).detach();
            Y.one(_strBtnCancel).detach();

            // fire update setup dashboard event
            Y.fire('SetupDash:refresh', {
                // pass data here if needed
            });
        },

        validateCopyParams = function (fromSessionKey, sessionName, sessionDesc) {
            var isValid = false;

            if (fromSessionKey != null && fromSessionKey != '' 
                && sessionName != null && sessionName != ''
                && sessionDesc != null && sessionDesc != '') 
            {
                isValid = true;
            }

            return isValid;
        },

        submitCopySession = function(fromSessionKey, sessionName, sessionDesc) {
            // should de-register the buttonclick to prevent multiple posts here too?

            // Configuration object for POST transaction 
		    cfg = {
			    method: "POST",
			    data: "fromsessionkey=" + fromSessionKey + "&sessionname=" + sessionName + "&sessiondescription=" + sessionDesc,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function(id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status === "success") {
                            close(null, _strWinCopySim);
                            alert("Session was copied successfully as: " + sessionName);
                        }
                        else { 
                            alert("Failed to copy session: " + response.reason);
                        }
                           
                    },
                    failure: function (id, response) {
                        alert("Failed to copy session.");
                    }
                }
	    };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/CopySession", cfg);
        },
        
        btnSubmit_Click = function (e) {
            var fromSessionKey = _fromSessionKey,
                sessionName = Y.one(_strTbSessionName).get('value'),
                sessionDesc = Y.one(_strTbSessionDesc).get('value');

            if (validateCopyParams(fromSessionKey, sessionName, sessionDesc)) {
                submitCopySession(fromSessionKey, sessionName, sessionDesc);
            } else {
                alert("All fields are required.");
            }
        },
        btnCancel_Click = function (e) {
            close();
        };

        return {
            open: open
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "event"] });