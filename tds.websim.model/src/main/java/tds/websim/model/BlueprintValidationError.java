/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class BlueprintValidationError
{
  private String _severity;
  private String _test;
  private String _error;

  @JsonProperty ("Severity")
  public String getSeverity () {
    return _severity;
  }

  public void setSeverity (String value) {
    this._severity = value;
  }

  @JsonProperty ("Test")
  public String getTest () {
    return _test;
  }

  public void setTest (String value) {
    this._test = value;
  }

  @JsonProperty ("Error")
  public String getError () {
    return _error;
  }

  public void setError (String value) {
    this._error = value;
  }
}
