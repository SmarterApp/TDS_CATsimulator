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
public class LoaderError
{
  private String _configID;
  private String _severity;
  private String _test;
  private String _error;

  public String getConfigID () {
    return _configID;
  }

  public void setConfigID (String value) {
    this._configID = value;
  }

  public String getSeverity () {
    return _severity;
  }

  public void setSeverity (String value) {
    this._severity = value;
  }

  public String getTest () {
    return _test;
  }

  public void setTest (String value) {
    this._test = value;
  }

  public String getError () {
    return _error;
  }

  public void setError (String value) {
    this._error = value;
  }
}
