/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.simpublish;

import org.w3c.dom.Node;

public class SimPubSegmentItem
{
  private String  _segmentKey  = null;
  private String  _groupID     = null;
  private String  _itemKey     = null;
  private Boolean _isActive    = true;
  private Boolean _isRequired  = true;
  private Boolean _isFieldTest = false;

  public String getSegmentKey () {
    return _segmentKey;
  }

  public void setSegmentKey (String segmentKey) {
    this._segmentKey = segmentKey;
  }

  public String getGroupID () {
    return _groupID;
  }

  public void setGroupID (String groupID) {
    this._groupID = groupID;
  }

  public String getItemKey () {
    return _itemKey;
  }

  public void setItemKey (String itemKey) {
    this._itemKey = itemKey;
  }

  public Boolean isActive () {
    return _isActive;
  }

  public void setIsActive (Boolean isActive) {
    this._isActive = isActive;
  }

  public Boolean isRequired () {
    return _isRequired;
  }

  public void setIsRequired (Boolean isRequired) {
    this._isRequired = isRequired;
  }

  public Boolean isFieldTest () {
    return _isFieldTest;
  }

  public void setIsFieldTest (Boolean isFieldTest) {
    this._isFieldTest = isFieldTest;
  }

  public void updateTestPackage (Node node) {
    Node attr = null;
    if (node.hasAttributes ()) {
      attr = node.getAttributes ().getNamedItem ("isactive");
      if (attr != null)
        attr.setNodeValue (isActive ().toString ());

      attr = node.getAttributes ().getNamedItem ("adminrequired");
      if (attr != null)
        attr.setNodeValue (isRequired ().toString ());

      attr = node.getAttributes ().getNamedItem ("isfieldtest");
      if (attr != null)
        attr.setNodeValue (isFieldTest ().toString ());
    }
  }
}
