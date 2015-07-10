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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SimPubSegment
{
  private static final String                          _pathItemSelectorRelative                   = "itemselector";
  private static final String                          _pathItemSelectionParameterRelative         = "itemselectionparameter";
  private static final String                          _pathItemSelectionParameterPropertyRelative = "property";
  private static final String                          _pathBpElementId                            = "bpelementid";
  private static final String                          _pathSegmentPoolRelative                    = "segmentpool";
  private static final String                          _pathItemGroupRelative                      = "itemgroup";
  private static final String                          _pathIdentifierRelative                     = "identifier";
  private static final String                          _pathUniqueId                               = "uniqueid";

  private Hashtable<String, SimPubSegmentContentLevel> _simContentLevels                           = new Hashtable<String, SimPubSegmentContentLevel> ();
  private Hashtable<String, SimPubSegmentItemGroup>    _simItemGroups                              = new Hashtable<String, SimPubSegmentItemGroup> ();

  private String                                       _adminSubjectKey;
  private String                                       _segmentKey;
  private Float                                        _startAbility;
  private Float                                        _startInfo;
  private Float                                        _blueprintWeight;
  private Integer                                      _cset1size;
  private Integer                                      _cset2Random;
  private Integer                                      _cset2InitialRandom;
  private Float                                        _itemWeight;
  private Float                                        _abilityOffset;
  private String                                       _selectionAlgorithm;
  private String                                       _cset1Order;
  private Float                                        _abilityWeight;
  private Float                                        _rCAbilityWeight;
  private Float                                        _precisionTarget;
  private Float                                        _precisionTargetMetWeight;
  private Float                                        _precisionTargetNotMetWeight;
  private Float                                        _adaptiveCut;
  private Float                                        _tooCloseSEs;
  private Boolean                                      _terminationOverallInfo;
  private Boolean                                      _terminationRCInfo;
  private Boolean                                      _terminationMinCount;
  private Boolean                                      _terminationTooClose;
  private Boolean                                      _terminationFlagsAnd;

  public String getAdminSubjectKey () {
    return _adminSubjectKey;
  }

  public void setAdminSubjectKey (String _adminSubjectKey) {
    this._adminSubjectKey = _adminSubjectKey;
  }

  public String getSegmentKey () {
    return _segmentKey;
  }

  public void setSegmentKey (String _segmentKey) {
    this._segmentKey = _segmentKey;
  }

  public Float getStartAbility () {
    return _startAbility;
  }

  public void setStartAbility (Float _startAbility) {
    this._startAbility = _startAbility;
  }

  public Float getStartInfo () {
    return _startInfo;
  }

  public void setStartInfo (Float _startInfo) {
    this._startInfo = _startInfo;
  }

  public Float getBlueprintWeight () {
    return _blueprintWeight;
  }

  public void setBlueprintWeight (Float _blueprintWeight) {
    this._blueprintWeight = _blueprintWeight;
  }

  public Integer getCset1size () {
    return _cset1size;
  }

  public void setCset1size (Integer _cset1size) {
    this._cset1size = _cset1size;
  }

  public Integer getCset2Random () {
    return _cset2Random;
  }

  public void setCset2Random (Integer _cset2Random) {
    this._cset2Random = _cset2Random;
  }

  public Integer getCset2InitialRandom () {
    return _cset2InitialRandom;
  }

  public void setCset2InitialRandom (Integer _cset2InitialRandom) {
    this._cset2InitialRandom = _cset2InitialRandom;
  }

  public Float getItemWeight () {
    return _itemWeight;
  }

  public void setItemWeight (Float _itemWeight) {
    this._itemWeight = _itemWeight;
  }

  public Float getAbilityOffset () {
    return _abilityOffset;
  }

  public void setAbilityOffset (Float _abilityOffset) {
    this._abilityOffset = _abilityOffset;
  }

  public String getSelectionAlgorithm () {
    return _selectionAlgorithm;
  }

  public void setSelectionAlgorithm (String _selectionAlgorithm) {
    this._selectionAlgorithm = _selectionAlgorithm;
  }

  public String getCset1Order () {
    return _cset1Order;
  }

  public void setCset1Order (String _cset1Order) {
    this._cset1Order = _cset1Order;
  }

  public Float getAbilityWeight () {
    return _abilityWeight;
  }

  public void setAbilityWeight (Float _abilityWeight) {
    this._abilityWeight = _abilityWeight;
  }

  public Float getRCAbilityWeight () {
    return _rCAbilityWeight;
  }

  public void setRCAbilityWeight (Float _rCAbilityWeight) {
    this._rCAbilityWeight = _rCAbilityWeight;
  }

  public Float getPrecisionTarget () {
    return _precisionTarget;
  }

  public void setPrecisionTarget (Float _precisionTarget) {
    this._precisionTarget = _precisionTarget;
  }

  public Float getPrecisionTargetMetWeight () {
    return _precisionTargetMetWeight;
  }

  public void setPrecisionTargetMetWeight (Float _precisionTargetMetWeight) {
    this._precisionTargetMetWeight = _precisionTargetMetWeight;
  }

  public Float getPrecisionTargetNotMetWeight () {
    return _precisionTargetNotMetWeight;
  }

  public void setPrecisionTargetNotMetWeight (Float _precisionTargetNotMetWeight) {
    this._precisionTargetNotMetWeight = _precisionTargetNotMetWeight;
  }

  public Float getAdaptiveCut () {
    return _adaptiveCut;
  }

  public void setAdaptiveCut (Float _adaptiveCut) {
    this._adaptiveCut = _adaptiveCut;
  }

  public Float getTooCloseSEs () {
    return _tooCloseSEs;
  }

  public void setTooCloseSEs (Float _tooCloseSEs) {
    this._tooCloseSEs = _tooCloseSEs;
  }

  public Boolean isTerminationOverallInfo () {
    return _terminationOverallInfo;
  }

  public void setTerminationOverallInfo (Boolean _terminationOverallInfo) {
    this._terminationOverallInfo = _terminationOverallInfo;
  }

  public Boolean isTerminationRCInfo () {
    return _terminationRCInfo;
  }

  public void setTerminationRCInfo (Boolean _terminationRCInfo) {
    this._terminationRCInfo = _terminationRCInfo;
  }

  public Boolean isTerminationMinCount () {
    return _terminationMinCount;
  }

  public void setTerminationMinCount (Boolean _terminationMinCount) {
    this._terminationMinCount = _terminationMinCount;
  }

  public Boolean isTerminationTooClose () {
    return _terminationTooClose;
  }

  public void setTerminationTooClose (Boolean _terminationTooClose) {
    this._terminationTooClose = _terminationTooClose;
  }

  public Boolean isTerminationFlagsAnd () {
    return _terminationFlagsAnd;
  }

  public void setTerminationFlagsAnd (Boolean _terminationFlagsAnd) {
    this._terminationFlagsAnd = _terminationFlagsAnd;
  }

  public SimPubSegmentContentLevel getContentLevel (String contentLevel) {
    return _simContentLevels.containsKey (contentLevel) ? _simContentLevels.get (contentLevel) : null;
  }

  public void addContentLevel (SimPubSegmentContentLevel contentLevel) {
    _simContentLevels.put (contentLevel.getContentLevel (), contentLevel);
  }

  public SimPubSegmentItemGroup getItemGroup (String groupID) {
    return _simItemGroups.containsKey (groupID) ? _simItemGroups.get (groupID) : null;
  }

  public void addItemGroup (SimPubSegmentItemGroup itemGroup) {
    _simItemGroups.put (itemGroup.getGroupID (), itemGroup);
  }

  public void updateTestPackage (Document doc, Node node) {
    updateItemSelectionParameters (node);
    updateSegmentPool (node);
  }

  private void updateItemSelectionParameters (Node node) {
    Node itemSelectorNode = SimPubSession.getXmlNodeChild (node, _pathItemSelectorRelative);
    if (itemSelectorNode == null)
      return;

    List<Node> itemSelectorParamNodeList = SimPubSession.getXmlNodeChildren (itemSelectorNode, _pathItemSelectionParameterRelative);
    for (Node itemSelectorParamNode : itemSelectorParamNodeList) {
      String bpElementId = null;
      if (itemSelectorParamNode.hasAttributes ()) {
        Node attr = itemSelectorParamNode.getAttributes ().getNamedItem (_pathBpElementId);
        if (attr != null)
          bpElementId = attr.getNodeValue ();
      }
      if (bpElementId == null)
        continue;
      if (getSegmentKey ().equalsIgnoreCase (bpElementId))
        updateTestPackageSegmentParameters (itemSelectorParamNode);
      else if (_simContentLevels.containsKey (bpElementId))
        _simContentLevels.get (bpElementId).updateTestPackage (itemSelectorParamNode);
    }
  }

  private void updateTestPackageSegmentParameters (Node itemSelectorParamNode) {
    // ElementProperty.SetElementPropertyValueStrings(this, null, nl);
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
      case "startability":
        valueAttr.setNodeValue (getStartAbility ().toString ());
        break;
      case "startinfo":
        valueAttr.setNodeValue (getStartInfo ().toString ());
        break;
      case "bpweight":
        valueAttr.setNodeValue (getBlueprintWeight ().toString ());
        break;
      case "cset1size":
        valueAttr.setNodeValue (getCset1size ().toString ());
        break;
      case "cset2random":
        valueAttr.setNodeValue (getCset2Random ().toString ());
        break;
      case "cset2initialrandom":
        valueAttr.setNodeValue (getCset2InitialRandom ().toString ());
        break;
      case "itemweight":
        valueAttr.setNodeValue (getItemWeight ().toString ());
        break;
      case "abilityoffset":
        valueAttr.setNodeValue (getAbilityOffset ().toString ());
        break;
      case "selectionalgorithm":
        valueAttr.setNodeValue (getSelectionAlgorithm ().toString ());
        break;
      case "cset1order":
        valueAttr.setNodeValue (getCset1Order ().toString ());
        break;
      case "abilityweight":
        valueAttr.setNodeValue (getAbilityWeight ().toString ());
        break;
      case "rcabilityweight":
        valueAttr.setNodeValue (getRCAbilityWeight ().toString ());
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
      case "adaptivecut":
        valueAttr.setNodeValue (getAdaptiveCut ().toString ());
        break;
      case "toocloseses":
        valueAttr.setNodeValue (getTooCloseSEs ().toString ());
        break;
      case "terminationoverallinfo":
        valueAttr.setNodeValue (isTerminationOverallInfo ().toString ());
        break;
      case "terminationmincount":
        valueAttr.setNodeValue (isTerminationMinCount ().toString ());
        break;
      case "terminationtooclose":
        valueAttr.setNodeValue (isTerminationTooClose ().toString ());
        break;
      default:
        break;
      }
    }
  }

  private void updateSegmentPool (Node node) {
    Node segmentPoolNode = SimPubSession.getXmlNodeChild (node, _pathSegmentPoolRelative);
    if (segmentPoolNode == null)
      return;

    List<Node> itemGroupNodeList = SimPubSession.getXmlNodeChildren (segmentPoolNode, _pathItemGroupRelative);
    for (Node itemGroupNode : itemGroupNodeList) {
      Node identifierNode = SimPubSession.getXmlNodeChild (itemGroupNode, _pathIdentifierRelative);
      if (identifierNode == null)
        continue;

      SimPubSegmentItemGroup simItemGroup = null;
      Node groupIdAttr = identifierNode.getAttributes ().getNamedItem (_pathUniqueId);
      if (groupIdAttr != null) {
        String groupId = groupIdAttr.getNodeValue ();
        if (groupId != null && _simItemGroups.containsKey (groupId))
          simItemGroup = _simItemGroups.get (groupId);
      }
      if (simItemGroup != null)
        simItemGroup.updateTestPackage (itemGroupNode);
    }
  }
}