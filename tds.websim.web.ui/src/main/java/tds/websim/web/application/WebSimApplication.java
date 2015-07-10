/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.web.application;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AIR.Common.Web.Session.BaseServletContextListener;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class WebSimApplication extends BaseServletContextListener
{
  private final static Logger _logger = LoggerFactory.getLogger (WebSimApplication.class);

  @Override
  public void contextInitialized (ServletContextEvent sce) {
    super.contextInitialized (sce);
    StringBuilder log = new StringBuilder ("WebSim application Started: ");
    _logger.info (log.toString ());
  }

  @Override
  public void contextDestroyed (ServletContextEvent sce) {
    super.contextDestroyed (sce);
    _logger.info ("WebSim application shutdown");
  }
}
