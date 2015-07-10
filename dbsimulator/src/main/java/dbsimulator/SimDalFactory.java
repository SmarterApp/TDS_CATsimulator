/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/

package dbsimulator;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.ObjectFactory;

@Component("simDALClientFactory")
public class SimDalFactory {

  private ObjectFactory<SimDal> factory;

  public void setFactory(ObjectFactory<SimDal> factory) {
    this.factory = factory;
  }

  public SimDal createSimDal(String sessionKey) {
    SimDal simDal = this.factory.getObject();
    simDal.setSessionKey(sessionKey);
    return simDal;
  }	
}
