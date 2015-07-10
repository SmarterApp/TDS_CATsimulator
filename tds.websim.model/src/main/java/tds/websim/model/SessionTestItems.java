/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class SessionTestItems
{
  private List<ItemProperty>      _itemProperties;
  private List<ItemGroupProperty> _itemGroupProperties;

  @JsonProperty ("ItemProperties")
  public List<ItemProperty> getItemProperties () {
    return _itemProperties;
  }

  @JsonProperty ("ItemGroupProperties")
  public List<ItemGroupProperty> getItemGroupProperties () {
    return _itemGroupProperties;
  }

  public SessionTestItems () {
    this._itemProperties = new ArrayList<ItemProperty> ();
    this._itemGroupProperties = new ArrayList<ItemGroupProperty> ();
  }
}
