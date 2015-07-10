/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.dal.interfaces;

import java.util.List;

import tds.websim.model.BlueprintValidationStatus;
import tds.websim.model.Clients;
import tds.websim.model.SessionLanguages;
import tds.websim.model.SessionTestBlueprint;
import tds.websim.model.SessionTestItems;
import tds.websim.model.SessionTests;
import tds.websim.model.Sessions;
import tds.websim.model.SimReport;
import tds.websim.model.SimulationErrors;
import tds.websim.model.WebSimUser;
import tds.websim.model.itemselectionparam.ItemSelectionParamTemplate;
import tds.websim.model.itemselectionparam.ItemSelectionParams;
import tds.websim.model.simpublish.SimPubSession;
import tds.websim.model.simpublish.SimPubTestPackage;
import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;

public interface ISessionDao
{

  public abstract boolean validateUser (WebSimUser user)
      throws ReturnStatusException;

  public abstract Clients getClients (String userId)
      throws ReturnStatusException;

  public abstract Sessions getSessions (String userId, String clientName)
      throws ReturnStatusException;

  public abstract SessionTests getSessionTests (String sessionKey)
      throws ReturnStatusException;

  public abstract List<String> getItemTypes (String adminSubject)
      throws ReturnStatusException;

  public abstract SessionLanguages getSessionLanguages (String clientName,
      String sessionType) throws ReturnStatusException;

  public abstract SessionTests getSessionTestsToAdd (String clientName,
      String sessionType) throws ReturnStatusException;

  public abstract SessionTestBlueprint getTestBlueprint (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract SessionTestItems getTestItems (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract ReturnStatus setSessionDescription (String sessionKey,
      String description) throws ReturnStatusException;

  @Deprecated
  public abstract List<List<String>> getLanguageGradeSubject (String clientName)
      throws ReturnStatusException;

  public abstract ReturnStatus copySession (String fromSessionKey,
      String sessionName, String sessionDescription)
      throws ReturnStatusException;

  public abstract ReturnStatus createSession (String clientName,
      String userId, String sessionName, String language,
      String sessionDescription, String sessionType)
      throws ReturnStatusException;

  public abstract ReturnStatus alterSessionTest (String sessionKey,
      String testKey, String iterations, String opportunities,
      String meanProficiency, String sdProficiency,
      String strandCorrelation, String handScoreItemTypes)
      throws ReturnStatusException;

  public abstract ReturnStatus addSessionTests (String sessionKey,
      String testKey, int iterations, int opportunities,
      float meanProficiency, float sdProficiency,
      float strandCorrelation, String handScoreItemTypes)
      throws ReturnStatusException;

  public abstract ReturnStatus deleteSession (String sessionKey)
      throws ReturnStatusException;

  public abstract ReturnStatus deleteSessionTest (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract ReturnStatus deleteSessionOppData (String sessionKey)
      throws ReturnStatusException;

  public abstract ReturnStatus deleteSessionOppData (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract String getSessionSimStatusString (String sessionKey)
      throws ReturnStatusException;

  public abstract ReturnStatus alterSegment (String sessionKey,
      String testKey, String segmentKey, String startAbility,
      String startInfo, String minItems, String maxItems,
      String ftStartPos, String ftEndPos, String ftMinItems,
      String ftMaxItems, String bpWeight, String cset1size,
      String cset2InitialRandom, String cset2Random, String itemWeight,
      String abilityOffset, String selectionAlgorithm, String cset1Order,
      String rcAbilityWeight, String abilityWeight,
      String precisionTargetNotMetWeight,
      String precisionTargetMetWeight, String precisionTarget,
      String adaptiveCut, String tooCloseSEs, String terminationMinCount,
      String terminationOverallInfo, String terminationRCInfo,
      String terminationTooClose, String terminationFlagsAnd)
      throws ReturnStatusException;

  public abstract ReturnStatus alterSegmentContentLevel (String sessionKey,
      String testKey, String segmentKey, String contentLevel,
      String minItems, String maxItems, String bpWeight,
      String isStrictMax) throws ReturnStatusException;

  public abstract ReturnStatus changeStrandAsContentLevel (String sessionKey,
      String testKey, String segmentKey, String strand)
      throws ReturnStatusException;

  public abstract ReturnStatus changeContentLevelAsStrand (String sessionKey,
      String testKey, String segmentKey, String contentLevel)
      throws ReturnStatusException;

  public abstract ReturnStatus alterItemProperties (String sessionKey,
      String testKey, String segmentKey, String itemKey, String isActive,
      String isRequired) throws ReturnStatusException;

  public abstract ReturnStatus alterItemGroupProperties (String sessionKey,
      String testKey, String segmentKey, String groupID, String maxItems)
      throws ReturnStatusException;

  public abstract ReturnStatus alterSegmentStrand (String sessionKey,
      String testKey, String segmentKey, String strand, String minItems,
      String maxItems, String bpWeight, String isStrictMax,
      String startAbility, String startInfo, String adaptiveCut,
      String scalar, String abilityWeight,
      String precisionTargetNotMetWeight,
      String precisionTargetMetWeight, String precisionTarget)
      throws ReturnStatusException;

  public abstract ReturnStatus alterItemSelectionParameter (String sessionKey, String testKey, String segmentKey, String bpElementID, String paramName, String paramValue)
      throws ReturnStatusException;

  public abstract ItemSelectionParams getItemSelectionParameters (String sessionKey, String testKey)
      throws ReturnStatusException;

  public abstract ReturnStatus addItemSelectionParameterDefaultRecords (List<ItemSelectionParamTemplate> dt)
      throws ReturnStatusException;

  public abstract SimReport getReportSummaryStats (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract SimReport getReportBPSummary (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract SimReport getReportScores (String sessionKey, String testKey)
      throws ReturnStatusException;

  public abstract SimReport getReportFieldTestDistribution (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract SimReport getReportItemDistribution (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract SimReport getReportOpportunities (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract SimReport getReportItems (String sessionKey, String testKey)
      throws ReturnStatusException;

  public abstract SimReport getFormDistributions (String sessionKey,
      String testKey) throws ReturnStatusException;

  public abstract BlueprintValidationStatus validateBlueprints (
      String sessionKey) throws ReturnStatusException;

  public abstract ReturnStatus setSessionAbort (String sessionKey,
      boolean abort) throws ReturnStatusException;

  public abstract ReturnStatus setSimulationRunProperties (String sessionKey)
      throws ReturnStatusException;

  public abstract ReturnStatus setSimulationErrorProperties (String sessionKey)
      throws ReturnStatusException;

  public abstract SimulationErrors getSimulationErrors (String sessionKey)
      throws ReturnStatusException;

  public abstract ReturnStatus publishSession (String sessionKey)
      throws ReturnStatusException;
  
  public abstract SimPubSession getSessionDataForPublish(String sessionKey)
      throws ReturnStatusException;
  
  public abstract SimPubTestPackage getSessionTestPackage (String sessionKey, String testKey) 
      throws ReturnStatusException;
}
