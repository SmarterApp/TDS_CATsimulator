/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.itemselectionparam;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import AIR.Common.Helpers._Ref;

public class AdhocParamsTest
{

  @Before
  public void setUp () throws Exception {
  }

  @Test
  public void testAppendItemSelectionParameterDefaultRecords () {
    List<ItemSelectionParamTemplate> dt = new ArrayList<ItemSelectionParamTemplate> ();
    AdhocParams adaptive2AdhocParams = new AdhocParams ("adaptive2");
    AdhocParams altAdaptiveAdhocParams = new AdhocParams ("altadaptive");
    adaptive2AdhocParams.appendItemSelectionParameterDefaultRecords (dt);
    altAdaptiveAdhocParams.appendItemSelectionParameterDefaultRecords (dt);
    // TODO: Check, we have data for all algorithm types in dt
    assert (true);
  }

  @Test
  public void testMeets () {
    AdhocParams adhocParams = new AdhocParams ("adaptive2");
    _Ref<String> sMesg = new _Ref<String> ();
    boolean bMet = adhocParams.meets ("Segment", "offGradeMinItemsAdministered", String.valueOf (40), sMesg);
    boolean bExpected = true;
    assertEquals (sMesg.get (), bExpected, bMet);
  }

}
