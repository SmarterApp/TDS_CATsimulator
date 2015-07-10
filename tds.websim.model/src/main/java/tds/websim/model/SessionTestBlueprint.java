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
public class SessionTestBlueprint
{
  private List<BlueprintSegment>             _blueprintSegments;
  private List<BlueprintSegmentStrand>       _blueprintSegmentStrands;
  private List<BlueprintSegmentContentLevel> _blueprintSegmentContentLevels;

  @JsonProperty ("BlueprintSegments")
  public List<BlueprintSegment> getBlueprintSegments () {
    return _blueprintSegments;
  }

  @JsonProperty ("BlueprintSegmentStrands")
  public List<BlueprintSegmentStrand> getBlueprintSegmentStrands () {
    return _blueprintSegmentStrands;
  }

  @JsonProperty ("BlueprintSegmentContentLevels")
  public List<BlueprintSegmentContentLevel> getBlueprintSegmentContentLevels () {
    return _blueprintSegmentContentLevels;
  }

  public SessionTestBlueprint () {
    this._blueprintSegments = new ArrayList<BlueprintSegment> ();
    this._blueprintSegmentStrands = new ArrayList<BlueprintSegmentStrand> ();
    this._blueprintSegmentContentLevels = new ArrayList<BlueprintSegmentContentLevel> ();
  }
}
