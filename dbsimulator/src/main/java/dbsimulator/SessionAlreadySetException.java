/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator;

public class SessionAlreadySetException extends RuntimeException {
  /**
   * 
   */
  private static final long serialVersionUID = 2249230885937420855L;

  public SessionAlreadySetException(String sessionKey) {
    super(String.format("Session already set to %s", sessionKey));
  }
}
