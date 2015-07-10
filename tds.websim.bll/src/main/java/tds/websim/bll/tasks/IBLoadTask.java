/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.bll.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;
import tds.websim.dal.interfaces.IItemBankDao;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
@Service ("ibLoadTask")
@Scope ("prototype")
public class IBLoadTask
{
  @Autowired
  private IItemBankDao _itembankDAO;

  public ReturnStatus loadConfig (String testKey, String xmlTestPackage) throws ReturnStatusException
  {
    return _itembankDAO.loaderMain (testKey, xmlTestPackage);
  }
}
