/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.itemselectionparam;

import java.util.ArrayList;

public class ItemSelectionParams extends ArrayList<ItemSelectionParam>
{
  private static final long serialVersionUID = 3373334239644872350L;
  private String            _errorMsg;

  public String getErrorMsg () {
    return _errorMsg;
  }

  public void setErrorMsg (String errorMsg) {
    this._errorMsg = errorMsg;
  }

}
