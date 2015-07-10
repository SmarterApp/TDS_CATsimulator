/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.bll.model;

import java.util.UUID;

import tds.websim.model.WebSimUser;
import TDS.Shared.Exceptions.ReturnStatusException;
import TDS.Shared.Web.UserCookie;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public abstract class WebSimUserTask extends DaoTaskBase
{
  public abstract boolean validateUser (WebSimUser user) throws ReturnStatusException;

  public boolean logout (UserCookie userInfo) {
    userInfo.ExpiresCookie ();
    return true;
  }

  public boolean loadUser (WebSimUser user, UserCookie userInfo)
  {
    UUID key;
    try {
      key = UUID.fromString (userInfo.GetValue ("browserKey"));
    } catch (IllegalArgumentException e) {
      return false;
    }

    user.setId (userInfo.GetValue ("userID"));
    user.setBrowserKey (key);
    user.setFullname (userInfo.GetValue ("fullName"));
    user.setPassword (userInfo.GetValue ("password"));
    user.setAuth (Boolean.parseBoolean (userInfo.GetValue ("isAuth")));
    user.setNew (Boolean.parseBoolean (userInfo.GetValue ("isNew")));
    user.setClientName (userInfo.GetValue ("clientName"));
    return true;
  }

  public boolean saveUser (WebSimUser user, UserCookie userInfo)
  {
    userInfo.SetValue ("userID", user.getId ());
    userInfo.SetValue ("browserKey", user.getBrowserKey ().toString ());
    userInfo.SetValue ("fullName", user.getFullname ());
    userInfo.SetValue ("password", user.getPassword ());
    userInfo.SetValue ("isAuth", Boolean.toString (user.isAuth ()));
    userInfo.SetValue ("isNew", Boolean.toString (user.isNew ()));
    userInfo.SetValue ("clientName", user.getClientName ());
    return true;
  }
}
