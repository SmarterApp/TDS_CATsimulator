/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class ItemGroupProperty
{
  private String _segmentKey;
  private String _groupID;
  private int    _maxItems;
  private int    _activeItems;

  @JsonProperty ("SegmentKey")
  public String getSegmentKey () {
    return _segmentKey;
  }

  public void setSegmentKey (String value) {
    this._segmentKey = value;
  }

  @JsonProperty ("GroupID")
  public String getGroupID () {
    return _groupID;
  }

  public void setGroupID (String value) {
    this._groupID = value;
  }

  @JsonProperty ("MaxItems")
  public int getMaxItems () {
    return _maxItems;
  }

  public void setMaxItems (int value) {
    this._maxItems = value;
  }

  @JsonProperty ("ActiveItems")
  public int getActiveItems () {
    return _activeItems;
  }

  public void setActiveItems (int value) {
    this._activeItems = value;
  }
}
