/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.itemselectionparam;

import AIR.Common.Criteria.BinaryFloatCriteria;
import AIR.Common.Criteria.BinaryIntegerCriteria;

public class SegmentParams extends BpEntityParams
{
  private static final String   _entityType                    = "Segment";

  private BinaryIntegerCriteria _offGradeMinItemsAdministered  = null;
  private BinaryIntegerCriteria _proficientPLevel              = null;
  private BinaryFloatCriteria   _offGradeProbAffectProficiency = null;

  public SegmentParams () {
  }

  @Override
  public String getEntityType () {
    return _entityType;
  }

  @Override
  public void initialize (String algorithmType) {
    boolean bAdaptive2 = algorithmType.equalsIgnoreCase ("ADAPTIVE2");

    _offGradeMinItemsAdministered = new BinaryIntegerCriteria ("offGradeMinItemsAdministered",
        "Minimum number of on-grade operational items administered before considering for off-grade items", bAdaptive2, 0, 200, 30);
    _proficientPLevel = new BinaryIntegerCriteria ("proficientPLevel",
        "Level at which student is considered proficient for the test", bAdaptive2, 1, 5, 3);
    _offGradeProbAffectProficiency = new BinaryFloatCriteria ("offGradeProbAffectProficiency",
        "Probability that introducing off-grade items will influence the student proficiency must be less than this value", bAdaptive2, 0.0F, 1.0F, 0.01F);

    addCriteria (_offGradeMinItemsAdministered.getName (), _offGradeMinItemsAdministered);
    addCriteria (_proficientPLevel.getName (), _proficientPLevel);
    addCriteria (_offGradeProbAffectProficiency.getName (), _offGradeProbAffectProficiency);
  }
}
