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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import tds.itemselection.base.TestItem;
import AIR.Common.Helpers._Ref;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = { "classpath:dbsimulator-test-context.xml" })
public class VirtualStudentTest {

  @Autowired
  @Qualifier("simDALClientFactory")
  private SimDalFactory _simDalFactory;

  private VirtualStudent _student = null;	
  private double _fErrorMargin;
  private SimDal _simDAL;

  @Before
  public void setUp() throws Exception {		
    List<String> strands = new ArrayList<String>();
    strands.add("BadStuff");
    strands.add("AvgStuff");
    strands.add("GoodStuff");
    _student = new VirtualStudent(0.0, 0.2, strands, 0.1, 0.01);	
    _student.initialize(1);

    String sessionKey = "5F8CEA73-F7A0-407A-B796-55557AF219DF"; 
    _simDAL = _simDalFactory.createSimDal(sessionKey); 		

    _fErrorMargin = 0.0001;
  }

  @Test
  public void testGetTesteeKey() {
    assertEquals("Testee key not equal", _student.getTesteeKey(), 1);
  }

  @Test
  public void testGetProficiency() {
    assertEquals("Proficiency differs", _student.getProficiency(), 0.0, _fErrorMargin);
  }

  @Test
  public void testGetStrands() {
    assertArrayEquals("Strands are different", _student.getStrands().toArray(), new String[]{"BadStuff", "AvgStuff", "GoodStuff"});
  }

  @Test
  public void testInitialAbility() {
    double fEpsilon = 0.2;
    assertTrue("Initial ability not within range", (_student.initialAbility(fEpsilon) >= _student.getProficiency() - fEpsilon) && 
        (_student.initialAbility(fEpsilon) <= _student.getProficiency() + fEpsilon ));
  }

  @Test
  public void testItemScoreNoDimensions() {
    TestItem item = new TestItem("1234", "G-1234", 1, false, "GoodStuff", true, 0.7); 
    SessionTest test = new SessionTest("Hello", "Hello", 20, 1, 20);
    _Ref<ScoreInfo> overAllScoreInfo = new _Ref<ScoreInfo>(new ScoreInfo());
    _student.ItemScore(_simDAL, test, item, overAllScoreInfo);

    String scoreInfoXML = overAllScoreInfo.get().toXMLString();
    System.out.print(scoreInfoXML);

    String expectedXML = "<ScoreInfo scoreDimension=\"overall\" scorePoint=\"-1\" scoreStatus=\"NotScored\"><SubScoreList/></ScoreInfo>";
    try {
      XMLAssert.assertXMLEqual("ScoreInfo xml nodes are different", expectedXML, scoreInfoXML);
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }					
  }
}
