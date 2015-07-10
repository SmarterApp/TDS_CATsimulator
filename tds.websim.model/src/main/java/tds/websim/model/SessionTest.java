/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class SessionTest implements Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String            _adminSubject;
  private String            _testID;
  private String            _simBlueprint;
  private int               _iterations;
  private int               _opportunities;
  private String            _selectionAlgorithm;
  private float             _meanProficiency;
  private float             _sdProficiency;
  private float             _strandCorrelation;
  private String            _grade;
  private String            _gradeCode;
  private String            _subject;
  private String            _language;
  private String            _languageCode;
  private String            _simThreads;
  private String            _simThinkTime;
  private String            _errorMsg;
  private String            _handScoreItemTypes;

  private int               _simulations;

  @JsonProperty ("AdminSubject")
  public String getAdminSubject () {
    return _adminSubject;
  }

  public void setAdminSubject (String value) {
    this._adminSubject = value;
  }

  @JsonProperty ("TestID")
  public String getTestID () {
    return _testID;
  }

  public void setTestID (String value) {
    this._testID = value;
  }

  @JsonProperty ("Sim_Blueprint")
  public String getSim_Blueprint () {
    return _simBlueprint;
  }

  public void setSim_Blueprint (String value) {
    this._simBlueprint = value;
  }

  @JsonProperty ("Iterations")
  public int getIterations () {
    return _iterations;
  }

  public void setIterations (int value) {
    this._iterations = value;
  }

  @JsonProperty ("Opportunities")
  public int getOpportunities () {
    return _opportunities;
  }

  public void setOpportunities (int value) {
    this._opportunities = value;
  }

  @JsonProperty ("SelectionAlgorithm")
  public String getSelectionAlgorithm () {
    return _selectionAlgorithm;
  }

  public void setSelectionAlgorithm (String value) {
    this._selectionAlgorithm = value;
  }

  @JsonProperty ("MeanProficiency")
  public float getMeanProficiency () {
    return _meanProficiency;
  }

  public void setMeanProficiency (float value) {
    // double d = Double.parseDouble(Float.toString(value));
    this._meanProficiency = value;
  }

  @JsonProperty ("SdProficiency")
  public float getSdProficiency () {
    return _sdProficiency;
  }

  public void setSdProficiency (float value) {
    // double d = Double.parseDouble(Float.toString(value));
    this._sdProficiency = value;
  }

  @JsonProperty ("StrandCorrelation")
  public float getStrandCorrelation () {
    return _strandCorrelation;
  }

  public void setStrandCorrelation (float value) {
    // double d = Double.parseDouble(Float.toString(value));
    this._strandCorrelation = value;
  }

  @JsonProperty ("Grade")
  public String getGrade () {
    return _grade;
  }

  public void setGrade (String value) {
    this._grade = value;
  }

  @JsonProperty ("GradeCode")
  public String getGradeCode () {
    return _gradeCode;
  }

  public void setGradeCode (String value) {
    this._gradeCode = value;
  }

  @JsonProperty ("Subject")
  public String getSubject () {
    return _subject;
  }

  public void setSubject (String value) {
    this._subject = value;
  }

  @JsonProperty ("Language")
  public String getLanguage () {
    return _language;
  }

  public void setLanguage (String value) {
    this._language = value;
  }

  @JsonProperty ("LanguageCode")
  public String getLanguageCode () {
    return _languageCode;
  }

  public void setLanguageCode (String value) {
    this._languageCode = value;
  }

  @JsonProperty ("Sim_Threads")
  public String getSim_Threads () {
    return _simThreads;
  }

  public void setSim_Threads (String value) {
    this._simThreads = value;
  }

  @JsonProperty ("Sim_ThinkTime")
  public String getSim_ThinkTime () {
    return _simThinkTime;
  }

  public void setSim_ThinkTime (String value) {
    this._simThinkTime = value;
  }

  @JsonProperty ("ErrorMsg")
  public String getErrorMsg () {
    return _errorMsg;
  }

  public void setErrorMsg (String value) {
    this._errorMsg = value;
  }

  @JsonProperty ("HandScoreItemTypes")
  public String getHandScoreItemTypes () {
    return _handScoreItemTypes;
  }

  public void setHandScoreItemTypes (String value) {
    this._handScoreItemTypes = value;
  }

  @JsonProperty ("Simulations")
  public int getSimulations () {
    return _simulations;
  }

  public void setSimulations (int value) {
    this._simulations = value;
  }
}
