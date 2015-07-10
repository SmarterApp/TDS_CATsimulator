/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import AIR.Common.JsonSerializers.BoolToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class Client
{
  private String  _name;
  private boolean _isAdmin;
  private String  _errorMsg;

  @JsonProperty ("Name")
  public String getName () {
    return _name;
  }

  public void setName (String value) {
    this._name = value;
  }

  @JsonProperty ("IsAdmin")
  @JsonSerialize (using = BoolToString.class)
  public boolean getIsAdmin () {
    return _isAdmin;
  }

  public void setIsAdmin (boolean value) {
    this._isAdmin = value;
  }

  @JsonIgnore
  public String getErrorMsg () {
    return _errorMsg;
  }

  public void setErrorMsg (String value) {
    this._errorMsg = value;
  }
}
