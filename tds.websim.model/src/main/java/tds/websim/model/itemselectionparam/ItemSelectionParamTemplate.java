/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *   
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * http://www.smarterapp.org/documents/American_Institutes_for_Research_Open_Source_Software_License.pdf
 ******************************************************************************/
package tds.websim.model.itemselectionparam;

public class ItemSelectionParamTemplate
{
  private String _algorithmType;
  private String _entityType;
  private String _name;
  private String _value;
  private String _label;

  public ItemSelectionParamTemplate (String algorithmType, String entityType, String name, String value, String label) {
    this._algorithmType = algorithmType;
    this._entityType = entityType;
    this._name = name;
    this._value = value;
    this._label = label;
  }

  public String getAlgorithmType () {
    return _algorithmType;
  }

  public void setAlgorithmType (String algorithmType) {
    this._algorithmType = algorithmType;
  }

  public String getEntityType () {
    return _entityType;
  }

  public void setEntityType (String entityType) {
    this._entityType = entityType;
  }

  public String getName () {
    return _name;
  }

  public void setName (String name) {
    this._name = name;
  }

  public String getValue () {
    return _value;
  }

  public void setValue (String value) {
    this._value = value;
  }

  public String getLabel () {
    return _label;
  }

  public void setLabel (String label) {
    this._label = label;
  }
}
