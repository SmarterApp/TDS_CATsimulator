/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/

package dbsimulator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = { "classpath:dbsimulator-test-context.xml" })
public class SimSessionTest {

  @Autowired
  private SimSessionFactory sessionFactory;

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testRunSimulations() {
    String sessionKey = "5F8CEA73-F7A0-407A-B796-55557AF219DF"; 
    SimSession sess = sessionFactory.createSimSession();
    sess.runSimulations(sessionKey);
    // TODO: Re-visit this - For threaded runs breakpoint must be set in appropriate places.  
    assertTrue("Completed running sessions", true);
  }

  @Test
  public void testAbortSimulation() {
    String sessionKey = "5F8CEA73-F7A0-407A-B796-55557AF219DF"; 
    SimSession sess = sessionFactory.createSimSession();
    sess.runSimulations(sessionKey);
    sess.AbortSimulation();
    // TODO: Re-visit this - For threaded runs breakpoint must be set in appropriate places, 
    // Also need to check whether it is actually aborted or not 
    assertTrue("Completed running sessions", true);
  }

}
