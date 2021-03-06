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

@Component("simSessionClientFactory")
public class SimSessionFactory {
  private ObjectFactory<SimSession> factory;

  public void setFactory(ObjectFactory<SimSession> factory) {
    this.factory = factory;
  }

  public SimSession createSimSession() {
    SimSession simSession = this.factory.getObject();
    return simSession;
  }	
}
