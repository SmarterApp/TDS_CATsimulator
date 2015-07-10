
/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import AIR.Common.Helpers._Ref;
import AIR.Common.Sql.AbstractDateUtilDll;
import tds.itemselection.api.IAIROnline;
import tds.itemselection.base.ItemGroup;
import tds.itemselection.base.TestItem;

public class TestThread implements Runnable {
  private boolean _done = false;
  private int _itemcount;
  private String _opportunityKey;

  private SessionTest _test = null;
  private SimDal _dal;
  private IAIROnline _air;
  private Thread _thread;
  private String _sim_messages; // accumulate simulation messages and post in the database somewhere, perhaps in SystemErrors table?
  private VirtualStudent _student = null;
  private int _numStudents = 0;
  private String _loginKeys;

  public TestThread(VirtualStudent vStudent, int iterations, String loginKeys, SessionTest test, IAIROnline air, SimDal dal) {
    _loginKeys = loginKeys;
    _student = vStudent;
    _numStudents = iterations;

    _test = test;
    _dal = dal;
    _air = air;
  }


  @Override
  public void run() {
    runStudents(null);
  }

  public boolean isDone() {
    return _done;
  }


  public void setDone(boolean _done) {
    this._done = _done;
  }


  public int getItemCount() {
    return _itemcount;
  }


  public void setItemCount(int _itemcount) {
    this._itemcount = _itemcount;
  }


  public String getOpportunityKey() {
    return _opportunityKey;
  }


  public void setOpportunityKey(String _opportunityKey) {
    this._opportunityKey = _opportunityKey;
  }


  public void setThread(Thread value) {
    _thread = value;
  }
  public Thread getThread() {
    return _thread;
  }

  public VirtualStudent getStudent() {
    return _student;
  }

  public void setStudent(VirtualStudent value) {
    _student = value;
  }

  public String getErrors() {
    return _sim_messages;
  }

  public void runStudents(Object threadContext) {
    _done = false;
    for (int i = 0; i < _numStudents; ++i) {
      runStudent();
      String errors = getErrors(); // Exit, if we are errored
      if (!(errors == null || errors.isEmpty()))
        break; 			
    }
    _done = true;
  }

  public void runStudent() {
    try {
      int testee;
      _Ref<String> error = new _Ref<String>();
      _itemcount = 0;
      testee = _dal.loginStudent(_loginKeys, error);
      if (error.get() != null) {
        _sim_messages += error;
        return;
      }
      _student.initialize(testee);
      administerTest();
    } catch (Exception e) {
      _sim_messages += e.getMessage();
    }
  }

  private void administerTest() {
    int opportunities = _test.getOpportunities();
    _Ref<String> oppkey = new _Ref<String>();
    _Ref<String> error = new _Ref<String>();
    String status;
    boolean success = true;
    int approveAttempts;
    int approveWait = 500;
    int approveMaxAttempts = 30;

    try {
      for (int i = 1; success && i <= opportunities; ++i) 
      {
        _opportunityKey = null;
        _itemcount = 0;
        status = _dal.openTestOpp(_student.getTesteeKey(), _test.getKey(), oppkey, error);
        if (_dal.abort() || status.equals("failed")) {
          _sim_messages += "\n" + error.get();
          success = false;
          break;
        }
        _opportunityKey = oppkey.get();
        _dal.setLanguage(oppkey.get(), _test.getLanguage(), error);
        if (_dal.abort() || error.get() != null) {
          _sim_messages += _dal.getAbortSimulation() ? "Simulation aborted"
              : "\n" + error.get();
          success = false;
          break;
        }
        if (_dal.abort() || !recordTrueTheta(oppkey.get())) {
          _sim_messages += _dal.getAbortSimulation() ? "Simulation aborted"
              : "\n" + "Failed to record student attributes for "
              + oppkey.get();
          success = false;
          break;
        }
        Thread.sleep(approveWait);
        status = _dal.approveOpp(oppkey.get(), error);
        if (_dal.abort() || error.get() != null) {
          _sim_messages += _dal.getAbortSimulation() ? "Simulation aborted"
              : "\n" + error.get();
          success = false;
          break;
        }

        approveAttempts = 0;
        status = _dal.getOppStatus(oppkey.get());
        while (_dal.abort() || !status.equals( "approved") 
            && approveAttempts < approveMaxAttempts) {
          Thread.sleep(approveWait);
          ++approveAttempts;
          status = _dal.getOppStatus(oppkey.get());
        }
        if (_dal.abort() || !status.equals( "approved")) {
          _sim_messages += _dal.getAbortSimulation() ? "Simulation aborted"
              : "\n" + "Opportunity never approved (" + status
              + ")";
          success = false;
          break;
        }
        success = this.runTestOpp(oppkey.get(), i == 1, 1.0); 
        if (_sim_messages != null) {
          _dal.logError("TEST THREAD", _test.getKey(), oppkey.get(),
              _sim_messages);
          _sim_messages = null;

        }
        if (!success){
          break;
        }
      }
    } catch (Exception e) {
      _dal.logError("TEST THREAD", _test.getKey(), oppkey.get(),
          e.getMessage());
    }
  }

  private boolean recordTrueTheta(String oppkey) {
    int rows;
    String context = "TRUE THETA";
    rows = _dal.insertTesteeAttribute(oppkey, "OVERALL", new Double(
        _student.getProficiency()).toString(), context);
    if (rows < 1)
      return false;

    double theta;
    for (String strand : _student.getStrands()) {
      theta = _student.getStrandTheta(strand);
      rows = _dal.insertTesteeAttribute(oppkey, strand + " THETA",
          new Double(theta).toString(), context);
      if (rows < 1)
        return false;

    }
    return true;
  }

  private boolean runTestOpp(String oppkey, boolean simulateInitialAbility, double abilityEpsilon) {
    _Ref<String> status = new _Ref<String>();
    _Ref<String> dateCreated = new _Ref<String>();
    ItemGroup itemgroup;
    _Ref<Double> ability = new _Ref<Double>(new Double(0));
    _Ref<Integer> numSegments = new _Ref<Integer>(new Integer(0));
    List<InsertedItem> insertedItems;

    _Ref<Integer> startPosition = new _Ref<Integer>(new Integer(0));
    _Ref<String> error = new _Ref<String>();

    try {
      int itemScore;
      _Ref<String> itemResponse = new _Ref<String>();
      _Ref<String> dateCompleted = new _Ref<String>();
      _Ref<String> rowdelim = new _Ref<String>();
      _Ref<String> coldelim = new _Ref<String>();
      String scoreTuples;
      _Ref<String> forms = new _Ref<String>("");
      TestItem item;
      int page = 0;
      int itemThinkTime = 0;
      status.set(_dal.startTestopp(oppkey, ability, startPosition,
          numSegments, error));
      if (_dal.abort() || error.get() != null) {
        _sim_messages += "\n" + error;
        return false;
      }
      if (simulateInitialAbility) { // simulates ability of first opportunity coming from previous year's data
        ability.set(_student.initialAbility(abilityEpsilon));
        _dal.insertTesteeAttribute(oppkey, "INITIALABILITY",
            ability.toString(), "SIMULATOR");
        _dal.saveAbilityEstimate(oppkey, 0, "%", ability.get(), 0.2,
            -9999.0);
      }

      boolean bAdaptive2 = false; // TODO - Need to find a way

      while (!_dal.abort() && !_dal.isOppComplete(oppkey)) {
        ++page;
        itemgroup = _air.getNextItemGroup(_dal.getSQLConnection(), UUID.fromString(oppkey), error);
        if (error.get() != null || itemgroup == null || itemgroup.items.size() < 1) {
          if (error.get() != null
              && "Test Complete".equalsIgnoreCase(error.get())
              && _dal.isOppComplete(oppkey)) // just in case
          {
            error.set(null);
            break;
          }
          _sim_messages += "\n" + error.get() != null ? error.get()
              : "Unable to select next itemgroup";
          itemgroup = _air.getNextItemGroup(_dal.getSQLConnection(), UUID.fromString(oppkey), error); 
          return false;
        }

        insertedItems = _dal.insertItems(oppkey, itemgroup, page, status, dateCreated, error);
        if (insertedItems.size() != itemgroup.getItemCount()) {
          _sim_messages += "\n" + "Failed to insert itemgroup " + itemgroup.groupID;
          return false;
        }
        _itemcount += insertedItems.size();
        if (_test.getThinkTime() > 0) { // is in seconds, distribute to each item in group and convert to milliseconds
          itemThinkTime = (int) Math.round(1000.0 * _test.getThinkTime()
              / (double) insertedItems.size());
        }
        for (InsertedItem insItem : insertedItems) {
          if (_test.getThinkTime() > 0)
            Thread.sleep(itemThinkTime);

          item = itemgroup.getItem(insItem.getItemID());
          _Ref<ScoreInfo> scoreInfo = new _Ref<ScoreInfo>();
          itemScore = _student.ItemScore(_dal, _test, item, scoreInfo);
          itemResponse.set(itemScore > 0 ? "CORRECT" : "INCORRECT");
          _dal.updateResponse(oppkey, insItem.getItemID(), insItem.getPage(), insItem.getPosition(), 
              dateCreated.get(), 1, itemScore, itemResponse.get(), error,
              scoreInfo.get() == null ? null : scoreInfo.get().toXMLString());
          if (error.get() != null) {
            _sim_messages += "\n" + error.get();
            return false;
          }
        }
      }
      if (_dal.abort()) {
        _sim_messages += "\nSimulation aborted";
        return false;
      }
      _dal.setOppCompleted(oppkey, error);

      if (error.get() != null) {
        _sim_messages += "\n" + error;
        return false;
      }

      itemResponse.set(null);
      dateCompleted.set(null);

      rowdelim.set(null);
      coldelim.set(null);
      forms.set("");

      status.set(_dal.getScoreString(oppkey, itemResponse, dateCompleted, rowdelim, coldelim, error, forms, bAdaptive2));
      // some tests are not designed to be scored. COMPLETE is the final state
      if ("COMPLETE: Do Not Score".equalsIgnoreCase(error.get()))
        return true;
      if (error.get() != null) {
        _sim_messages += "\n" + error;
        return false;
      }
      // Score the test
      if (_test.isScorerLoaded()) {
        try {
          Date dtC = null;
          if (dateCompleted.get() != null)
            dtC = (new SimpleDateFormat(AbstractDateUtilDll.DB_DATETIME_FORMAT_MS_PRECISION)).parse(dateCompleted.get());
          else
            dtC = null;
          scoreTuples = _test.getSeScorer().testScore(_test.getKey(),
              itemResponse.get(), dtC, rowdelim.get().charAt(0),
              coldelim.get().charAt(0), status.get(), "", forms.get());
          _dal.insertScores(oppkey, scoreTuples, rowdelim.get(), coldelim.get(), error);
          if (error.get() != null
              && !error.get()
              .startsWith("Unable to queue up XML")) {
            _sim_messages += "\n" + error.get();
            return false;
          }
        } catch (Exception e) {
          _dal.logError("ScoringEngine", _test.getKey(), oppkey,
              e.getMessage());
          _sim_messages += "\n" + e.getMessage();
        }
      }
    } catch (Exception e) {
      _dal.logError("RunTestopp", _test.getKey(), oppkey, e.getMessage());
      return false;
    }
    return true;
  }
}
