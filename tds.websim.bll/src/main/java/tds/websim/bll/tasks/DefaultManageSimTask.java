/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.bll.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;
import tds.websim.bll.model.ManageSimTask;
import tds.websim.dal.interfaces.ISessionDao;
import tds.websim.model.BlueprintValidationStatus;
import tds.websim.model.SimReport;
import tds.websim.model.SimulationErrors;
import tds.websim.model.simpublish.SimPubSession;
import tds.websim.model.simpublish.SimPubTestPackage;
import dbsimulator.*;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
@Service ("manageSimTask")
public class DefaultManageSimTask extends ManageSimTask
{

  private static final Logger _logger = LoggerFactory.getLogger (DefaultManageSimTask.class);

  @Autowired
  private SimSessionFactory   _sessionFactory;

  public ReturnStatus runSimulation (String sessionKey) throws ReturnStatusException
  {
    final ISessionDao dao = this.getSessionDAO ();
    ReturnStatus ret;

    String sim_status = dao.getSessionSimStatusString (sessionKey);
    if (sim_status != null && (sim_status.equalsIgnoreCase ("RUNNING")))
    {
      ret = new ReturnStatus ("failed", "Session is already running. Please refresh the session view");
      return ret;
    }

    // first, we validate the blueprint
    BlueprintValidationStatus validationResult = dao.validateBlueprints (sessionKey);
    if (validationResult.getStatus ().equalsIgnoreCase ("failed")
        && validationResult.getNumFatals () > 0)
    {
      ret = new ReturnStatus ("failed", "Blueprint Validation Failed.  Unable to run the simulation.");
      return ret;
    }

    // then set the abort flag to false
    ret = dao.setSessionAbort (sessionKey, false);
    if (ret.getStatus ().equalsIgnoreCase ("failed"))
    {
      return ret;
    }

    final String sessionKeyParam = sessionKey;
    final SimSession session = _sessionFactory.createSimSession ();
    Thread runSimTask = new Thread (new Runnable ()
    {
      @Override
      public void run () {
        final ISessionDao runSimDAO = dao;
        try {
          runSimDAO.setSimulationRunProperties (sessionKeyParam);
          session.runSimulations (sessionKeyParam);
        } catch (Exception e) {
          _logger.error (e.getMessage ());
          try {
            runSimDAO.setSimulationErrorProperties (sessionKeyParam);
          } catch (Exception ex) {
            _logger.error (ex.getMessage ());
          }
        }
      }
    }, "runSimulation");
    runSimTask.start ();

    ret = new ReturnStatus ("success", "Blueprint validation has been kicked off");

    return ret;
  }

  public ReturnStatus publishSession (String sessionKey) throws ReturnStatusException {
    return this.getSessionDAO ().publishSession (sessionKey);
  }
  
  public SimPubSession getSessionDataForPublish (String sessionKey) throws ReturnStatusException {
    return this.getSessionDAO ().getSessionDataForPublish (sessionKey);
  }
  
  public SimPubTestPackage getSessionTestPackage (String sessionKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getSessionTestPackage (sessionKey, testKey);
  }
  
  public BlueprintValidationStatus validateBlueprint (String sessionKey) throws ReturnStatusException {
    return this.getSessionDAO ().validateBlueprints (sessionKey);
  }

  public ReturnStatus cancelSimulation (String sessionKey) throws ReturnStatusException {
    return this.getSessionDAO ().setSessionAbort (sessionKey, true);
  }

  public SimReport getReportSummaryStats (String sessionKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getReportSummaryStats (sessionKey, testKey);
  }

  public SimReport getReportBPSummary (String sessionKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getReportBPSummary (sessionKey, testKey);
  }

  public SimReport getReportScores (String sessionKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getReportScores (sessionKey, testKey);
  }

  public SimReport getReportFieldTestDistribution (String sessionKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getReportFieldTestDistribution (sessionKey, testKey);
  }

  public SimReport getReportItemDistribution (String sessionKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getReportItemDistribution (sessionKey, testKey);
  }

  public SimReport getReportOpportunities (String segmentKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getReportOpportunities (segmentKey, testKey);
  }

  public SimReport getReportItems (String sessionKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getReportItems (sessionKey, testKey);
  }

  public SimReport getFormDistributions (String sessionKey, String testKey) throws ReturnStatusException {
    return this.getSessionDAO ().getFormDistributions (sessionKey, testKey);
  }

  public SimulationErrors getSimulationErrors (String sessionKey) throws ReturnStatusException {
    return this.getSessionDAO ().getSimulationErrors (sessionKey);
  }
}
