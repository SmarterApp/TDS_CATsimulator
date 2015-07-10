/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.bll.model;

import org.springframework.beans.factory.annotation.Autowired;

import tds.websim.dal.interfaces.ISessionDao;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
abstract class DaoTaskBase
{
  @Autowired
  private ISessionDao _sessionDao;

  protected ISessionDao getSessionDAO () {
    return this._sessionDao;
  }

}
