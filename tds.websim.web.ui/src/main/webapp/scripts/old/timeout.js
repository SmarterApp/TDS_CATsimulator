//required: YAHOO.util
var timeoutTDS = {
    that: null,
    configTimeout: 1200000, //config timeout in the web config file, 20mins = 20x60x1000 milliseconds
    serverCheckURL: "Services/XHR.axd/ServerActivity",  //XHR url to call to check the server for activity   
    hasServerActivity: true,
    starttime: 0, //last activity time, in milliseconds
    activityCheckInterval: 1200000, //1200000-->20mins = 20x60x1000 milliseconds , check for user's activity 
    intervalId: 0,
    requiredServerCheck: true, //true if required to ping the server
    logoutURL: '../Login.aspx?exl=false', //page to call when inactivity period=configTimeout
    logoutURL_alt: './Login.aspx?exl=false', //page to call when inactivity period=configTimeout

    init: function(configTimeout, logoutURL, requiredServerCheck, serverCheckURL, activityCheckInterval) {
        this.that = this;
        this.configTimeout = configTimeout;
        if (logoutURL != null)//if null used default
            this.logoutURL = logoutURL;
        this.requiredServerCheck = requiredServerCheck;
        if (serverCheckURL != null)
            this.serverCheckURL = serverCheckURL;
        //if not required server check then the activity check time will be equal to the config timeout          
        if (!requiredServerCheck || activityCheckInterval == null)
            this.activityCheckInterval = configTimeout;
        else
            this.activityCheckInterval = activityCheckInterval;

        YAHOO.util.Event.addListener(document, 'keydown', this.registerActivity);
        YAHOO.util.Event.addListener(document, 'mousedown', this.registerActivity);
        this.registerActivity();
        //this.testDisplay();
    },

    reset: function() {
        var that = timeoutTDS.that;
        that.checkServerActivity();
    },

    resetInterval: function() {
        var that = timeoutTDS.that;
        clearTimeout(that.intervalId);
        that.intervalId = setTimeout('timeoutTDS.reset()', that.activityCheckInterval);
    },

    now: function() {
        return (new Date()).getTime();
    },

    registerActivity: function() {
        var that = timeoutTDS.that;
        that.starttime = that.now();
        that.resetInterval();
    },

    checkServerActivity: function() {
        var that = timeoutTDS.that;
        if (!that.requiredServerCheck) //if server check is not needed ...
        {//logout user            
            that.logout(); return;
        }
        //XHR ping to server to check for activity 
        var callback =
        {
            success: function(o) {
                var that = timeoutTDS.that;
                if (o.responseText != undefined) {
                    var returnedStatus = YAHOO.lang.JSON.parse(o.responseText);
                    if (returnedStatus.Status != undefined && returnedStatus.Status == "True") {
                        Util.log("that.hasServerActivity=true");
                        that.hasServerActivity = true;
                        //reset the timeout allowance
                        that.registerActivity();
                    }
                    else {
                        Util.log("that.hasServerActivity = false --> that.logout();");
                        that.hasServerActivity = false;
                        //logout user
                        that.logout();
                    }
                }
            },
            failure: function(o) {
                var that = timeoutTDS.that;
                that.hasServerActivity = true; //This would never happened but just in case
                //reset the timeout allowance
                that.registerActivity();
            }
        };
        YAHOO.util.Connect.asyncRequest('POST', that.serverCheckURL, callback, null);
    },

    logout: function() {
        var that = timeoutTDS.that;
        clicked = true;
        var loc = window.location.href;
        if (loc.indexOf("Home") > 0)
            window.location = that.logoutURL_alt;
        else
            window.location = that.logoutURL;
   },

    testDisplay: function() {
        var that = timeoutTDS.that;
        setTimeout('timeoutTDS.testDisplay()', 1000);

        Util.log("Server config - configTimeout: " + that.configTimeout / (1000) + " seconds | " +
                        "Your Idle time - idleTime: " + (that.now() - that.starttime) / (1000) + " seconds");
    }
}
