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

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class SessionTests extends ArrayList<SessionTest>
{
  /**
   * Eclipse generated serial ID;
   */
  protected static final long serialVersionUID = 4956229172078272049L;
  private String              _errorMsg;

  public String getErrorMsg () {
    return _errorMsg;
  }

  public void setErrorMsg (String value) {
    this._errorMsg = value;
  }

}
