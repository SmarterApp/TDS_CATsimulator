/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.web.backing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean (name = "loadConfigs")
@RequestScoped
public class LoadConfigs extends WebSimBasePage
{
  @SuppressWarnings ("unused")
  private final static Logger _logger = LoggerFactory.getLogger (LoadConfigs.class);

  @Override
  protected void onInit () {
    super.onInit ();
  }
}
