/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Tongliang Liu [tliu@air.org]
 * 
 */
public class SessionLanguage
{
  private String _language;
  private String _languageCode;
  private String _sessionType;

  @JsonProperty ("Language")
  public String getLanguage () {
    return _language;
  }

  public void setLanguage (String value) {
    this._language = value;
  }

  @JsonProperty ("LanguageCode")
  public String getLanguageCode () {
    return _languageCode;
  }

  public void setLanguageCode (String value) {
    this._languageCode = value;
  }

  @JsonProperty ("SessionType")
  public String getSessionType () {
    return _sessionType;
  }

  public void setSessionType (String value) {
    this._sessionType = value;
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_language == null) ? 0 : _language.hashCode ());
    result = prime * result + ((_languageCode == null) ? 0 : _languageCode.hashCode ());
    result = prime * result + ((_sessionType == null) ? 0 : _sessionType.hashCode ());
    return result;
  }

  @Override
  public boolean equals (Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass () != obj.getClass ())
      return false;
    SessionLanguage other = (SessionLanguage) obj;
    if (_language == null) {
      if (other._language != null)
        return false;
    } else if (!_language.equals (other._language))
      return false;
    if (_languageCode == null) {
      if (other._languageCode != null)
        return false;
    } else if (!_languageCode.equals (other._languageCode))
      return false;
    if (_sessionType == null) {
      if (other._sessionType != null)
        return false;
    } else if (!_sessionType.equals (other._sessionType))
      return false;
    return true;
  }
}
