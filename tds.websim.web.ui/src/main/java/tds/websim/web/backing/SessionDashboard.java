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

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
@javax.faces.bean.ManagedBean (name = "sessionDashboard")
@javax.faces.bean.RequestScoped
public class SessionDashboard extends WebSimBasePage
{
  @SuppressWarnings ("unused")
  private final static Logger _logger = LoggerFactory.getLogger (SessionDashboard.class);

  @Override
  protected void onInit () {
    super.onInit ();
  }
}
