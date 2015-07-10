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

import AIR.Common.Helpers._Ref;

/**
 * 
 * This class is to hold ad-hoc algorithm parameters defined at all different entity levels
 * Ad-hoc parameters are algorithm parameters which are added later on for an algorithm type    
 * Currently, parameters are defined only at the 'Segment' level for adaptive2 with off-grade selection feature
 *    
 * When necessary, define new BpEntityParams derived classes with parameters and add them to this class 
 *  
 * aphilip - Initial version 12/2014   
 *
 */
public class AdhocParams
{
  private String                            _algorithmType;
  private SegmentParams                     _segmentLevelParams = new SegmentParams ();
  private Hashtable<String, BpEntityParams> _bpEntityParams     = new Hashtable<String, BpEntityParams> ();

  public AdhocParams () {
    _bpEntityParams.put (_segmentLevelParams.getEntityType (), _segmentLevelParams);
  }

  public AdhocParams (String algorithmType) {
    this ();
    initialize (algorithmType);
  }

  public void initialize (String algorithmType) {
    _algorithmType = algorithmType;
    for (String name : _bpEntityParams.keySet ()) {
      BpEntityParams bpEntityParam = _bpEntityParams.get (name);
      bpEntityParam.initialize (_algorithmType);
    }
  }

  public void appendItemSelectionParameterDefaultRecords (List<ItemSelectionParamTemplate> dt) {
    for (String name : _bpEntityParams.keySet ()) {
      BpEntityParams bpEntityParam = _bpEntityParams.get (name);
      bpEntityParam.appendItemSelectionParameterDefaultRecords (_algorithmType, dt);
    }
  }

  public boolean meets (String paramLevel, String paramName, String paramValue, _Ref<String> sMessage) {
    boolean bMet = false;
    if (_bpEntityParams.containsKey (paramLevel))
    {
      BpEntityParams bpEntityParam = _bpEntityParams.get (paramLevel);
      bMet = bpEntityParam.meets (paramName, paramValue, sMessage);
    }
    else
    {
      sMessage.set (String.format ("Unknown entity: %s", paramLevel));
    }
    return bMet;
  }
}

