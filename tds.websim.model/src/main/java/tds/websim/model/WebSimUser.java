/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import java.util.UUID;

import TDS.Shared.Security.TDSUser;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class WebSimUser extends TDSUser
{
  private UUID _browserKey;

  public UUID getBrowserKey () {
    return _browserKey;
  }

  public void setBrowserKey (UUID value) {
    this._browserKey = value;
  }

  public WebSimUser () {

  }

  public WebSimUser (String userID, String password) {
    this.setId (userID);
    this.setPassword (password);
  }
}
