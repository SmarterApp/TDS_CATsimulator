/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import scoringengine.Scorer;
import scoringengine.TestCollection;
import tds.itemselection.api.IAIROnline;
import tds.itemselection.loader.SegmentCollection;

@Component("simSession")
public class SimSession {

  private String _sessionKey;
  private String _clientname;
  private String _itembank;
  private List<SessionTest> _tests;
  private TestCollection _se_TestCollection = null;
  private Scorer _se_Scorer = null;
  private SimDal _dal;

  @Autowired
  @Qualifier("simDALClientFactory")
  private SimDalFactory _simDalFactory;

  @Autowired
  @Qualifier("aironline")
  private IAIROnline _air;

  public SimSession() {
  }

  public String runSimulations(String sessionkey) {
    _sessionKey = sessionkey;
    final SimSession sess = this;
    Thread thrd = new Thread(new Runnable() {
      @Override
      public void run() {
        sess.runThreadedSimulations();
      }
    });
    thrd.setDaemon(true);
    thrd.start();
    return null;
  }

  public void AbortSimulation() {
    _dal.setAbortSimulation(true);
  }

  private void runThreadedSimulations() {
    initialize();
//    if (_itembank == null) {
//      _dal.logError("RunSimulations", "NA", "Failed to obtain itembank");
//    }

    String error;
    int numerrs = 0;
    _tests = _dal.loadSessionTests();
    int N = _tests.size();
    int k = 0;
    for (SessionTest test : _tests){
      if (!_dal.abort()
          && test.getIterations() > 0
          && test.getTotalOpps() < test.getIterations()
          * test.getOpportunities()) {
        ++k;
        _dal.cleanupTest(test.getKey());
        if ((error = loadTest(test)) != null) 
        {
          ++numerrs;
          _dal.logError("RunSimulations", test.getKey(), error);
          continue;
        }

        test.simulate(_air, _dal, _se_Scorer, N - k);
        if (test.getErrors() != null) {
          ++numerrs;
          _dal.logError("Test Simulations", test.getKey(), test.getErrors());
        }
      }
    }
    String status;
    if (numerrs > 0)
      status = "errors";
    else if (_dal.getAbortSimulation())
      status = "aborted";
    else
      status = "completed";

    _dal.endSession(status);
    SegmentCollection col = SegmentCollection.getInstance();
    col.RemoveSession(_sessionKey);
    // TODO: Hopefully there is only one segment collection
  }

  private void initialize() {
    SegmentCollection col = SegmentCollection.getInstance();
    col.RemoveSession(_sessionKey);     
    // TODO: Hopefully there is only one segment collection

    _dal = _simDalFactory.createSimDal(_sessionKey);

    _clientname = _dal.getClientName();       
    _itembank = _dal.getItembank();

    if (_itembank == null)
      return;

    //        String conn2 = null;    // need to be sure the full connection string contains exactly the database to load from
    //        String[] parts = null; //_dal.Connection.Split(';');
    //        for (String part : parts)
    //        {
    //            if (part.contains("database"))
    //                conn2 += ";database=" + _itembank;
    //            else if (part.indexOf("Initial Catalog") >= 0)
    //                conn2 += ";Initial Catalog=" + _itembank;                
    //            else if (conn2 == null)
    //                conn2 = part;
    //            else conn2 += ";" + part;
    //        }
    _se_TestCollection = new TestCollection(null, "SIM", false); //TODO: This need to change to have a bean with MySQL itembank dal
    _se_TestCollection.loadConversionTables(_clientname);
    _se_Scorer = new Scorer(_se_TestCollection);
  }

  private String loadTest(SessionTest test) {
    String error = _dal.loadTestControls(test.getKey(), test);
    if (error != null)
      return error;

    try {
      if (!_se_TestCollection.hasTest(test.getKey()))
        _se_TestCollection.loadTest(test.getKey());
      // test.setScorerLoaded(true); // TODO: For now stub out scoring engine functionality    
    } catch (Exception e) {
      _dal.logError("LoadScoringEngine", test.getKey(), "NA", e.getMessage());
      test.setScorerLoaded(false);
    }
    return null;
  }
}
