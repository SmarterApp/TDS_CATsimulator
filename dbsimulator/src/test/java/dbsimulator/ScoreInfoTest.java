/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package dbsimulator;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.custommonkey.xmlunit.XMLAssert;

public class ScoreInfoTest {

  @Test
  public void testToXMLStringFull() {
    ScoreInfo overAllScoreInfo = new ScoreInfo(3, "overall", "Scored" );
    overAllScoreInfo.addSubScore(new ScoreInfo(1, "conventions", "Scored"));
    overAllScoreInfo.addSubScore(new ScoreInfo(1, "literary", "Scored"));
    overAllScoreInfo.addSubScore(new ScoreInfo(1, "greatstuff", "Scored"));
    String scoreInfoXML = overAllScoreInfo.toXMLString();
    String expectedXML = "<ScoreInfo scoreDimension=\"overall\" scorePoint=\"3\" scoreStatus=\"Scored\"><SubScoreList><ScoreInfo scoreDimension=\"conventions\" scorePoint=\"1\" scoreStatus=\"Scored\"><SubScoreList /></ScoreInfo><ScoreInfo scoreDimension=\"literary\" scorePoint=\"1\" scoreStatus=\"Scored\"><SubScoreList /></ScoreInfo><ScoreInfo scoreDimension=\"greatstuff\" scorePoint=\"1\" scoreStatus=\"Scored\"><SubScoreList /></ScoreInfo></SubScoreList></ScoreInfo>";
    try {
      XMLAssert.assertXMLEqual("ScoreInfo xml nodes are different", expectedXML, scoreInfoXML);
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }		
  }


  @Test
  public void testToXMLStringOverallOnly() {
    ScoreInfo overAllScoreInfo = new ScoreInfo(0, "overall", "Scored" );
    String scoreInfoXML = overAllScoreInfo.toXMLString();
    String expectedXML = "<ScoreInfo scoreDimension=\"overall\" scorePoint=\"0\" scoreStatus=\"Scored\"><SubScoreList/></ScoreInfo>";
    try {
      XMLAssert.assertXMLEqual("ScoreInfo xml nodes are different", expectedXML, scoreInfoXML);
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }			
  }
}
