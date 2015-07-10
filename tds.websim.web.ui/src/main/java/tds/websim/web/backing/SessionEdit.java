/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.web.backing;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import AIR.Common.Web.Session.HttpContext;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
@javax.faces.bean.ManagedBean (name = "sessionEdit")
@javax.faces.bean.RequestScoped
public class SessionEdit extends SessionDashboard
{
  private String _sessionKey;
  private String _clientName;
  private String _sessionType;
  private String _description;
  private String _language;
  private String _sessionID;
  private String _newSess;

  public String getSessionKey () {
    return this._sessionKey;
  }

  public String getClientName () {
    return this._clientName;
  }

  public String getSessionType () {
    return this._sessionType;
  }

  public String getDescription () {
    return this._description;
  }

  public String getLanguage () {
    return this._language;
  }

  public String getSessionID () {
    return this._sessionID;
  }

  public String getNewSess () {
    return this._newSess == null ? StringUtils.EMPTY : this._newSess;
  }

  @Override
  protected void onInit () {
    super.onInit ();

    this.getFormParams ();
    this.saveToSession ();
  }

  private void getFormParams () {
    Map<String, String> request = this.getRequestParams ();
    this._sessionKey = request.get ("hfsk");
    this._clientName = request.get ("hfcn");
    this._sessionType = request.get ("hfst");
    this._description = request.get ("hfdn");
    this._language = request.get ("hflg");
    this._sessionID = request.get ("hfsid");
    this._newSess = request.get ("hfnew");
  }

  private void saveToSession () {
    HttpSession session = HttpContext.getCurrentContext ().getRequest ().getSession ();

    if (!StringUtils.isEmpty (this._sessionKey))
      session.setAttribute ("hfsk", this._sessionKey);

    if (!StringUtils.isEmpty (this._clientName))
      session.setAttribute ("hfcn", this._clientName);

    if (!StringUtils.isEmpty (this._sessionType))
      session.setAttribute ("hfst", this._sessionType);

    if (!StringUtils.isEmpty (this._description))
      session.setAttribute ("hfdn", this._description);

    if (!StringUtils.isEmpty (this._language))
      session.setAttribute ("hflg", this._language);

    if (!StringUtils.isEmpty (this._sessionID))
      session.setAttribute ("hfsid", this._sessionID);
  }
}
