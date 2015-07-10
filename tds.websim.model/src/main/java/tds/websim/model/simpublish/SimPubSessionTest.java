/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.simpublish;

import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimPubSessionTest
{
  private static final String              _pathAdminSegment = "adminsegment";                         // "/testspecification/administration/adminsegment";
  private static final String              _pathSegmentId    = "segmentid";
  private Hashtable<String, SimPubSegment> _simSegments      = new Hashtable<String, SimPubSegment> ();
  private String                           _adminSubjectKey;

  public String getAdminSubjectKey () {
    return _adminSubjectKey;
  }

  public void setAdminSubjectKey (String adminSubjectKey) {
    this._adminSubjectKey = adminSubjectKey;
  }

  public SimPubSegment getSegment (String adminSubjectKey) {
    return _simSegments.containsKey (adminSubjectKey) ? _simSegments.get (adminSubjectKey) : null;
  }

  public void addSegment (SimPubSegment segment) {
    if (_simSegments.containsKey (segment.getAdminSubjectKey ()))
      ; // TODO: Throw exception
    _simSegments.put (segment.getAdminSubjectKey (), segment);
  }

  public void updateTestPackage (Document doc) {
    NodeList nl = doc.getElementsByTagName (_pathAdminSegment);
    for (int i = 0; i < nl.getLength (); i++) {
      Node node = nl.item (i);
      if (node == null)
        continue;
      SimPubSegment simSegment = null;
      if (node.hasAttributes ()) {
        Node segmentIdNode = node.getAttributes ().getNamedItem (_pathSegmentId);
        String segmentID = segmentIdNode.getNodeValue ();
        if (_simSegments.containsKey (segmentID))
          simSegment = _simSegments.get (segmentID);
      }
      if (simSegment == null)
        continue;
      simSegment.updateTestPackage (doc, node);
    }
  }
}
