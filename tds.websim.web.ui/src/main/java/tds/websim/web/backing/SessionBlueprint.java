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

@javax.faces.bean.ManagedBean (name = "sessionBlueprint")
@javax.faces.bean.RequestScoped
public class SessionBlueprint extends WebSimBasePage
{
  Map<String, String> requestForm;

  public String getHfsk () {
    return requestForm.get ("hfsk");
  }

  public String getHftk () {
    return requestForm.get ("hftk");
  }

  public String getHfcn () {
    return requestForm.get ("hfcn");
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
