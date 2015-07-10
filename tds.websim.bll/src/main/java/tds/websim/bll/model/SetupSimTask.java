/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.bll.model;

import java.util.List;

import tds.websim.model.Clients;
import tds.websim.model.SessionLanguages;
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
public abstract class SetupSimTask extends DaoTaskBase
{
  public abstract Sessions getSessions (String userID, String clientname) throws ReturnStatusException;

  public abstract SessionTests getSessionTests (String sessionKey) throws ReturnStatusException;

  public abstract SessionTests getSessionTestsToAdd (String clientName, String sessionType) throws ReturnStatusException;

  public abstract Clients getUserClients (String userID);

  public abstract SessionLanguages getSessionLanguages (String clientName, String sessionType) throws ReturnStatusException;

  public abstract List<String> getItemTypes (String adminsubject) throws ReturnStatusException;

  public abstract SessionTestItems getTestItems (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract SessionTestBlueprint getTestBlueprint (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract ReturnStatus copySession (String fromSessionKey, String sessionName, String sessionDescription) throws ReturnStatusException;

  public abstract ReturnStatus createSession (String clientName, String userID, String sessionName, String language, String sessionDescription, String sessionType) throws ReturnStatusException;

  public abstract List<ReturnStatus> addSessionTests (String sessionKey, SessionTests sessionTests) throws ReturnStatusException;

  public abstract ReturnStatus setSessionDescription (String sessionKey, String description) throws ReturnStatusException;

  public abstract ReturnStatus alterSessionTest (String sessionKey, String testKey, String iterations, String opportunities, String meanProficiency, String sdProficiency, String strandCorrelation,
      String handScoreItemTypes) throws ReturnStatusException;

  public abstract ReturnStatus deleteSession (String sessionKey) throws ReturnStatusException;

  public abstract ReturnStatus deleteSessionTest (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract ReturnStatus deleteSessionOppData (String sessionKey) throws ReturnStatusException;

  public abstract ReturnStatus deleteSessionOppData (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract ReturnStatus alterItemProperties (String sessionKey, String testKey, String segmentKey, String itemKey, String isActive, String isRequired) throws ReturnStatusException;

  public abstract ReturnStatus alterItemGroupProperties (String sessionKey, String testKey, String segmentKey, String groupID, String maxItems) throws ReturnStatusException;

  public abstract ReturnStatus alterSegment (String sessionKey, String testKey, String segmentKey, String startAbility, String startInfo, String minItems, String maxItems, String ftStartPos,
      String ftEndPos,
      String ftMinItems, String ftMaxItems, String bpWeight, String cset1Size, String cset2InitialRandom, String cset2Random, String itemWeight, String abilityOffset, String selectionAlgorithm,
      String cset1Order,
      String rcAbilityWeight, String abilityWeight, String precisionTargetNotMetWeight, String precisionTargetMetWeight, String precisionTarget, String adaptiveCut, String tooCloseSEs,
      String terminationMinCount, String terminationOverallInfo,
      String terminationRCInfo, String terminationTooClose, String terminationFlagsAnd) throws ReturnStatusException;

  public abstract ReturnStatus alterSegmentStrand (String sessionKey, String testKey, String segmentKey, String strand, String minItems, String maxItems, String bpWeight, String isStrictMax,
      String startAbility, String startInfo, String adaptiveCut, String scalar,
      String abilityWeight, String precisionTargetNotMetWeight, String precisionTargetMetWeight, String precisionTarget) throws ReturnStatusException;

  public abstract ReturnStatus alterSegmentContentLevel (String sessionKey, String testKey, String segmentKey, String contentLevel, String minItems, String maxItems, String bpWeight,
      String isStrictMax)
      throws ReturnStatusException;

  public abstract ReturnStatus changeStrandAsContentLevel (String sessionKey, String testKey, String segmentKey, String strand) throws ReturnStatusException;

  public abstract ReturnStatus changeContentLevelAsStrand (String sessionKey, String testKey, String segmentKey, String contentLevel) throws ReturnStatusException;

  public abstract ReturnStatus alterItemSelectionParameter (String sessionKey, String testKey, String segmentKey, String bpElementID, String paramName, String paramValue) throws ReturnStatusException;

  public abstract ItemSelectionParams getItemSelectionParameters (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract ReturnStatus addItemSelectionParameterDefaultRecords (List<ItemSelectionParamTemplate> dt) throws ReturnStatusException;
}
