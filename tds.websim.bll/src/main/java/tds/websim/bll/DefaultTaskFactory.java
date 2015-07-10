/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.bll;

import tds.websim.bll.model.ManageSimTask;
import tds.websim.bll.model.SetupSimTask;
import tds.websim.bll.model.TaskFactory;
import tds.websim.bll.model.WebSimUserTask;
import tds.websim.bll.tasks.DefaultManageSimTask;
import tds.websim.bll.tasks.DefaultSetupSimTask;
import tds.websim.bll.tasks.DefaultWebSimUserTask;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class DefaultTaskFactory implements TaskFactory
{
  @Override
  public SetupSimTask createSetupSimTask () {
    return new DefaultSetupSimTask ();
  }

  @Override
  public ManageSimTask createManageSimTask () {
    return new DefaultManageSimTask ();
  }

  @Override
  public WebSimUserTask createWebSimUserTask () {
    return new DefaultWebSimUserTask ();
  }

}
