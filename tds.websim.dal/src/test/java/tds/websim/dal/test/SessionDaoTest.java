/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.dal.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import tds.websim.dal.interfaces.ISessionDao;
import tds.websim.model.BlueprintSegment;
import tds.websim.model.BlueprintSegmentContentLevel;
import tds.websim.model.BlueprintSegmentStrand;
import tds.websim.model.Client;
import tds.websim.model.Clients;
import tds.websim.model.SessionLanguage;
import tds.websim.model.SessionLanguages;
import tds.websim.model.SessionTestBlueprint;
import tds.websim.model.SessionTestItems;
import tds.websim.model.SessionTests;
import tds.websim.model.Sessions;
import tds.websim.model.SimReport;
import tds.websim.model.WebSimUser;
import AIR.test.framework.AbstractTest;
import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;

/**
 * @author Tongliang LIU [tliu@air.org]
 * 
 */

// How To Test:
// 0. Prepare Microsoft JDBC Driver 4.0 for SQL Server
// (1) Download here:
// http://www.microsoft.com/en-us/download/details.aspx?id=11774.
// (2) Copy sqljdbc4.jar to
// ~/.m2/repository/com/microsoft/sqlserver/sqljdbc4/4.0/sqljdbc4-4.0.jar
// (Linux/Mac)
// or C:\Users\{username}\com\microsoft\sqlserver\sqljdbc4\4.0\sqljdbc4-4.0.jar
// (Windows).
// (3) Add the following dependency
// <dependency>
// <groupId>com.microsoft.sqlserver</groupId>
// <artifactId>sqljdbc4</artifactId>
// <version>4.0</version>
// <scope>test</scope>
// </dependency>
//
// 1. Create test-dal-root-context.xml in src/test/resources. (though the file
// should be created)
//
// 2. Add bean: tdsSettings
// <beans:bean id="tdsSettings" class="TDS.Shared.Configuration.TDSSettings"
// scope="prototype" />
//
// 3. Add bean: appSettings
// <beans:bean factory-bean="appSettings" factory-method="updateProperties"
// lazy-init="false">
// <beans:constructor-arg>
// <util:map map-class="java.util.HashMap">
// <beans:entry key="DBDialect" value="SQLSERVER" />
// </util:map>
// </beans:constructor-arg>
// </beans:bean>
//
// 4. Add bean: applicationDataSource
// <beans:bean id="applicationDataSource"
// class="com.mchange.v2.c3p0.ComboPooledDataSource"
// destroy-method="close">
// <beans:property name="jdbcUrl"
// value="jdbc:sqlserver://38.118.82.146;DatabaseName=TDSCore_test_Session_2012"
// />
// <beans:property name="user" value="dbtds" />
// <beans:property name="password" value="KOJ89238876234rUHJ" />
// </beans:bean>
//
// 5. Run->Run/Debug Configurations->Maven Build
// If run without debug, set Goal to test
// If debug, set Goal to -Dmaven.surefire.debug test
// Don't forget to check "Resolve Workspace Artifacts"

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = { "classpath:test-dal-root-context.xml" })
public class SessionDaoTest extends AbstractTest
{
  private final static String INVALID_INTEGER_VALUE_STR = "__NOT_AN_INTEGER__";
  private final static String INVALID_STRING_VALUE      = "___NO_SUCH_STRING___";
  private final static String INVALID_UUID_STRING       = "00000000-2587-1234-4567-000496686da2";

  @Autowired
  private ISessionDao          sessionDao;

  private boolean doValidateUser (String username, String password) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    WebSimUser user = new WebSimUser (username, password);
    try {
      return this.sessionDao.validateUser (user);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return false;
  }

  /**
   * Tests validateUser using <br/>
   * <ol>
   * <li>A valid {@code username} & {@code password}: expected return
   * {@code true}</li>
   * <li>An invalid {@code username} & a valid {@code password} (meaning it is
   * valid for some user): expected return {@code false}</li>
   * <li>A correct {@code username} & an invalid {@code password}: expected
   * return {@code false}</li>
   * <li>An invalid {@code username} & an invalid {@code password}: expected
   * return {@codefalse}</li>
   * </ol>
   */
  @Test
  public void testValidateUser_fourCases () {
    final String[] usernames = { "paul", INVALID_STRING_VALUE, "paul", INVALID_STRING_VALUE };
    final String[] passwords = { "hexhex", "hexhex", INVALID_STRING_VALUE, INVALID_STRING_VALUE };
    final boolean[] expected = { true, false, false, false };

    for (int i = 0; i < usernames.length; i++) {
      String username = usernames[i];
      String password = passwords[i];
      boolean ret = this.doValidateUser (username, password);
      Assert.assertEquals (String.format ("Should return %1$s but %2$s given username = %3$s, password = %4$s.", expected[i], ret, username, password), expected[i], ret);
    }
  }

  private Clients doGetClient (String userId) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getClients (userId);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getClients using a valid {@code userId}: expect to return a list of
   * clients with identical values as they are specified at the beginning of the
   * test (orders don't matter).
   */
  @Test
  public void testGetClients_validUserId () {
    final List<String> expectedResults = new ArrayList<> ();
    expectedResults.add ("Hawaii");
    expectedResults.add ("Ohio");
    expectedResults.add ("oregon");

    final String userId = "paul";
    Clients clients = this.doGetClient (userId);

    CollectionUtils.forAllDo (clients, new Closure ()
    {
      @Override
      public void execute (Object input) {
        // TODO Auto-generated method stub
        Client c = (Client) input;
        Assert.assertTrue (String.format ("%1$s should exist in the returned list given userId = %2$s.", c.getName (), userId), expectedResults.contains (c.getName ()));
      }
    });
  }

  /**
   * Tests getClients using an invalid {@code userId}: expect to return an empty
   * list
   */
  @Test
  public void testGetClients_invalidUserId () {
    String userId = INVALID_STRING_VALUE;
    Clients clients = this.doGetClient (userId);
    Assert.assertEquals (String.format ("Should return an empty list given userId=%1$s.", userId), 0, clients.size ());
  }

  private Sessions doGetSessions (String userId, String clientName) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getSessions (userId, clientName);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getSessions using a valid userId and a valid {@code clientName}:
   * expect to return a list with size != 0 and with elements in a descending
   * order.
   */
  @Test
  public void testGetSessions_validUserId_validClientName () {
    String userId = "gdounkov";
    String clientName = "Ohio";
    Sessions sessions = this.doGetSessions (userId, clientName);
    Assert.assertNotEquals (String.format ("Should return a list of size != 0 element(s) given userId = %1$s and %2$s.", userId, clientName), 0, sessions.size ());

    if (sessions.size () > 1) {
      for (int i = 1; i < sessions.size (); i++) {
        Assert.assertTrue ("Returned session list should be ordered by descending order.", sessions.get (i - 1).getDateCreated ().compareTo (sessions.get (i).getDateCreated ()) > 0);
      }
    } else
      Assert.assertTrue (true);
  }

  /**
   * Tests getSessions using an invalid userId and a valid {@code clientName}:
   * expect to return an empty list.
   */
  @Test
  public void testGetSessions_invalidUserId_validClientName () {
    String userId = INVALID_STRING_VALUE;
    String clientName = "Ohio";
    Sessions sessions = this.doGetSessions (userId, clientName);
    Assert.assertEquals (String.format ("Should return an empty list given userId = %1$s and clientName = %2$s.", userId, clientName), 0, sessions.size ());
  }

  private SessionTests doGetSessionTests (String sessionKey) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getSessionTests (sessionKey);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getSessionTests using a valid {@code sessionKey}: expect to return a
   * list with size != 0
   */
  @Test
  public void testGetSessionTests_validSessionKey () {
    String sessionKey = "3ae153eb-a8ed-404a-87ee-000496686da2";
    SessionTests tests = this.doGetSessionTests (sessionKey);
    Assert.assertNotEquals (String.format ("Should return a list of size != 0 given sessionKey = %1$s", sessionKey), 0, tests.size ());
  }

  /**
   * Tests getSessionTests using an invalid {@code sessionKey}: expect to return
   * an empty list
   */
  @Test
  public void testGetSessionTests_invalidSessionKey () {
    String sessionKey = INVALID_UUID_STRING;
    SessionTests tests = this.doGetSessionTests (sessionKey);
    Assert.assertEquals (String.format ("Should return an empty list given sessionKey = %1$s", sessionKey), 0, tests.size ());
  }

  private List<String> doGetItemTypes (String adminSubject) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getItemTypes (adminSubject);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getItemTypes using a valid {@code adminSubject}: expect to return a
   * list with size != 0
   */
  @Test
  public void testGetItemTypes_validAdminSubject () {
    String adminSubject = "(Ohio)OH-Alt-PAPER-ELA-8-Spring-2013-2014";
    List<String> ret = this.doGetItemTypes (adminSubject);
    Assert.assertNotNull (String.format ("Should not return a null pointer given adminSubject = %1$s", adminSubject), ret);
    Assert.assertNotEquals (String.format ("Should return a list of size != 0 given adminSubject = %1$s", adminSubject), 0, ret.size ());
  }

  /**
   * Tests getItemTypes using an invalid {@code adminSubject}: expect to return
   * an empty list
   */
  @Test
  public void testGetItemTypes_invalidAdminSubject () {
    String adminSubject = INVALID_STRING_VALUE;
    List<String> ret = this.doGetItemTypes (adminSubject);
    Assert.assertNotNull (String.format ("Should not return a null pointer given adminSubject = %1$s", adminSubject), ret);
    Assert.assertEquals (String.format ("Should return an empty list given adminSubject = %1$s", adminSubject), 0, ret.size ());
  }

  private SessionLanguages doGetSessionLanguages (String clientName, String sessionType) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getSessionLanguages (clientName, sessionType);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getSessionLanguages using a valid {@code clientName} and a valid
   * {@code sessionType}: expect to return a list with size != 0
   */
  @Test
  public void testGetSessionLanguages_validClientName_validSessionType () {
    String clientName = "Ohio";
    String sessionType = "1";
    SessionLanguages langs = this.doGetSessionLanguages (clientName, sessionType);
    Assert.assertNotEquals (String.format ("Should return a list of size != 0 given clientName = %1$s and sessionType = %2$s", clientName, sessionType), 0, langs.size ());

    Set<SessionLanguage> langSets = new HashSet<SessionLanguage> (langs.size ());
    for (SessionLanguage lang : langs) {
      Assert.assertTrue (String.format ("Should return a list of distinct languages given clientName = %1$s and sessionType = %2$s", clientName, sessionType), langSets.add (lang));
    }
    langs.clear ();
    langs = null;
    langSets.clear ();
    langSets = null;
  }

  /**
   * Tests getSessionLanguages using an invalid {@code clientName} and an
   * invalid {@code sessionType}: expect to return an empty list.
   */
  @Test
  public void testGetSessionLanguages_invalidClientName_invalidSessionType () {
    String clientName = INVALID_STRING_VALUE;
    String[] sessionTypes = { null, INVALID_INTEGER_VALUE_STR };
    for (String sessionType : sessionTypes) {
      SessionLanguages langs = this.doGetSessionLanguages (clientName, sessionType);
      Assert.assertEquals (String.format ("Should return an empty list given clientName = %1$s and sessionType = %2$s", clientName, sessionType), 0, langs.size ());
    }
  }

  private SessionTests doGetSessionTestsToAdd (String clientName, String sessionType) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getSessionTestsToAdd (clientName, sessionType);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getSessionTestsToAdd using a valid {@code clientName} and a valid
   * {@code sessionType}: expect to return a list with size != 0
   */
  @Test
  public void testGetSessionTestsToAdd_validClientName_validSessionType () {
    String clientName = "Ohio";
    String sessionType = "1";
    SessionTests tests = this.doGetSessionTestsToAdd (clientName, sessionType);
    Assert.assertNotEquals (String.format ("Should return a list of size != 0 given clientName = %1$s and sessionType = %2$s", clientName, sessionType), 0, tests.size ());
  }

  /**
   * Tests getSessionTestsToAdd using an invalid {@code clientName} and an
   * invalid {@code sessionType}: expect to return an empty list
   */
  @Test
  public void testGetSessionTestsToAdd_invalidClientName_invalidSessionType () {
    String clientName = INVALID_STRING_VALUE;
    String[] sessionTypes = { null, INVALID_INTEGER_VALUE_STR };
    for (String sessionType : sessionTypes) {
      SessionTests tests = this.doGetSessionTestsToAdd (clientName, sessionType);
      Assert.assertEquals (String.format ("Should return an empty list given clientName = %1$s and sessionType = %2$s", clientName, sessionType), 0, tests.size ());
    }
  }

  private SessionTestBlueprint doGetTestBlueprint (String sessionKey, String testKey) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getTestBlueprint (sessionKey, testKey);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getTestBlueprint using a valid {@code sessionKey} and a valid
   * {@code testKey}: expect to return a {@link SessionTestBlueprint} object,
   * containing a list of {@link BlueprintSegment} objects, a list of
   * {@link BlueprintSegmentStrand} objects, and a list of
   * {@link BlueprintSegmentContentLevel} objects. There should be at least one
   * list with size > 0.
   */
  @Test
  public void testGetTestBlueprint_validSessionKey_validTestKey () {
    String sessionKey = "8CAEF74B-1B75-44CC-BD36-176F9895D90D";
    String testKey = "(Ohio)OH-Alt-PAPER-ELA-5-Spring-2013-2014";
    SessionTestBlueprint b = this.doGetTestBlueprint (sessionKey, testKey);
    int segCount = b.getBlueprintSegments ().size ();
    int strandCount = b.getBlueprintSegmentStrands ().size ();
    int lvlCount = b.getBlueprintSegmentContentLevels ().size ();
    Assert.assertTrue (String.format ("Should return at lease one non-empty list given sessionKey = %1$s and testKey = %2$s", sessionKey, testKey), segCount > 0 || strandCount > 0 || lvlCount > 0);
  }

  /**
   * Tests getTestBlueprint using an invalid {@code sessionKey} and an invalid
   * {@code testKey}: expect to return a {@link SessionTestBlueprint} object,
   * containing three empty lists.
   */
  @Test
  public void testGetTestBlueprint_invalidSessionKey_invalidTestKey () {
    String sessionKey = INVALID_UUID_STRING;
    String testKey = INVALID_STRING_VALUE;
    SessionTestBlueprint b = this.doGetTestBlueprint (sessionKey, testKey);
    int segCount = b.getBlueprintSegments ().size ();
    int strandCount = b.getBlueprintSegmentStrands ().size ();
    int lvlCount = b.getBlueprintSegmentContentLevels ().size ();
    Assert.assertEquals (String.format ("Should return an empty list of BlueprintSegments, given sessionKey = %1$s and testKey = %2$s", sessionKey, testKey), 0, segCount);
    Assert.assertEquals (String.format ("Should return an empty list of BlueprintSegmentStrands, given sessionKey = %1$s and testKey = %2$s", sessionKey, testKey), 0, strandCount);
    Assert.assertEquals (String.format ("Should return an empty list of BlueprintSegmentContentLevels, given sessionKey = %1$s and testKey = %2$s", sessionKey, testKey), 0, lvlCount);
  }

  private SessionTestItems doGetTestItems (String sessionKey, String testKey) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getTestItems (sessionKey, testKey);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getTestItems using a valid {@code sessionKey} and a valid
   * {@code testKey}: expect to return a {@link SessionTestItems} with at least
   * one non-empty member list
   */
  @Test
  public void testGetTestItems_validSessionKey_validTestKey () {
    String sessionKey = "8CAEF74B-1B75-44CC-BD36-176F9895D90D";
    String testKey = "(Ohio)OH-Alt-PAPER-ELA-5-Spring-2013-2014";
    SessionTestItems items = this.doGetTestItems (sessionKey, testKey);
    int pCount = items.getItemProperties ().size ();
    int gpCount = items.getItemGroupProperties ().size ();
    Assert.assertTrue (String.format ("Should return at lease one non-empty list given sessionKey = %1$s and testKey = %2$s", sessionKey, testKey), pCount > 0 || gpCount > 0);
  }

  /**
   * Tests getTestItems using an invalid {@code sessionKey} and an invalid
   * {@code testKey}: expect to return a {@link SessionTestItems} with two empty
   * member lists
   */
  @Test
  public void testGetTestItems_invalidSessionKey_invalidTestKey () {
    String sessionKey = INVALID_UUID_STRING;
    String testKey = INVALID_STRING_VALUE;
    SessionTestItems items = this.doGetTestItems (sessionKey, testKey);
    int pCount = items.getItemProperties ().size ();
    int gpCount = items.getItemGroupProperties ().size ();
    Assert.assertEquals (String.format ("Should return an empty list of ItemProperties, given sessionKey = %1$s and testKey = %2$s", sessionKey, testKey), 0, pCount);
    Assert.assertEquals (String.format ("Should return an empty list of ItemGroupProperties, given sessionKey = %1$s and testKey = %2$s", sessionKey, testKey), 0, gpCount);
  }

  private ReturnStatus doSetSessionDescription (String sessionKey, String description) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.setSessionDescription (sessionKey, description);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests setSessionDescription given a valid {@code sessionKey} and a
   * {@code null} {@code description}: expect to return a {@link ReturnStatus}
   * object with status not equal to {@code failed}
   */
  @Test
  public void testSetSessionDescription_validSessionKey_nullDescription () {
    String sessionKey = "8caef74b-1b75-44cc-bd36-176f9895d90d";
    String description = null;
    ReturnStatus ret = this.doSetSessionDescription (sessionKey, description);
    Assert.assertNotEquals (
        String.format ("Session's description should be set successfully, given sessionKey = %1$s and description = %2$s. Return status: reason = %3$s, context = %4$s", sessionKey, description,
            ret.getReason (), ret.getContext ()), "failed", ret.getStatus ());
  }

  /**
   * Tests setSessionDescription given a valid {@code sessionKey} and a valid
   * {@code description}: expect to return a {@link ReturnStatus} object with
   * status not equal to {@code failed}
   */
  @Test
  public void testSetSessionDescription_validSessionKey_validDescription () {
    String sessionKey = "8caef74b-1b75-44cc-bd36-176f9895d90d";
    String description = "SessionDaoTest@" + (new Date ()).toString ();
    ReturnStatus ret = this.doSetSessionDescription (sessionKey, description);
    Assert.assertNotEquals (
        String.format ("Session's description should be set successfully, given sessionKey = %1$s and description = %2$s. Return status: reason = %3$s, context = %4$s", sessionKey, description,
            ret.getReason (), ret.getContext ()), "failed", ret.getStatus ());
  }

  /**
   * Tests setSessionDescription given an invalid {@code sessionKey} and a
   * {@code null} {@code description}: expect to return a {@link ReturnStatus}
   * object with status equal to {@code success} meaning the query was
   * successfully executed, even the sessionKey is invalid.
   */
  @Test
  public void testSetSessionDescription_invalidSessionKey_nullDescription () {
    String sessionKey = null;
    String description = null;
    ReturnStatus ret = this.doSetSessionDescription (sessionKey, description);
    Assert.assertNotEquals (String.format (
        "ReturnStatus should be success, meaning the query was successfully executed, given invalid sessionKey = %1$s and description = %2$s. Return status: reason = %3$s, context = %4$s",
        sessionKey, description, ret.getReason (), ret.getContext ()), "failed", ret.getStatus ());
  }

  @Deprecated
  @SuppressWarnings ("unused")
  private List<List<String>> doGetLanguageGradeSubject (String clientName) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getLanguageGradeSubject (clientName);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * No Test provided since the method may have been deprecated.
   */
  @Test
  @Deprecated
  public void testGetLanguageGradeSubject_validClientName () {
    /**
     * Remark: The method may be deprecated, so no test provided.
     * 
     * @author Tongliang Liu [tliu@air.org]
     */
  }

  private ReturnStatus doCopySession (String fromSessionKey, String sessionName, String sessionDescription) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.copySession (fromSessionKey, sessionName, sessionDescription);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests copySession with <br/>
   * <ol>
   * <li>valid {@code sessionKey}, {@code sessionName} and
   * {@code sessionDescription}: expect to return a "success" status</li>
   * <li>valid {@code sessionKey} and {@code sessionName}, and a {@code null}
   * {@code sessionDescription}: expect to return a "success" status</li>
   * <li>valid {@code sessionKey}, a {@code null} {@code sessionName}, and a
   * {@code null} {@code sessionDescription}: expect to return a "failed" status
   * </li>
   * <li>an invalid {@code sessionKey}, a valid {@code sessionName}, and a
   * {@code null} {@code sessionDescription}: expect to return a "failed" status
   * </li>
   * </ol>
   */
  @Test
  public void testCopySession_variousCases () {
    Date cur = new Date ();
    String[] fromSessionKeys = { "1531b61a-00d8-4a44-b53c-6362d36bfdb1", "1531b61a-00d8-4a44-b53c-6362d36bfdb1", "1531b61a-00d8-4a44-b53c-6362d36bfdb1", INVALID_UUID_STRING };
    String[] sessionNames = { "session@JUnit_" + cur.getTime (), "session_JUnit@" + cur.toString (), null, "session_JUnit@" + cur.toString () };
    String[] sessionDescriptions = { "JUnit@" + cur.toString (), null, "JUnit@" + cur.toString (), null };
    String[] expectedRetStatus = { "success", "success", "failed", "failed" };

    for (int i = 0; i < fromSessionKeys.length; i++) {
      String fromSessionKey = fromSessionKeys[i];
      String sessionName = sessionNames[i];
      String sessionDescription = sessionDescriptions[i];
      ReturnStatus ret = this.doCopySession (fromSessionKey, sessionName, sessionDescription);

      Assert.assertEquals (String.format ("Should return \"%1$s\" given fromSessionKey = %2$s,  sessionName = %3$s and sessionDescription = %4$s", expectedRetStatus[i], fromSessionKey, sessionName,
          sessionDescription), expectedRetStatus[i], ret.getStatus ());
    }
  }

  private ReturnStatus doCreateSession (String clientName, String userId, String sessionName, String language, String sessionDescription, String sessionType) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.createSession (clientName, userId, sessionName, language, sessionDescription, sessionType);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests create session with <br/>
   * <ol>
   * <li>all valid: expect to return "success"</li>
   * <li>without optional parameters: expected to return "success"</li>
   * <li>{@code null client} and {@code null sessionName}: expected to return
   * "failed" because they cannot be null</li>
   * <li>{@code null userId} and {@code null sessionName}: expected to return
   * "failed" because they cannot be null</li>
   * </ol>
   */
  @Test
  public void testCreateSession_variousCases () {
    Date cur = new Date ();
    String[] clientNames = { "Ohio", "Ohio", "Ohio", null, "Ohio" };
    String[] userIds = { "paul", "paul", "paul", "paul", null };
    String[] sessionNames = { "sessionName_JUnit@" + cur.toString (), "sessionName_JUnit@" + cur.toString (), "sessionName_JUnit@" + cur.toString (), null, null, };
    String[] languages = { "ENU", "ENU", "ENU", null, null };
    String[] sessionDescriptions = { "sessionDesp_JUnit@" + cur.toString (), "sessionDesp_JUnit@" + cur.toString (), null, null, null };
    String[] sessionTypes = { "0", "1", "1", null, null };
    String[] expectedRetStatus = { "success", "success", "success", "failed", "failed" };

    for (int i = 0; i < clientNames.length; i++) {
      String clientName = clientNames[i];
      String userId = userIds[i];
      String sessionName = sessionNames[i];
      String language = languages[i];
      String sessionDescription = sessionDescriptions[i];
      String sessionType = sessionTypes[i];
      ReturnStatus ret = this.doCreateSession (clientName, userId, sessionName, language, sessionDescription, sessionType);

      Assert.assertEquals (String.format ("Should return \"%1$s\" given clientName = %2$s,  userId = %3$s, sessionName = %4$s, language = %5$s, sessionDescription = %6$s and sessionType = %7$s",
          expectedRetStatus[i], clientName, userId, sessionName, language, sessionDescription, sessionType), expectedRetStatus[i], ret.getStatus ());
    }
  }

  private SimReport doGetReportSummaryStats (String sessionKey, String testKey) {
    Assert.assertNotNull ("sessionDao should not be null.", sessionDao);
    try {
      return this.sessionDao.getReportSummaryStats (sessionKey, testKey);
    } catch (ReturnStatusException e) {
      Assert.fail (e.getMessage ());
    } catch (Exception ee) {
      Assert.fail (ee.getMessage ());
    }
    return null;
  }

  /**
   * Tests getReportSummaryStats with
   * <ol>
   * <li>all valid: expect to return >=2 tables</li>
   * <li>all valid but has no summary status: expect to return only 1 table</li>
   * </ol>
   */
  @Test
  public void testGetReportSummaryStats () {
    String[] sessionKeys = { "55474bee-0324-4489-8504-cef01a5ea4f0", "55474bee-0324-4489-8504-cef01a5ea4f0" };
    String[] testKeys = { "(Ohio)OH-Alt-ELA-10-Spring-2013-2014", "(Ohio)OH-Alt-SR-ELA-10-Spring-2013-2014" };
    int[] expectedTables = { 2, 1 };

    for (int i = 0; i < sessionKeys.length; i++) {
      SimReport report = this.doGetReportSummaryStats (sessionKeys[i], testKeys[i]);
      Assert.assertEquals (String.format ("Should return %1$d tables given sessionKey = %2$s abd testKey = %2$s", expectedTables[i], sessionKeys[i], testKeys[i]), expectedTables[i], report
          .getTables ().size ());
    }
  }
}
