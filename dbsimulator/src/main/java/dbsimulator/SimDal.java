/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tds.itemselection.base.ItemGroup;
import AIR.Common.DB.AbstractDAO;
import AIR.Common.Helpers._Ref;

public abstract class SimDal extends AbstractDAO {
  private static Logger _logger = LoggerFactory.getLogger(SimDal.class);
  private boolean _sessionAlreadyOpened = false;
  private String _sessionKey;

  private boolean _abortSimulation = false;
  private String _client;
  private String _browserKey;
  private String _sessionID;

  private String _language;
  private int _proctorDelay;
  private String _itembank;
  
//  public void set_itembank (String _itembank) {
//    this._itembank = _itembank;
//  }

  private long _proctorKey = 0;

  private List<String> _loginReqs = new ArrayList<String>();


  public abstract List<SessionTest> loadSessionTests();
  public abstract boolean abort();
  public abstract String loadTestControls(String testKey, SessionTest sessTest);
  public abstract String getItembank();
  public abstract boolean clearSessionOpps(String testKey);
  public abstract void endSession(String status);
  public abstract void cleanupTest(String testkey);
  public abstract boolean clearTestOpps(String testkey);
  public abstract List<String> getLoginRequirements();
  public abstract int loginStudent(String keyValues, _Ref<String> error);
  public abstract String openTestOpp(int testee, String testkey, _Ref<String> oppkey, _Ref<String> error);
  public abstract String setLanguage(String oppkey, String language, _Ref<String> error);
  public abstract int insertTesteeAttribute(String oppkey, String attribute, String value, String context);
  public abstract String saveAbilityEstimate(String oppkey, int itempos, String strand, double theta, double info, double lambda);
  public abstract String approveOpp(String oppkey, _Ref<String> error);
  public abstract String getOppStatus(String oppkey);
  public abstract boolean isOppComplete(String oppkey);
  public abstract String startTestopp(String oppkey, _Ref<Double> ability, 
      _Ref<Integer> startPosition, _Ref<Integer> numSegments,_Ref<String> error);
  public abstract String setOppStatus(String oppkey, String status);
  public abstract String getItemType(String sItemKey);
  public abstract List<InsertedItem> insertItems(String oppkey, ItemGroup itemgroup, int page, _Ref<String> status,
      _Ref<String> dateCreated, _Ref<String> error);
  public abstract String updateResponse(String oppkey, String itemkey, int page, int position, String dateCreated, int sequence,
      int score, String response, _Ref<String> error, String scoreDimensions);
  public abstract String setOppCompleted(String oppkey, _Ref<String> error);
  public abstract String getScoreString(String oppkey, _Ref<String> itemstring, _Ref<String> dateCompleted,
      _Ref<String> rowdelim, _Ref<String> coldelim, _Ref<String> error, _Ref<String> forms, boolean bAdaptive2);
  public abstract String insertScores(String oppkey, String scorestring, String rowdelim, String coldelim, _Ref<String> error);
  protected abstract boolean openSession();


  public SimDal() {
  }

  public void logError(String procname, String testKey, String oppKey,String message) {
    _logger.error(String
        .format("There was a error in %s : %s. Test key is %s and opp key is %s",
            procname, message, testKey, oppKey));
  }

  public void logError(String procname, String testKey, String message) {
    _logger.error(String.format(
        "There was a error in %s : %s. Test key is %s.", 
        procname, message, testKey));
  }

  public String getLang() {
    return _language;
  }

  public void setLang(String _language) {
    this._language = _language;
  }

  public String getSessionID() {
    return _sessionID;
  }

  public long getProctorKey() {
    return _proctorKey;
  }

  public void setProctorKey(long _proctorKey) {
    this._proctorKey = _proctorKey;
  }

  public void setSessionID(String _sessionID) {
    this._sessionID = _sessionID;
  }

  public List<String> getLoginReqs() {
    return _loginReqs;
  }

  public void setLoginReqs(List<String> _loginReqs) {
    this._loginReqs = _loginReqs;
  }

  public int getProctorDelay() {
    return _proctorDelay;
  }

  public void setProctorDelay(int _proctorDelay) {
    this._proctorDelay = _proctorDelay;
  }

  public String getItemBank() {
    return _itembank;
  }

  public void setItembank(String _itembank) {
    this._itembank = _itembank;
  }

  public String getClientName() {
    return _client;
  }

  public void setClientName(String _client) {
    this._client = _client;
  }

  public void setAbortSimulation(boolean value) {
    _abortSimulation = value;
  }

  public boolean getAbortSimulation() {
    return _abortSimulation;
  }

  public String getSessionKey() {
    return _sessionKey;
  }

  public void setSessionKey(String value) {
    initialize(value);
  }

  public String getBrowserKey() {
    return _browserKey;
  }

  public void setBrowserKey(String _browserKey) {
    this._browserKey = _browserKey;
  }

  public boolean clearSessionOpps() {
    return clearSessionOpps(null);
  }

  private void initialize(String sessionKey) {
    synchronized (this) {
      if (_sessionAlreadyOpened)
        throw new SessionAlreadySetException(_sessionKey);
      _sessionKey = sessionKey;
      try {
        openSession();
        _sessionAlreadyOpened = true;
      } catch (Exception exp) {
        _logger.error(String.format(
            "openSession() call failed. Session key %s",
            _sessionKey));
      }
    }
  }	
}
