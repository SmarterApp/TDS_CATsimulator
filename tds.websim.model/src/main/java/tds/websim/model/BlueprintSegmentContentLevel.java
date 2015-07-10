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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class BlueprintSegmentContentLevel
{
  private String  _segmentKey;
  private String  _contentLevel;
  private int     _minItems;
  private int     _maxItems;
  private boolean _isStrictMax;
  private float   _blueprintWeight;
  private int     _opActiveItemCount;
  private boolean _isReportingCategory;
  private String  _featureClass;

  @JsonProperty ("BlueprintWeight")
  public float getBlueprintWeight () {
    return _blueprintWeight;
  }

  public void setBlueprintWeight (float value) {
    this._blueprintWeight = value;
  }

  @JsonProperty ("ContentLevel")
  public String getContentLevel () {
    return _contentLevel;
  }

  public void setContentLevel (String value) {
    this._contentLevel = value;
  }

  @JsonProperty ("FeatureClass")
  public String getFeatureClass () {
    return _featureClass;
  }

  public void setFeatureClass (String value) {
    this._featureClass = value;
  }

  @JsonProperty ("IsReportingCategory")
  @JsonSerialize (using = BoolToString.class)
  public boolean getIsReportingCategory () {
    return _isReportingCategory;
  }

  public void setIsReportingCategory (boolean value) {
    this._isReportingCategory = value;
  }

  @JsonProperty ("IsStrictMax")
  @JsonSerialize (using = BoolToString.class)
  public boolean getIsStrictMax () {
    return _isStrictMax;
  }

  public void setIsStrictMax (boolean value) {
    this._isStrictMax = value;
  }

  @JsonProperty ("MaxItems")
  public int getMaxItems () {
    return _maxItems;
  }

  public void setMaxItems (int value) {
    this._maxItems = value;
  }

  @JsonProperty ("MinItems")
  public int getMinItems () {
    return _minItems;
  }

  public void setMinItems (int value) {
    this._minItems = value;
  }

  @JsonProperty ("OPActiveItemCount")
  public int getOpActiveItemCount () {
    return _opActiveItemCount;
  }

  public void setOpActiveItemCount (int value) {
    this._opActiveItemCount = value;
  }

  @JsonProperty ("SegmentKey")
  public String getSegmentKey () {
    return _segmentKey;
  }

  public void setSegmentKey (String value) {
    this._segmentKey = value;
  }
}
