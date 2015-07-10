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
public class BlueprintSegment
{
  private String  _testKey;
  private String  _segmentKey;
  private float   _startAbility;
  private float   _startInfo;
  private int     _minItems;
  private int     _maxItems;
  private int     _ftStartPos;
  private int     _ftEndPos;
  private int     _ftMinItems;
  private int     _ftMaxItems;
  private String  _formSelection;
  private float   _blueprintWeight;
  private int     _cset1Size;
  private int     _cset2Random;
  private int     _cset2InitialRandom;
  private long    _loadConfig;
  private long    _updateConfig;
  private float   _itemWeight;
  private float   _abilityOffset;
  private int     _segmentPosition;
  private String  _segmentID;
  private String  _selectionAlgorithm;
  private String  _cset1Order;
  private int     _opActiveItemCount;
  private int     _opActiveGroupCount;
  private float   _rcAbilityWeight;
  private float   _abilityWeight;
  private float   _precisionTargetNotMetWeight;
  private float   _precisionTargetMetWeight;
  private float   _precisionTarget;
  private float   _adaptiveCut;
  private float   _tooCloseSEs;
  private boolean _terminationMinCount;
  private boolean _terminationOverallInfo;
  private boolean _terminationRCInfo;
  private boolean _terminationTooClose;
  private boolean _terminationFlagsAnd;

  @JsonProperty ("TestKey")
  public String getTestKey () {
    return _testKey;
  }

  public void setTestKey (String value) {
    this._testKey = value;
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

  @JsonProperty ("MinItems")
  public int getMinItems () {
    return _minItems;
  }

  public void setMinItems (int value) {
    this._minItems = value;
  }

  @JsonProperty ("MaxItems")
  public int getMaxItems () {
    return _maxItems;
  }

  public void setMaxItems (int value) {
    this._maxItems = value;
  }

  @JsonProperty ("FtStartPos")
  public int getFtStartPos () {
    return _ftStartPos;
  }

  public void setFtStartPos (int value) {
    this._ftStartPos = value;
  }

  @JsonProperty ("FtEndPos")
  public int getFtEndPos () {
    return _ftEndPos;
  }

  public void setFtEndPos (int value) {
    this._ftEndPos = value;
  }

  @JsonProperty ("FtMinItems")
  public int getFtMinItems () {
    return _ftMinItems;
  }

  public void setFtMinItems (int value) {
    this._ftMinItems = value;
  }

  @JsonProperty ("FtMaxItems")
  public int getFtMaxItems () {
    return _ftMaxItems;
  }

  public void setFtMaxItems (int value) {
    this._ftMaxItems = value;
  }

  @JsonProperty ("FormSelection")
  public String getFormSelection () {
    return _formSelection;
  }

  public void setFormSelection (String value) {
    this._formSelection = value;
  }

  @JsonProperty ("BlueprintWeight")
  public float getBlueprintWeight () {
    return _blueprintWeight;
  }

  public void setBlueprintWeight (float value) {
    this._blueprintWeight = value;
  }

  @JsonProperty ("Cset1Size")
  public int getCset1Size () {
    return _cset1Size;
  }

  public void setCset1Size (int value) {
    this._cset1Size = value;
  }

  @JsonProperty ("Cset2Random")
  public int getCset2Random () {
    return _cset2Random;
  }

  public void setCset2Random (int value) {
    this._cset2Random = value;
  }

  @JsonProperty ("Cset2InitialRandom")
  public int getCset2InitialRandom () {
    return _cset2InitialRandom;
  }

  public void setCset2InitialRandom (int value) {
    this._cset2InitialRandom = value;
  }

  @JsonProperty ("LoadConfig")
  public long getLoadConfig () {
    return _loadConfig;
  }

  public void setLoadConfig (long value) {
    this._loadConfig = value;
  }

  @JsonProperty ("UpdateConfig")
  public long getUpdateConfig () {
    return _updateConfig;
  }

  public void setUpdateConfig (long value) {
    this._updateConfig = value;
  }

  @JsonProperty ("ItemWeight")
  public float getItemWeight () {
    return _itemWeight;
  }

  public void setItemWeight (float value) {
    this._itemWeight = value;
  }

  @JsonProperty ("AbilityOffset")
  public float getAbilityOffset () {
    return _abilityOffset;
  }

  public void setAbilityOffset (float value) {
    this._abilityOffset = value;
  }

  @JsonProperty ("SegmentPosition")
  public int getSegmentPosition () {
    return _segmentPosition;
  }

  public void setSegmentPosition (int value) {
    this._segmentPosition = value;
  }

  @JsonProperty ("SegmentID")
  public String getSegmentID () {
    return _segmentID;
  }

  public void setSegmentID (String value) {
    this._segmentID = value;
  }

  @JsonProperty ("SelectionAlgorithm")
  public String getSelectionAlgorithm () {
    return _selectionAlgorithm;
  }

  public void setSelectionAlgorithm (String value) {
    this._selectionAlgorithm = value;
  }

  @JsonProperty ("Cset1Order")
  public String getCset1Order () {
    return _cset1Order;
  }

  public void setCset1Order (String value) {
    this._cset1Order = value;
  }

  @JsonProperty ("OPActiveItemCount")
  public int getOpActiveItemCount () {
    return _opActiveItemCount;
  }

  public void setOpActiveItemCount (int value) {
    this._opActiveItemCount = value;
  }

  @JsonProperty ("OPActiveGroupCount")
  public int getOpActiveGroupCount () {
    return _opActiveGroupCount;
  }

  public void setOpActiveGroupCount (int value) {
    this._opActiveGroupCount = value;
  }

  @JsonProperty ("RCAbilityWeight")
  public float getRcAbilityWeight () {
    return _rcAbilityWeight;
  }

  public void setRcAbilityWeight (float value) {
    this._rcAbilityWeight = value;
  }

  @JsonProperty ("AbilityWeight")
  public float getAbilityWeight () {
    return _abilityWeight;
  }

  public void setAbilityWeight (float value) {
    this._abilityWeight = value;
  }

  @JsonProperty ("PrecisionTargetNotMetWeight")
  public float getPrecisionTargetNotMetWeight () {
    return _precisionTargetNotMetWeight;
  }

  public void setPrecisionTargetNotMetWeight (float value) {
    this._precisionTargetNotMetWeight = value;
  }

  @JsonProperty ("PrecisionTargetMetWeight")
  public float getPrecisionTargetMetWeight () {
    return _precisionTargetMetWeight;
  }

  public void setPrecisionTargetMetWeight (float value) {
    this._precisionTargetMetWeight = value;
  }

  @JsonProperty ("AdaptiveCut")
  public float getAdaptiveCut () {
    return _adaptiveCut;
  }

  public void setAdaptiveCut (float value) {
    this._adaptiveCut = value;
  }

  @JsonProperty ("PrecisionTarget")
  public float getPrecisionTarget () {
    return _precisionTarget;
  }

  public void setPrecisionTarget (float value) {
    this._precisionTarget = value;
  }

  @JsonProperty ("TerminationFlagsAnd")
  @JsonSerialize (using = BoolToString.class)
  public boolean getTerminationFlagsAnd () {
    return _terminationFlagsAnd;
  }

  public void setTerminationFlagsAnd (boolean value) {
    this._terminationFlagsAnd = value;
  }

  @JsonProperty ("TerminationMinCount")
  @JsonSerialize (using = BoolToString.class)
  public boolean getTerminationMinCount () {
    return _terminationMinCount;
  }

  public void setTerminationMinCount (boolean terminationMinCount) {
    this._terminationMinCount = terminationMinCount;
  }

  @JsonProperty ("TerminationOverallInfo")
  @JsonSerialize (using = BoolToString.class)
  public boolean getTerminationOverallInfo () {
    return _terminationOverallInfo;
  }

  public void setTerminationOverallInfo (boolean terminationOverallInfo) {
    this._terminationOverallInfo = terminationOverallInfo;
  }

  @JsonProperty ("TerminationRCInfo")
  @JsonSerialize (using = BoolToString.class)
  public boolean getTerminationRCInfo () {
    return _terminationRCInfo;
  }

  public void setTerminationRCInfo (boolean terminationRCInfo) {
    this._terminationRCInfo = terminationRCInfo;
  }

  @JsonProperty ("TerminationTooClose")
  @JsonSerialize (using = BoolToString.class)
  public boolean getTerminationTooClose () {
    return _terminationTooClose;
  }

  public void setTerminationTooClose (boolean terminationTooClose) {
    this._terminationTooClose = terminationTooClose;
  }

  @JsonProperty ("TooCloseSEs")
  public float getTooCloseSEs () {
    return _tooCloseSEs;
  }

  public void setTooCloseSEs (float tooCloseSEs) {
    this._tooCloseSEs = tooCloseSEs;
  }

}
