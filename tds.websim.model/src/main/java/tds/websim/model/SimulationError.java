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
public class SimulationError
{
  private String _procname;
  private int    _numErrors;

  @JsonProperty ("Procname")
  public String getProcname () {
    return _procname;
  }

  public void setProcname (String value) {
    this._procname = value;
  }

  @JsonProperty ("NumErrors")
  public int getNumErrors () {
    return _numErrors;
  }

  public void setNumErrors (int value) {
    this._numErrors = value;
  }

}
