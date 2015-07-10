/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.dal.mysql;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import AIR.Common.DB.SQLConnection;
import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;
import tds.dll.api.ISimDLL;
import tds.websim.dal.interfaces.DAO;
import tds.websim.dal.interfaces.IItemBankDao;

@Component
@Scope ("prototype")
public class MysqlItemBankDao extends DAO implements IItemBankDao
{
  private static final Logger _logger     = LoggerFactory.getLogger (MysqlItemBankDao.class);

  @Autowired
  private ISimDLL             _simdll;

  @Override
  public ReturnStatus loaderMain (String testKey, String xmlTestPackage) throws ReturnStatusException {
    try (SQLConnection conn = getSQLConnection ()) {
      _simdll.SIM_LoaderMain (conn, testKey, xmlTestPackage);
    } catch (SQLException e) {
      _logger.error ("ItemBankDao.loaderMain: error running SIM_LoaderMain.", e);
      throw new ReturnStatusException (e);
    }
    return new ReturnStatus ("success", "Successfully loaded the test package.");
  }
}
