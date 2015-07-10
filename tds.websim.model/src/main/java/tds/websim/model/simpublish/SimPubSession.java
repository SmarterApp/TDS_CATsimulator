/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.simpublish;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimPubSession
{
  private Hashtable<String, SimPubSessionTest> _simTests = new Hashtable<String, SimPubSessionTest> ();

  private String                               _sessionKey;

  public String getSessionKey () {
    return _sessionKey;
  }

  public void setSessionKey (String sessionKey) {
    this._sessionKey = sessionKey;
  }

  public SimPubSession (String sessionKey) {
    setSessionKey (sessionKey);
  }

  public SimPubSessionTest getTest (String adminSubjectKey) {
    return _simTests.containsKey (adminSubjectKey) ? _simTests.get (adminSubjectKey) : null;
  }

  public void addTest (SimPubSessionTest test) {
    if (_simTests.containsKey (test.getAdminSubjectKey ()))
      ; // TODO: Throw exception
    _simTests.put (test.getAdminSubjectKey (), test);
  }

  public void updateTestPackage (Document doc, String testKey)
  {
    SimPubSessionTest simTest = null;
    if (_simTests.containsKey (testKey)) {
      simTest = _simTests.get (testKey);
      simTest.updateTestPackage (doc);
    }
  }

  public static Node getXmlNodeChild (Node parentNode, String xmlTag) {
    List<Node> filteredNodeList = getXmlNodeChildren (parentNode, xmlTag);
    return filteredNodeList.isEmpty () ? null : filteredNodeList.get (0);
  }

  public static List<Node> getXmlNodeChildren (Node parentNode, String xmlTag) {
    List<Node> filteredNodeList = new ArrayList<Node> ();
    NodeList childNodeList = parentNode.getChildNodes ();
    for (int i = 0; i < childNodeList.getLength (); i++) {
      Node node = childNodeList.item (i);
      if (node != null && node.getNodeName ().equalsIgnoreCase (xmlTag))
        filteredNodeList.add (node);
    }
    return filteredNodeList;
  }

}
