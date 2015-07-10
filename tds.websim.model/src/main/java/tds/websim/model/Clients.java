/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class Clients extends ArrayList<Client>
{
  /**
   * Auto-generated serial ID by Eclipse
   */
  private static final long serialVersionUID = 8077497775754816692L;
  private String            _errorMsg;

  @JsonProperty ("ErrrorMsg")
  public String getErrorMsg () {
    return _errorMsg;
  }

  public void setErrorMsg (String value) {
    this._errorMsg = value;
  }

}
