/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class BlueprintValidationStatus
{
  private String                         _status;
  private int                            _numFatals;
  private int                            _numWarnings;

  private List<BlueprintValidationError> _errors;

  @JsonProperty ("Status")
  public String getStatus () {
    return _status;
  }

  public void setStatus (String value) {
    this._status = value;
  }

  @JsonProperty ("NumFatals")
  public int getNumFatals () {
    return _numFatals;
  }

  public void setNumFatals (int value) {
    this._numFatals = value;
  }

  @JsonProperty ("NumWarnings")
  public int getNumWarnings () {
    return _numWarnings;
  }

  public void setNumWarnings (int value) {
    this._numWarnings = value;
  }

  @JsonProperty ("Errors")
  public List<BlueprintValidationError> getErrors () {
    return _errors;
  }

  public void setErrors (List<BlueprintValidationError> value) {
    this._errors = value;
  }

}
