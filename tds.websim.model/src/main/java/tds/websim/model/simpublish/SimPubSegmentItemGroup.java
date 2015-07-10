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
import java.util.List;

import org.w3c.dom.Node;

public class SimPubSegmentItemGroup
{
  private static final String                  _pathGroupItemRelative = "groupitem";
  private static final String                  _pathItemId            = "itemid";
  private String                               _segmentKey            = new String ();
  private String                               _groupID               = new String ();
  private Hashtable<String, SimPubSegmentItem> _simItems              = new Hashtable<String, SimPubSegmentItem> ();

  public SimPubSegmentItemGroup () {
  }

  public SimPubSegmentItemGroup (String segmentKey, String gid) {
    setSegmentKey (segmentKey);
    setGroupID (gid);
  }

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

  public SimPubSegmentItem getItem (String sItemKey) {
    return _simItems.containsKey (sItemKey) ? _simItems.get (sItemKey) : null;
  }

  public void addItem (SimPubSegmentItem item) {
    _simItems.put (item.getItemKey (), item);
  }

  public void updateTestPackage (Node node) {
    List<Node> itemNodeList = SimPubSession.getXmlNodeChildren (node, _pathGroupItemRelative);
    for (Node itemNode : itemNodeList) {
      // Get the item id to find the item object to update with
      if (!itemNode.hasAttributes ())
        continue;
      Node itemIdAttr = itemNode.getAttributes ().getNamedItem (_pathItemId);
      if (itemIdAttr == null)
        continue;
      String itemId = itemIdAttr.getNodeValue ();
      if (itemId == null || !_simItems.containsKey (itemId))
        continue;

      // Update the package using item object
      SimPubSegmentItem simItem = _simItems.get (itemId);
      if (simItem != null)
        simItem.updateTestPackage (itemNode);
    }
  }
}
