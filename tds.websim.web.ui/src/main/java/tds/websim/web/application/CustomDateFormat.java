/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.web.application;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateFormat extends SimpleDateFormat
{
  private static final long serialVersionUID = 1274711962546200251L;

  @Override
  public StringBuffer format (Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
    if (date == null || date.getTime () == 0) {
      return toAppendTo;
    }
    return super.format (date, toAppendTo, fieldPosition);
  }
}
