**Prerequisites**
* TDS database that is not used for production purposes. This web application simulates students taking tests, and should not interfere with an existing TDS database being used for UAT or other purposes.

* A standard sized Linux OS wtih Tomcat 8.0 and Java 1.7 should be sufficient to host the web application


**Installation Steps**
1. Download version 3.1.1 of the .war web application archive file. The .war file can be found on the Smarter Balanced Artifactory site at https://airdev.artifactoryonline.com/airdev/webapp/#/artifacts/browse/tree/General/libs-releases-local/org/opentestsystem/delivery/tds.websim.web.ui/3.1.1.RELEASE/tds.websim.web.ui-3.1.1.RELEASE.war

2. Place the .war file in the webapps directory of the tomcat installation and start tomcat
    > An installation of Tomcat 8.0.41 with java 1.7.0 on Windows 10 was proven to install the .war file.
   
3. Once the .war file has been deployed, edit the settings.xml file found in the WEB-INF/classes directory
    * Edit the jdbc.url, jdbc.userName, and jdbc.password values for the TDS database instance that will be used for the simulator.
    * Restart the tomcat instance once the settings.xml file has been edited and saved

4. Once installed, the web app should be accessible at http://root-url-of-the-tomcat-instance/tds.websim.web.ui-3.1.1.RELEASE

5. The web application uses a local table in the TDS database as configured in the settings.xml file to store user logins. To add users, open a MySQL interface and execute the following statement in the `session` database, replacing the `userid`, `username`, `password`, and `userkey` with unique values for the user:
    > INSERT INTO session.sim_user (userid, username, browserkey, password, userkey)
    > VALUES('`userid`', '`user name`', unhex(replace(uuid(), '-', '')), '`password`', `userkey`);
