/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.web.backing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tds.websim.bll.model.WebSimUserTask;
import tds.websim.bll.tasks.DefaultWebSimUserTask;
import TDS.Shared.Security.FormsAuthentication;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
@javax.faces.bean.ManagedBean (name = "siteMasterBacking")
@javax.faces.bean.RequestScoped
public class SiteMaster extends WebSimBasePage
{
  @SuppressWarnings ("unused")
  private final static Logger _logger         = LoggerFactory.getLogger (SiteMaster.class);

  private WebSimUserTask      _webSimUserTask = this.getBean ("webSimUserTask", DefaultWebSimUserTask.class);

  public String getUserName () {
    return this.getCurrUser ().getFullname ();
  }

  public String btnLogoutOnClick () {
    _webSimUserTask.logout (this.getUserInfo ());
    FormsAuthentication.signOut ();
    return "/Login.xhtml?faces-redirect=true";
  }
}
