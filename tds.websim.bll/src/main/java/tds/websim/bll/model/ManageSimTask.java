/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.bll.model;

import tds.websim.model.BlueprintValidationStatus;
import tds.websim.model.SimReport;
import tds.websim.model.SimulationErrors;
import tds.websim.model.simpublish.SimPubSession;
import tds.websim.model.simpublish.SimPubTestPackage;
import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;

public abstract class ManageSimTask extends DaoTaskBase
{
  public abstract ReturnStatus runSimulation (String sessionKey) throws ReturnStatusException;

  public abstract ReturnStatus publishSession (String sessionKey) throws ReturnStatusException;

  public abstract SimPubSession getSessionDataForPublish (String sessionKey) throws ReturnStatusException;

  public abstract SimPubTestPackage getSessionTestPackage (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract BlueprintValidationStatus validateBlueprint (String sessionKey) throws ReturnStatusException;

  public abstract ReturnStatus cancelSimulation (String sessionKey) throws ReturnStatusException;

  public abstract SimReport getReportSummaryStats (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract SimReport getReportBPSummary (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract SimReport getReportScores (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract SimReport getReportFieldTestDistribution (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract SimReport getReportItemDistribution (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract SimReport getReportOpportunities (String segmentKey, String testKey) throws ReturnStatusException;

  public abstract SimReport getReportItems (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract SimReport getFormDistributions (String sessionKey, String testKey) throws ReturnStatusException;

  public abstract SimulationErrors getSimulationErrors (String sessionKey) throws ReturnStatusException;

}
