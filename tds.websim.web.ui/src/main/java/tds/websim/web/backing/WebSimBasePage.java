/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.web.backing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import tds.websim.bll.model.SetupSimTask;
import tds.websim.bll.model.WebSimUserTask;
import tds.websim.bll.tasks.DefaultSetupSimTask;
import tds.websim.bll.tasks.DefaultWebSimUserTask;
import tds.websim.model.Client;
import tds.websim.model.Clients;
import tds.websim.model.WebSimUser;
import AIR.Common.Web.Session.Server;
import TDS.Shared.Web.BasePage;
import TDS.Shared.Web.UserCookie;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public abstract class WebSimBasePage extends BasePage
{
  protected static final String COOKIE_NAME     = "WebSim";

  private WebSimUser            _user;
  private UserCookie            _userInfo;
  private SetupSimTask          setupSimTask    = this.getBean ("setupSimTask", DefaultSetupSimTask.class);
  private WebSimUserTask        _webSimUserTask = this.getBean ("webSimUserTask", DefaultWebSimUserTask.class);

  private String                _selectedClient;
  private Clients               _clients;

  public String getServerPath () {
    return Server.getContextPath ();
  }

  public WebSimUser getCurrUser () {
    if (this._user == null) {
      this._user = new WebSimUser ();
      _webSimUserTask.loadUser (this._user, this.getUserInfo ());
    }
    return this._user;
  }

  public void setCurrUser (WebSimUser value) {
    this._user = value;
  }

  public UserCookie getUserInfo () {
    if (this._userInfo == null)
      this._userInfo = new UserCookie (getCurrentContext (), this.getTdsSettings ().getCookieName (COOKIE_NAME));

    return this._userInfo;
  }

  public String getSelectedClient () {
    return _selectedClient;
  }

  public void setSelectedClient (String value) {
    this._selectedClient = value;
  }

  public List<SelectItem> getClientList () {
    Map<String, String> request = this.getRequestParams ();

    List<SelectItem> lst = new ArrayList<SelectItem> ();

    // In JSF, value comes first, and label comes last.
    // In .NET, reversely.
    lst.add (new SelectItem (StringUtils.EMPTY, "Select a client..."));

    for (Client c : this._clients) {
      lst.add (new SelectItem (c.getName (), c.getName ()));

      String clientNameTmp = StringUtils.EMPTY;
      if (request.containsKey ("clientname"))
        clientNameTmp = request.get ("clientname");
      if (clientNameTmp.equals (c.getName ()))
        this.setSelectedClient (c.getName ());
    }
    return lst;
  }

  public void onClientValueChanged (ValueChangeEvent e) {
    this.setSelectedClient (e.getNewValue ().toString ());
  }

  private void initClientList () {
    this._clients = this.setupSimTask.getUserClients (this.getCurrUser ().getId ());
  }

  protected Map<String, String> getRequestParams () {
    return FacesContext.getCurrentInstance ().getExternalContext ().getRequestParameterMap ();
  }

  /**
   * Mimic .NET's System.Web.UI.Page.OnInit Event handler using JSF's
   * {@code @PostContruct} <br/>
   * 
   * Ref: http://stackoverflow.com/questions/3406555/why-use-postconstruct
   */
  @PostConstruct
  protected void onInit () {
    this.initClientList ();
  }
}
