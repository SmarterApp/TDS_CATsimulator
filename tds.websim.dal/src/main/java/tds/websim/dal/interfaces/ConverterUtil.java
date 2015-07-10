/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.dal.interfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tongliang LIU [tliu@air.org]
 * 
 */
public final class ConverterUtil
{
  private ConverterUtil () {

  }

  private final static Logger _logger = LoggerFactory.getLogger (ConverterUtil.class);

  public static int sessionTypeStrToInt (String sessionType) {
    int sessionTypeNum = -1;
    if (sessionType != null) {
      try {
        sessionTypeNum = Integer.parseInt (sessionType);
        if (sessionTypeNum < 0)
          sessionTypeNum = -1;
      } catch (NumberFormatException e) {
        _logger.warn (String.format ("ConverterUtil.sessionTypeStrToInt: sessionType should be either null or numeric, but it is %1$s.", sessionType));
      }
    }
    return sessionTypeNum;
  }

  public static double floatObjectToDouble (Object o) {
    double d = 0;
    if (o instanceof Float) {
      d = (double) ((float) o);
    }
    else
      _logger.warn (String.format ("ConverterUtil.floatObjectToDouble: o should be a Float object, but it is %1$s.", o.getClass ().toString ()));

    return d;
  }

}
