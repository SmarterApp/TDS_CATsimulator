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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class ItembankConfig
{
  private String _clientname;
  private String _bankKey;
  private String _configID;
  private Date   _dateLoaded;
  private String _error;

  @JsonProperty ("ClientName")
  public String getClientname () {
    return _clientname;
  }

  public void setClientname (String value) {
    this._clientname = value;
  }

  @JsonProperty ("BankKey")
  public String getBankKey () {
    return _bankKey;
  }

  public void setBankKey (String value) {
    this._bankKey = value;
  }

  @JsonProperty ("ConfigID")
  public String getConfigID () {
    return _configID;
  }

  public void setConfigID (String value) {
    this._configID = value;
  }

  @JsonProperty ("DateLoaded")
  public Date getDateLoaded () {
    return _dateLoaded;
  }

  public void setDateLoaded (Date value) {
    this._dateLoaded = value;
  }

  @JsonProperty ("Error")
  public String getError () {
    return _error;
  }

  public void setError (String value) {
    this._error = value;
  }

}
