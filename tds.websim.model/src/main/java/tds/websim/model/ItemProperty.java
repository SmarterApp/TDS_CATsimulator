/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import AIR.Common.JsonSerializers.BoolToString;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class ItemProperty
{
  private String  _segmentKey;
  private String  _strand;
  private String  _groupID;
  private String  _itemKey;
  private boolean _isActive;
  private boolean _isRequired;
  private boolean _isFieldTest;

  @JsonProperty ("SegmentKey")
  public String getSegmentKey () {
    return _segmentKey;
  }

  public void setSegmentKey (String value) {
    this._segmentKey = value;
  }

  @JsonProperty ("Strand")
  public String getStrand () {
    return _strand;
  }

  public void setStrand (String value) {
    this._strand = value;
  }

  @JsonProperty ("GroupID")
  public String getGroupID () {
    return _groupID;
  }

  public void setGroupID (String value) {
    this._groupID = value;
  }

  @JsonProperty ("ItemKey")
  public String getItemKey () {
    return _itemKey;
  }

  public void setItemKey (String value) {
    this._itemKey = value;
  }

  @JsonProperty ("IsActive")
  @JsonSerialize (using = BoolToString.class)
  public boolean getIsActive () {
    return _isActive;
  }

  public void setIsActive (boolean value) {
    this._isActive = value;
  }

  @JsonProperty ("IsRequired")
  @JsonSerialize (using = BoolToString.class)
  public boolean getIsRequired () {
    return _isRequired;
  }

  public void setIsRequired (boolean value) {
    this._isRequired = value;
  }

  @JsonProperty ("IsFieldTest")
  @JsonSerialize (using = BoolToString.class)
  public boolean getIsFieldTest () {
    return _isFieldTest;
  }

  public void setIsFieldTest (boolean value) {
    this._isFieldTest = value;
  }

  public static void main (String[] argv) {
    try {
      ObjectMapper mapper = new ObjectMapper ();
      ItemProperty prop = new ItemProperty ()
      {
        {
          setIsActive (true);
        }
      };
      mapper.writeValue (System.err, prop);
    } catch (Exception exp)
    {
      exp.printStackTrace ();
    }
  }
}
