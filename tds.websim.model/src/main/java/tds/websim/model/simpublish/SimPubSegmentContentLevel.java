/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.simpublish;

import java.util.List;

import org.w3c.dom.Node;

public class SimPubSegmentContentLevel
{
  private static final String _pathItemSelectionParameterPropertyRelative = "property";
  private String              _segmentKey;
  private String              _contentLevel;
  private Float               _adaptiveCut;
  private Float               _startAbility;
  private Float               _startInfo;
  private Float               _scalar;
  private Boolean             _isStrictMax;
  private Float               _bpWeight;
  private Float               _abilityWeight;
  private Float               _precisionTarget;
  private Float               _precisionTargetMetWeight;
  private Float               _precisionTargetNotMetWeight;

  public String getSegmentKey () {
    return _segmentKey;
  }

  public void setSegmentKey (String segmentKey) {
    this._segmentKey = segmentKey;
  }

  public String getContentLevel () {
    return _contentLevel;
  }

  public void setContentLevel (String contentLevel) {
    this._contentLevel = contentLevel;
  }

  public Float getAdaptiveCut () {
    return _adaptiveCut;
  }

  public void setAdaptiveCut (Float adaptiveCut) {
    this._adaptiveCut = adaptiveCut;
  }

  public Float getStartAbility () {
    return _startAbility;
  }

  public void setStartAbility (Float startAbility) {
    this._startAbility = startAbility;
  }

  public Float getStartInfo () {
    return _startInfo;
  }

  public void setStartInfo (Float startInfo) {
    this._startInfo = startInfo;
  }

  public Float getScalar () {
    return _scalar;
  }

  public void setScalar (Float scalar) {
    this._scalar = scalar;
  }

  public Boolean isStrictMax () {
    return _isStrictMax;
  }

  public void setIsStrictMax (Boolean isStrictMax) {
    this._isStrictMax = isStrictMax;
  }

  public Float getBpWeight () {
    return _bpWeight;
  }

  public void setBpWeight (Float bpWeight) {
    this._bpWeight = bpWeight;
  }

  public Float getAbilityWeight () {
    return _abilityWeight;
  }

  public void setAbilityWeight (Float abilityWeight) {
    this._abilityWeight = abilityWeight;
  }

  public Float getPrecisionTarget () {
    return _precisionTarget;
  }

  public void setPrecisionTarget (Float precisionTarget) {
    this._precisionTarget = precisionTarget;
  }

  public Float getPrecisionTargetMetWeight () {
    return _precisionTargetMetWeight;
  }

  public void setPrecisionTargetMetWeight (Float precisionTargetMetWeight) {
    this._precisionTargetMetWeight = precisionTargetMetWeight;
  }

  public Float getPrecisionTargetNotMetWeight () {
    return _precisionTargetNotMetWeight;
  }

  public void setPrecisionTargetNotMetWeight (Float precisionTargetNotMetWeight) {
    this._precisionTargetNotMetWeight = precisionTargetNotMetWeight;
  }

  public void updateTestPackage (Node itemSelectorParamNode)
  {
    List<Node> propertyNodeList = SimPubSession.getXmlNodeChildren (itemSelectorParamNode, _pathItemSelectionParameterPropertyRelative);
    for (Node propertyNode : propertyNodeList) {
      if (!propertyNode.hasAttributes ())
        continue;

      Node nameAttr = propertyNode.getAttributes ().getNamedItem ("name");
      Node valueAttr = propertyNode.getAttributes ().getNamedItem ("value");

      if (nameAttr == null || valueAttr == null)
        continue;

      String name = nameAttr.getNodeValue ();
      switch (name) {
      case "adaptivecut":
        valueAttr.setNodeValue (getAdaptiveCut ().toString ());
        break;
      case "startability":
        valueAttr.setNodeValue (getStartAbility ().toString ());
        break;
      case "startinfo":
        valueAttr.setNodeValue (getStartInfo ().toString ());
        break;
      case "scalar":
        valueAttr.setNodeValue (getScalar ().toString ());
        break;
      case "isstrictmax":
        valueAttr.setNodeValue (isStrictMax ().toString ());
        break;
      case "bpweight":
        valueAttr.setNodeValue (getBpWeight ().toString ());
        break;
      case "abilityweight":
        valueAttr.setNodeValue (getAbilityWeight ().toString ());
        break;
      case "precisiontarget":
        valueAttr.setNodeValue (getPrecisionTarget ().toString ());
        break;
      case "precisiontargetmetweight":
        valueAttr.setNodeValue (getPrecisionTargetMetWeight ().toString ());
        break;
      case "precisiontargetnotmetweight":
        valueAttr.setNodeValue (getPrecisionTargetNotMetWeight ().toString ());
        break;
      default:
        break;
      }
    }
  }
}