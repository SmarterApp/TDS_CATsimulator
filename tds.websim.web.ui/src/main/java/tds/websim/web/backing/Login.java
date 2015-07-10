/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.web.backing;

import java.util.Collections;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tds.websim.bll.model.WebSimUserTask;
import tds.websim.bll.tasks.DefaultWebSimUserTask;
import tds.websim.model.WebSimUser;
import TDS.Shared.Exceptions.ReturnStatusException;
import TDS.Shared.Security.TDSIdentity;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
@javax.faces.bean.ManagedBean (name = "loginBacking")
@javax.faces.bean.RequestScoped
public class Login extends WebSimBasePage
{
  private final static Logger _logger        = LoggerFactory.getLogger (Login.class);
  private WebSimUserTask      webSimUserTask = this.getBean ("webSimUserTask", DefaultWebSimUserTask.class);

  private String              _userId;
  private String              _password;

  public String getUserId () {
    return _userId;
  }

  public void setUserId (String value) {
    this._userId = value;
  }

  public String getPassword () {
    return _password;
  }

  public void setPassword (String value) {
    this._password = value;
  }

  public String btnLoginOnClick () {
    FacesMessage message = null;
    String outcome = null;

    if (!StringUtils.isEmpty (this.getUserId ()) && !StringUtils.isEmpty (this.getPassword ())) {
      try {
        _logger.info ("Attempting Authentication - uid:" + this.getUserId ());

        if (this.authenticateUser (this.getUserId (), this.getPassword ())) {
          _logger.info ("Authentication Successful - uid:" + this.getUserId ());
          // Force redirect to the session dashboard page
          outcome = "Setup/Session_Dashboard.xhtml?faces-redirect=true";
          message = new FacesMessage ();
        } else {
          _logger.warn ("Authentication Failed - uid:" + this.getUserId ());
          message = new FacesMessage ("Invalid Username and/or Password.");
        }
      } catch (ReturnStatusException e) {
        _logger.error (e.getMessage (), e);
        message = new FacesMessage ("Problem authenticating user.");
      }
    } else {
      message = new FacesMessage ("Username and Password required.");
      // outcome = "/Login.xhtml";
    }

    FacesContext.getCurrentInstance ().addMessage (null, message);
    return outcome;
  }

  private boolean authenticateUser (String userID, String password) throws ReturnStatusException
  {
    boolean isAuth = false;
    WebSimUser user = new WebSimUser (userID, password);
    WebSimUserTask task = this.webSimUserTask;
    if (task.validateUser (user))
    {
      task.saveUser (user, this.getUserInfo ());
      isAuth = true;
      TDSIdentity.createNew (user.getId (), Collections.<String, String> emptyMap ()).saveAuthCookie ();
    } else {
      TDSIdentity.createNew ("", Collections.<String, String> emptyMap ()).saveAuthCookie ();
    }
    return isAuth;
  }
}
