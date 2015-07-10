/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.itemselectionparam;

import java.util.Hashtable;
import java.util.List;

import AIR.Common.Criteria.INamedCriteria;
import AIR.Common.Helpers._Ref;

public abstract class BpEntityParams
{
  public abstract String getEntityType ();

  public abstract void initialize (String algorithmType);

  private Hashtable<String, INamedCriteria> _criteria = new Hashtable<String, INamedCriteria> ();

  public void appendItemSelectionParameterDefaultRecords (String sAlgorithmType, List<ItemSelectionParamTemplate> dt) {
    for (String name : _criteria.keySet ()) {
      INamedCriteria criteria = _criteria.get (name);
      if (criteria.isEnabled ())
        dt.add (new ItemSelectionParamTemplate (sAlgorithmType, getEntityType (), criteria.getName (), criteria.getDefaultValueString (), criteria.getDescription ()));
    }
  }

  public boolean meets (String sCriteriaName, String sValue, _Ref<String> sMessage) {
    boolean bMet = false;
    if (_criteria.containsKey (sCriteriaName))
    {
      INamedCriteria criteria = _criteria.get (sCriteriaName);
      if (criteria.isEnabled ())
        bMet = criteria.meets (sValue, sMessage);
    }
    else
    {
      sMessage.set (String.format ("Unknown criteria: %s", sCriteriaName));
    }
    return bMet;
  }

  protected void addCriteria (String name, INamedCriteria criteria) {
    _criteria.put (name, criteria);
  }
}
