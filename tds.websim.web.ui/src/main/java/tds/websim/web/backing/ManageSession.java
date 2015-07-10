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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean (name = "manageSession")
@RequestScoped
public class ManageSession extends WebSimBasePage
{
  Map<String, String> requestForm;

  public String getHfsk () {
    return requestForm.get ("hfsk");
  }

  public String getHfcn () {
    return requestForm.get ("hfcn");
  }

  public String getHfst () {
    return requestForm.get ("hfst");
  }

  public String getHfdn () {
    return requestForm.get ("hfdn");
  }

  public String getHflg () {
    return requestForm.get ("hflg");
  }

  public String getHfsid () {
    return requestForm.get ("hfsid");
  }

  @Override
  protected void onInit () {
    super.onInit ();
    requestForm = getRequestParams ();
  }
}
