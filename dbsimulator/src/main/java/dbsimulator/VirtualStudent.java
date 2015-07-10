/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import AIR.Common.Helpers._Ref;
import tds.itemselection.base.Dimension;
import tds.itemselection.base.TestItem;

public class VirtualStudent {
  private Map<String, Double> _strandTheta = new HashMap<String, Double>();
  private String _id;
  private int _testeeKey;
  private double _proficiency;
  private double _meanProf;
  private double _sdProf;
  private double _profVar;
  private double _profCovar;
  private List<String> _strands;
  private Random _rand = new Random();
  private double _profRand = 0.5;
  private DistributionNormal _distribution;
  private DistributionNormal _strandDistribution = new DistributionNormal(0.0, 1.0);

  public VirtualStudent(double meanProficiency, double sdProficiency,
      List<String> strands, double proficiencyVariance,
      double strandCorrelation) {
    _meanProf = meanProficiency;
    _sdProf = sdProficiency;
    _distribution = new DistributionNormal(meanProficiency, sdProficiency);
    _strands = strands;
    _profCovar = proficiencyVariance * strandCorrelation
        * strandCorrelation;
    _profVar = proficiencyVariance;
    for (String strand : strands) {
      _strandTheta.put(strand, 0.0);
    }
  }

  public void initialize(int testee) {
    _testeeKey = testee;
    _id = "student" + new Integer(testee).toString();
    _proficiency = _distribution.InvCDF(_profRand);

    double a = Math.sqrt(_profCovar);
    double b = Math.sqrt(_profVar - _profCovar);

    for (String strand : _strands) {
      _strandTheta.put(
          strand,
          a * _proficiency + b * _strandDistribution.InvCDF(_rand.nextDouble()));
    }
  }

  public int getTesteeKey() {
    return _testeeKey;
  }

  public List<String> getStrands() {
    return _strands;
  }

  public double getStrandTheta(String strandName) {
    return _strandTheta.get(strandName);
  }

  public double getProficiency() {
    return _proficiency;
  }

  public void setProfRand(double value) {
    _profRand = value;
  }

  public double initialAbility(double epsilon) {
    // using assigned proficiency as a central point, return a value that is
    // randomly selected between proficiency +/- epsilon
    double value = _proficiency
        + (_rand.nextDouble() * 2 * epsilon - epsilon);
    return value;
  }

  public int ItemScore(SimDal dal, SessionTest test, TestItem item, _Ref<ScoreInfo> overallScoreInfo) {

    boolean bRequireScoring = !(item.isFieldTest || item.dimensions.size() < 1);

    if (bRequireScoring) // We don't need to score hand score items
    {
      String sItemType = dal.getItemType(item.itemID);
      if (sItemType == null || test.isHandScoreItem(sItemType))
        bRequireScoring = false;
    }

    overallScoreInfo.set(new ScoreInfo(-1, "overall", "NotScored"));
    double fStrandTheta = 0F;
    int nDimensionScoresSum = 0;

    if (bRequireScoring) {
      fStrandTheta = getStrandTheta(item.strandName);
      overallScoreInfo.get().setScoreStatus("Scored");
      overallScoreInfo.get().setScorePoint(0);
    }

    // Parse out the dimensions.
    for (Dimension dim : item.dimensions) // Generate scores in each dimension
    {
      int nItemDimensionScore = -1; // Score for the current dimension
      if (bRequireScoring) {
        double fRandomScoreProbability = _rand.nextDouble(); // Random probability
        double fItemDimensionCumulativeScoreProbability = 0F;
        // Cumulative probability based on student ability for possible score points
        for (int z = 0; z <= dim.getScorePoints(); z++) {
          fItemDimensionCumulativeScoreProbability += dim.irtModelInstance.Probability(z, fStrandTheta);
          if (fItemDimensionCumulativeScoreProbability < fRandomScoreProbability)
            continue;
          nItemDimensionScore = z;
          // Take the score point in which we reach the probability as
          // the dimension score
          break;
        }
      }
      // If the current dimension is overall then initialize with the computed score
      // If there are sub-dimensions this score will be replaced with calculated sum of sub-dimension scores
      if ("OVERALL".compareToIgnoreCase(dim.name) == 0)
        overallScoreInfo.get().setScorePoint(nItemDimensionScore);
      else {
        ScoreInfo dimensionScoreInfo = new ScoreInfo(nItemDimensionScore, dim.name,
            nItemDimensionScore >= 0 ? "Scored" : "NotScored");
        // Construct a sub-dimension score
        overallScoreInfo.get().addSubScore(dimensionScoreInfo);
        nDimensionScoresSum += nItemDimensionScore;
      }
    }
    if (overallScoreInfo.get().getSubScoreList().length > 0 && bRequireScoring)
      overallScoreInfo.get().setScorePoint(nDimensionScoresSum);
    return overallScoreInfo.get().getScorePoint();
  }
}
