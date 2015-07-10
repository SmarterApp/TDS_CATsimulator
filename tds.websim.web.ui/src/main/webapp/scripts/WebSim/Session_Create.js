/*
*   Module to create a new session.  This will process all events on the create new session window
*/
YUI.add('session_create', function (Y) {
    Y.CreateSession = function () {
        var _strNewSimWindow = "#newSim",
            _strDivModalContainer = "#modalContainer",
            _strTbSimName = "#new-simName",
            _strTbSimDesc = "#new-simDesc",
            _strDdlSimType = "#new-simType",
            _strDdlSimLang = "#new-simLang",
            _strBtnSubmit = "#new-submit",
            _strBtnCancel = "#new-cancel",
        // needed for redirect
            _strPostForm = "#postForm",
            _strServerPath = "#serverpath",

        // copy of data
            _dataSessionLanguages,
        // other data
            _clientName,

        open = function (clientname) {

            // display the window
            Y.one(_strNewSimWindow).setStyle('display', 'block');
            Y.one(_strDivModalContainer).removeAttribute('style');

            _clientName = clientname;

            // retrieve language data to populate dropdown for both sessionTypes
            getLanguages(clientname);

            // clear fields
            Y.one(_strTbSimName).set('value', '');
            Y.one(_strTbSimDesc).set('value', '');

            // attach event handlers
            Y.on('change', ddlSimType_Change, _strDdlSimType);
            Y.on('click', btnSubmit_Click, _strBtnSubmit);
            Y.on('click', btnCancel_Click, _strBtnCancel);
        },
        // retrieve list of languages to populate language dropdown control
        getLanguages = function () {
            var callback = {
                on: {
                    success: function (id, xhr) {
                        Y.log("RAW JSON DATA: " + xhr.responseText);
                        var sessionLanguages;

                        try {
                            sessionLanguages = Y.JSON.parse(xhr.responseText);
                        }
                        catch (e) {
                            alert("JSON parse failed!");
                            return;
                        }

                        if (sessionLanguages.length == 0) {
                            alert("No Session Languages found.");
                        }

                        //clone the session languages data to local copy
                        _dataSessionLanguages = Y.clone(sessionLanguages);

                        Y.log("PARSED DATA: " + Y.Lang.dump(sessionLanguages));

                        //populate the controls
                        updateLanguageDropdownControl();
                    },
                    failure: function (id, xhr) {
                        alert("Failed to retrieve the list of languages");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetDistinctLanguages?clientname=" + _clientName + "&sessiontype=" + Y.one(_strDdlSimType).get('value'), callback);
        },
        // populate the language dropdown control
        updateLanguageDropdownControl = function () {
            var ddlLanguage = Y.one(_strDdlSimLang),
                len = _dataSessionLanguages.length,
                i, optn;

            ddlLanguage.empty();

            for (i = 0; i < len; i++) {
                optn = Y.Node.create('<option value=\"' + _dataSessionLanguages[i].LanguageCode + '\">' + _dataSessionLanguages[i].Language + '</option>');
                ddlLanguage.appendChild(optn);
            }
        },

        // close the popup window
        close = function () {
            Y.one(_strNewSimWindow).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');

            //detach events
            Y.one(_strBtnSubmit).detach();
            Y.one(_strBtnCancel).detach();
            Y.one(_strDdlSimType).detach();

            //fire update setup dashboard event
            Y.fire('SetupDash:refresh', {
            //pass data here if needed
        });
    },

    // redirect user to the edit page and send accompanying data via http post
            postToEditSession = function (sessionKey, clientName, sessionType, description, language, sessionID) {
                var postForm = Y.one(_strPostForm),
                    serverPath = Y.one(_strServerPath).get('value');

                if (serverPath != null && serverPath != '') {
                    //clear
                    postForm.empty();

                    postForm.set('method', 'post');
                    postForm.set('target', '_self');
                    postForm.set('action', serverPath + "/Setup/Session_Edit.aspx");

                    postForm.appendChild(Y.Node.create('<input type="hidden" name="hfsk" value="' + sessionKey + '" />'));
                    postForm.appendChild(Y.Node.create('<input type="hidden" name="hfcn" value="' + clientName + '" />'));
                    postForm.appendChild(Y.Node.create('<input type="hidden" name="hfst" value="' + sessionType + '" />'));
                    postForm.appendChild(Y.Node.create('<input type="hidden" name="hfdn" value="' + description + '" />'));
                    postForm.appendChild(Y.Node.create('<input type="hidden" name="hflg" value="' + language + '" />'));
                    postForm.appendChild(Y.Node.create('<input type="hidden" name="hfsid" value="' + sessionID + '" />'));
                    postForm.appendChild(Y.Node.create('<input type="hidden" name="hfnew" value="' + 1 + '" />'));

                    postForm.submit();
                }
            },

            validateCreateSessionParams = function (sessionName, language, sessionDescription, sessionType) {
                var isValid = false;

                if (sessionName != null && sessionName.length > 0
                        && language != null && language.length > 0
                        && sessionDescription != null && sessionDescription.length > 0
                        && sessionType != null) {
                    isValid = true;
                }

                return isValid;
            },


    /*
    *       EVENT HANDLERS
    */

    // handle changes to the simulation type control
            ddlSimType_Change = function (e) {
                e.preventDefault();
                updateLanguageDropdownControl();
            },
    // handle click events for the submit button to create a new session
            btnSubmit_Click = function (e) {
                e.preventDefault();

                //should we de-register the buttonclick to prevent multiple posts? probably.

                // grab values to submit
                var sessionName = Y.one(_strTbSimName).get('value'),
                    sessionDescription = Y.one(_strTbSimDesc).get('value'),
                    sessionType = Y.one(_strDdlSimType).get('value'),
                    language = Y.one(_strDdlSimLang).get('value'),

                //Configuration object for POST transaction 
		        cfg = {
		            method: "POST",
		            data: "clientname=" + _clientName + "&sessionName=" + sessionName + "&language=" + language
                        + "&sessiondescription=" + sessionDescription + "&sessiontype=" + sessionType,
		            on: {
		                success: function (id, xhr, arguments) {
		                    var response = Y.JSON.parse(xhr.responseText);
		                    if (response.status === "success") {
		                        var arrVals = response.reason.split("&"),
                                    sessionKey, sessionID,
                                    keyVal = {}, i = '', temp;

		                        for (i in arrVals) {
		                            temp = arrVals[i].split("=");
		                            keyVal[temp[0]] = temp[1];
		                        }

		                        sessionKey = keyVal["sessionkey"];
		                        sessionID = keyVal["sessionid"];

		                        //TODO:redirect on success?
		                        postToEditSession(sessionKey, _clientName, sessionType, sessionDescription, language, sessionID);

		                        //may not even need to close since redirecting
		                        //close();
		                    }
		                    else {
		                        alert(response.reason);
		                    }
		                },
		                failure: function (id, response) {
		                    alert("Failed to create new session");
		                }
		            }
		        };

                if (validateCreateSessionParams(sessionName, language, sessionDescription, sessionType)) {
                    Y.io("Services/WebSimXHR.ashx/CreateSession", cfg);
                } else {
                    alert("All fields are required.");
                }
            },
    // handle click events for the cancel button
            btnCancel_Click = function (e) {
                close();
            };

    return {
        open: open
    };
} ();
}, '0.0.1', { requires: ["node", "io"] });