/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.itemselectionparam;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemSelectionParam implements Serializable
{
  private static final long serialVersionUID = -144466960567433454L;
  private String _bpElementType;
  private String _segmentKey;
  private String _bpElementID;
  private String _paramName;
  private String _paramValue;
  private String _label;

  public ItemSelectionParam (String bpElementType, String segmentKey, String bpElementID, String paramName, String paramValue, String label) {
    this._bpElementType = bpElementType;
    this._segmentKey = segmentKey;
    this._bpElementID = bpElementID;
    this._paramName = paramName;
    this._paramValue = paramValue;
    this._label = label;
  }

  @JsonProperty ("BpElementType")
  public String getBpElementType () {
    return _bpElementType;
  }

  public void setBpElementType (String bpElementType) {
    this._bpElementType = bpElementType;
  }
  
  @JsonProperty ("SegmentKey")
  public String getSegmentKey () {
    return _segmentKey;
  }

  public void setSegmentKey (String segmentKey) {
    this._segmentKey = segmentKey;
  }

  @JsonProperty ("BpElementID")
  public String getBpElementID () {
    return _bpElementID;
  }

  public void setBpElementID (String bpElementID) {
    this._bpElementID = bpElementID;
  }

  @JsonProperty ("ParamName")
  public String getParamName () {
    return _paramName;
  }

  public void setParamName (String paramName) {
    this._paramName = paramName;
  }

  @JsonProperty ("ParamValue")
  public String getParamValue () {
    return _paramValue;
  }

  public void setParamValue (String paramValue) {
    this._paramValue = paramValue;
  }

  @JsonProperty ("Label")
  public String getLabel () {
    return _label;
  }

  public void setLabel (String label) {
    this._label = label;
  }
}
