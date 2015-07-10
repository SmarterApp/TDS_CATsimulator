/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package dbsimulator;

public class InsertedItem {
  private String _itemID;
  private int _page;
  private int _position;

  public String getItemID() {
    return _itemID;
  }

  public void setItemID(String value) {
    this._itemID = value;
  }

  public int getPage() {
    return _page;
  }

  public void setPage(int value) {
    this._page = value;
  }

  public int getPosition() {
    return _position;
  }

  public void setPosition(int value) {
    this._position = value;
  }
}
