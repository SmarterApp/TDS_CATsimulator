/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.bll.tasks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import tds.websim.bll.model.SetupSimTask;
import tds.websim.dal.interfaces.ISessionDao;
import tds.websim.model.Clients;
import tds.websim.model.SessionLanguages;
import tds.websim.model.SessionTest;
import tds.websim.model.SessionTestBlueprint;
import tds.websim.model.SessionTestItems;
import tds.websim.model.SessionTests;
import tds.websim.model.Sessions;
import tds.websim.model.itemselectionparam.ItemSelectionParamTemplate;
import tds.websim.model.itemselectionparam.ItemSelectionParams;
import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
@Service ("setupSimTask")
public class DefaultSetupSimTask extends SetupSimTask
{
  public Clients getUserClients (String userID) {
    try {
      return this.getSessionDAO ().getClients (userID);
    } catch (ReturnStatusException e) {
      return new Clients ();
    }
  }

  public Sessions getSessions (String userID, String clientName) throws ReturnStatusException {
    return getSessionDAO ().getSessions (userID, clientName);
  }

  public SessionLanguages getSessionLanguages (String clientName, String sessionType) throws ReturnStatusException {
    return getSessionDAO ().getSessionLanguages (clientName, sessionType);
  }

  public List<String> getItemTypes (String adminSubject) throws ReturnStatusException {
    return getSessionDAO ().getItemTypes (adminSubject);
  }

  public SessionTests getSessionTests (String sessionKey) throws ReturnStatusException {
    return getSessionDAO ().getSessionTests (sessionKey);
  }

  public SessionTests getSessionTestsToAdd (String clientName, String sessionType) throws ReturnStatusException {
    return getSessionDAO ().getSessionTestsToAdd (clientName, sessionType);
  }

  public SessionTestBlueprint getTestBlueprint (String sessionKey, String testKey) throws ReturnStatusException {
    return getSessionDAO ().getTestBlueprint (sessionKey, testKey);
  }

  public SessionTestItems getTestItems (String sessionKey, String testKey) throws ReturnStatusException {
    return getSessionDAO ().getTestItems (sessionKey, testKey);
  }

  public ReturnStatus copySession (String fromSessionKey, String sessionName, String sessionDescription) throws ReturnStatusException {
    return getSessionDAO ().copySession (fromSessionKey, sessionName, sessionDescription);
  }

  public ReturnStatus createSession (String clientName, String userID, String sessionName, String language, String sessionDescription, String sessionType) throws ReturnStatusException {
    return getSessionDAO ().createSession (clientName, userID, sessionName, language, sessionDescription, sessionType);
  }

  public List<ReturnStatus> addSessionTests (String sessionKey, SessionTests sessionTests) throws ReturnStatusException
  {
    ISessionDao dao = this.getSessionDAO ();
    List<ReturnStatus> returnStatuses = new ArrayList<ReturnStatus> ();

    for (SessionTest sessionTest : sessionTests)
    {
      returnStatuses.add (dao.addSessionTests (sessionKey, sessionTest.getAdminSubject (), sessionTest.getIterations (),
          sessionTest.getOpportunities (), sessionTest.getMeanProficiency (), sessionTest.getSdProficiency (),
          sessionTest.getStrandCorrelation (), sessionTest.getHandScoreItemTypes ()));
    }

    return returnStatuses;
  }

  public ReturnStatus setSessionDescription (String sessionKey, String description) throws ReturnStatusException
  {
    return this.getSessionDAO ().setSessionDescription (sessionKey, description);
  }

  public ReturnStatus alterSessionTest (String sessionKey, String testKey, String iterations, String opportunities, String meanProficiency, String sdProficiency, String strandCorrelation,
      String handScoreItemTypes) throws ReturnStatusException
  {
    ISessionDao dao = this.getSessionDAO ();
    ReturnStatus response = null;
    response = dao.deleteSessionOppData (sessionKey, testKey);
    if (response.getStatus ().equalsIgnoreCase ("success"))
    {
      response = dao.alterSessionTest (sessionKey, testKey, iterations, opportunities, meanProficiency,
          sdProficiency, strandCorrelation, handScoreItemTypes);
    }

    return response;
  }

  public ReturnStatus deleteSession (String sessionKey) throws ReturnStatusException
  {
    return this.getSessionDAO ().deleteSession (sessionKey);
  }

  public ReturnStatus deleteSessionTest (String sessionKey, String testKey) throws ReturnStatusException
  {
    ISessionDao dao = this.getSessionDAO ();
    ReturnStatus response;
    response = dao.deleteSessionTest (sessionKey, testKey);
    return response;
  }

  public ReturnStatus deleteSessionOppData (String sessionKey) throws ReturnStatusException
  {
    return this.getSessionDAO ().deleteSessionOppData (sessionKey);
  }

  public ReturnStatus deleteSessionOppData (String sessionKey, String testKey) throws ReturnStatusException
  {
    return this.getSessionDAO ().deleteSessionOppData (sessionKey, testKey);
  }

  public ReturnStatus alterItemProperties (String sessionKey, String testKey, String segmentKey, String itemKey, String isActive, String isRequired) throws ReturnStatusException
  {
    return this.getSessionDAO ().alterItemProperties (sessionKey, testKey, segmentKey, itemKey, isActive, isRequired);
  }

  public ReturnStatus alterItemGroupProperties (String sessionKey, String testKey, String segmentKey, String groupID, String maxItems) throws ReturnStatusException
  {
    return this.getSessionDAO ().alterItemGroupProperties (sessionKey, testKey, segmentKey, groupID, maxItems);
  }

  public ReturnStatus alterSegment (String sessionKey, String testKey, String segmentKey, String startAbility, String startInfo, String minItems, String maxItems, String ftStartPos, String ftEndPos,
      String ftMinItems, String ftMaxItems, String bpWeight, String cset1Size, String cset2InitialRandom, String cset2Random, String itemWeight, String abilityOffset, String selectionAlgorithm,
      String cset1Order,
      String rcAbilityWeight, String abilityWeight, String precisionTargetNotMetWeight, String precisionTargetMetWeight, String precisionTarget, String adaptiveCut, String tooCloseSEs,
      String terminationMinCount, String terminationOverallInfo,
      String terminationRCInfo, String terminationTooClose, String terminationFlagsAnd) throws ReturnStatusException
  {
    return this.getSessionDAO ().alterSegment (sessionKey, testKey, segmentKey, startAbility, startInfo, minItems, maxItems, ftStartPos, ftEndPos, ftMinItems, ftMaxItems, bpWeight, cset1Size,
        cset2InitialRandom, cset2Random, itemWeight, abilityOffset, selectionAlgorithm, cset1Order,
        rcAbilityWeight, abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, adaptiveCut,
        tooCloseSEs, terminationMinCount, terminationOverallInfo, terminationRCInfo, terminationTooClose, terminationFlagsAnd);
  }

  public ReturnStatus alterSegmentStrand (String sessionKey, String testKey, String segmentKey, String strand, String minItems, String maxItems, String bpWeight, String isStrictMax,
      String startAbility, String startInfo, String adaptiveCut, String scalar,
      String abilityWeight, String precisionTargetNotMetWeight, String precisionTargetMetWeight, String precisionTarget) throws ReturnStatusException
  {
    return this.getSessionDAO ().alterSegmentStrand (sessionKey, testKey, segmentKey, strand, minItems, maxItems, bpWeight, isStrictMax, startAbility, startInfo, adaptiveCut, scalar,
        abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget);
  }

  public ReturnStatus alterSegmentContentLevel (String sessionKey, String testKey, String segmentKey, String contentLevel, String minItems, String maxItems, String bpWeight, String isStrictMax)
      throws ReturnStatusException
  {
    return this.getSessionDAO ().alterSegmentContentLevel (sessionKey, testKey, segmentKey, contentLevel, minItems, maxItems, bpWeight, isStrictMax);
  }

  public ReturnStatus changeStrandAsContentLevel (String sessionKey, String testKey, String segmentKey, String strand) throws ReturnStatusException
  {
    return this.getSessionDAO ().changeStrandAsContentLevel (sessionKey, testKey, segmentKey, strand);
  }

  public ReturnStatus changeContentLevelAsStrand (String sessionKey, String testKey, String segmentKey, String contentLevel) throws ReturnStatusException
  {
    return this.getSessionDAO ().changeContentLevelAsStrand (sessionKey, testKey, segmentKey, contentLevel);
  }

  public ReturnStatus alterItemSelectionParameter (String sessionKey, String testKey, String segmentKey, String bpElementID, String paramName, String paramValue) throws ReturnStatusException
  {
    return this.getSessionDAO ().alterItemSelectionParameter (sessionKey, testKey, segmentKey, bpElementID, paramName, paramValue);
  }

  public ItemSelectionParams getItemSelectionParameters (String sessionKey, String testKey) throws ReturnStatusException
  {
    return this.getSessionDAO ().getItemSelectionParameters (sessionKey, testKey);
  }

  public ReturnStatus addItemSelectionParameterDefaultRecords (List<ItemSelectionParamTemplate> dt) throws ReturnStatusException
  {
    return this.getSessionDAO ().addItemSelectionParameterDefaultRecords (dt);
  }
}
