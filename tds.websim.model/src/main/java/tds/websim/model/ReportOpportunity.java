/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class ReportOpportunity
{
  private String _student;
  private String _opportunity;
  private String _status;
  private String _historyAbility;
  private String _initialAbility;
  private String _trueTheta;
  private String _thetaScore;
  private String _thetaSE;
  private String _firstGroup;
  private String _firstB;
  private String _groups;
  private String _items;
  private String _meanDif;
  private String _varDif;

  public String getStudent () {
    return _student;
  }

  public void setStudent (String value) {
    this._student = value;
  }

  public String getOpportunity () {
    return _opportunity;
  }

  public void setOpportunity (String value) {
    this._opportunity = value;
  }

  public String getStatus () {
    return _status;
  }

  public void setStatus (String value) {
    this._status = value;
  }

  public String getHistoryAbility () {
    return _historyAbility;
  }

  public void setHistoryAbility (String value) {
    this._historyAbility = value;
  }

  public String getInitialAbility () {
    return _initialAbility;
  }

  public void setInitialAbility (String value) {
    this._initialAbility = value;
  }

  public String getTrueTheta () {
    return _trueTheta;
  }

  public void setTrueTheta (String value) {
    this._trueTheta = value;
  }

  public String getThetaScore () {
    return _thetaScore;
  }

  public void setThetaScore (String value) {
    this._thetaScore = value;
  }

  public String getThetaSE () {
    return _thetaSE;
  }

  public void setThetaSE (String value) {
    this._thetaSE = value;
  }

  public String getFirstGroup () {
    return _firstGroup;
  }

  public void setFirstGroup (String value) {
    this._firstGroup = value;
  }

  public String getFirstB () {
    return _firstB;
  }

  public void setFirstB (String value) {
    this._firstB = value;
  }

  public String getGroups () {
    return _groups;
  }

  public void setGroups (String value) {
    this._groups = value;
  }

  public String getItems () {
    return _items;
  }

  public void setItems (String value) {
    this._items = value;
  }

  public String getMeanDif () {
    return _meanDif;
  }

  public void setMeanDif (String value) {
    this._meanDif = value;
  }

  public String getVarDif () {
    return _varDif;
  }

  public void setVarDif (String value) {
    this._varDif = value;
  }
}
