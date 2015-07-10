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
public class BlueprintSegmentStrand
{
  private String  _segmentKey;
  private String  _strand;
  private int     _minItems;
  private int     _maxItems;
  private boolean _isStrictMax;
  private float   _blueprintWeight;
  private float   _adaptiveCut;
  private float   _startAbility;
  private float   _startInfo;
  private float   _scalar;
  private int     _opActiveItemCount;
  private float   _abilityWeight;
  private float   _precisionTargetNotMetWeight;
  private float   _precisionTargetMetWeight;
  private float   _precisionTarget;
  private boolean _isReportingCategory;
  private String  _isStrand;
  private String  _featureClass;

  @JsonProperty ("AbilityWeight")
  public float getAbilityWeight () {
    return _abilityWeight;
  }

  public void setAbilityWeight (float value) {
    this._abilityWeight = value;
  }

  @JsonProperty ("AdaptiveCut")
  public float getAdaptiveCut () {
    return _adaptiveCut;
  }

  public void setAdaptiveCut (float value) {
    this._adaptiveCut = value;
  }

  @JsonProperty ("BlueprintWeight")
  public float getBlueprintWeight () {
    return _blueprintWeight;
  }

  public void setBlueprintWeight (float value) {
    this._blueprintWeight = value;
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

  @JsonProperty ("IsStrand")
  public String getIsStrand () {
    return _isStrand;
  }

  public void setIsStrand (String value) {
    this._isStrand = value;
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

  @JsonProperty ("PrecisionTarget")
  public float getPrecisionTarget () {
    return _precisionTarget;
  }

  public void setPrecisionTarget (float value) {
    this._precisionTarget = value;
  }

  @JsonProperty ("PrecisionTargetMetWeight")
  public float getPrecisionTargetMetWeight () {
    return _precisionTargetMetWeight;
  }

  public void setPrecisionTargetMetWeight (float value) {
    this._precisionTargetMetWeight = value;
  }

  @JsonProperty ("PrecisionTargetNotMetWeight")
  public float getPrecisionTargetNotMetWeight () {
    return _precisionTargetNotMetWeight;
  }

  public void setPrecisionTargetNotMetWeight (float value) {
    this._precisionTargetNotMetWeight = value;
  }

  @JsonProperty ("Scalar")
  public float getScalar () {
    return _scalar;
  }

  public void setScalar (float value) {
    this._scalar = value;
  }

  @JsonProperty ("SegmentKey")
  public String getSegmentKey () {
    return _segmentKey;
  }

  public void setSegmentKey (String value) {
    this._segmentKey = value;
  }

  @JsonProperty ("StartAbility")
  public float getStartAbility () {
    return _startAbility;
  }

  public void setStartAbility (float value) {
    this._startAbility = value;
  }

  @JsonProperty ("StartInfo")
  public float getStartInfo () {
    return _startInfo;
  }

  public void setStartInfo (float value) {
    this._startInfo = value;
  }

  @JsonProperty ("Strand")
  public String getStrand () {
    return _strand;
  }

  public void setStrand (String value) {
    this._strand = value;
  }
}
