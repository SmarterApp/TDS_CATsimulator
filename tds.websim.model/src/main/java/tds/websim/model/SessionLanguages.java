/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class SessionLanguages extends ArrayList<SessionLanguage>
{

  /**
   * Eclipse generated serial ID
   */
  private static final long serialVersionUID = -1395079499053761123L;

  public SessionLanguages () {
    super ();
  }

  public SessionLanguages (Collection<SessionLanguage> list) {
    super (list);
  }
}
