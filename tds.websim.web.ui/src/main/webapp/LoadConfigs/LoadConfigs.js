/** 
* The Session Dashboard module manages the Dashboard for the Web-based Simulator's Setup page 
*/
YUI.add('loadconfigs', function (Y) {
    /** 
    * The SetupDash class populates all elements related to the Web-based Simulator Setup Page's dashboard.
    */
    Y.LoadConfigs = function () {
        var // id's that will be used in this module
            _strClientDdl = "#ctl00_MainContent_ddlClientPicker",
        // hf variables
            _strHfClientName = "#ctl00_MainContent_hfcn",
        // div id for navigation
            _divNav = "#navigation",
        // loading panel
            _loadConfigLoadingPanel,
        // admin controls grouped in this control
            _strAdminControls = "#adminControls",

        // initialize the "module"
        init = function () {
            // retrieve value from hidden variables that are pulled from http posts / session data
            getHiddenVars();

            // update the table and navigation links with the selected clientname
            getClients();
            updateNavLinks(Y.one(_strClientDdl).get('value'));

            // loading panel setup
            _loadConfigLoadingPanel = new Y.Panel({
                srcNode: '#loadConfigLoadingPanel',
                zIndex: 5,
                centered: true,
                modal: true,
                visible: false,
                render: true,
                buttons: []
            });
            _loadConfigLoadingPanel.hide();
            Y.one('#loadConfigLoadingPanel').setStyle('display', 'inherit');

            // listen for change in dropdown list
            Y.on('change', ddlClient_Change, _strClientDdl);
        },

        getHiddenVars = function () {
            _clientName = Y.one(_strHfClientName).get('value');
        },

        // retrieve clients so that you can determine which client you are authorized to load/clear configs for
        getClients = function () {
            var ddlClient = Y.one(_strClientDdl),
                initClientName = Y.QueryString.parse(window.location.href).clientname,
                clientVal, optn,
            // create the io callback
            callback = {
                on: {
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.responseText);
                        var clients = [];

                        try {
                            clients = Y.JSON.parse(response.responseText);
                        }
                        catch (e) {
                            alert("Failed to parse the JSON data.");
                            return;
                        }
                        Y.log("PARSED DATA: " + Y.Lang.dump(clients));

                        // if error, alert and stop here
                        if (clients.errormsg) {
                            alert(clients.errormsg);
                            return;
                        }

                        _clients = Y.clone(clients);

                        // determine who is authorized to load/clear configs
                        authorizeAccessToControls(Y.one(_strClientDdl).get('value'));
                    },
                    failure: function (id, response) {
                        alert("Failed to retrieve clients.");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetClients", callback);
        },

        // this will update the clientname querystring value for navigation and breadcrumbs
        updateNavLinks = function (clientname) {
            Y.one(_divNav + ' .leftNav').all('li a').each(function () {
                var href = this.get('href');
                href = href.split('?', 1)[0];
                this.set('href', href + '?&clientname=' + clientname);
            });
        },

    /*
    *       EVENT HANDLERS
    */

    // handle dropdown client change events
        ddlClient_Change = function (e) {
            e.preventDefault();
            var clientName = Y.one(_strClientDdl).get('value');
            _clientName = clientName;

            // init will take care of updating configs from dropdown postback onchange
            //            getConfigsLoaded();
            //update clientname on nav links
            updateNavLinks(clientName);
        };

    return {
        init: init
    };
} ();
}, '0.0.1', { requires: ["node", "io", "querystring-parse-simple", "panel", "loadererrors"] });

YUI({
    modules: {
        loadererrors: {
            fullpath: '../scripts/WebSim/Loader_Errors.js',
            require: ['node', 'io']
        }
    }
}).use("node", "io", "dump", "json-parse", "loadconfigs", function (Y) {
    var loadConfigs = Y.LoadConfigs;
    loadConfigs.init();
});