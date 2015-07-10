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
import java.util.ArrayList;
import java.util.Random;

import scoringengine.Scorer;
import tds.itemselection.api.IAIROnline;

public class SessionTest {
  private String _id;
  private String _key;
  private int _iterations;
  private int _opportunities;
  private int _totalOpps;

  private int _numThreads = 1;
  private String _language;	
  private String _itemBank;                                     
  private double _meanProficiency;
  private double _sdProficiency;
  private double _strandCorrelation;
  private int _thinkTime = 1;	
  private List<String> _handScoreItemTypes = new ArrayList<String>();
  private List<String> _strands = new ArrayList<>();

  private boolean  _scorerLoaded = false;
  private Scorer _se_Scorer = null;

  private String _sim_messages;

  public SessionTest(String testID, String testKey, int students, int opps, int totalOpps) {
    _id = testID;
    _key = testKey;
    _iterations = students;
    _opportunities = opps;
    _totalOpps = totalOpps;
  }

  public String getId() {
    return _id;
  }

  public void setId(String _id) {
    this._id = _id;
  }

  public String getKey() {
    return _key;
  }

  public void setKey(String _key) {
    this._key = _key;
  }

  public int getIterations() {
    return _iterations;
  }

  public void setIterations(int _iterations) {
    this._iterations = _iterations;
  }

  public int getOpportunities() {
    return _opportunities;
  }

  public void setOpportunities(int _opportunities) {
    this._opportunities = _opportunities;
  }

  public int getTotalOpps() {
    return _totalOpps;
  }

  public void setTotalOpps(int _totalOpps) {
    this._totalOpps = _totalOpps;
  }

  public int getNumThreads() {
    return _numThreads;
  }

  public void setNumThreads(int _numThreads) {
    this._numThreads = _numThreads;
  }

  public String getLanguage() {
    return _language;
  }

  public void setLanguage(String _language) {
    this._language = _language;
  }

  public String getItemBank() {
    return _itemBank;
  }

  public void setItemBank(String _itemBank) {
    this._itemBank = _itemBank;
  }

  public double getMeanProficiency() {
    return _meanProficiency;
  }

  public void setMeanProficiency(double _meanProficiency) {
    this._meanProficiency = _meanProficiency;
  }

  public double getSdProficiency() {
    return _sdProficiency;
  }

  public void setSdProficiency(double _sdProficiency) {
    this._sdProficiency = _sdProficiency;
  }

  public double getStrandCorrelation() {
    return _strandCorrelation;
  }

  public void setStrandCorrelation(double _strandCorrelation) {
    this._strandCorrelation = _strandCorrelation;
  }

  public int getThinkTime() {
    return _thinkTime;
  }

  public void setThinkTime(int _thinkTime) {
    this._thinkTime = _thinkTime;
  }

  public List<String> getHandScoreItemTypes() {
    return _handScoreItemTypes;
  }

  public void setHandScoreItemTypes(List<String> _handScoreItemTypes) {
    this._handScoreItemTypes = _handScoreItemTypes;
  }

  public void addHandScoreItemTypes(String handScoreItemType) {
    this._handScoreItemTypes.add(handScoreItemType);
  }

  public boolean isHandScoreItem(String strItemType) {
    return _handScoreItemTypes.contains(strItemType);
  }

  public List<String> getStrands() {
    return _strands;
  }

  public void setStrands(List<String> strands) {
    this._strands = strands;
  }

  public void addStrand(String strand) {
    this._strands.add(strand);
  }	

  public boolean isScorerLoaded() {
    return _scorerLoaded;
  }

  public void setScorerLoaded(boolean _scorerLoaded) {
    this._scorerLoaded = _scorerLoaded;
  }

  public Scorer getSeScorer() {
    return _se_Scorer;
  }

  public String getErrors() {
    return _sim_messages;
  }

  public void setErrors(String value) {
    if (_sim_messages == null)
      _sim_messages = value;
    else
      _sim_messages += "\n" + value;
  }

  public void simulate(IAIROnline air, SimDal dal, Scorer scorer, int threadLWM) {
    try {
      _se_Scorer = scorer;
      if (_numThreads > 1)
        runThreadedSimulations(air, dal); // multi-threaded version
      else
        runSimulations(air, dal); // single-threaded version
    } catch (Exception e) {
      dal.logError("Simulate", this._key, e.getMessage());
    }
  }

  private void runThreadedSimulations(IAIROnline air, SimDal dal) throws InterruptedException {
    VirtualStudent student = null;
    int threadIndex;

    int numStudents = _iterations - (_totalOpps / _opportunities);
    Random rand = new Random(); // provide a new random number to each student sequentially to compute his true theta
    TestThread testThrd;
    int maxWorkers = java.lang.Thread.activeCount();
    if (dal.abort()) {
      return;
    }
    _numThreads = Math.min(_numThreads, numStudents); // make sure there are not more threads than iterations
    _numThreads = Math.min(_numThreads, maxWorkers);
    double proficiencyVar = 1.0;

    Thread thrd;
    List<String> loginReqs = dal.getLoginRequirements();
    String keyvals = null;
    for (String key : loginReqs) {
      if (keyvals == null)
        keyvals = key + ":" + "GUEST";
      else
        keyvals += ";" + key + ":" + "GUEST";
    }

    TestThread[] threads = new TestThread[_numThreads];
    for (int i = 0; !dal.abort() && i < _numThreads; ++i) {

      student = new VirtualStudent(_meanProficiency, _sdProficiency, _strands, proficiencyVar, _strandCorrelation);
      student.setProfRand(rand.nextDouble());
      threads[i] = new TestThread(student, 1, keyvals, this, air, dal);
      thrd = new Thread(threads[i]);
      threads[i].setThread(thrd);
      thrd.start();
    }

    for (int i = _numThreads; !dal.abort() && i < numStudents;) {
      threadIndex = -1;
      Thread.sleep(1000);
      for (int k = 0; k < _numThreads; ++k) {
        if (threads[k].isDone()) {
          threadIndex = k;
          break;
        }
      }

      if (threadIndex == -1) // no threads done at this time
        continue;

      // found a 'done' thread. Assign it a new student to run
      testThrd = threads[threadIndex];
      student = new VirtualStudent(_meanProficiency, _sdProficiency, _strands, proficiencyVar, _strandCorrelation);
      testThrd.setStudent(student);
      student.setProfRand(rand.nextDouble());
      testThrd.setThread(new Thread(threads[threadIndex]));
      if (numStudents - i <= _numThreads){// try to force the thread to above normal priority
        testThrd.getThread().setPriority((java.lang.Thread.NORM_PRIORITY + java.lang.Thread.MAX_PRIORITY)/2);
      }
      testThrd.getThread().start();
      ++i; 
    }
    boolean notDone = true;
    while (notDone) {
      notDone = false;
      Thread.sleep(1000);
      for (int i = 0; i < _numThreads; ++i) {
        if (threads[i] != null && !threads[i].isDone())
          notDone = true;
        else if (threads[i] != null && threads[i].isDone()) {
          threads[i] = null; 
        }
      }
    }
  }

  private void runSimulations(IAIROnline air, SimDal dal) {
    VirtualStudent student = null;
    Random rand = new Random(); // provide a new random number to each student sequentially to compute his true theta
    double proficiencyVar = 1.0;
    List<String> loginReqs = dal.getLoginRequirements();
    String keyvals = null;

    for (String key : loginReqs) {
      if (keyvals == null)
        keyvals = key + ":" + "GUEST";
      else
        keyvals += ";" + key + ":" + "GUEST";
    }

    for (int i = 0; !dal.abort() && i < _iterations; ++i) {
      student = new VirtualStudent(_meanProficiency, _sdProficiency, _strands, proficiencyVar, _strandCorrelation);
      student.setProfRand(rand.nextDouble());
      TestThread thread = new TestThread(student, 1, keyvals, this, air, dal);
      thread.runStudent();
    }
  }
}
