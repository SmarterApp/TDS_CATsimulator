/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class Table
{
  private String         _tableName;
  private List<String>   _tableHeaders;
  private List<TableRow> _tableRows;

  @JsonProperty ("TableName")
  public String getTableName () {
    return _tableName;
  }

  public void setTableName (String value) {
    this._tableName = value;
  }

  @JsonProperty ("TableHeaders")
  public List<String> getTableHeaders () {
    return _tableHeaders;
  }

  public void setTableHeaders (List<String> value) {
    this._tableHeaders = value;
  }

  @JsonProperty ("TableRows")
  public List<TableRow> getTableRows () {
    return _tableRows;
  }

  public void setTableRows (List<TableRow> value) {
    this._tableRows = value;
  }

}
