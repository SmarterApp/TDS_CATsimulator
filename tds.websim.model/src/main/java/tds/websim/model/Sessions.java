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
public class Sessions extends ArrayList<Session>
{
  /**
   * Eclipse generated serial id.
   */
  private static final long serialVersionUID = -5781951486104714751L;

  private String            _errorMsg;

  public String getErrorMsg () {
    return _errorMsg;
  }

  public void setErrorMsg (String value) {
    this._errorMsg = value;
  }

}
