/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator.dal.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import TDS.Shared.Exceptions.ReturnStatusException;
import tds.dll.api.ICommonDLL;
import tds.dll.api.IProctorDLL;
import tds.dll.api.ISimDLL;
import tds.dll.api.IStudentDLL;
import tds.itemselection.base.ItemGroup;
import AIR.Common.DB.SQLConnection;
import AIR.Common.DB.results.DbResultRecord;
import AIR.Common.DB.results.MultiDataResultSet;
import AIR.Common.DB.results.SingleDataResultSet;
import AIR.Common.Helpers._Ref;
import dbsimulator.InsertedItem;
import dbsimulator.SessionTest;
import dbsimulator.SimDal;

@Repository("simDal")
public class MySqlSimDal extends SimDal {
  private static Logger _logger = LoggerFactory.getLogger(MySqlSimDal.class);

  @Autowired
  private ISimDLL _simdll;

  @Autowired
  private IStudentDLL _studentdll;

  @Autowired
  private IProctorDLL _proctordll;

  @Autowired
  private ICommonDLL _commondll;

  public MySqlSimDal() {
  }

  @Override
  public List<SessionTest> loadSessionTests() {

    List<SessionTest> sessionTests = new ArrayList<>();

    try (SQLConnection connection = getSQLConnection()) {
      UUID session = UUID.fromString(getSessionKey());
      SingleDataResultSet rs = _simdll.SIM_GetSessionTests2_SP(connection, session);
      Iterator<DbResultRecord> iterator = rs.getRecords();
      while (iterator.hasNext()) {
        DbResultRecord rec = iterator.next();
        String testkey = rec.<String> get("testkey");
        String testid = rec.<String> get("testid");
        Integer iterations = rec.<Integer> get("iterations");
        Integer opportunities = rec.<Integer> get("opportunities");
        Integer simulations = rec.<Integer> get("simulations");
        SessionTest sessionTest = new SessionTest(testid, testkey, iterations, opportunities, simulations);
        sessionTests.add(sessionTest);
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }

    return sessionTests;
  }

  @Override
  public boolean abort() {

    try (SQLConnection connection = getSQLConnection()) {
      UUID session = UUID.fromString(getSessionKey());
      boolean abort = _simdll.simGetSimAbort(connection, session);
      setAbortSimulation(abort);
      return abort;
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    // TODO just feels wrong to return false here
    return false;
  }

  @Override
  public String loadTestControls(String testKey, SessionTest sessTest) {
    try (SQLConnection connection = getSQLConnection()) {

      UUID session = UUID.fromString(getSessionKey());
      MultiDataResultSet sets = _simdll.SIM_GetTestControls2_SP(
          connection, session, testKey);

      Iterator<SingleDataResultSet> iterator = sets.getResultSets();
      if (iterator.hasNext()) {
        // First result set
        SingleDataResultSet rs = iterator.next();

        if (rs.getCount() != 1)
          return String.format("Error loading test %s", testKey);

        DbResultRecord rec = rs.getRecords().next();
        sessTest.setLanguage(rec.<String> get("language"));
        sessTest.setOpportunities(rec.<Integer> get("opportunities"));
        sessTest.setMeanProficiency(rec.<Float> get("meanProficiency"));
        sessTest.setSdProficiency(rec.<Float> get("sdProficiency"));
        sessTest.setStrandCorrelation(rec.<Float> get("strandCorrelation"));

        String handScoreItemTypes = rec.<String> get("handScoreItemTypes");
        if (StringUtils.isEmpty(handScoreItemTypes) == false) {
          String[] types = handScoreItemTypes.split(",");
          for (String type : types) {
            sessTest.addHandScoreItemTypes(type);
          }
        }
        sessTest.setItemBank(rec.<String> get("itembank"));
        sessTest.setNumThreads(rec.<Integer> get("threads"));
        sessTest.setThinkTime(rec.<Integer> get("thinktime"));
        sessTest.setTotalOpps(rec.<Integer> get("simulations"));
      }
      if (iterator.hasNext()) {
        // Second result set
        SingleDataResultSet rs = iterator.next();
        Iterator<DbResultRecord> iter = rs.getRecords();
        while (iter.hasNext()) {
          DbResultRecord rec = iter.next();
          String strand = rec.<String> get("strand");
          sessTest.addStrand(strand);
        }
      }
      return null;

    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return String.format("Error loading test %s", testKey);
  }

  @Override
  public String getItembank() {
    // Elena: This method is not called by anyone
    return null;
  }

  @Override
  public boolean clearSessionOpps(String testKey) {

    String status = null;
    try (SQLConnection connection = getSQLConnection()) {

      UUID session = UUID.fromString(getSessionKey());
      SingleDataResultSet rs = _simdll
          .SIM_ClearSessionOpportunityData_SP(connection, session);
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        status = rec.<String> get("status");
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    if ("success".equalsIgnoreCase(status))
      return true;
    else
      return false;
  }

  @Override
  public void endSession(String status) {

    try (SQLConnection connection = getSQLConnection()) {

      UUID session = UUID.fromString(getSessionKey());

      _simdll.SIM_EndSimulation(connection, session, status);

    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
  }

  @Override
  public void cleanupTest(String testkey) {

    try (SQLConnection connection = getSQLConnection()) {

      UUID session = UUID.fromString(getSessionKey());

      _simdll.SIM_CleanupSessionTest(connection, session, testkey);

    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
  }

  @Override
  public boolean clearTestOpps(String testkey) {

    String status = null;
    try (SQLConnection connection = getSQLConnection()) {

      UUID session = UUID.fromString(getSessionKey());
      SingleDataResultSet rs = _simdll
          .SIM_ClearSessionOpportunityData_SP(connection, session,
              testkey);
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        status = rec.<String> get("status");
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    if ("success".equalsIgnoreCase(status))
      return true;
    else
      return false;
  }

  @Override
  public List<String> getLoginRequirements() {

    if (getLoginReqs().isEmpty()) {
      try (SQLConnection connection = getSQLConnection()) {
        SingleDataResultSet rs = _studentdll.T_LoginRequirements(
            connection, getClientName());
        Iterator<DbResultRecord> iter = rs.getRecords();
        List<String> loginReqs = new ArrayList<>();
        while (iter.hasNext()) {
          DbResultRecord rec = iter.next();
          loginReqs.add(rec.<String> get("TDS_ID"));
        }
        setLoginReqs(loginReqs);
      } catch (SQLException se) {
        _logger.error(se.getMessage());
      } catch (ReturnStatusException re) {
        _logger.error(re.getMessage());
      }
    }
    return getLoginReqs();
  }

  @Override
  public int loginStudent(String keyValuesString, _Ref<String> error) {

    try (SQLConnection connection = getSQLConnection()) {

      Map<String, String> keyValues = new HashMap<>();
      for (String line : StringUtils.split(keyValuesString, ';')) {
        String[] parts = StringUtils.split(line, ":", 2);
        if (parts.length < 2 || StringUtils.isEmpty(parts[0])) {
          continue;
        }
        keyValues.put(parts[0], parts[1]);
      }

      MultiDataResultSet sets = _studentdll.T_Login_SP(connection,
          getClientName(), keyValues, getSessionID());
      Iterator<SingleDataResultSet> iter = sets.getResultSets();
      // First set
      if (iter.hasNext()) {
        SingleDataResultSet rs = iter.next();
        DbResultRecord rec = rs.getRecords().next();
        String status = rec.<String> get("status");
        if ("failed".equalsIgnoreCase(status)) {
          error.set(status);
          return 0;
        }
        if (rec.hasColumn("entitykey")) {
          Long ent = rec.<Long> get("entityKey");
          return ent.intValue();
        }
      }

    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return 0;
  }

  @Override
  public String openTestOpp(int testee, String testkey, _Ref<String> oppkey,
      _Ref<String> error) {

    oppkey.set(null);
    error.set(null);
    String status = null;

    try (SQLConnection connection = getSQLConnection()) {
      SingleDataResultSet rs = _studentdll.T_OpenTestOpportunity_SP(
          connection, (long) testee, testkey,
          UUID.fromString(getSessionKey()),
          UUID.fromString(getBrowserKey()));
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        status = rec.<String> get("status");
        if ("failed".equalsIgnoreCase(status)
            || "denied".equalsIgnoreCase(status))
          error.set(rec.<String> get("reason"));
        else {
          UUID opp = rec.<UUID> get("oppkey");
          if (opp != null)
            oppkey.set(opp.toString());
        }
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return status;
  }

  @Override
  public String setLanguage(String oppkey, String language, _Ref<String> error) {

    error.set(null);

    try (SQLConnection connection = getSQLConnection()) {

      SingleDataResultSet rs = _proctordll.P_ApproveAccommodations_SP(
          connection, UUID.fromString(getSessionKey()),
          (long) getProctorKey(), UUID.fromString(getBrowserKey()),
          UUID.fromString(oppkey), 0, language);
      // i.e. there was an error
      if (rs != null) {

        DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords()
            .next() : null);
        if (rec != null) {
          // Elena: there is a bug in.Net simulator, error is not set
          // here
          // but is checked in calling module
          error.set(rec.<String> get("reason"));
          return rec.<String> get("reason");
        }
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return null;
  }

  @Override
  public int insertTesteeAttribute(String oppkey, String attribute,
      String value, String context) {

    int cnt = 0;
    try (SQLConnection connection = getSQLConnection()) {

      cnt = _simdll.SIM_InsertTesteeAttribute_SP(connection,
          UUID.fromString(oppkey), attribute, value, context);
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return cnt;
  }

  @Override
  public String saveAbilityEstimate(String oppkey, int itempos,
      String strand, double theta, double info, double lambda) {

    try (SQLConnection connection = getSQLConnection()) {
      _simdll.AA_UpdateAbilityEstimates_SP(connection,
          UUID.fromString(oppkey), itempos, strand, (float) theta,
          (float) info, (float) lambda);

    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return null;
  }

  @Override
  public String approveOpp(String oppkey, _Ref<String> error) {

    String status = null;
    try (SQLConnection connection = getSQLConnection()) {

      SingleDataResultSet rs = _proctordll.P_ApproveOpportunity_SP(
          connection, UUID.fromString(getSessionKey()),
          (long) getProctorKey(), UUID.fromString(getBrowserKey()),
          UUID.fromString(oppkey));
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {

        error.set(rec.<String> get("reason"));
        status = rec.<String> get("status");
      }

    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return status;
  }

  @Override
  public String getOppStatus(String oppkey) {
    String status = null;
    try (SQLConnection connection = getSQLConnection()) {

      SingleDataResultSet rs = _simdll.GetOpportunityStatus_SP(
          connection, UUID.fromString(oppkey));
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {

        status = rec.<String> get("status");
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }

    return status;
  }

  @Override
  public boolean isOppComplete(String oppkey) {
    boolean result = false;
    try (SQLConnection connection = getSQLConnection()) {

      SingleDataResultSet rs = _studentdll.T_IsTestComplete_SP(
          connection, UUID.fromString(oppkey));
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        result = rec.<Boolean> get("iscomplete");
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return result;
  }

  @Override
  public String startTestopp(String oppkey, _Ref<Double> ability,
      _Ref<Integer> startPosition, _Ref<Integer> numSegments,
      _Ref<String> error) {
    String status = null;
    ability.set(null);
    startPosition.set(null);
    numSegments.set(null);
    error.set(null);

    try (SQLConnection connection = getSQLConnection()) {

      SingleDataResultSet rs = _studentdll.T_StartTestOpportunity_SP(
          connection, UUID.fromString(oppkey), null, null, null);
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        status = rec.<String> get("status");
        if ("failed".equalsIgnoreCase(status))
          error.set(rec.<String> get("reason"));
        else {
          // Elena: I really think that all Double in levels
          // above SimDLL should be replaced with Float
          // becase Mysql database has corresponding columns as
          // float and converting float to double on Java
          // leads to ugly results:
          // for example 0.1234 float will become
          // 0.1234000045645645 double
          String abilityStr = rec.<Float> get("initialability")
              .toString();
          Double abilityDouble = Double.parseDouble(abilityStr);
          ability.set(abilityDouble);
          startPosition.set(rec.<Integer> get("startPosition"));
          numSegments.set(rec.<Integer> get("segments"));
        }
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return status;
  }

  @Override
  public String setOppStatus(String oppkey, String status) {
    String result = null;
    try (SQLConnection connection = getSQLConnection()) {

      SingleDataResultSet rs = _commondll.SetOpportunityStatus_SP(
          connection, UUID.fromString(oppkey), status);
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {

        result = rec.<String> get("status");
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }

    return result;
  }

  @Override
  public String getItemType(String sItemKey) {

    String itemType = null;

    try (SQLConnection connection = getSQLConnection()) {
      itemType = _simdll.sim_getItemType(connection, sItemKey);
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return itemType;
  }

  @Override
  public List<InsertedItem> insertItems(String oppkey, ItemGroup itemgroup,
      int page, _Ref<String> status, _Ref<String> dateCreated,
      _Ref<String> error) {

    List<InsertedItem> items = new ArrayList<>();
    status.set(null);
    dateCreated.set(null);
    error.set(null);
    try (SQLConnection connection = getSQLConnection()) {
      // Elena: same ugly double-to-float issue
      Double groupBDouble = itemgroup.getGroupB();
      Float groupB = Float.parseFloat(groupBDouble.toString());

      MultiDataResultSet sets = _studentdll.T_InsertItems_SP(connection,
          UUID.fromString(oppkey), UUID.fromString(getSessionKey()),
          UUID.fromString(getBrowserKey()),
          itemgroup.getSegmentPosition(), itemgroup.getSegmentID(),
          page, itemgroup.getGroupID(),
          itemgroup.getItemIDString(","), ',',
          itemgroup.getNumRequired(), groupB, 0, false);

      Iterator<SingleDataResultSet> iter = sets.getResultSets();
      if (iter.hasNext()) {
        // first result set
        SingleDataResultSet rs1 = iter.next();
        DbResultRecord rec1 = (rs1.getCount() > 0 ? rs1.getRecords()
            .next() : null);
        if (rec1 != null) {
          status.set(rec1.<String> get("status"));
          if ("failed".equalsIgnoreCase(status.get())) {
            error.set(rec1.<String> get("reason"));
            return items;
          }
          dateCreated.set(rec1.<String> get("dateCreated"));
        }
      }
      // second result set
      if (iter.hasNext()) {
        SingleDataResultSet rs2 = iter.next();
        Iterator<DbResultRecord> it = rs2.getRecords();
        while (it.hasNext()) {
          InsertedItem itm = new InsertedItem();
          DbResultRecord rec2 = it.next();
          itm.setItemID(rec2.<String> get("bankitemkey"));
          itm.setPage(rec2.<Integer> get("page"));
          itm.setPosition(rec2.<Integer> get("position"));
          items.add(itm);
        }
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return items;
  }

  @Override
  public String updateResponse(String oppkey, String itemkey, int page,
      int position, String dateCreated, int sequence, int score,
      String response, _Ref<String> error, String scoreDimensions) {

    String status = null;
    error.set(null);
    try (SQLConnection connection = getSQLConnection()) {

      SingleDataResultSet rs = _studentdll.T_UpdateScoredResponse2_SP(
          connection, UUID.fromString(oppkey),
          UUID.fromString(getSessionKey()),
          UUID.fromString(getBrowserKey()), itemkey, page, position,
          dateCreated, sequence, score, response, true, true, 0,
          null, null, scoreDimensions);
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        status = rec.<String> get("status");
        if ("failed".equalsIgnoreCase(status)) {
          error.set(rec.<String> get("reason"));
        }
        setAbortSimulation(rec.<Boolean> get("abortSim"));
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return status;
  }

  @Override
  public String setOppCompleted(String oppkey, _Ref<String> error) {
    String status = null;
    error.set(null);
    try (SQLConnection connection = getSQLConnection()) {

      SingleDataResultSet rs = _studentdll.T_SetOpportunityStatus_SP(
          connection, UUID.fromString(oppkey), "completed",
          UUID.fromString(getSessionKey()),
          UUID.fromString(getBrowserKey()), null);
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        status = rec.<String> get("status");
        if ("failed".equalsIgnoreCase(status)) {
          error.set(rec.<String> get("reason"));
        }
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return status;
  }

  @Override
  public String getScoreString(String oppkey, _Ref<String> itemstring,
      _Ref<String> dateCompleted, _Ref<String> rowdelim,
      _Ref<String> coldelim, _Ref<String> error, _Ref<String> forms,
      boolean bAdaptive2) {
    String status = null;
    itemstring.set(null);
    dateCompleted.set(null);
    rowdelim.set(null);
    coldelim.set(null);
    error.set(null);
    forms.set(null);

    try (SQLConnection connection = getSQLConnection()) {
      SingleDataResultSet rs = null;
      if (bAdaptive2)

        rs = _studentdll.T_GetTestforScoring2_SP(connection,
            UUID.fromString(oppkey), null);
      else
        rs = _studentdll.T_GetTestforScoring2_SP(connection,
            UUID.fromString(oppkey));

      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        status = rec.<String> get("status");
        if ("failed".equalsIgnoreCase(status)) {
          error.set(rec.<String> get("reason"));
          return status;
        }
        itemstring.set(rec.<String> get("itemstring"));
        dateCompleted.set((rec.<Date> get("dateCompleted")).toString());
        rowdelim.set(rec.<String> get("rowdelim"));
        coldelim.set(rec.<String> get("coldelim"));
        forms.set(rec.<String> get("segmentforms"));

      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return status;
  }

  @Override
  public String insertScores(String oppkey, String scorestring,
      String rowdelim, String coldelim, _Ref<String> error) {
    String status = null;
    error.set(null);
    try (SQLConnection connection = getSQLConnection()) {
      SingleDataResultSet rs = _studentdll.S_InsertTestScores_SP(
          connection, UUID.fromString(oppkey), scorestring,
          rowdelim.charAt(0), coldelim.charAt(0));
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        status = rec.<String> get("status");
        if ("failed".equalsIgnoreCase(status)) {
          error.set(rec.<String> get("reason"));
        }
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return status;
  }

  @Override
  protected boolean openSession() {
    boolean result = false;

    try (SQLConnection connection = getSQLConnection()) {
      SingleDataResultSet rs = _simdll.SIM_OpenSession_SP(connection,
          UUID.fromString(getSessionKey()));
      DbResultRecord rec = (rs.getCount() > 0 ? rs.getRecords().next()
          : null);
      if (rec != null) {
        result = true;
        setClientName(rec.<String> get("clientname"));
        setSessionID(rec.<String> get("sessionid"));
        setProctorKey(rec.<Long> get("proctorkey"));
        UUID browserkey = rec.<UUID> get("browserkey");
        if (browserkey != null)
          setBrowserKey(browserkey.toString());
        else
          setBrowserKey(null);
        setLang(rec.<String> get("language"));
        setProctorDelay(rec.<Integer> get("proctordelay"));
        setItembank(rec.<String> get("itembank"));
      }
    } catch (SQLException se) {
      _logger.error(se.getMessage());
    } catch (ReturnStatusException re) {
      _logger.error(re.getMessage());
    }
    return result;
  }

}
