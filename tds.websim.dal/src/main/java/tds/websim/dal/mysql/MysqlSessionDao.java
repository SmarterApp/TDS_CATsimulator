/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.dal.mysql;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;
import tds.dll.api.ICommonDLL;
import tds.dll.api.ISimDLL;
import tds.dll.api.IStudentDLL;
import tds.websim.dal.interfaces.ConverterUtil;
import tds.websim.dal.interfaces.DAO;
import tds.websim.dal.interfaces.ISessionDao;
import tds.websim.model.BlueprintSegment;
import tds.websim.model.BlueprintSegmentContentLevel;
import tds.websim.model.BlueprintSegmentStrand;
import tds.websim.model.BlueprintValidationError;
import tds.websim.model.BlueprintValidationStatus;
import tds.websim.model.Client;
import tds.websim.model.Clients;
import tds.websim.model.ItemGroupProperty;
import tds.websim.model.ItemProperty;
import tds.websim.model.Session;
import tds.websim.model.SessionLanguage;
import tds.websim.model.SessionLanguages;
import tds.websim.model.SessionTest;
import tds.websim.model.SessionTestBlueprint;
import tds.websim.model.SessionTestItems;
import tds.websim.model.SessionTests;
import tds.websim.model.Sessions;
import tds.websim.model.SimReport;
import tds.websim.model.SimulationError;
import tds.websim.model.SimulationErrors;
import tds.websim.model.Table;
import tds.websim.model.TableRow;
import tds.websim.model.WebSimUser;
import tds.websim.model.itemselectionparam.ItemSelectionParam;
import tds.websim.model.itemselectionparam.ItemSelectionParamTemplate;
import tds.websim.model.itemselectionparam.ItemSelectionParams;
import tds.websim.model.simpublish.SimPubSegment;
import tds.websim.model.simpublish.SimPubSegmentContentLevel;
import tds.websim.model.simpublish.SimPubSegmentItem;
import tds.websim.model.simpublish.SimPubSegmentItemGroup;
import tds.websim.model.simpublish.SimPubSession;
import tds.websim.model.simpublish.SimPubSessionTest;
import tds.websim.model.simpublish.SimPubTestPackage;
import AIR.Common.DB.SQLConnection;
import AIR.Common.DB.results.DbResultRecord;
import AIR.Common.DB.results.MultiDataResultSet;
import AIR.Common.DB.results.SingleDataResultSet;
import AIR.Common.Helpers._Ref;

@Component
@Scope ("prototype")
public class MysqlSessionDao extends DAO implements ISessionDao
{
  private static final Logger _logger     = LoggerFactory.getLogger (MysqlSessionDao.class);
  @Autowired
  private ICommonDLL          _commonDll  = null;
  @Autowired
  private IStudentDLL         _studentDll = null;
  @Autowired
  private ISimDLL             _simdll;

  public MysqlSessionDao () {
    super ();
  }

  public void setiCommonDLL (ICommonDLL _dll) {
    _commonDll = _dll;
  }

  public void setiStudentDLL (IStudentDLL _dll) {
    _studentDll = _dll;
  }

  @Override
  public boolean validateUser (WebSimUser user) throws ReturnStatusException {
    try (SQLConnection connection = getSQLConnection ()) {
      SingleDataResultSet records = _simdll.SIM_ValidateUser_SP (connection, user.getId (), user.getPassword ());
      if (records.getCount () > 0) {
        DbResultRecord record = records.getRecords ().next ();
        user.setFullname (record.<String> get ("userName"));
        user.setBrowserKey (record.<UUID> get ("browserKey"));
        user.setAuth (true);
        return true;
      }
      return false;
    } catch (SQLException exp) {
      _logger.error ("Error executing simDll.SIM_ValidateUser_SP", exp);
      throw new ReturnStatusException (exp);
    }
  }

  @Override
  public Clients getClients (String userId) throws ReturnStatusException {

    Clients clients = new Clients ();

    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_GetUserClients_SP (conn, userId);

      Iterator<DbResultRecord> iterator = rs.getRecords ();
      while (iterator.hasNext ()) {
        DbResultRecord dr = iterator.next ();
        Client c = new Client ();
        c.setName (dr.<String> get ("clientname"));
        c.setIsAdmin (dr.<Boolean> get ("isAdmin"));
        clients.add (c);
      }

    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running SIM_GetUserClients.", e);
      throw new ReturnStatusException (e);
    }
    return clients;

  }

  @Override
  public Sessions getSessions (String userId, String clientName) throws ReturnStatusException {

    Sessions sessions = new Sessions ();

    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_GetUserSessions_SP (conn, userId, clientName);
      rs.setFixNulls (true);

      Iterator<DbResultRecord> iterator = rs.getRecords ();
      while (iterator.hasNext ()) {
        DbResultRecord dr = iterator.next ();
        Session s = new Session ();
        s.setKey (dr.<UUID> get ("_Key"));
        s.setSessionID (dr.<String> get ("sessionID"));
        s.setStatus (dr.<String> get ("status"));
        s.setName (dr.<String> get ("name").toString ());
        s.setDescription (dr.<String> get ("description"));
        s.setDateCreated (dr.<Date> get ("datecreated"));
        s.setClientName (dr.<String> get ("clientname"));
        s.setEnvironment (dr.<String> get ("environment"));
        s.setSim_Language (dr.<String> get ("language"));
        s.setSim_ProctorDelay (dr.<Integer> get ("sim_proctorDelay"));
        s.setSessionType (dr.<Integer> get ("sessiontype"));
        s.setSim_Abort (dr.<Boolean> get ("sim_abort"));
        s.setSim_Status (dr.<String> get ("sim_status"));
        s.setSim_Start (dr.<Date> get ("sim_start"));
        s.setSim_Stop (dr.<Date> get ("sim_stop"));
        sessions.add (s);
      }

    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running SIM_GetUserSessions.", e);
      throw new ReturnStatusException (e);
    }

    // Sort by date created by descending order
    Collections.sort (sessions, new Comparator<Session> ()
    {

      @Override
      public int compare (Session s1, Session s2) {
        return s2.getDateCreated ().compareTo (s1.getDateCreated ());
      }
    });

    return sessions;
  }

  @Override
  public SessionTests getSessionTests (String sessionKey) throws ReturnStatusException {
    SessionTests tests = new SessionTests ();

    try (SQLConnection conn = getSQLConnection ()) {

      UUID session = UUID.fromString (sessionKey);
      SingleDataResultSet rs = _simdll.SIM_GetSessionTests2_SP (conn, session);
      rs.setFixNulls (true);

      Iterator<DbResultRecord> iterator = rs.getRecords ();
      while (iterator.hasNext ()) {
        DbResultRecord dr = iterator.next ();
        SessionTest t = new SessionTest ();
        t.setAdminSubject (dr.<String> get ("testkey"));
        t.setTestID (dr.<String> get ("testID"));
        t.setIterations (dr.<Integer> get ("iterations"));
        t.setOpportunities (dr.<Integer> get ("opportunities"));
        t.setMeanProficiency (dr.<Float> get ("meanproficiency"));
        t.setSdProficiency (dr.<Float> get ("sdproficiency"));
        t.setStrandCorrelation (dr.<Float> get ("strandcorrelation"));
        // t.setSelectionAlgorithm ( dr.get("selectionAlgorithm"));
        t.setSimulations (dr.<Integer> get ("simulations"));
        t.setHandScoreItemTypes (dr.<String> get ("handScoreItemTypes"));
        tests.add (t);
      }

    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running SIM_GetSessionTests2.", e);
      throw new ReturnStatusException (e);
    }
    return tests;
  }

  @Override
  public List<String> getItemTypes (String adminSubject) throws ReturnStatusException {

    List<String> itemTypeList = new ArrayList<String> ();

    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.sim_getItemTypes (conn, adminSubject);
      rs.setFixNulls (true);
      Iterator<DbResultRecord> iterator = rs.getRecords ();
      while (iterator.hasNext ()) {
        DbResultRecord dr = iterator.next ();
        itemTypeList.add (dr.<String> get ("ItemType"));
      }

    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running sim_getItemTypes.", e);
      throw new ReturnStatusException (e);
    }

    return itemTypeList;
  }

  @Override
  public SessionLanguages getSessionLanguages (String clientName, String sessionType) throws ReturnStatusException {

    // Using Set to prevent duplicates
    Set<SessionLanguage> languages = new HashSet<SessionLanguage> ();

    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _studentDll.IB_ListTests_SP (conn, clientName, ConverterUtil.sessionTypeStrToInt (sessionType));
      rs.setFixNulls (true);

      Iterator<DbResultRecord> iterator = rs.getRecords ();
      while (iterator.hasNext ()) {
        DbResultRecord dr = iterator.next ();
        SessionLanguage lang = new SessionLanguage ();
        lang.setLanguage (dr.<String> get ("Language"));
        lang.setLanguageCode (dr.<String> get ("LanguageCode"));
        lang.setSessionType (sessionType);
        languages.add (lang);
      }

    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running IB_ListTests.", e);
      throw new ReturnStatusException (e);
    }

    return new SessionLanguages (languages);
  }

  @Override
  public SessionTests getSessionTestsToAdd (String clientName, String sessionType) throws ReturnStatusException {
    SessionTests tests = new SessionTests ();

    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _studentDll.IB_ListTests_SP (conn, clientName, ConverterUtil.sessionTypeStrToInt (sessionType));
      rs.setFixNulls (true);

      Iterator<DbResultRecord> reader = rs.getRecords ();
      while (reader.hasNext ()) {
        DbResultRecord dr = reader.next ();

        boolean isSelectable = (boolean) dr.get ("IsSelectable");
        if (!isSelectable)
          continue;

        SessionTest t = new SessionTest ();
        t.setAdminSubject (dr.<String> get ("_Key"));
        t.setTestID (dr.<String> get ("TestId"));
        t.setMeanProficiency (dr.<Float> get ("startAbility"));
        t.setGradeCode (dr.<String> get ("GradeCode"));
        t.setLanguage (dr.<String> get ("Language"));
        t.setLanguageCode (dr.<String> get ("LanguageCode"));
        t.setSubject (dr.<String> get ("Subject"));
        t.setOpportunities (dr.<Integer> get ("MaxOpportunities"));

        tests.add (t);
      }
    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running IB_ListTests.", e);
      throw new ReturnStatusException (e);
    }
    return tests;
  }

  @Override
  public SessionTestBlueprint getTestBlueprint (String sessionKey, String testKey) throws ReturnStatusException {

    SessionTestBlueprint sessionTestBlueprint = new SessionTestBlueprint ();
    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet dsBlueprintSegment = null;
      SingleDataResultSet dsBlueprintSegmentStrand = null;
      SingleDataResultSet dsBlueprintSegmentContentLevel = null;

      UUID session = UUID.fromString (sessionKey);
      MultiDataResultSet resultSets = _simdll.SIM_GetTestBlueprint2_SP (conn, session, testKey);
      boolean havingEnoughResults = true;
      Iterator<SingleDataResultSet> multiRsReader = resultSets.getResultSets ();
      if (multiRsReader.hasNext ()) {
        dsBlueprintSegment = multiRsReader.next ();
        dsBlueprintSegment.setFixNulls (true);
      } else
        havingEnoughResults = false;

      if (multiRsReader.hasNext ()) {
        dsBlueprintSegmentStrand = multiRsReader.next ();
        dsBlueprintSegmentStrand.setFixNulls (true);
      } else
        havingEnoughResults = false;

      if (multiRsReader.hasNext ()) {
        dsBlueprintSegmentContentLevel = multiRsReader.next ();
        dsBlueprintSegmentContentLevel.setFixNulls (true);
      } else
        havingEnoughResults = false;

      if (havingEnoughResults) {
        Iterator<DbResultRecord> iterator = dsBlueprintSegment.getRecords ();
        while (iterator.hasNext ()) {
          DbResultRecord dr = iterator.next ();
          sessionTestBlueprint.getBlueprintSegments ().add (createBlueprintSegment (dr));
        }

        iterator = dsBlueprintSegmentStrand.getRecords ();
        while (iterator.hasNext ()) {
          DbResultRecord dr = iterator.next ();
          sessionTestBlueprint.getBlueprintSegmentStrands ().add (createBlueprintSegmentStrand (dr));
        }

        iterator = dsBlueprintSegmentContentLevel.getRecords ();
        while (iterator.hasNext ()) {
          DbResultRecord dr = iterator.next ();
          sessionTestBlueprint.getBlueprintSegmentContentLevels ().add (createBlueprintContentLevel (dr));
        }
      }
    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running SIM_GetTestBlueprint2.", e);
      throw new ReturnStatusException (e);
    }
    return sessionTestBlueprint;
  }

  @Override
  public SessionTestItems getTestItems (String sessionKey, String testKey) throws ReturnStatusException {

    SessionTestItems items = new SessionTestItems ();
    try (SQLConnection conn = getSQLConnection ()) {

      UUID session = UUID.fromString (sessionKey);
      MultiDataResultSet resultSets = _simdll.SIM_GetTestItems_SP (conn, session, testKey);
      Iterator<SingleDataResultSet> multiRsReader = resultSets.getResultSets ();

      SingleDataResultSet dsTestItemProps = null;
      SingleDataResultSet dsTestItemGoupProps = null;
      boolean havingEnoughResults = true;

      if (multiRsReader.hasNext ()) {
        dsTestItemProps = multiRsReader.next ();
        dsTestItemProps.setFixNulls (true);
      } else
        havingEnoughResults = false;

      if (multiRsReader.hasNext ()) {
        dsTestItemGoupProps = multiRsReader.next ();
        dsTestItemGoupProps.setFixNulls (true);
      } else
        havingEnoughResults = false;

      if (havingEnoughResults) {
        Iterator<DbResultRecord> iterator = dsTestItemProps.getRecords ();
        while (iterator.hasNext ()) {
          DbResultRecord dr = iterator.next ();
          items.getItemProperties ().add (createItemProperty (dr));
        }

        iterator = dsTestItemGoupProps.getRecords ();
        while (iterator.hasNext ()) {
          DbResultRecord dr = iterator.next ();
          items.getItemGroupProperties ().add (createItemGroupProperties (dr));
        }
      }
    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running SIM_GetTestItems.", e);
      throw new ReturnStatusException (e);
    }
    return items;
  }

  @Override
  public ReturnStatus setSessionDescription (String sessionKey, String description) throws ReturnStatusException {

    try (SQLConnection conn = getSQLConnection ()) {
      /*
       * EF: porting it as it was done in .Net code: expect no reply. Any
       * unforeseen database problems should cause SQLException thrown.
       */
      UUID session = UUID.fromString (sessionKey);
      _simdll.SIM_SetSessionDescription_SP (conn, session, description);

    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running SIM_SetSessionDescription.", e);
      throw new ReturnStatusException (e);
    }

    return new ReturnStatus ("success");
  }

  @Override
  @Deprecated
  public List<List<String>> getLanguageGradeSubject (String clientName) throws ReturnStatusException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ReturnStatus copySession (String fromSessionKey, String sessionName, String sessionDescription) throws ReturnStatusException {
    ReturnStatus ret = null;

    try (SQLConnection conn = getSQLConnection ()) {

      UUID fromSession = UUID.fromString (fromSessionKey);
      SingleDataResultSet resultSet = _simdll.SIM_CopySession2_SP (conn, fromSession, sessionName, sessionDescription, null);
      // resultSet.setFixNulls (true);
      DbResultRecord r = (resultSet.getCount () > 0 ? resultSet.getRecords ().next () : null);
      if (r != null) {
        String status = r.get ("status");

        if ("failed".equalsIgnoreCase (status)) {
          ret = new ReturnStatus (status, r.<String> get ("reason"), r.<String> get ("context"));
        } else
          ret = new ReturnStatus (status);
      }
      if (ret == null)
        ret = new ReturnStatus ("failed", "Failed to copy Session");

    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running SIM_CopySession2.", e);
      throw new ReturnStatusException (e);
    }

    return ret;
  }

  @Override
  public ReturnStatus createSession (String clientName, String userId, String sessionName, String language, String sessionDescription, String sessionType) throws ReturnStatusException {
    ReturnStatus ret = null;

    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet resultSet = _simdll.SIM_CreateSession_SP (conn, clientName, userId, sessionName, language,
          sessionDescription, ConverterUtil.sessionTypeStrToInt (sessionType));
      DbResultRecord r = (resultSet.getCount () > 0 ? resultSet.getRecords ().next () : null);
      if (r != null) {
        String status = r.get ("status");

        if ("failed".equalsIgnoreCase (status)) {
          ret = new ReturnStatus (status, r.<String> get ("reason"), r.<String> get ("context"));
        } else {
          // send back sessionkey as reason if create was successful.
          // ret = new ReturnStatus(sqlDr["status"].ToString(), "sessionkey=" +
          // sqlDr["sessionKey"].ToString() + "&sessionid=" +
          // sqlDr["sessionID"].ToString());

          UUID sessionKey = r.<UUID> get ("sessionKey");
          String sessionId = r.<String> get ("sessionId");
          String reason = String.format ("sessionkey=%s&sessionid=%s", sessionKey.toString (), sessionId);
          ret = new ReturnStatus (status, reason);
        }
      }
      if (ret == null)
        ret = new ReturnStatus ("failed", "Failed to create new Session");

    } catch (SQLException e) {
      _logger.error ("SessionDao.validateUser: error running SIM_CopySession2.", e);
      throw new ReturnStatusException (e);
    }

    return ret;
  }

  @Override
  public ReturnStatus alterSessionTest (String sessionKey, String testKey, String iterations, String opportunities, String meanProficiency, String sdProficiency, String strandCorrelation,
      String handScoreItemTypes) throws ReturnStatusException {

    ReturnStatus ret = null;

    try (SQLConnection conn = getSQLConnection ()) {

      UUID session = UUID.fromString (sessionKey);
      SingleDataResultSet rs = _simdll.SIM_AlterSessionTest2_SP (conn, session, testKey,
          new Integer (iterations), new Integer (opportunities), new Float (meanProficiency),
          new Float (sdProficiency), new Float (strandCorrelation),
          (StringUtils.isEmpty (handScoreItemTypes) ? StringUtils.EMPTY : handScoreItemTypes));

      DbResultRecord r = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (r != null) {
        String status = r.get ("status");

        if ("failed".equalsIgnoreCase (status)) {
          ret = new ReturnStatus (status, r.<String> get ("reason"), r.<String> get ("context"));
        } else
          ret = new ReturnStatus (status);
      }
      if (ret == null)
        ret = new ReturnStatus ("failed", "Failed to Update Session Test");

    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    return ret;
  }

  @Override
  public ReturnStatus addSessionTests (String sessionKey, String testKey, int iterations, int opportunities, float meanProficiency, float sdProficiency, float strandCorrelation,
      String handScoreItemTypes) throws ReturnStatusException {

    ReturnStatus ret = null;
    try (SQLConnection conn = getSQLConnection ()) {
      // TODO EF: anything we need to do about null/zero values for
      // iterations, opportunities, meanProficiencies,
      // sdProficiency and strandCorrelation ?
      UUID session = UUID.fromString (sessionKey);
      SingleDataResultSet rs = _simdll.SIM_AddSessionTest2_SP (conn, session, testKey,
          iterations, opportunities, meanProficiency,
          sdProficiency, strandCorrelation, handScoreItemTypes);

      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", "Failed to add " + testKey);
        else
          ret = new ReturnStatus (status);
      }
    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to add new Session Tests");

    return ret;
  }

  @Override
  public ReturnStatus deleteSession (String sessionKey) throws ReturnStatusException {

    ReturnStatus ret = null;

    try (SQLConnection conn = getSQLConnection ()) {

      UUID session = UUID.fromString (sessionKey);
      SingleDataResultSet rs = _simdll.SIM_DeleteSession_SP (conn, session);
      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }

    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to Delete Session");

    return ret;
  }

  @Override
  public ReturnStatus deleteSessionTest (String sessionKey, String testKey) throws ReturnStatusException {

    ReturnStatus ret = null;
    try (SQLConnection conn = getSQLConnection ()) {

      UUID session = UUID.fromString (sessionKey);
      SingleDataResultSet rs = _simdll.SIM_DeleteTest_SP (conn, session, testKey);
      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }

    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to Delete Test from Session");

    return ret;
  }

  @Override
  public ReturnStatus deleteSessionOppData (String sessionKey) throws ReturnStatusException {
    return deleteSessionOppData (sessionKey, null);
  }

  @Override
  public ReturnStatus deleteSessionOppData (String sessionKey, String testKey) throws ReturnStatusException {
    ReturnStatus ret = null;

    try (SQLConnection conn = getSQLConnection ()) {

      UUID session = UUID.fromString (sessionKey);

      // check if simulation's status is "RUNNING".
      // if not, try to clear sess opp data.
      String sim_status = _simdll.sim_GetSimStatus (conn, session);

      if (sim_status != null && ("running".equalsIgnoreCase (sim_status)))
        return new ReturnStatus ("failed", "Session Opportunity data cannot be cleared because a simulation is running.  Please cancel the simulation first.");

      if (StringUtils.isEmpty (testKey))
        testKey = null;
      // TODO .Net code increases cmd.CommandTimeout to 180 sec from default 30
      // However in java port we execute multiple requests sent to database
      // server,
      // rather than just one request to execute stored procedure done by .Net
      // ??? conn.setNetworkTimeout (Executors.newFixedThreadPool(numThreads),
      // 180000);
      SingleDataResultSet rs = _simdll.SIM_ClearSessionOpportunityData_SP (conn, session, testKey);
      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }

    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    // if return value has not been set, send a generic failed message.
    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to Delete Session Opportunity data.");

    return ret;
  }

  @Override
  public String getSessionSimStatusString (String sessionKey) throws ReturnStatusException {

    String sim_status = null;
    try (SQLConnection conn = getSQLConnection ()) {

      UUID session = UUID.fromString (sessionKey);
      sim_status = _simdll.sim_GetSimStatus (conn, session);

    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }
    return sim_status;
  }

  @Override
  public ReturnStatus alterSegment (String sessionKey, String testKey, String segmentKey, String startAbility, String startInfo, String minItems, String maxItems, String ftStartPos, String ftEndPos,
      String ftMinItems, String ftMaxItems, String bpWeight, String cset1size, String cset2InitialRandom, String cset2Random, String itemWeight, String abilityOffset, String selectionAlgorithm,
      String cset1Order, String rcAbilityWeight, String abilityWeight, String precisionTargetNotMetWeight, String precisionTargetMetWeight, String precisionTarget, String adaptiveCut,
      String tooCloseSEs, String terminationMinCount, String terminationOverallInfo, String terminationRCInfo, String terminationTooClose, String terminationFlagsAnd) throws ReturnStatusException {

    ReturnStatus ret = null;
    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_AlterSegment2_SP (conn, UUID.fromString (sessionKey), testKey,
          segmentKey, Float.parseFloat (startAbility), Float.parseFloat (startInfo),
          Integer.parseInt (minItems), Integer.parseInt (maxItems),
          Float.parseFloat (bpWeight), Integer.parseInt (cset1size),
          Integer.parseInt (cset2InitialRandom), Integer.parseInt (cset2Random),
          Float.parseFloat (itemWeight), Float.parseFloat (abilityOffset), selectionAlgorithm, cset1Order,
          Integer.parseInt (ftMinItems), Integer.parseInt (ftMaxItems),
          Integer.parseInt (ftStartPos), Integer.parseInt (ftEndPos),
          Float.parseFloat (rcAbilityWeight), Float.parseFloat (abilityWeight),
          Float.parseFloat (precisionTargetMetWeight), Float.parseFloat (precisionTargetNotMetWeight),
          (!StringUtils.isEmpty (precisionTarget) ? Float.parseFloat (precisionTarget) : null),
          (!StringUtils.isEmpty (adaptiveCut) ? Float.parseFloat (adaptiveCut) : null),
          (!StringUtils.isEmpty (tooCloseSEs) ? Float.parseFloat (tooCloseSEs) : null),
          Boolean.parseBoolean (terminationOverallInfo), Boolean.parseBoolean (terminationRCInfo),
          Boolean.parseBoolean (terminationMinCount), Boolean.parseBoolean (terminationTooClose),
          Boolean.parseBoolean (terminationFlagsAnd));

      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }

    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to Alter Segment");

    return ret;
  }

  @Override
  public ReturnStatus alterSegmentContentLevel (String sessionKey, String testKey, String segmentKey, String contentLevel, String minItems, String maxItems, String bpWeight, String isStrictMax)
      throws ReturnStatusException {

    ReturnStatus ret = null;

    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_AlterSegmentContentLevel_SP (conn, UUID.fromString (sessionKey),
          testKey, segmentKey, contentLevel, Integer.parseInt (minItems), Integer.parseInt (maxItems),
          Float.parseFloat (bpWeight), Boolean.parseBoolean (isStrictMax));

      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }
    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to Alter Segment Content Level");

    return ret;
  }

  @Override
  public ReturnStatus changeStrandAsContentLevel (String sessionKey, String testKey, String segmentKey, String strand) throws ReturnStatusException {

    ReturnStatus ret = null;
    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_ChangeSegmentReportingCategoryAsNonReportingCategory_SP (conn,
          UUID.fromString (sessionKey), testKey, segmentKey, strand);

      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }
    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to change reporting category as non reporting category");

    return ret;
  }

  @Override
  public ReturnStatus changeContentLevelAsStrand (String sessionKey, String testKey, String segmentKey, String contentLevel) throws ReturnStatusException {

    ReturnStatus ret = null;
    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_ChangeSegmentNonReportingCategoryAsReportingCategory_SP (conn,
          UUID.fromString (sessionKey), testKey, segmentKey, contentLevel);

      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }
    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }
    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to change non reporting category as reporting category");

    return ret;
  }

  @Override
  public ReturnStatus alterItemProperties (String sessionKey, String testKey, String segmentKey, String itemKey, String isActive, String isRequired) throws ReturnStatusException {

    ReturnStatus ret = null;
    try (SQLConnection conn = getSQLConnection ()) {

      // TODO Shiva isActive and isRequester are bits. should they be parsed
      // into Boolean?

      SingleDataResultSet rs = _simdll.SIM_AlterSegmentItem_SP (conn, UUID.fromString (sessionKey),
          testKey, segmentKey, itemKey, Boolean.parseBoolean (isActive), Boolean.parseBoolean (isRequired));

      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }
    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }

    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to Alter Segment Item");

    return ret;
  }

  @Override
  public ReturnStatus alterItemGroupProperties (String sessionKey, String testKey, String segmentKey, String groupID, String maxItems) throws ReturnStatusException {

    ReturnStatus ret = null;
    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_AlterSegmentItemgroup_SP (conn, UUID.fromString (sessionKey),
          testKey, segmentKey, groupID, Integer.parseInt (maxItems));

      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }
    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }
    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to Alter Segment Item Group");

    return ret;
  }

  @Override
  public ReturnStatus alterSegmentStrand (String sessionKey, String testKey, String segmentKey, String strand, String minItems, String maxItems, String bpWeight, String isStrictMax,
      String startAbility, String startInfo, String adaptiveCut, String scalar, String abilityWeight, String precisionTargetNotMetWeight, String precisionTargetMetWeight, String precisionTarget)
      throws ReturnStatusException {

    ReturnStatus ret = null;
    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_AlterSegmentStrand2_SP (conn, UUID.fromString (sessionKey),
          testKey, segmentKey, strand, Integer.parseInt (minItems), Integer.parseInt (maxItems),
          Float.parseFloat (bpWeight), Boolean.parseBoolean (isStrictMax),
          Float.parseFloat (startAbility), Float.parseFloat (startInfo),
          (StringUtils.isNotEmpty (adaptiveCut) ? Float.parseFloat (adaptiveCut) : null),
          (StringUtils.isNotEmpty (scalar) ? Float.parseFloat (scalar) : null),
          Float.parseFloat (abilityWeight), Float.parseFloat (precisionTargetNotMetWeight),
          Float.parseFloat (precisionTargetMetWeight), Float.parseFloat (precisionTarget));

      DbResultRecord record = (rs.getCount () > 0 ? rs.getRecords ().next () : null);
      if (record != null) {
        String status = record.<String> get ("status");
        if ("failed".equalsIgnoreCase (status))
          ret = new ReturnStatus ("failed", record.<String> get ("reason"), record.<String> get ("context"));
        else
          ret = new ReturnStatus (status);
      }
    } catch (Exception ex) {
      _logger.error (ex.getMessage ());
      throw new ReturnStatusException (ex);
    }
    if (ret == null)
      ret = new ReturnStatus ("failed", "Failed to Alter Segment Strand");

    return ret;
  }

  @Override
  public ReturnStatus alterItemSelectionParameter (String sessionKey, String testKey, String segmentKey, String bpElementID, String paramName, String paramValue) throws ReturnStatusException {
    try (SQLConnection conn = getSQLConnection ()) {
      UUID session = UUID.fromString (sessionKey);
      _simdll.SIM_AlterItemSelectionParameter (conn, session, testKey, segmentKey, bpElementID, paramName, paramValue);
    } catch (SQLException e) {
      _logger.error ("SessionDao.getItemSelectionParameters: error running SIM_GetItemSelectionParameters", e);
      throw new ReturnStatusException (e);
    }
    return new ReturnStatus ("success");
  }

  @Override
  public ItemSelectionParams getItemSelectionParameters (String sessionKey, String testKey) throws ReturnStatusException {
    ItemSelectionParams itemSelectionParams = new ItemSelectionParams ();
    try (SQLConnection conn = getSQLConnection ()) {
      UUID session = UUID.fromString (sessionKey);
      SingleDataResultSet rs = _simdll.SIM_GetItemSelectionParameters (conn, session, testKey);
      Iterator<DbResultRecord> iterator = rs.getRecords ();
      while (iterator.hasNext ()) {
        DbResultRecord dr = iterator.next ();
        itemSelectionParams.add (new ItemSelectionParam (
            dr.<String> get ("bpelementtype"),
            dr.<String> get ("_fk_adminsubject"),
            dr.<String> get ("bpelementid"),
            dr.<String> get ("name"),
            dr.<String> get ("value"),
            dr.<String> get ("label")
            ));
      }

    } catch (SQLException e) {
      _logger.error ("SessionDao.getItemSelectionParameters: error running SIM_GetItemSelectionParameters", e);
      throw new ReturnStatusException (e);
    }
    return itemSelectionParams;
  }

  @Override
  public ReturnStatus addItemSelectionParameterDefaultRecords (List<ItemSelectionParamTemplate> dt) throws ReturnStatusException {
    try (SQLConnection conn = getSQLConnection ()) {
      _simdll.SIM_DeleteAllItemSelectionParameterDefaultRecords (conn);
      for (ItemSelectionParamTemplate d : dt) {
        _simdll.SIM_AddItemSelectionParameterDefaultRecord (conn, d.getAlgorithmType (), d.getEntityType (), d.getName (), d.getValue (), d.getLabel ());
      }
    } catch (SQLException e) {
      _logger.error ("SessionDao.addItemSelectionParameterDefaultRecords: error running SIM_DeleteAllItemSelectionParameterDefaultRecords or SIM_AddItemSelectionParameterDefaultRecord", e);
      throw new ReturnStatusException (e);
    }
    return new ReturnStatus ("success");
  }

  @Override
  public SimReport getReportSummaryStats (String sessionKey, String testKey) throws ReturnStatusException {

    SimReport reportSummaryStats;

    try (SQLConnection conn = getSQLConnection ()) {

      // TODO Where to set the timeout? 2 minutes to complete summary
      // ??? conn.setNetworkTimeout (Executor executor, int millisec)
      MultiDataResultSet sets = _simdll.SIM_ReportSummaryStats_SP (conn, UUID.fromString (sessionKey), testKey);

      reportSummaryStats = this.populateSimReport (sets);

    } catch (SQLException e) {
      _logger.error ("SessionDao.getReportSummaryStats: error running SIM_ReportSummaryStats.", e);
      throw new ReturnStatusException (e);
    }
    return reportSummaryStats;
  }

  @Override
  public SimReport getReportBPSummary (String sessionKey, String testKey) throws ReturnStatusException {

    SimReport reportSummaryStats;

    try (SQLConnection conn = getSQLConnection ()) {

      // TODO Where to set the timeout? 2 minutes to complete summary
      // ??? conn.setNetworkTimeout (Executor executor, int millisec)
      MultiDataResultSet sets = _simdll.SIM_ReportBPSummary_SP (conn, UUID.fromString (sessionKey), testKey);

      reportSummaryStats = this.populateSimReport (sets);

    } catch (SQLException e) {
      _logger.error ("SessionDao.getReportSummaryStats: error running SIM_ReportBPSummary.", e);
      throw new ReturnStatusException (e);
    }
    return reportSummaryStats;
  }

  @Override
  public SimReport getReportScores (String sessionKey, String testKey) throws ReturnStatusException {

    SimReport reportSummaryStats;

    try (SQLConnection conn = getSQLConnection ()) {

      // TODO Where to set the timeout? 1 minutes to complete summary
      // ??? conn.setNetworkTimeout (Executor executor, int millisec)

      MultiDataResultSet sets = _simdll.SIM_ReportScores_SP (conn, UUID.fromString (sessionKey), testKey);

      reportSummaryStats = this.populateSimReport (sets);

    } catch (SQLException e) {
      _logger.error ("SessionDao.getReportScores: error running SIM_ReportScores.", e);
      throw new ReturnStatusException (e);
    }
    return reportSummaryStats;
  }

  @Override
  public SimReport getReportFieldTestDistribution (String sessionKey, String testKey) throws ReturnStatusException {

    SimReport reportSummaryStats;

    try (SQLConnection conn = getSQLConnection ()) {

      // TODO Where to set the timeout? 1 minutes to complete summary
      // ??? conn.setNetworkTimeout (Executor executor, int millisec)

      MultiDataResultSet sets = _simdll.SIM_FieldtestDistribution_SP (conn, UUID.fromString (sessionKey), testKey);

      reportSummaryStats = this.populateSimReport (sets);
    } catch (SQLException e) {
      _logger.error ("SessionDao.getReportFieldTestDistribution: error running SIM_FieldTestDistribution.", e);
      throw new ReturnStatusException (e);
    }

    return reportSummaryStats;
  }

  @Override
  public SimReport getReportItemDistribution (String sessionKey, String testKey) throws ReturnStatusException {

    SimReport reportSummaryStats;

    try (SQLConnection conn = getSQLConnection ()) {

      // TODO Where to set the timeout? 90 sec to complete summary
      // ??? conn.setNetworkTimeout (Executor executor, int millisec)

      MultiDataResultSet sets = _simdll.SIM_ItemDistribution_SP (conn, UUID.fromString (sessionKey), testKey);

      reportSummaryStats = this.populateSimReport (sets);
    } catch (SQLException e) {
      _logger.error ("SessionDao.getReportItemDistribution: error running SIM_ItemDistribution.", e);
      throw new ReturnStatusException (e);
    }
    return reportSummaryStats;
  }

  @Override
  public SimReport getReportOpportunities (String sessionKey, String testKey) throws ReturnStatusException {

    SimReport reportSummaryStats;

    try (SQLConnection conn = getSQLConnection ()) {

      // TODO Where to set the timeout? 90 sec to complete summary
      // ??? conn.setNetworkTimeout (Executor executor, int millisec)

      MultiDataResultSet sets = _simdll.SIM_ReportOpportunities_SP (conn, UUID.fromString (sessionKey), testKey);

      reportSummaryStats = this.populateSimReport (sets);
    } catch (SQLException e) {
      _logger.error ("SessionDao.getReportOpportunities: error running SIM_ReportOpportunities.", e);
      throw new ReturnStatusException (e);
    }
    return reportSummaryStats;
  }

  @Override
  public SimReport getReportItems (String sessionKey, String testKey) throws ReturnStatusException {

    SimReport reportSummaryStats;

    try (SQLConnection conn = getSQLConnection ()) {

      // TODO Where to set the timeout? 120 sec to complete summary
      // ??? conn.setNetworkTimeout (Executor executor, int millisec)

      MultiDataResultSet sets = _simdll.SIM_ReportItems_SP (conn, UUID.fromString (sessionKey), testKey);

      reportSummaryStats = this.populateSimReport (sets);
    } catch (SQLException e) {
      _logger.error ("SessionDao.getReportItems: error running SIM_ReportItems.", e);
      throw new ReturnStatusException (e);
    }
    return reportSummaryStats;
  }

  @Override
  public SimReport getFormDistributions (String sessionKey, String testKey) throws ReturnStatusException {

    SimReport reportSummaryStats;

    try (SQLConnection conn = getSQLConnection ()) {
      // TODO Where to set the timeout? 120 sec to complete summary
      // ??? conn.setNetworkTimeout (Executor executor, int millisec)

      MultiDataResultSet sets = _simdll.SIM_ReportFormDistributions_SP (conn, UUID.fromString (sessionKey), testKey);

      reportSummaryStats = this.populateSimReport (sets);
    } catch (SQLException e) {
      _logger.error ("SessionDao.getFormDistributions: error running SIM_ReportFormDistributions.", e);
      throw new ReturnStatusException (e);
    }
    return reportSummaryStats;
  }

  @Override
  public BlueprintValidationStatus validateBlueprints (String sessionKey) throws ReturnStatusException {

    BlueprintValidationStatus ret = new BlueprintValidationStatus ();

    try (SQLConnection conn = getSQLConnection ()) {

      MultiDataResultSet mds = _simdll.SIM_ValidateBlueprints_SP (conn, UUID.fromString (sessionKey));

      int resultSetSize = mds.getCount ();
      if (resultSetSize > 0) {
        Iterator<DbResultRecord> drBlueprintValidation = mds.get (0).getRecords ();
        if (drBlueprintValidation.hasNext ()) {
          DbResultRecord r = drBlueprintValidation.next ();
          ret.setStatus (r.<String> get ("status"));
          ret.setNumFatals (r.<Integer> get ("fatals"));
          ret.setNumWarnings (r.<Integer> get ("warnings"));
        }

        // one table returned - success, if two - failure, otherwise - error:
        // unexpected
        if (resultSetSize == 2) {
          List<BlueprintValidationError> errList = new ArrayList<BlueprintValidationError> ();
          Iterator<DbResultRecord> drBlueprintvalidationErr = mds.get (1).getRecords ();
          while (drBlueprintvalidationErr.hasNext ()) {
            DbResultRecord r = drBlueprintvalidationErr.next ();

            BlueprintValidationError bpValErr = new BlueprintValidationError ();
            bpValErr.setSeverity (r.<String> get ("severity"));
            bpValErr.setTest (r.<String> get ("object"));
            bpValErr.setError (r.<String> get ("error"));
            errList.add (bpValErr);
          }
          ret.setErrors (errList);
        }
      }
    } catch (SQLException e) {
      _logger.error ("SessionDao.validateBlueprints: error running SIM_ValidateBlueprints.", e);
      throw new ReturnStatusException (e);
    }
    return ret;
  }

  @Override
  public ReturnStatus setSessionAbort (String sessionKey, boolean abort) throws ReturnStatusException {

    try (SQLConnection conn = getSQLConnection ()) {

      // there is no success/failure return from this stored procedure
      _simdll.SIM_SetSessionAbort_SP (conn, UUID.fromString (sessionKey), abort);

    } catch (SQLException e) {
      _logger.error ("SessionDao.setSessionAbort: error running SIM_SetSessionAbort.", e);
      throw new ReturnStatusException (e);
    }
    return new ReturnStatus ("success", "Session abort status has been updated.");
  }

  @Override
  public ReturnStatus setSimulationRunProperties (String sessionKey) throws ReturnStatusException {

    ReturnStatus ret = null;

    try (SQLConnection conn = getSQLConnection ()) {
      String status = "running";
      int rowsAffected = _simdll.sim_setSimulationRunProps (conn, UUID.fromString (sessionKey), status);

      if (rowsAffected < 1) {
        ret = new ReturnStatus ("failed", "Failed to update sim_status and sim_start.");
      } else {
        ret = new ReturnStatus ("success", "sim_status and sim_start have been updated.");
      }

    } catch (SQLException e) {
      _logger.error ("SessionDao.setSimulationRunProperties: error updating Session table.", e);
      throw new ReturnStatusException (e);
    }

    return ret;
  }

  @Override
  public ReturnStatus setSimulationErrorProperties (String sessionKey) throws ReturnStatusException {

    ReturnStatus ret = null;

    try (SQLConnection conn = getSQLConnection ()) {
      String status = "error";
      int rowsAffected = _simdll.sim_setSimulationErrorProps (conn, UUID.fromString (sessionKey), status);

      if (rowsAffected < 1) {
        ret = new ReturnStatus ("failed", "Failed to update sim_status to 'error'.");
      } else {
        ret = new ReturnStatus ("success");
      }
    } catch (SQLException e) {
      _logger.error ("SessionDao.setSimulationRunProperties: error updating Session table.", e);
      throw new ReturnStatusException (e);
    }

    return ret;
  }

  @Override
  public SimulationErrors getSimulationErrors (String sessionKey) throws ReturnStatusException {

    SimulationErrors simErrors = new SimulationErrors ();

    try (SQLConnection conn = getSQLConnection ()) {

      SingleDataResultSet rs = _simdll.SIM_GetErrors_SP (conn, UUID.fromString (sessionKey));
      Iterator<DbResultRecord> dr = rs.getRecords ();
      while (dr.hasNext ()) {
        DbResultRecord r = dr.next ();
        SimulationError simError = new SimulationError ();
        simError.setProcname (r.<String> get ("procname"));
        simError.setNumErrors (r.<Integer> get ("numErrors"));

        simErrors.add (simError);
      }
    } catch (SQLException e) {
      _logger.error ("SessionDao.getSimulationErrors: error running SIM_GetErrors.", e);
      throw new ReturnStatusException (e);
    }

    return simErrors;
  }

  @Override
  public ReturnStatus publishSession (String sessionKey) throws ReturnStatusException {

    ReturnStatus ret = null;

    // TODO in Java port we do not publish simulation results into SIMP_xxx
    // tables
    // That will be addressed later

    ret = new ReturnStatus ("failed", "PublishSession funtionality not implemented");

    return ret;
  }  

  @Override
  public SimPubSession getSessionDataForPublish (String sessionKey) throws ReturnStatusException {
    SimPubSession simPubSession = new SimPubSession(sessionKey);
    try (SQLConnection conn = getSQLConnection ()) {
      UUID session = UUID.fromString (sessionKey);      
      MultiDataResultSet resultSets = _simdll.SIM_GetSessionForPublish (conn, session);
      Iterator<SingleDataResultSet> rs = resultSets.getResultSets ();

      // 1. Session table, Skip it
      if (rs.hasNext ()) {
        rs.next ();
      }

      // 2. Session Test table
      if (rs.hasNext ()) {
        Iterator<DbResultRecord> drSegmentTests = rs.next ().getRecords ();
        while (drSegmentTests.hasNext ()){
          SimPubSessionTest sessionTest = new SimPubSessionTest();
          loadSimPubSessionTest(sessionTest, drSegmentTests.next());
          simPubSession.addTest (sessionTest);
        }                
      }

      Hashtable<String, SimPubSegment> allSegments = new Hashtable<String, SimPubSegment>();

      // 3. Segments
      if (rs.hasNext ()) {
        Iterator<DbResultRecord> drSegments = rs.next ().getRecords ();
        while (drSegments.hasNext ()){
          SimPubSegment segment = new SimPubSegment();
          loadSimPubSegment(segment, drSegments.next());   
          simPubSession.getTest (segment.getAdminSubjectKey ()).addSegment (segment);
          allSegments.put (segment.getSegmentKey (), segment);
        }                
      }

      // 4. Content Level or Strands
      if (rs.hasNext ()) {
        Iterator<DbResultRecord> drContentLevels = rs.next ().getRecords ();
        while (drContentLevels.hasNext ()){
          SimPubSegmentContentLevel contentLevel = new SimPubSegmentContentLevel();
          loadSimPubContentLevel(contentLevel, drContentLevels.next());   
          allSegments.get (contentLevel.getSegmentKey ()).addContentLevel (contentLevel);
        }                
      }

      // 5. Affinity groups
      if (rs.hasNext ()) {
        Iterator<DbResultRecord> drAffinityGroups = rs.next ().getRecords ();
        while (drAffinityGroups.hasNext ()){
          SimPubSegmentContentLevel affinityGroup = new SimPubSegmentContentLevel();
          loadSimPubContentLevel(affinityGroup, drAffinityGroups.next());   
          allSegments.get (affinityGroup.getSegmentKey ()).addContentLevel (affinityGroup);
        }                
      }

      // 6. Item groups
      if (rs.hasNext ()) {
        Iterator<DbResultRecord> drItemGroups = rs.next ().getRecords ();
        while (drItemGroups.hasNext ()){
          SimPubSegmentItemGroup itemGroup = new SimPubSegmentItemGroup();
          loadSimPubItemGroup(itemGroup, drItemGroups.next());   
          allSegments.get (itemGroup.getSegmentKey ()).addItemGroup (itemGroup);
        }                
      }

      // 7. Items
      if (rs.hasNext ()) {
        Iterator<DbResultRecord> drItems = rs.next ().getRecords ();
        while (drItems.hasNext ()){
          SimPubSegmentItem item = new SimPubSegmentItem();
          loadSimPubItem(item, drItems.next());             
          SimPubSegmentItemGroup itemGroup = allSegments.get (item.getSegmentKey ()).getItemGroup (item.getGroupID ());
          if (itemGroup == null) {
            itemGroup = new SimPubSegmentItemGroup(item.getSegmentKey (), item.getGroupID ());
            allSegments.get (itemGroup.getSegmentKey ()).addItemGroup (itemGroup);
          }
          itemGroup.addItem (item);
        }                
      } 

    } catch (SQLException e) {
      _logger.error ("SessionDao.getSessionDataForPublish: error getting session data", e);
      throw new ReturnStatusException (e);
    }
    return simPubSession;
  }  

  @Override
  public SimPubTestPackage getSessionTestPackage (String sessionKey, String testKey) throws ReturnStatusException {
    SimPubTestPackage simTestPackage = new SimPubTestPackage();
    try (SQLConnection conn = getSQLConnection ()) {
      UUID session = UUID.fromString (sessionKey);      
      SingleDataResultSet rs = _simdll.SIM_GetSessionTestPackage (conn, session, testKey);

      Iterator<DbResultRecord> iterator = rs.getRecords ();
      if (iterator.hasNext ()) {   
        DbResultRecord dr = iterator.next ();        
        String xml = (dr.<String> get ("testpackage"));
        DocumentBuilderFactory documentBuildFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuildFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(new InputSource(new StringReader(xml)));
        doc.getDocumentElement().normalize();        
        simTestPackage.setTestPackage (doc);
      }      
    } catch (Exception e) {
      simTestPackage.setErrorMsg ("SessionDao.getSessionTestPackage: error getting session data");
      _logger.error (simTestPackage.getErrorMsg (), e);
      throw new ReturnStatusException (e); 
    }
    return simTestPackage;
  }

  private BlueprintSegment createBlueprintSegment (DbResultRecord dr) {
    BlueprintSegment seg = new BlueprintSegment ();
    seg.setTestKey (dr.<String> get ("_efk_AdminSubject"));
    seg.setSegmentKey (dr.<String> get ("_efk_Segment"));
    seg.setStartAbility (dr.<Float> get ("StartAbility"));
    seg.setStartInfo (dr.<Float> get ("StartInfo"));
    seg.setMinItems (dr.<Integer> get ("MinItems"));
    seg.setMaxItems (dr.<Integer> get ("MaxItems"));
    seg.setFtStartPos (dr.<Integer> get ("FtStartPos"));
    seg.setFtEndPos (dr.<Integer> get ("FtEndPos"));
    seg.setFtMinItems (dr.<Integer> get ("FtMinItems"));
    seg.setFtMaxItems (dr.<Integer> get ("FtMaxItems"));
    seg.setFormSelection (dr.<String> get ("formSelection"));
    seg.setBlueprintWeight (dr.<Float> get ("blueprintWeight"));
    seg.setCset1Size (dr.<Integer> get ("cset1size"));
    seg.setCset2Random (dr.<Integer> get ("cset2Random"));
    seg.setCset2InitialRandom (dr.<Integer> get ("cset2InitialRandom"));
    seg.setLoadConfig (dr.<Long> get ("loadConfig"));
    seg.setUpdateConfig (dr.<Long> get ("updateConfig"));
    seg.setItemWeight (dr.<Float> get ("itemWeight"));
    seg.setAbilityOffset (dr.<Float> get ("abilityOffset"));
    seg.setSegmentPosition (dr.<Integer> get ("segmentPosition"));
    seg.setSegmentID (dr.<String> get ("segmentID"));
    seg.setSelectionAlgorithm (dr.<String> get ("selectionAlgorithm"));
    seg.setCset1Order (dr.<String> get ("cset1Order"));
    seg.setOpActiveItemCount (dr.<Integer> get ("OP Active ItemCount"));
    seg.setOpActiveGroupCount (dr.<Integer> get ("OP Active GroupCount"));
    seg.setRcAbilityWeight (dr.<Float> get ("rcAbilityWeight"));
    seg.setAbilityWeight (dr.<Float> get ("abilityWeight"));
    seg.setPrecisionTargetNotMetWeight (dr.<Float> get ("precisionTargetNotMetWeight"));
    seg.setPrecisionTargetMetWeight (dr.<Float> get ("precisionTargetMetWeight"));
    seg.setPrecisionTarget (dr.<Float> get ("precisionTarget"));
    seg.setAdaptiveCut (dr.<Float> get ("adaptiveCut"));
    seg.setTooCloseSEs (dr.<Float> get ("tooCloseSEs"));
    seg.setTerminationMinCount (dr.<Boolean> get ("terminationMinCount"));
    seg.setTerminationOverallInfo (dr.<Boolean> get ("terminationOverallInfo"));
    seg.setTerminationRCInfo (dr.<Boolean> get ("terminationRCInfo"));
    seg.setTerminationTooClose (dr.<Boolean> get ("terminationTooClose"));
    seg.setTerminationFlagsAnd (dr.<Boolean> get ("terminationFlagsAnd"));
    return seg;
  }

  private BlueprintSegmentStrand createBlueprintSegmentStrand (DbResultRecord dr) {
    BlueprintSegmentStrand strand = new BlueprintSegmentStrand ();

    strand.setSegmentKey (dr.<String> get ("_efk_Segment"));
    strand.setStrand (dr.<String> get ("strand"));
    strand.setMinItems (dr.<Integer> get ("MinItems"));
    strand.setMaxItems (dr.<Integer> get ("MaxItems"));
    strand.setIsStrictMax (dr.<Boolean> get ("IsStrictMax"));
    strand.setBlueprintWeight (dr.<Float> get ("bpweight"));
    strand.setAdaptiveCut (dr.<Float> get ("AdaptiveCut"));
    strand.setStartAbility (dr.<Float> get ("StartAbility"));
    strand.setStartInfo (dr.<Float> get ("StartInfo"));
    strand.setScalar (dr.<Float> get ("Scalar"));
    strand.setOpActiveItemCount (dr.<Integer> get ("OP Active ItemCount"));
    strand.setAbilityWeight (dr.<Float> get ("abilityWeight"));
    strand.setPrecisionTargetNotMetWeight (dr.<Float> get ("precisionTargetNotMetWeight"));
    strand.setPrecisionTargetMetWeight (dr.<Float> get ("precisionTargetMetWeight"));
    strand.setPrecisionTarget (dr.<Float> get ("precisionTarget"));
    strand.setFeatureClass (dr.<String> get ("FeatureClass"));
    strand.setIsReportingCategory (true);

    return strand;
  }

  private BlueprintSegmentContentLevel createBlueprintContentLevel (DbResultRecord dr) {
    BlueprintSegmentContentLevel level = new BlueprintSegmentContentLevel ();
    level.setSegmentKey (dr.<String> get ("_efk_Segment"));
    level.setContentLevel (dr.<String> get ("contentLevel"));
    level.setMinItems (dr.<Integer> get ("MinItems"));
    level.setMaxItems (dr.<Integer> get ("MaxItems"));
    level.setIsStrictMax (dr.<Boolean> get ("IsStrictMax"));
    level.setBlueprintWeight (dr.<Float> get ("bpweight"));
    level.setOpActiveItemCount (dr.<Integer> get ("OP Active ItemCount"));
    level.setFeatureClass (dr.<String> get ("FeatureClass"));
    level.setIsReportingCategory (false);
    return level;
  }

  private ItemProperty createItemProperty (DbResultRecord dr) {
    ItemProperty p = new ItemProperty ();
    p.setSegmentKey (dr.<String> get ("segmentKey"));
    p.setStrand (dr.<String> get ("strand"));
    p.setGroupID (dr.<String> get ("groupID"));
    p.setItemKey (dr.<String> get ("itemKey"));
    p.setIsActive (dr.<Boolean> get ("isActive"));
    p.setIsRequired (dr.<Boolean> get ("isRequired"));
    p.setIsFieldTest (dr.<Boolean> get ("isFieldTest"));
    return p;
  }

  private ItemGroupProperty createItemGroupProperties (DbResultRecord dr) {
    ItemGroupProperty gp = new ItemGroupProperty ();
    gp.setSegmentKey (dr.<String> get ("segmentKey"));
    gp.setGroupID (dr.<String> get ("groupID"));
    gp.setMaxItems (dr.<Integer> get ("maxItems"));
    gp.setActiveItems (dr.<Integer> get ("activeItems"));
    return gp;
  }

  private SimReport populateSimReport (MultiDataResultSet dsReport) {
    SimReport simReport = new SimReport ();

    List<Table> tables = new ArrayList<Table> ();
    Iterator<SingleDataResultSet> sdsIter = dsReport.getResultSets ();
    while (sdsIter.hasNext ()) {
      SingleDataResultSet sds = sdsIter.next ();

      Table table = new Table ();
      List<String> tableHeaders = new ArrayList<String> ();
      List<TableRow> tableRows = new ArrayList<TableRow> ();

      // retrieve all column headers,
      // but store them in TableHeaders in order
      // of column indexes; getColumnNames()
      // does not guarantee that order.
      // We want to display the columns in order of their indexes.
      // Remember that column indexes start from 1 rather than 0.
      int colMax = sds.getNumberOfColumns ();
      String[] arrColNames = new String[colMax + 1];
      Iterator<String> columnNames = sds.getColumnNames ();
      while (columnNames.hasNext ()) {
        String colName = columnNames.next ();
        _Ref<Integer> colNumberRef = sds.getColumnToIndex (colName);
        if (colNumberRef.get () <= colMax)
          arrColNames[colNumberRef.get ()] = colName;
      }
      for (int i = 1; i <= colMax; i++) {
        if (arrColNames[i] != null)
          tableHeaders.add (arrColNames[i]);
      }
      table.setTableHeaders (tableHeaders);

      // retrieve all table rows
      Iterator<DbResultRecord> dr = sds.getRecords ();
      while (dr.hasNext ()) {
        DbResultRecord dbRow = dr.next ();

        TableRow tableRow = new TableRow ();
        List<String> colVals = new ArrayList<String> ();
        // add values for each column
        for (String colName : tableHeaders) {
          if (dbRow.get (colName) != null)
            colVals.add (dbRow.get (colName).toString ());
          else
            colVals.add ("");
        }
        tableRow.setColVals (colVals);
        tableRows.add (tableRow);
      }
      table.setTableRows (tableRows);
      tables.add (table);
    }
    simReport.setTables (tables);

    return simReport;
  }

  private void loadSimPubItem(SimPubSegmentItem pubItem, DbResultRecord dr) {
    pubItem.setSegmentKey (dr.<String> get("_efk_segment"));
    pubItem.setGroupID (dr.<String> get("groupid"));
    pubItem.setItemKey (dr.<String> get("_efk_item"));
    pubItem.setIsActive (dr.<Boolean> get("isactive"));
    pubItem.setIsRequired (dr.<Boolean> get("isrequired"));
    pubItem.setIsFieldTest (dr.<Boolean> get("isfieldtest"));
  }

  private void loadSimPubItemGroup(SimPubSegmentItemGroup pubItemGroup, DbResultRecord dr) {
    pubItemGroup.setSegmentKey (dr.<String> get("_efk_segment"));
    pubItemGroup.setGroupID (dr.<String> get("groupid"));
  }

  private void loadSimPubContentLevel(SimPubSegmentContentLevel pubContentLevel, DbResultRecord dr) {
    pubContentLevel.setSegmentKey (dr.<String> get("_efk_segment"));
    pubContentLevel.setContentLevel (dr.<String> get("contentlevel"));
    pubContentLevel.setAdaptiveCut (dr.<Float> get("adaptivecut"));
    pubContentLevel.setStartAbility (dr.<Float> get("startability"));
    pubContentLevel.setStartInfo (dr.<Float> get("startinfo"));
    pubContentLevel.setScalar (dr.<Float> get("scalar"));
    pubContentLevel.setIsStrictMax (dr.<Boolean> get("isstrictmax"));
    pubContentLevel.setBpWeight (dr.<Float> get("bpweight"));
    pubContentLevel.setAbilityWeight (dr.<Float> get("abilityweight"));
    pubContentLevel.setPrecisionTarget (dr.<Float> get("precisiontarget"));
    pubContentLevel.setPrecisionTargetMetWeight (dr.<Float> get("precisiontargetmetweight"));
    pubContentLevel.setPrecisionTargetNotMetWeight (dr.<Float> get("precisiontargetnotmetweight"));    
  }

  private void loadSimPubSegment(SimPubSegment pubSegment, DbResultRecord dr) {    
    pubSegment.setAdminSubjectKey (dr.<String> get("_efk_adminsubject"));
    pubSegment.setSegmentKey (dr.<String> get("_efk_segment"));
    pubSegment.setStartAbility (dr.<Float> get("startability"));
    pubSegment.setStartInfo (dr.<Float> get("startinfo"));
    pubSegment.setBlueprintWeight (dr.<Float> get("blueprintweight"));
    pubSegment.setCset1size (dr.<Integer> get("cset1size"));
    pubSegment.setCset2Random (dr.<Integer> get("cset2random"));
    pubSegment.setCset2InitialRandom (dr.<Integer> get("cset2initialrandom"));
    pubSegment.setItemWeight (dr.<Float> get("itemweight"));
    pubSegment.setAbilityOffset (dr.<Float> get("abilityoffset"));
    pubSegment.setSelectionAlgorithm (dr.<String> get("selectionalgorithm"));
    pubSegment.setCset1Order (dr.<String> get("cset1order"));
    pubSegment.setAbilityWeight (dr.<Float> get("abilityweight"));
    pubSegment.setRCAbilityWeight (dr.<Float> get("rcabilityweight"));
    pubSegment.setPrecisionTarget (dr.<Float> get("precisiontarget"));
    pubSegment.setPrecisionTargetMetWeight (dr.<Float> get("precisiontargetmetweight"));
    pubSegment.setPrecisionTargetNotMetWeight (dr.<Float> get("precisiontargetnotmetweight"));
    pubSegment.setAdaptiveCut (dr.<Float> get("adaptivecut"));
    pubSegment.setTooCloseSEs (dr.<Float> get("toocloseses"));
    pubSegment.setTerminationOverallInfo (dr.<Boolean> get("terminationoverallinfo"));
    pubSegment.setTerminationRCInfo (dr.<Boolean> get("terminationrcinfo"));
    pubSegment.setTerminationMinCount (dr.<Boolean> get("terminationmincount"));
    pubSegment.setTerminationTooClose (dr.<Boolean> get("terminationtooclose"));
    pubSegment.setTerminationFlagsAnd (dr.<Boolean> get("terminationflagsand"));
  }

  private void loadSimPubSessionTest(SimPubSessionTest pubSessionTest, DbResultRecord dr) {   
    pubSessionTest.setAdminSubjectKey (dr.<String> get("_efk_adminsubject"));
  }  
}
