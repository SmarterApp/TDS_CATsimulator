/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ScoreInfo")
public class ScoreInfo {

  private int _scorePoint;
  private String _scoreDimension;
  private String _scoreStatus;
  private List<ScoreInfo> _subScoreList = new ArrayList<ScoreInfo>();

  public ScoreInfo() {
  }

  public ScoreInfo(int scorePoint, String scoreDimension, String scoreStatus) {
    setScorePoint(scorePoint);
    setScoreDimension(scoreDimension);
    setScoreStatus(scoreStatus);
  }

  public String toXMLString() {

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(ScoreInfo.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
      StringWriter strw = new StringWriter();
      jaxbMarshaller.marshal(this, strw);
      return strw.toString();
    } catch (JAXBException e) {
      e.printStackTrace();
      throw new RuntimeException("Problem serializing:", e);
    }
  }

  @XmlAttribute(name = "scorePoint")
  public int getScorePoint() {
    return _scorePoint;
  }

  public void setScorePoint(int value) {
    _scorePoint = value;
  }

  @XmlAttribute(name = "scoreDimension")
  public String getScoreDimension() {
    return _scoreDimension;
  }

  public void setScoreDimension(String value) {
    _scoreDimension = value;
  }

  @XmlAttribute(name = "scoreStatus")
  public String getScoreStatus() {
    return _scoreStatus;
  }

  public void setScoreStatus(String value) {
    _scoreStatus = value;
  }

  @XmlElementWrapper(name = "SubScoreList")
  @XmlElementRef
  public ScoreInfo[] getSubScoreList() {
    return (ScoreInfo[]) _subScoreList.toArray(new ScoreInfo[_subScoreList.size()]);
  }

  public void setSubScoreList(List<ScoreInfo> subScores) {
    _subScoreList = subScores;
  }

  public void addSubScore(ScoreInfo subScore) {
    _subScoreList.add(subScore);
  }
}