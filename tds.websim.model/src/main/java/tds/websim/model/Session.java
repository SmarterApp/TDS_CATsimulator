/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import java.util.Date;
import java.util.UUID;

import AIR.Common.JsonSerializers.BoolToString;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class Session
{
  private UUID         _key;
  private String       _sessionID;
  private String       _proctorID;
  private String       _status;
  private String       _name;
  private String       _description;
  private Date         _dateCreated;
  private Date         _dateBegin;
  private Date         _dateEnd;
  private String       _serverAddress;
  private String       _reserved;
  private Date         _dateChanged;
  private Date         _dateVisited;
  private String       _clientName;
  private String       _environment;
  private int          _sessionType;
  private String       _simLanguage;
  private int          _simProctorDelay;
  private boolean      _simAbort;
  private String       _simStatus;
  private Date         _simStart;
  private Date         _simStop;
  private SessionTests _tests;
  private String       _errorMsg;

  @JsonProperty ("Key")
  public UUID getKey () {
    return _key;
  }

  public void setKey (UUID value) {
    this._key = value;
  }

  @JsonProperty ("SessionID")
  public String getSessionID () {
    return _sessionID;
  }

  public void setSessionID (String value) {
    this._sessionID = value;
  }

  @JsonProperty ("ProctorID")
  public String getProctorID () {
    return _proctorID;
  }

  public void setProctorID (String value) {
    this._proctorID = value;
  }

  @JsonProperty ("Status")
  public String getStatus () {
    return _status;
  }

  public void setStatus (String value) {
    this._status = value;
  }

  @JsonProperty ("Name")
  public String getName () {
    return _name;
  }

  public void setName (String value) {
    this._name = value;
  }

  @JsonProperty ("Description")
  public String getDescription () {
    return _description;
  }

  public void setDescription (String value) {
    this._description = value;
  }

  @JsonProperty ("DateCreated")
  public Date getDateCreated () {
    return _dateCreated;
  }

  public void setDateCreated (Date value) {
    this._dateCreated = value;
  }

  @JsonProperty ("DateBegin")
  public Date getDateBegin () {
    return _dateBegin;
  }

  public void setDateBegin (Date value) {
    this._dateBegin = value;
  }

  @JsonProperty ("DateEnd")
  public Date getDateEnd () {
    return _dateEnd;
  }

  public void setDateEnd (Date value) {
    this._dateEnd = value;
  }

  @JsonProperty ("ServerAddress")
  public String getServerAddress () {
    return _serverAddress;
  }

  public void setServerAddress (String value) {
    this._serverAddress = value;
  }

  @JsonProperty ("Reserved")
  public String getReserved () {
    return _reserved;
  }

  public void setReserved (String value) {
    this._reserved = value;
  }

  @JsonProperty ("DateChanged")
  public Date getDateChanged () {
    return _dateChanged;
  }

  public void setDateChanged (Date value) {
    this._dateChanged = value;
  }

  @JsonProperty ("DateVisited")
  public Date getDateVisited () {
    return _dateVisited;
  }

  public void setDateVisited (Date value) {
    this._dateVisited = value;
  }

  @JsonProperty ("ClientName")
  public String getClientName () {
    return _clientName;
  }

  public void setClientName (String value) {
    this._clientName = value;
  }

  @JsonProperty ("Environment")
  public String getEnvironment () {
    return _environment;
  }

  public void setEnvironment (String value) {
    this._environment = value;
  }

  @JsonProperty ("SessionType")
  public int getSessionType () {
    return _sessionType;
  }

  public void setSessionType (int value) {
    this._sessionType = value;
  }

  @JsonProperty ("Sim_Language")
  public String getSim_Language () {
    return _simLanguage;
  }

  public void setSim_Language (String value) {
    this._simLanguage = value;
  }

  @JsonProperty ("Sim_ProctorDelay")
  public int getSim_ProctorDelay () {
    return _simProctorDelay;
  }

  public void setSim_ProctorDelay (int value) {
    this._simProctorDelay = value;
  }

  @JsonProperty ("Sim_Abort")
  @JsonSerialize (using = BoolToString.class)
  public boolean getSim_Abort () {
    return _simAbort;
  }

  public void setSim_Abort (boolean value) {
    this._simAbort = value;
  }

  @JsonProperty ("Sim_Status")
  public String getSim_Status () {
    return _simStatus;
  }

  public void setSim_Status (String value) {
    this._simStatus = value;
  }

  @JsonProperty ("Sim_Start")
  public Date getSim_Start () {
    return _simStart;
  }

  public void setSim_Start (Date value) {
    this._simStart = value;
  }

  @JsonProperty ("Sim_Stop")
  public Date getSim_Stop () {
    return _simStop;
  }

  public void setSim_Stop (Date value) {
    this._simStop = value;
  }

  @JsonProperty ("Tests")
  public SessionTests getTests () {
    return _tests;
  }

  public void setTests (SessionTests value) {
    this._tests = value;
  }

  @JsonProperty ("ErrorMsg")
  public String getErrorMsg () {
    return _errorMsg;
  }

  public void setErrorMsg (String value) {
    this._errorMsg = value;
  }
}
