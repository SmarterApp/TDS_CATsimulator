/**
*   The Session Blueprint module allows users to manage blueprint properties for a test
*/
YUI.add('session_blueprint', function (Y) {
    Y.Session_Blueprint = function () {
        var _bpSegTblRow,
            _bpSegStrandTblRow,
            _bpSegContentLvlTblRow,
            _strServerPath = "#serverpath",
        //hf controls
            _strHfSessionKey = "#ctl00_MainContent_hfsk",
            _strHfTestKey = "#ctl00_MainContent_hftk",
            _strHfClientName = "#ctl00_MainContent_hfcn",
            _strHfSessionID = "#ctl00_MainContent_hfsid",
        // div id for navigation
            _divNav = "#navigation",
        //client dropdown
            _strClientDdl = "#ddlClientPicker",
        //segment blueprint
            _strManageTestBlueprint = "#segmentBlueprint",
            _strBtnAlterTestItemProps = "#btnAlterTestItemProps",
            _strBtnAlterItemSelectionParams = "#btnAlterItemSelectionParams",
            _strBtnExportBlueprint = "#btnExportBlueprint",
            _strBtnEditBlueprintSegment = "#btnEditBlueprintSeg",
            _strBtnSaveBlueprintSegment = "#btnSaveBlueprintSeg",
            _strBtnCancelBlueprintSegment = "#btnCancelBlueprintSeg",
            _strBtnEditBlueprintStrand = "#btnEditBlueprintSegStrand",
            _strBtnSaveBlueprintStrand = "#btnSaveBlueprintSegStrand",
            _strBtnCancelBlueprintStrand = "#btnCancelBlueprintSegStrand",
            _strBtnConvertBlueprintStrand = "#btnConvertBlueprintSegStrand",
            _strBtnConvertSaveBlueprintStrand = "#btnConvertSaveBlueprintSegStrand",
            _strBtnConvertCancelBlueprintStrand = "#btnConvertCancelBlueprintSegStrand",
            _strBtnEditBlueprintContentLevel = "#btnEditBlueprintSegContentLevel",
            _strBtnSaveBlueprintContentLevel = "#btnSaveBlueprintSegContentLevel",
            _strBtnCancelBlueprintContentLevel = "#btnCancelBlueprintSegContentLevel",
            _strBtnConvertBlueprintContentLevel = "#btnConvertBlueprintSegContentLevel",
            _strBtnConvertSaveBlueprintContentLevel = "#btnConvertSaveBlueprintSegContentLevel",
            _strBtnConvertCancelBlueprintContentLevel = "#btnConvertCancelBlueprintSegContentLevel",
            _strDdlSelectTest = "#ddlSelectTest",
            _strTblBpSeg = "#tblSegBlueprint",
        //segment strand & content level dropdowns
            _strDdlSelectSegment = "#blueprint-SegmentID",
            _strDdlSelectStrand = "#blueprint-StrandID",
        //segment strand table
            _strTblBpSegStrand = "#tblSegStrand",
        //segment content level table
            _strTblBpSegContentLvl = "#tblSegContentLevel",
        //copy of data
            _dataBpSegs,
            _dataBpSegStrands,
            _dataBpSegContentLvls,
        //collection of modified data
            _modifiedSegments = {},
            _modifiedStrands = {},
            _modifiedContentLevels = {},
        //other data
            _sessionKey,
            _testKey,
            _clientName,
        //loading panel
            _loadingPanel,

        init = function () {
            //retrieve values from post
            getPostVars();

            //update navigation links with clientname querystring
            updateNavLinks(_clientName);
            
            if ((_sessionKey === null || _sessionKey === '') || (_testKey === null || _testKey === '')) {
                alert('Session Key and Test Key required');
                return;
            }

            // set up the loading panel
            _loadingPanel = new Y.Panel({
                srcNode: '#divLoadingPanel',
                zIndex: 10000,  // this is to get it on top of all panels...
                centered: true,
                modal: true,
                visible: false,
                render: true,
                buttons: []
            });
            _loadingPanel.hide();
            Y.one('#divLoadingPanel').setStyle('display', 'inherit');

            // store table rows
            var tempTableTbody = Y.one(_strTblBpSeg).one('tbody');
            if (_bpSegTblRow == null)
                _bpSegTblRow = tempTableTbody.one('tr').cloneNode(true);
            tempTableTbody.empty();
            tempTableTbody = Y.one(_strTblBpSegStrand).one('tbody');
            if (_bpSegStrandTblRow == null)
                _bpSegStrandTblRow = tempTableTbody.one('tr').cloneNode(true);
            tempTableTbody.empty();
            tempTableTbody = Y.one(_strTblBpSegContentLvl).one('tbody');
            if (_bpSegContentLvlTblRow == null)
                _bpSegContentLvlTblRow = tempTableTbody.one('tr').cloneNode(true);
            tempTableTbody.empty();

            //set the client dropdown
            Y.one(_strClientDdl).appendChild(Y.Node.create('<option>' + Y.one(_strHfClientName).get('value') + '</option>'));
            // populate the test dropdown list
            populateTestDropdown(_sessionKey);

            // display the table
            Y.one(_strTblBpSeg).removeAttribute('style');
            Y.one(_strTblBpSegStrand).removeAttribute('style');
            Y.one(_strTblBpSegContentLvl).removeAttribute('style');

            //event handlers
            Y.on("change", ddlSelectTest_Change, _strDdlSelectTest);
            Y.on("change", ddlSelectSegment_Change, _strDdlSelectSegment);
            Y.on("change", ddlSelectStrand_Change, _strDdlSelectStrand);

            //add eventhandler to refresh the tables
            Y.on('ManageTestBlueprint:refresh', function (data) {
                // populate the segment blueprint table
                updateBlueprintSegmentTable();
                // populate the segment dropdown list
                updateBlueprintSegmentDropdown();
                // populate the segment strand table
                updateBlueprintStrandTable();
                // populate the segment content level table
                updateBlueprintContentLevelTable();
            });
        },

        currentSelectionAlgorithm = function () {
            if (_dataBpSegs.length > 0) {
                return _dataBpSegs[0].SelectionAlgorithm;            
            }
            return "unknown";
        },

        isAdaptiveTest = function () {
            if (_dataBpSegs.length > 0) {
                if (_dataBpSegs[0].SelectionAlgorithm === "adaptive" || _dataBpSegs[0].SelectionAlgorithm === "adaptive2")
                    return true;
            }
            return false;
        },

        isStrand = function(segment, strand) {
            var lenBpSegStrands = _dataBpSegStrands.length;            
            for (i = 0; i < lenBpSegStrands; i++) {
                if (_dataBpSegStrands[i].SegmentKey === segment && _dataBpSegStrands[i].Strand === strand && _dataBpSegStrands[i].FeatureClass === "Strand")
                    return true;
            }
            return false;
        },

        getPostVars = function () {
            _sessionKey = Y.one(_strHfSessionKey).get('value');
            _testKey = Y.one(_strHfTestKey).get('value');
            _clientName = Y.one(_strHfClientName).get('value');
            _strHfSessionID = Y.one(_strHfSessionID).get('value');
        },

        populateTestDropdown = function (sessionKey) {
            var callback = {
                on: {
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.responseText);
                        var ddlSelectTest = Y.one(_strDdlSelectTest),
                            sessionTests = [],
                            testKey;

                        try {
                            sessionTests = Y.JSON.parse(response.responseText);
                        }
                        catch (e) {
                            alert("Failed to parse session test JSON data.");
                            return;
                        }

                        Y.log("PARSED DATA: " + Y.Lang.dump(sessionTests));

                        //display errors
                        if (sessionTests.errormsg) {
                            alert(sessionTests.errormsg);
                            return;
                        }

                        //else, populate table after clearing
                        ddlSelectTest.empty();
                        for (var i = 0; i < sessionTests.length; i++) {
                            if (sessionTests[i].AdminSubject == _testKey) {
                                ddlSelectTest.appendChild(Y.Node.create('<option value=\"' + sessionTests[i].AdminSubject + '\" selected>' + sessionTests[i].TestID + '</option>'));
                            } else {
                                ddlSelectTest.appendChild(Y.Node.create('<option value=\"' + sessionTests[i].AdminSubject + '\">' + sessionTests[i].TestID + '</option>'));
                            }
                        }

                        //get Test Blueprint info from the first testkey
                        getTestBlueprint(sessionKey, _testKey);

                        //add eventhandler for sessionTest
                        Y.one(_strBtnAlterTestItemProps).detach();
                        Y.on("click", btnAlterTestItemProps_Click, _strBtnAlterTestItemProps);
                        Y.one(_strBtnAlterItemSelectionParams).detach();
                        Y.on("click", btnAlterItemSelectionParams_Click, _strBtnAlterItemSelectionParams);
                        Y.one(_strBtnExportBlueprint).set('href', 'Services/WebSimXHR.ashx/GetTestBlueprintCSV?sessionkey=' + _sessionKey + '&testkey=' + _testKey);
                        //add eventhandler for edit/save/cancel blueprint segment
                        Y.on("click", btnEditBlueprintSegment_Click, _strBtnEditBlueprintSegment);
                        Y.on("click", btnSaveBlueprintSegment_Click, _strBtnSaveBlueprintSegment);
                        Y.on("click", btnCancelBlueprintSegment_Click, _strBtnCancelBlueprintSegment);
                        //add eventhandler for edit/save/cancel blueprint strand
                        Y.on("click", btnEditBlueprintStrand_Click, _strBtnEditBlueprintStrand);
                        Y.on("click", btnSaveBlueprintStrand_Click, _strBtnSaveBlueprintStrand);
                        Y.on("click", btnCancelBlueprintStrand_Click, _strBtnCancelBlueprintStrand);
                        Y.on("click", btnConvertBlueprintStrandAsContentLevel_Click, _strBtnConvertBlueprintStrand);
                        Y.on("click", btnConvertSaveBlueprintStrandAsContentLevel_Click, _strBtnConvertSaveBlueprintStrand);
                        Y.on("click", btnConvertCancelBlueprintStrandAsContentLevel_Click, _strBtnConvertCancelBlueprintStrand);
                        //add eventhandler for edit/save/cancel blueprint content level
                        Y.on("click", btnEditBlueprintContentLevel_Click, _strBtnEditBlueprintContentLevel);
                        Y.on("click", btnSaveBlueprintContentLevel_Click, _strBtnSaveBlueprintContentLevel);
                        Y.on("click", btnCancelBlueprintContentLevel_Click, _strBtnCancelBlueprintContentLevel);
                        Y.on("click", btnConvertBlueprintContentLevelAsStrand_Click, _strBtnConvertBlueprintContentLevel);
                        Y.on("click", btnConvertSaveBlueprintContentLevelAsStrand_Click, _strBtnConvertSaveBlueprintContentLevel);
                        Y.on("click", btnConvertCancelBlueprintContentLevelAsStrand_Click, _strBtnConvertCancelBlueprintContentLevel);
                    },
                    failure: function (id, response) {
                        alert('Failed to retrieve session tests.');
                    }
                }
            }

            Y.io("Services/WebSimXHR.ashx/GetSessionTests?sessionkey=" + sessionKey, callback);
        },

        getTestBlueprint = function (sessionKey, testKey) {
            var callback = {
                on: {
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.responseText);
                        var parsedResponse,
                            dataBpSegs = [],
                            dataBpSegStrands = [],
                            dataBpSegContentLvls = [],
                            lenBlueprintSegments,
                            lenBlueprintSegmentStrands,
                            lenBlueprintSegmentContentLevels;

                        try {
                            parsedResponse = Y.JSON.parse(response.responseText);
                        }
                        catch (e) {
                            alert("Failed to parse session test blueprint JSON data.");
                            return;
                        }

                        // check if failed to retrieve data
                        if (parsedResponse.status === "failed") {
                            alert (parsedResponse.Reason);
                            return;
                        }

                        // grab lengths of # of segments, strands, content levels
                        lenBlueprintSegments = parsedResponse.BlueprintSegments.length,
                        lenBlueprintSegmentStrands = parsedResponse.BlueprintSegmentStrands.length,
                        lenBlueprintSegmentContentLevels = parsedResponse.BlueprintSegmentContentLevels.length;

                        //display alert if no segments found
                        if (lenBlueprintSegments == 0) {
                            alert("No Blueprint Segments found for this client.");
                        }

                        for (var i = 0; i < lenBlueprintSegments; i++) {
                            dataBpSegs.push(parsedResponse.BlueprintSegments[i]);
                        }

                        for (var i = 0; i < lenBlueprintSegmentStrands; i++) {
                            dataBpSegStrands.push(parsedResponse.BlueprintSegmentStrands[i]);
                        }

                        for (var i = 0; i < lenBlueprintSegmentContentLevels; i++) {
                            dataBpSegContentLvls.push(parsedResponse.BlueprintSegmentContentLevels[i]);
                        }

                        // deep copy the data to the module variable
                        _dataBpSegs = Y.clone(dataBpSegs);
                        _dataBpSegStrands = Y.clone(dataBpSegStrands);
                        _dataBpSegContentLvls = Y.clone(dataBpSegContentLvls);

                        // populate the segment blueprint table
                        updateBlueprintSegmentTable();

                        // populate the segment dropdown list
                        updateBlueprintSegmentDropdown();
                        // populate the segment strand table
                        updateBlueprintStrandTable();
                        // populate the segment content level table
                        updateBlueprintContentLevelTable();
                    },
                    failure: function (id, response) {
                        alert('Failed to retrieve the test blueprint information.');
                    }
                }
            }

            Y.io("Services/WebSimXHR.ashx/GetTestBlueprint?sessionkey=" + sessionKey + "&testkey=" + testKey, callback);
        },

        // dropdown controls
        updateBlueprintSegmentDropdown = function () {
            var ddlSelectSegment = Y.one(_strDdlSelectSegment),
                lenBpSegs = _dataBpSegs.length;

            ddlSelectSegment.empty();
            ddlSelectSegment.appendChild(Y.Node.create('<option value=\"All\">All</option>'));
            for (var i = 0; i < lenBpSegs; i++) {
                ddlSelectSegment.appendChild(Y.Node.create('<option value=\"' + _dataBpSegs[i].SegmentKey + '\">' + _dataBpSegs[i].SegmentID + '</option>'));
            }

            // populate the segment strand dropdown list
            updateBlueprintStrandDropdown();
        },
        //blueprint strand dropdown dependent on selected segment value
        updateBlueprintStrandDropdown = function () {
            var ddlSelectStrand = Y.one(_strDdlSelectStrand),
                lenBpSegStrands = _dataBpSegStrands.length,
                selectedSegment = Y.one(_strDdlSelectSegment).get('value'),
                strandsToAdd = {}, i, strand = '';

            ddlSelectStrand.empty();
            ddlSelectStrand.appendChild(Y.Node.create('<option value=\"All\">All</option>'));
            ddlSelectStrand.appendChild(Y.Node.create('<option value=\"AffinityGroups\">Affinity Groups</option>'));

            for (i = 0; i < lenBpSegStrands; i++) {
                if (selectedSegment === "All" || _dataBpSegStrands[i].SegmentKey === selectedSegment) {
                    strandsToAdd[_dataBpSegStrands[i].Strand] = "append";
                }
            }

            for (strand in strandsToAdd) {
                ddlSelectStrand.appendChild(Y.Node.create('<option value=\"' + strand + '\">' + strand + '</option>'));
            }
        },

        // tables
        updateBlueprintSegmentTable = function () {
            var tableTbody = Y.one(_strTblBpSeg).one('tbody'),
                lenBlueprintSegments = _dataBpSegs.length,
                tableTrow,
                tableTcols;

            // display edit button and hide rest
            if (isAdaptiveTest() === true) {
                Y.one(_strBtnEditBlueprintSegment).removeAttribute('style');
            }
            else {
                Y.one(_strBtnEditBlueprintSegment).setStyle('display', 'none');
            }
            Y.one(_strBtnSaveBlueprintSegment).setStyle('display', 'none');
            Y.one(_strBtnCancelBlueprintSegment).setStyle('display', 'none');
            
            tableTbody.empty();
            for (var i = 0; i < lenBlueprintSegments; i++) {
                tableTrow = _bpSegTblRow.cloneNode(true);
                if (i % 2 != 0)
                    tableTrow.addClass('oddRow');
                else
                    tableTrow.addClass('evenRow');

                tableTcols = tableTrow.all('td');
                tableTcols.item(0).setContent(_dataBpSegs[i].SegmentKey);
                tableTcols.item(1).setContent(_dataBpSegs[i].SegmentPosition);
                tableTcols.item(2).setContent(_dataBpSegs[i].StartAbility);
                tableTcols.item(3).setContent(_dataBpSegs[i].StartInfo);
                tableTcols.item(4).setContent(_dataBpSegs[i].MinItems);
                tableTcols.item(5).setContent(_dataBpSegs[i].MaxItems);
                tableTcols.item(6).setContent(_dataBpSegs[i].FtStartPos);
                tableTcols.item(7).setContent(_dataBpSegs[i].FtEndPos);
                tableTcols.item(8).setContent(_dataBpSegs[i].FtMinItems);
                tableTcols.item(9).setContent(_dataBpSegs[i].FtMaxItems);
                tableTcols.item(10).setContent(_dataBpSegs[i].BlueprintWeight);
                tableTcols.item(11).setContent(_dataBpSegs[i].Cset1Size);
                tableTcols.item(12).setContent(_dataBpSegs[i].Cset2InitialRandom);
                tableTcols.item(13).setContent(_dataBpSegs[i].Cset2Random);
                tableTcols.item(14).setContent(_dataBpSegs[i].LoadConfig);
                tableTcols.item(15).setContent(_dataBpSegs[i].UpdateConfig);
                tableTcols.item(16).setContent(_dataBpSegs[i].ItemWeight);
                tableTcols.item(17).setContent(_dataBpSegs[i].AbilityOffset);
                tableTcols.item(18).setContent(_dataBpSegs[i].SelectionAlgorithm);
                tableTcols.item(19).setContent(_dataBpSegs[i].Cset1Order);
                tableTcols.item(20).setContent(_dataBpSegs[i].OPActiveItemCount);
                tableTcols.item(21).setContent(_dataBpSegs[i].OPActiveGroupCount);
                tableTcols.item(22).setContent(_dataBpSegs[i].RCAbilityWeight);
                tableTcols.item(23).setContent(_dataBpSegs[i].AbilityWeight);
                tableTcols.item(24).setContent(_dataBpSegs[i].PrecisionTargetNotMetWeight);
                tableTcols.item(25).setContent(_dataBpSegs[i].PrecisionTargetMetWeight);
                tableTcols.item(26).setContent(_dataBpSegs[i].PrecisionTarget);
                tableTcols.item(27).setContent(_dataBpSegs[i].AdaptiveCut);
                tableTcols.item(28).setContent(_dataBpSegs[i].TooCloseSEs);
                tableTcols.item(29).setContent(_dataBpSegs[i].TerminationMinCount);
                tableTcols.item(30).setContent(_dataBpSegs[i].TerminationOverallInfo);
                tableTcols.item(31).setContent(_dataBpSegs[i].TerminationRCInfo);
                tableTcols.item(32).setContent(_dataBpSegs[i].TerminationTooClose);
                tableTcols.item(33).setContent(_dataBpSegs[i].TerminationFlagsAnd);
                tableTbody.appendChild(tableTrow);
            }
        },
        updateBlueprintStrandTable = function () {
            var tableTbody = Y.one(_strTblBpSegStrand).one('tbody'),
                selectedSegment = Y.one(_strDdlSelectSegment).get('value'),
                lenBpSegStrand = _dataBpSegStrands.length,
                tableTrow,
                tableTcols,
                segmentKey,
                strand,
                startAbility,
                startInfo,
                minItems,
                maxItems,
                blueprintWeight,
                isStrictMax,
                adaptiveCut,
                scalar,
                opActiveItemCount,
                abilityWeight,
                precisionTargetNotMetWeight,
                precisionTargetMetWeight,
                precisionTarget,
                isReportingCategory,
                cnt = 1;

            //show the edit/convert buttons
            if (isAdaptiveTest() === true) {
                Y.one(_strBtnEditBlueprintStrand).removeAttribute('style');
                Y.one(_strBtnConvertBlueprintStrand).removeAttribute('style');
            }
            else {
                Y.one(_strBtnEditBlueprintStrand).setStyle('display', 'none');
                Y.one(_strBtnConvertBlueprintStrand).setStyle('display', 'none');
            }
            Y.one(_strBtnSaveBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnCancelBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnConvertSaveBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnConvertCancelBlueprintStrand).setStyle('display', 'none');

            tableTbody.empty();
            for (var i = 0; i < lenBpSegStrand; i++) {
                if (selectedSegment === "All" || selectedSegment === _dataBpSegStrands[i].SegmentKey) {
                    tableTrow = _bpSegStrandTblRow.cloneNode(true);
                    if (cnt % 2 != 0)
                        tableTrow.addClass('oddRow');
                    else
                        tableTrow.addClass('evenRow');

                    segmentKey = _dataBpSegStrands[i].SegmentKey;
                    strand = _dataBpSegStrands[i].Strand;
                    startAbility = _dataBpSegStrands[i].StartAbility;
                    startInfo = _dataBpSegStrands[i].StartInfo;
                    minItems = _dataBpSegStrands[i].MinItems;
                    maxItems = _dataBpSegStrands[i].MaxItems;
                    blueprintWeight = _dataBpSegStrands[i].BlueprintWeight;
                    isStrictMax = _dataBpSegStrands[i].IsStrictMax;
                    adaptiveCut = _dataBpSegStrands[i].AdaptiveCut;
                    scalar = _dataBpSegStrands[i].Scalar;
                    opActiveItemCount = _dataBpSegStrands[i].OPActiveItemCount;
                    abilityWeight = _dataBpSegStrands[i].AbilityWeight;
                    precisionTargetNotMetWeight = _dataBpSegStrands[i].PrecisionTargetNotMetWeight;
                    precisionTargetMetWeight = _dataBpSegStrands[i].PrecisionTargetMetWeight;
                    precisionTarget = _dataBpSegStrands[i].PrecisionTarget;
                    isReportingCategory = _dataBpSegStrands[i].IsReportingCategory;

                    //set the tablerow
                    tableTcols = tableTrow.all('td');

                    tableTcols.item(0).setContent(segmentKey);
                    tableTcols.item(1).setContent(strand);
                    tableTcols.item(2).setContent(startAbility);
                    tableTcols.item(3).setContent(startInfo);
                    tableTcols.item(4).setContent(minItems);
                    tableTcols.item(5).setContent(maxItems);
                    tableTcols.item(6).setContent(blueprintWeight);
                    tableTcols.item(7).setContent(isStrictMax);
                    tableTcols.item(8).setContent(adaptiveCut);
                    tableTcols.item(9).setContent(scalar);
                    tableTcols.item(10).setContent(opActiveItemCount);
                    tableTcols.item(11).setContent(abilityWeight);
                    tableTcols.item(12).setContent(precisionTargetNotMetWeight);
                    tableTcols.item(13).setContent(precisionTargetMetWeight);
                    tableTcols.item(14).setContent(precisionTarget);
                    tableTcols.item(15).setContent(isReportingCategory);

                    tableTbody.appendChild(tableTrow);

                    cnt++;
                }
            }
        },
        updateBlueprintContentLevelTable = function () {
            var tableTbody = Y.one(_strTblBpSegContentLvl).one('tbody'),
                selectedStrand = Y.one(_strDdlSelectStrand).get('value'),
                selectedSegment = Y.one(_strDdlSelectSegment).get('value'),
                lenBpSegContentLvl = _dataBpSegContentLvls.length,
                tableTrow, tableTcols, cnt = 1, val;

            //show the edit/convert buttons
            if (isAdaptiveTest() === true) {
                Y.one(_strBtnEditBlueprintContentLevel).removeAttribute('style');
                Y.one(_strBtnConvertBlueprintContentLevel).removeAttribute('style');
            }
            else {
                Y.one(_strBtnEditBlueprintContentLevel).setStyle('display', 'none');
                Y.one(_strBtnConvertBlueprintContentLevel).setStyle('display', 'none');
            }
            Y.one(_strBtnSaveBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnCancelBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnConvertSaveBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnConvertCancelBlueprintContentLevel).setStyle('display', 'none');

            //clear the table
            tableTbody.empty();
            for (var i = 0; i < lenBpSegContentLvl; i++) {
                //filter content levels for affinity groups
                if (selectedStrand === "AffinityGroups") {
                    isMatch = false;
                    //check if there are any matches for strand in content level
                    Y.one(_strDdlSelectStrand).get('options').each(function () {
                        val = this.get('value');
                        if (val != "All" && val != "AffinityGroups" && _dataBpSegContentLvls[i].ContentLevel.indexOf(val) >= 0) {
                            isMatch = true;
                        }
                    });
                    if (!isMatch && (selectedSegment === "All" || selectedSegment === _dataBpSegContentLvls[i].SegmentKey)) {
                        tableTrow = _bpSegContentLvlTblRow.cloneNode(true);
                        if (cnt % 2 != 0)
                            tableTrow.addClass('oddRow');
                        else
                            tableTrow.addClass('evenRow');

                        tableTcols = tableTrow.all('td');
                        tableTcols.item(0).setContent(_dataBpSegContentLvls[i].SegmentKey);
                        tableTcols.item(1).setContent(_dataBpSegContentLvls[i].ContentLevel);
                        tableTcols.item(2).setContent(_dataBpSegContentLvls[i].MinItems);
                        tableTcols.item(3).setContent(_dataBpSegContentLvls[i].MaxItems);
                        tableTcols.item(4).setContent(_dataBpSegContentLvls[i].BlueprintWeight);
                        tableTcols.item(5).setContent(_dataBpSegContentLvls[i].IsStrictMax);
                        tableTcols.item(6).setContent(_dataBpSegContentLvls[i].OPActiveItemCount);
                        tableTcols.item(7).setContent(_dataBpSegContentLvls[i].IsReportingCategory);

                        tableTbody.appendChild(tableTrow);

                        cnt++;
                    }
                }
                //filter content levels for everything else
                else if ((selectedSegment === "All" || _dataBpSegContentLvls[i].SegmentKey.indexOf(selectedSegment) >= 0) 
                        && (selectedStrand === "All" || _dataBpSegContentLvls[i].ContentLevel.indexOf(selectedStrand) >= 0)) {
                    tableTrow = _bpSegContentLvlTblRow.cloneNode(true);
                    if (cnt % 2 != 0)
                        tableTrow.addClass('oddRow');
                    else
                        tableTrow.addClass('evenRow');

                    tableTcols = tableTrow.all('td');
                    tableTcols.item(0).setContent(_dataBpSegContentLvls[i].SegmentKey);
                    tableTcols.item(1).setContent(_dataBpSegContentLvls[i].ContentLevel);
                    tableTcols.item(2).setContent(_dataBpSegContentLvls[i].MinItems);
                    tableTcols.item(3).setContent(_dataBpSegContentLvls[i].MaxItems);
                    tableTcols.item(4).setContent(_dataBpSegContentLvls[i].BlueprintWeight);
                    tableTcols.item(5).setContent(_dataBpSegContentLvls[i].IsStrictMax);
                    tableTcols.item(6).setContent(_dataBpSegContentLvls[i].OPActiveItemCount);
                    tableTcols.item(7).setContent(_dataBpSegContentLvls[i].IsReportingCategory);

                    tableTbody.appendChild(tableTrow);

                    cnt++;
                }
            }
        },

        // methods to make row editable
        editRowSegment = function(tableTrow) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(2).setContent(Y.Node.create('<input type=\"text\" size=\"15\" value=\"' + tableTcols.item(2).getContent() + '\" maxlength=\"20\" />'));
            tableTcols.item(3).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(3).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(4).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(4).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(5).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(5).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(6).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(6).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(7).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(7).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(8).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(8).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(9).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(9).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(10).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(10).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(11).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(11).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(12).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(12).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(13).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(13).getContent() + '\" maxlength=\"5\" />'));
//            tableTcols.item(14).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(14).getContent() + '\" maxlength=\"5\" />'));
//            tableTcols.item(15).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(15).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(16).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(16).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(17).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(17).getContent() + '\" maxlength=\"5\" />'));

            if (tableTcols.item(18).getContent() === "adaptive")
                tableTcols.item(18).setContent(Y.Node.create('<select><option selected=\"true\">adaptive</option><option>adaptive2</option></select>'));
            if (tableTcols.item(18).getContent() === "adaptive2")
                tableTcols.item(18).setContent(Y.Node.create('<select><option>adaptive</option><option selected=\"true\">adaptive2</option></select>'));
            
            if (tableTcols.item(19).getContent() === "ABILITY")
                tableTcols.item(19).setContent(Y.Node.create('<select><option selected=\"true\">ABILITY</option><option>DISTRIBUTION</option><option>RANDOM</option><option>ALL</option></select>'));
            else if (tableTcols.item(19).getContent() === "DISTRIBUTION")
                tableTcols.item(19).setContent(Y.Node.create('<select><option>ABILITY</option><option selected=\"true\">DISTRIBUTION</option><option>RANDOM</option><option>ALL</option></select>'));
            else if (tableTcols.item(19).getContent() === "RANDOM")
                tableTcols.item(19).setContent(Y.Node.create('<select><option>ABILITY</option><option>DISTRIBUTION</option><option selected=\"true\">RANDOM</option><option>ALL</option></select>'));
            else if (tableTcols.item(19).getContent() === "ALL")
                tableTcols.item(19).setContent(Y.Node.create('<select><option>ABILITY</option><option>DISTRIBUTION</option><option>RANDOM</option><option selected=\"true\">ALL</option></select>'));
            
            tableTcols.item(22).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(22).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(23).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(23).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(24).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(24).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(25).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(25).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(26).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(26).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(27).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(27).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(28).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(28).getContent() + '\" maxlength=\"5\" />'));

            if (tableTcols.item(29).getContent() == "True") {
                tableTcols.item(29).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(29).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }
            if (tableTcols.item(30).getContent() == "True") {
                tableTcols.item(30).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(30).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }
            if (tableTcols.item(31).getContent() == "True") {
                tableTcols.item(31).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(31).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }
            if (tableTcols.item(32).getContent() == "True") {
                tableTcols.item(32).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(32).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }
            if (tableTcols.item(33).getContent() == "True") {
                tableTcols.item(33).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(33).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }            

            //add event handler to see which rows were modified.  can use .all('input, select') but there is a bug for IE7 http://yuilibrary.com/projects/yui3/ticket/2532232
            tableTrow.all('input').on('change', function (e) {
                _modifiedSegments[tableTcols.item(0).getContent()] = "modified";
                tableTrow.setStyle('backgroundColor', '#ff9999');
            });
            tableTrow.all('select').on('change', function (e) {
                _modifiedSegments[tableTcols.item(0).getContent()] = "modified";
                tableTrow.setStyle('backgroundColor', '#ff9999');
            });
        },

        editRowSegmentStrand = function (tableTrow) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(2).setContent(Y.Node.create('<input type=\"text\" size=\"15\" value=\"' + tableTcols.item(2).getContent() + '\" maxlength=\"20\" />'));
            tableTcols.item(3).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(3).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(4).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(4).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(5).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(5).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(6).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(6).getContent() + '\" maxlength=\"5\" />'));
            if (tableTcols.item(7).getContent() == "True") {
                tableTcols.item(7).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            }
            else {
                tableTcols.item(7).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }
            tableTcols.item(8).setContent(Y.Node.create('<input type=\"text\" size=\"15\" value=\"' + tableTcols.item(8).getContent() + '\" maxlength=\"20\" />'));
            tableTcols.item(9).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(9).getContent() + '\" maxlength=\"5\" />'));
            
            tableTcols.item(11).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(11).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(12).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(12).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(13).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(13).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(14).setContent(Y.Node.create('<input type=\"text\" size=\"4\" value=\"' + tableTcols.item(14).getContent() + '\" maxlength=\"5\" />'));

            tableTrow.all('input').on('change', function (e) {
                _modifiedStrands[tableTcols.item(1).getContent()] = "modified";
                tableTrow.setStyle('backgroundColor', '#ff9999');
            });
        },
        editRowSegmentContentLevel = function (tableTrow) {
            var tableTcols = tableTrow.all('td');

            tableTcols.item(2).setContent(Y.Node.create('<input type=\"text\" size=\"5\" value=\"' + tableTcols.item(2).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(3).setContent(Y.Node.create('<input type=\"text\" size=\"5\" value=\"' + tableTcols.item(3).getContent() + '\" maxlength=\"5\" />'));
            tableTcols.item(4).setContent(Y.Node.create('<input type=\"text\" size=\"5\" value=\"' + tableTcols.item(4).getContent() + '\" maxlength=\"5\" />'));
            if (tableTcols.item(5).getContent() == "True") {
                tableTcols.item(5).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(5).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }

            tableTrow.all('input').on('change', function (e) {
                _modifiedContentLevels[tableTcols.item(1).getContent()] = "modified";
                tableTrow.setStyle('backgroundColor', '#ff9999');
            });
        },
        convertRowSegmentStrandAsContentLevel = function (tableTrow) {
            var tableTcols = tableTrow.all('td');

            if (isStrand(tableTcols.item(0).getContent(), tableTcols.item(1).getContent()) === false) {
                if (tableTcols.item(15).getContent() == "True") {
                    tableTcols.item(15).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
                } else {
                    tableTcols.item(15).setContent(Y.Node.create('<input type=\"checkbox\" />'));
                }
            }
            tableTrow.all('input').on('change', function (e) {
                if (tableTcols.item(15).one('input').get('checked')) {
                    if (_modifiedStrands[tableTcols.item(1).getContent()]){
                        delete _modifiedStrands[tableTcols.item(1).getContent()];
                        tableTrow.removeAttribute('style');
                    }
                }
                else {
                    _modifiedStrands[tableTcols.item(1).getContent()] = "modified";
                    tableTrow.setStyle('backgroundColor', '#ff9999');
                }
            });
        },
	    convertRowSegmentContentLevelAsStrand = function (tableTrow) {
            var tableTcols = tableTrow.all('td');

            if (tableTcols.item(7).getContent() == "True") {
                tableTcols.item(7).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(7).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }

            tableTrow.all('input').on('change', function (e) {
                if (tableTcols.item(7).one('input').get('checked')) {
                    _modifiedContentLevels[tableTcols.item(1).getContent()] = "modified";
                    tableTrow.setStyle('backgroundColor', '#ff9999');
                }
                else if (_modifiedContentLevels[tableTcols.item(1).getContent()]) {
                    delete _modifiedContentLevels[tableTcols.item(1).getContent()];
                    tableTrow.removeAttribute('style');
                }
            });
        },
			
        readOnlyRowSegment = function (tableTrow, startAbility, startInfo, minItems, maxItems, ftStartPos, ftEndPos, ftMinItems, ftMaxItems, blueprintWeight, cset1Size, cset2InitRand, cset2Rand, itemWeight, abilityOffset, selectionAlgorithm, cset1Order,
                                       rcAbilityWeight, abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, adaptiveCut, 
                                       tooCloseSEs, terminationMinCount, terminationOverallInfo, terminationRCInfo, terminationTooClose, terminationFlagsAnd) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(2).setContent(startAbility);
            tableTcols.item(3).setContent(startInfo);
            tableTcols.item(4).setContent(minItems);
            tableTcols.item(5).setContent(maxItems);
            tableTcols.item(6).setContent(ftStartPos);
            tableTcols.item(7).setContent(ftEndPos);
            tableTcols.item(8).setContent(ftMinItems);
            tableTcols.item(9).setContent(ftMaxItems);
            tableTcols.item(10).setContent(blueprintWeight);
            tableTcols.item(11).setContent(cset1Size);
            tableTcols.item(12).setContent(cset2InitRand);
            tableTcols.item(13).setContent(cset2Rand);
            tableTcols.item(16).setContent(itemWeight);
            tableTcols.item(17).setContent(abilityOffset);
            tableTcols.item(18).setContent(selectionAlgorithm);
            tableTcols.item(19).setContent(cset1Order);
            tableTcols.item(22).setContent(rcAbilityWeight);
            tableTcols.item(23).setContent(abilityWeight);
            tableTcols.item(24).setContent(precisionTargetNotMetWeight);
            tableTcols.item(25).setContent(precisionTargetMetWeight);
            tableTcols.item(26).setContent(precisionTarget);
            tableTcols.item(27).setContent(adaptiveCut);
            tableTcols.item(28).setContent(tooCloseSEs);
            tableTcols.item(29).setContent(terminationMinCount);
            tableTcols.item(30).setContent(terminationOverallInfo);
            tableTcols.item(31).setContent(terminationRCInfo);
            tableTcols.item(32).setContent(terminationTooClose);
            tableTcols.item(33).setContent(terminationFlagsAnd);

            //clear the hashtable that stores edited rows
            delete _modifiedSegments[tableTcols.item(0).getContent()];
        },

        // This one for FixedForm vs. AltAdaptive
        readOnlyRowSegment2 = function (tableTrow, selectionAlgorithm) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(18).setContent(selectionAlgorithm);

            //clear the hashtable that stores edited rows
            delete _modifiedSegments[tableTcols.item(0).getContent()];
        },
        readOnlyRowSegmentStrand = function (tableTrow, startAbility, startInfo, minItems, maxItems, bpWeight, isStrictMax, adaptiveCut, scalar,
                                             abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, isReportingCategory) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(2).setContent(startAbility);
            tableTcols.item(3).setContent(startInfo);
            tableTcols.item(4).setContent(minItems);
            tableTcols.item(5).setContent(maxItems);
            tableTcols.item(6).setContent(bpWeight);
            tableTcols.item(7).setContent(isStrictMax);
            tableTcols.item(8).setContent(adaptiveCut);
            tableTcols.item(9).setContent(scalar);
            tableTcols.item(11).setContent(abilityWeight);
            tableTcols.item(12).setContent(precisionTargetNotMetWeight);
            tableTcols.item(13).setContent(precisionTargetMetWeight);
            tableTcols.item(14).setContent(precisionTarget);
            tableTcols.item(15).setContent(isReportingCategory);

            //clear the hashtable that stores the edited rows
            delete _modifiedStrands[tableTcols.item(1).getContent()];
        },
        readOnlyRowSegmentContentLvl = function (tableTrow, minItems, maxItems, bpWeight, isStrictMax, isReportingCategory) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(2).setContent(minItems);
            tableTcols.item(3).setContent(maxItems);
            tableTcols.item(4).setContent(bpWeight);
            tableTcols.item(5).setContent(isStrictMax);
            tableTcols.item(7).setContent(isReportingCategory);

            //clear the hashtables that stores the edited rows
            delete _modifiedContentLevels[tableTcols.item(1).getContent()];
        },
        saveAlterSegment = function (sessionKey, testKey, tableTrow) {
            var tableTcols = tableTrow.all('td'),
                segmentKey = tableTcols.item(0).getContent(),
                startAbility = tableTcols.item(2).one('input').get('value'),
                startInfo = tableTcols.item(3).one('input').get('value'),
                minItems = tableTcols.item(4).one('input').get('value'),
                maxItems = tableTcols.item(5).one('input').get('value'),
                ftStartPos = tableTcols.item(6).one('input').get('value'),
                ftEndPos = tableTcols.item(7).one('input').get('value'),
                ftMinItems = tableTcols.item(8).one('input').get('value'),
                ftMaxItems = tableTcols.item(9).one('input').get('value'),
                bpWeight = tableTcols.item(10).one('input').get('value'),
                cset1Size = tableTcols.item(11).one('input').get('value'),
                cset2InitialRandom = tableTcols.item(12).one('input').get('value'),
                cset2Random = tableTcols.item(13).one('input').get('value'),
//                loadConfig = tableTcols.item(14).one('input').get('value'),
//                updateConfig = tableTcols.item(15).one('input').get('value'),
                itemWeight = tableTcols.item(16).one('input').get('value'),
                abilityOffset = tableTcols.item(17).one('input').get('value'),
                selectionAlgorithm = tableTcols.item(18).one('select').get('value'),
                cset1Order = tableTcols.item(19).one('select').get('value'),
                rcAbilityWeight = tableTcols.item(22).one('input').get('value'),
                abilityWeight = tableTcols.item(23).one('input').get('value'),
                precisionTargetNotMetWeight = tableTcols.item(24).one('input').get('value'),
                precisionTargetMetWeight = tableTcols.item(25).one('input').get('value'),
                precisionTarget = tableTcols.item(26).one('input').get('value'),
                adaptiveCut = tableTcols.item(27).one('input').get('value'),
                tooCloseSEs = tableTcols.item(28).one('input').get('value'),
                terminationMinCount = tableTcols.item(29).one('input').get('checked') ? "True" : "False",
                terminationOverallInfo = tableTcols.item(30).one('input').get('checked') ? "True" : "False",
                terminationRCInfo = tableTcols.item(31).one('input').get('checked') ? "True" : "False",
                terminationTooClose = tableTcols.item(32).one('input').get('checked') ? "True" : "False",
                terminationFlagsAnd = tableTcols.item(33).one('input').get('checked') ? "True" : "False",

            cfgAlterSeg = {
                method: "POST",
                data: "sessionkey=" + sessionKey + "&testkey=" + encodeURIComponent(testKey) + "&segmentkey=" + encodeURIComponent(segmentKey) + "&startability=" + startAbility + "&startinfo=" + startInfo
                    + "&minitems=" + minItems + "&maxitems=" + maxItems + "&ftstartpos=" + ftStartPos + "&ftendpos=" + ftEndPos + "&ftminitems="+ ftMinItems + "&ftmaxitems=" + ftMaxItems + "&bpweight=" + bpWeight + "&cset1size=" + cset1Size + "&cset2initialrandom=" + cset2InitialRandom
                    + "&cset2random=" + cset2Random + "&itemweight=" + itemWeight + "&abilityoffset=" + abilityOffset + "&selectionAlgorithm=" + selectionAlgorithm + "&cset1order=" + cset1Order 
                    + "&rcAbilityWeight=" + rcAbilityWeight + "&abilityWeight=" + abilityWeight + "&precisionTargetNotMetWeight=" + precisionTargetNotMetWeight 
                    + "&precisionTargetMetWeight=" + precisionTargetMetWeight + "&precisionTarget=" + precisionTarget
                    + "&adaptiveCut=" + adaptiveCut + "&tooCloseSEs=" + tooCloseSEs + "&terminationMinCount=" + terminationMinCount
                    + "&terminationOverallInfo=" + terminationOverallInfo + "&terminationRCInfo=" + terminationRCInfo 
                    + "&terminationTooClose=" + terminationTooClose + "&terminationFlagsAnd=" + terminationFlagsAnd,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText),
                            k, isEmpty = true;
                        if (response.status == "success") {
                            //delete the key from the hashtable & hide the border
                            delete _modifiedSegments[segmentKey];
                            tableTrow.removeAttribute('style');
                            readOnlyRowSegment(tableTrow, startAbility, startInfo, minItems, maxItems, ftStartPos, ftEndPos, ftMinItems, ftMaxItems, bpWeight, cset1Size, cset2InitialRandom, cset2Random, itemWeight, abilityOffset, selectionAlgorithm, cset1Order,
                                               rcAbilityWeight, abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, adaptiveCut,
                                               tooCloseSEs, terminationMinCount, terminationOverallInfo, terminationRCInfo, terminationTooClose, terminationFlagsAnd);

                            //if all modified segments have been saved, show the edit button and hide the rest
                            for (k in _modifiedSegments) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                //display edit button and hide rest
                                Y.one(_strBtnEditBlueprintSegment).removeAttribute('style');
                                Y.one(_strBtnSaveBlueprintSegment).setStyle('display', 'none');
                                Y.one(_strBtnCancelBlueprintSegment).setStyle('display', 'none');

                                //validate blueprint if all segments have been saved
                                ValidateBlueprint();
                            }

                            // update local copy of the data
                            var len = _dataBpSegs.length;
                            for (var i = 0; i < len; i++) {
                                if (_dataBpSegs[i].SegmentKey == segmentKey) {
                                    _dataBpSegs[i].StartAbility = startAbility;
                                    _dataBpSegs[i].StartInfo = startInfo;
                                    _dataBpSegs[i].MinItems = minItems;
                                    _dataBpSegs[i].MaxItems = maxItems;
                                    _dataBpSegs[i].FtStartPos = ftStartPos;
                                    _dataBpSegs[i].FtEndPos = ftEndPos;
                                    _dataBpSegs[i].FtMinItems = ftMinItems;
                                    _dataBpSegs[i].FtMaxItems = ftMaxItems;
                                    _dataBpSegs[i].BlueprintWeight = bpWeight;
                                    _dataBpSegs[i].Cset1Size = cset1Size;
                                    _dataBpSegs[i].Cset2InitialRandom = cset2InitialRandom;
                                    _dataBpSegs[i].Cset2Random = cset2Random;
//                                    _dataBpSegs[i].LoadConfig = loadConfig;
//                                    _dataBpSegs[i].UpdateConfig = updateConfig;
                                    _dataBpSegs[i].ItemWeight = itemWeight;
                                    _dataBpSegs[i].AbilityOffset = abilityOffset;
                                    _dataBpSegs[i].SelectionAlgorithm = selectionAlgorithm;
                                    _dataBpSegs[i].Cset1Order = cset1Order;
                                    _dataBpSegs[i].RCAbilityWeight = rcAbilityWeight;
                                    _dataBpSegs[i].AbilityWeight = abilityWeight;
                                    _dataBpSegs[i].PrecisionTargetNotMetWeight = precisionTargetNotMetWeight;
                                    _dataBpSegs[i].PrecisionTargetMetWeight = precisionTargetMetWeight;
                                    _dataBpSegs[i].PrecisionTarget = precisionTarget;
                                    _dataBpSegs[i].AdaptiveCut = adaptiveCut;
                                    _dataBpSegs[i].TooCloseSEs = tooCloseSEs;
                                    _dataBpSegs[i].TerminationMinCount = terminationMinCount;
                                    _dataBpSegs[i].TerminationOverallInfo = terminationOverallInfo;
                                    _dataBpSegs[i].TerminationRCInfo = terminationRCInfo;
                                    _dataBpSegs[i].TerminationTooClose = terminationTooClose;
                                    _dataBpSegs[i].TerminationFlagsAnd = terminationFlagsAnd;
                                    break;
                                }
                            }
                        } else {
                            alert("Response status: " + response.status + "; Reason: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to alter Segment properties.");
                    }
                }
            };
            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/AlterSegment", cfgAlterSeg);
        },
        saveAlterSegmentStrand = function (sessionKey, testKey, tableTrow) {
            //retrieve input values
            var tableTcols = tableTrow.all('td'),
                segmentKey = tableTcols.item(0).getContent(),
                strand = tableTcols.item(1).getContent(),
                startAbility = tableTcols.item(2).one('input').get('value'),
                startInfo = tableTcols.item(3).one('input').get('value'),
                minItems = tableTcols.item(4).one('input').get('value'),
                maxItems = tableTcols.item(5).one('input').get('value'),
                bpWeight = tableTcols.item(6).one('input').get('value'),
                isStrictMax = tableTcols.item(7).one('input').get('checked') ? "True" : "False", 
                adaptiveCut = tableTcols.item(8).one('input').get('value'),
                scalar = tableTcols.item(9).one('input').get('value'),
                abilityWeight = tableTcols.item(11).one('input').get('value'),
                precisionTargetNotMetWeight = tableTcols.item(12).one('input').get('value'),
                precisionTargetMetWeight = tableTcols.item(13).one('input').get('value'),
                precisionTarget = tableTcols.item(14).one('input').get('value'),
                isReportingCategory = tableTcols.item(15).getContent(),


            cfgAlterSegStrand = {
                method: "POST",
                data: "sessionkey=" + sessionKey + "&testkey=" + encodeURIComponent(testKey) + "&segmentkey=" + encodeURIComponent(segmentKey)
                    + "&strand=" + encodeURIComponent(strand) + "&minitems=" + minItems + "&maxitems=" + maxItems + "&bpweight=" + bpWeight
                    + "&isstrictmax=" + isStrictMax + "&startability=" + startAbility + "&startinfo=" + startInfo
                    + "&adaptivecut=" + adaptiveCut + "&scalar=" + scalar
                    + "&abilityWeight=" + abilityWeight + "&precisionTargetNotMetWeight=" + precisionTargetNotMetWeight
                    + "&precisionTargetMetWeight=" + precisionTargetMetWeight + "&precisionTarget=" + precisionTarget,
                    
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText),
                            k, isEmpty = true;
                        if (response.status == "success") {

                            //on success, clear the style/red border and remove strand from hashtable
                            delete _modifiedStrands[strand];
                            tableTrow.removeAttribute('style');
                            readOnlyRowSegmentStrand(tableTrow, startAbility, startInfo, minItems, maxItems, bpWeight, isStrictMax, adaptiveCut, scalar,
                                                     abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, isReportingCategory);
                            
                            //check if all modified strands have been saved successfully
                            for (k in _modifiedStrands) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                //display edit/convert buttons and hide rest
                                Y.one(_strBtnEditBlueprintStrand).removeAttribute('style');
                                Y.one(_strBtnSaveBlueprintStrand).setStyle('display', 'none');
                                Y.one(_strBtnCancelBlueprintStrand).setStyle('display', 'none');
                                Y.one(_strBtnConvertBlueprintStrand).removeAttribute('style');
                                Y.one(_strBtnConvertSaveBlueprintStrand).setStyle('display', 'none');
                                Y.one(_strBtnConvertCancelBlueprintStrand).setStyle('display', 'none');

                                //validate blueprint if all segments have been saved
                                ValidateBlueprint();
                            }

                            //update the local copy of the data
                            var len = _dataBpSegStrands.length;
                            for (var i = 0; i < len; i++) {
                                if (_dataBpSegStrands[i].SegmentKey == segmentKey && _dataBpSegStrands[i].Strand == strand) {
                                    _dataBpSegStrands[i].StartAbility = startAbility;
                                    _dataBpSegStrands[i].StartInfo = startInfo;
                                    _dataBpSegStrands[i].MinItems = minItems;
                                    _dataBpSegStrands[i].MaxItems = maxItems;
                                    _dataBpSegStrands[i].BpWeight = bpWeight;
                                    _dataBpSegStrands[i].IsStrictMax = isStrictMax;
                                    _dataBpSegStrands[i].AdaptiveCut = adaptiveCut;
                                    _dataBpSegStrands[i].Scalar = scalar;
                                    _dataBpSegStrands[i].AbilityWeight = abilityWeight;
                                    _dataBpSegStrands[i].PrecisionTargetNotMetWeight = precisionTargetNotMetWeight;
                                    _dataBpSegStrands[i].PrecisionTargetMetWeight = precisionTargetMetWeight;
                                    _dataBpSegStrands[i].PrecisionTarget = precisionTarget;
                                    _dataBpSegStrands[i].IsReportingCategory = isReportingCategory;
                                    break;
                                }
                            }
                        } else {
                            alert("Response status: " + response.status + "; Reason: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to alter Segment Strand properties.");
                    }
                }
            };
            
            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/AlterSegmentStrand", cfgAlterSegStrand);
        },
        saveAlterSegmentContentLevel = function (sessionKey, testKey, tableTrow) {
            var tableTcols = tableTrow.all('td'),
                segmentKey = tableTcols.item(0).getContent(),
                contentLevel = tableTcols.item(1).getContent(),
                minItems = tableTcols.item(2).one('input').get('value'),
                maxItems = tableTcols.item(3).one('input').get('value'),
                bpWeight = tableTcols.item(4).one('input').get('value'),
                isStrictMax = tableTcols.item(5).one('input').get('checked') ? "True" : "False",
                isReportingCategory = tableTcols.item(7).getContent();

            cfgAlterSegContentLevel = {
                method: "POST",
                data: "sessionkey=" + sessionKey + "&testkey=" + encodeURIComponent(testKey) + "&segmentkey=" + encodeURIComponent(segmentKey) + "&contentlevel=" + encodeURIComponent(contentLevel)
                    + "&minitems=" + minItems + "&maxitems=" + maxItems + "&bpweight=" + bpWeight + "&isstrictmax=" + isStrictMax,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText),
                            k, isEmpty = true;
                        if (response.status == "success") {
                            //remove content level key from hashtable and clear border
                            delete _modifiedContentLevels[contentLevel];
                            tableTrow.removeAttribute('style');
                            readOnlyRowSegmentContentLvl(tableTrow, minItems, maxItems, bpWeight, isStrictMax, isReportingCategory);

                            //if all modified segments have been saved, show the edit button and hide the rest
                            for (k in _modifiedContentLevels) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                //display edit/convert buttons
                                Y.one(_strBtnEditBlueprintContentLevel).removeAttribute('style');
                                Y.one(_strBtnSaveBlueprintContentLevel).setStyle('display', 'none');
                                Y.one(_strBtnCancelBlueprintContentLevel).setStyle('display', 'none');
                                Y.one(_strBtnConvertBlueprintContentLevel).removeAttribute('style');
                                Y.one(_strBtnConvertSaveBlueprintContentLevel).setStyle('display', 'none');
                                Y.one(_strBtnConvertCancelBlueprintContentLevel).setStyle('display', 'none');

                                //validate blueprint if all segments have been saved
                                ValidateBlueprint();
                            }

                            //update local copy of data
                            var len = _dataBpSegContentLvls.length;
                            for (var i = 0; i < len; i++) {
                                if (_dataBpSegContentLvls[i].SegmentKey == segmentKey && _dataBpSegContentLvls[i].ContentLevel == contentLevel) {
                                    _dataBpSegContentLvls[i].MinItems = minItems;
                                    _dataBpSegContentLvls[i].MaxItems = maxItems;
                                    _dataBpSegContentLvls[i].BlueprintWeight = bpWeight;
                                    _dataBpSegContentLvls[i].IsStrictMax = isStrictMax;
                                    _dataBpSegContentLvls[i].IsReportingCategory = isReportingCategory;
                                    break;
                                }
                            }
                        } else {
                            alert("Response status: " + response.status + "; Reason: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to alter Content Level properties.");
                    }
                }
            };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/AlterSegmentContentLevel", cfgAlterSegContentLevel);
        },
        saveConvertStrandAsContentLevel = function (sessionKey, testKey, tableTrow) {
            //retrieve input values
            var tableTcols = tableTrow.all('td'),
                segmentKey = tableTcols.item(0).getContent(),
                strand = tableTcols.item(1).getContent(),
                startAbility = tableTcols.item(2).getContent(),
                startInfo = tableTcols.item(3).getContent(),
                minItems = tableTcols.item(4).getContent(),
                maxItems = tableTcols.item(5).getContent(),
                bpWeight = tableTcols.item(6).getContent(),
                isStrictMax = tableTcols.item(7).getContent(),
                adaptiveCut = tableTcols.item(8).getContent(),
                scalar = tableTcols.item(9).getContent(),
                abilityWeight = tableTcols.item(11).getContent(),
                precisionTargetNotMetWeight = tableTcols.item(12).getContent(),
                precisionTargetMetWeight = tableTcols.item(13).getContent(),
                precisionTarget = tableTcols.item(14).getContent(),
                isReportingCategory = tableTcols.item(15).one('input').get('checked') ? "True" : "False",


            cfgConvertStrandAsContentLevel = {
                method: "POST",
                data: "sessionkey=" + sessionKey + "&testkey=" + encodeURIComponent(testKey) + "&segmentkey=" + encodeURIComponent(segmentKey)
                    + "&strand=" + encodeURIComponent(strand),

                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText),
                            k, isEmpty = true;
                        if (response.status == "success") {

                            //on success, clear the style/red border and remove strand from hashtable
                            delete _modifiedStrands[strand];
                            tableTrow.removeAttribute('style');
                            readOnlyRowSegmentStrand(tableTrow, startAbility, startInfo, minItems, maxItems, bpWeight, isStrictMax, adaptiveCut, scalar,
                                                        abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, isReportingCategory);

                            //check if all modified strands have been saved successfully
                            for (k in _modifiedStrands) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                //display edit/convert buttons and hide rest
                                Y.one(_strBtnEditBlueprintStrand).removeAttribute('style');
                                Y.one(_strBtnSaveBlueprintStrand).setStyle('display', 'none');
                                Y.one(_strBtnCancelBlueprintStrand).setStyle('display', 'none');
                                Y.one(_strBtnConvertBlueprintStrand).removeAttribute('style');
                                Y.one(_strBtnConvertSaveBlueprintStrand).setStyle('display', 'none');
                                Y.one(_strBtnConvertCancelBlueprintStrand).setStyle('display', 'none');

                                //validate blueprint if all segments have been saved
                                ValidateBlueprint();
                            }

                            //update the local copy of the data
                            var len = _dataBpSegStrands.length;
                            for (var i = 0; i < len; i++) {
                                if (_dataBpSegStrands[i].SegmentKey == segmentKey && _dataBpSegStrands[i].Strand == strand) {
                                    _dataBpSegStrands[i].StartAbility = startAbility;
                                    _dataBpSegStrands[i].StartInfo = startInfo;
                                    _dataBpSegStrands[i].MinItems = minItems;
                                    _dataBpSegStrands[i].MaxItems = maxItems;
                                    _dataBpSegStrands[i].BpWeight = bpWeight;
                                    _dataBpSegStrands[i].IsStrictMax = isStrictMax;
                                    _dataBpSegStrands[i].AdaptiveCut = adaptiveCut;
                                    _dataBpSegStrands[i].Scalar = scalar;
                                    _dataBpSegStrands[i].AbilityWeight = abilityWeight;
                                    _dataBpSegStrands[i].PrecisionTargetNotMetWeight = precisionTargetNotMetWeight;
                                    _dataBpSegStrands[i].PrecisionTargetMetWeight = precisionTargetMetWeight;
                                    _dataBpSegStrands[i].PrecisionTarget = precisionTarget;
                                    _dataBpSegStrands[i].IsReportingCategory = isReportingCategory;
                                    break;
                                }
                            }
                        } else {
                            alert("Response status: " + response.status + "; Reason: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to convert reporting category as non-reporting");
                    }
                }
            };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/ChangeStrandAsContentLevel", cfgConvertStrandAsContentLevel);
        },
        saveConvertContentLevelAsStrand = function (sessionKey, testKey, tableTrow) {
            var tableTcols = tableTrow.all('td'),
                segmentKey = tableTcols.item(0).getContent(),
                contentLevel = tableTcols.item(1).getContent(),
                minItems = tableTcols.item(2).getContent(),
                maxItems = tableTcols.item(3).getContent(),
                bpWeight = tableTcols.item(4).getContent(),
                isStrictMax = tableTcols.item(5).getContent(),
                isReportingCategory = tableTcols.item(7).one('input').get('checked') ? "True" : "False",

            cfgConvertContentLevelAsStrand = {
                method: "POST",
                data: "sessionkey=" + sessionKey + "&testkey=" + encodeURIComponent(testKey) + "&segmentkey=" + encodeURIComponent(segmentKey) + "&contentlevel=" + encodeURIComponent(contentLevel),
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText),
                            k, isEmpty = true;
                        if (response.status == "success") {
                            //remove content level key from hashtable and clear border
                            delete _modifiedContentLevels[contentLevel];
                            tableTrow.removeAttribute('style');
                            readOnlyRowSegmentContentLvl(tableTrow, minItems, maxItems, bpWeight, isStrictMax, isReportingCategory);

                            //if all modified segments have been saved, show the edit button and hide the rest
                            for (k in _modifiedContentLevels) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                //display edit/convert buttons
                                Y.one(_strBtnEditBlueprintContentLevel).removeAttribute('style');
                                Y.one(_strBtnSaveBlueprintContentLevel).setStyle('display', 'none');
                                Y.one(_strBtnCancelBlueprintContentLevel).setStyle('display', 'none');
                                Y.one(_strBtnConvertBlueprintContentLevel).removeAttribute('style');
                                Y.one(_strBtnConvertSaveBlueprintContentLevel).setStyle('display', 'none');
                                Y.one(_strBtnConvertCancelBlueprintContentLevel).setStyle('display', 'none');

                                //validate blueprint if all segments have been saved
                                ValidateBlueprint();
                            }

                            //update local copy of data
                            var len = _dataBpSegContentLvls.length;
                            for (var i = 0; i < len; i++) {
                                if (_dataBpSegContentLvls[i].SegmentKey == segmentKey && _dataBpSegContentLvls[i].ContentLevel == contentLevel) {
                                    _dataBpSegContentLvls[i].MinItems = minItems;
                                    _dataBpSegContentLvls[i].MaxItems = maxItems;
                                    _dataBpSegContentLvls[i].BlueprintWeight = bpWeight;
                                    _dataBpSegContentLvls[i].IsStrictMax = isStrictMax;
                                    _dataBpSegContentLvls[i].IsReportingCategory = isReportingCategory;
                                    break;
                                }
                            }
                        } else {
                            alert("Response status: " + response.status + "; Reason: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Failed to alter Content Level properties.");
                    }
                }
            };

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/ChangeContentLevelAsStrand", cfgConvertContentLevelAsStrand);
        },


        // this will update the clientname querystring value for navigation and breadcrumbs
        updateNavLinks = function (clientname) {
            Y.one(_divNav + ' .leftNav').all('li a').each(function () {
                var href = this.get('href');
                href = href.split('?', 1)[0];
                this.set('href', href + '?&clientname=' + clientname);
            });
        },

        ValidateBlueprint = function () {
            var cfg = {
                method: "POST",
                data: "sessionkey=" + _sessionKey,
                on: {
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.Status != "success") {
                            alert("Blueprint is invalid.\nSimulation will not run until all errors are fixed!");
                            
                            var serverPath = Y.one(_strServerPath).get('value'), newPathName;
                            if (serverPath != null && serverPath != '') {
                                // trim the trailing '/' character
                                if (serverPath.charAt(serverPath.length - 1) == '/') {
                                    serverPath = serverPath.substr(0, serverPath.length - 1);
                                }

                                newPathName = serverPath + "/Reports/BlueprintValidation.aspx?&sessionkey=" + _sessionKey;

                                window.open(newPathName);
                            } else {
                                alert("Unable to view Blueprint Validation Report - ServerPath not found.");
                            }
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/ValidateBlueprint", cfg);
        },

        validateBlueprintSegment = function (tableTrow) {
            var validationError,
                tableTcols, startAbility, startInfo, minItems, maxItems, bpWeight,
                cset1Size, cset2InitialRandom, cset2Random, itemWeight, abilityOffset, selectionAlgorithm, cset1Order,
                rcAbilityWeight, abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget,
                adaptiveCut, tooCloseSEs, terminationMinCount, terminationOverallInfo, terminationRCInfo, terminationTooClose, terminationFlagsAnd,
                regExInteger = /^\s*(\+|-)?\d+\s*$/,
                regExDecimal = /^\s*(\+|-)?((\d+(\.\d+)?)|(\.\d+))\s*$/;

            if (isAdaptiveTest()) {
                Y.one(_strTblBpSeg).one('tbody').all('tr').each(function () {
                    tableTcols = this.all('td');
                    startAbility = tableTcols.item(2).one('input').get('value');
                    startInfo = tableTcols.item(3).one('input').get('value');
                    minItems = tableTcols.item(4).one('input').get('value');
                    maxItems = tableTcols.item(5).one('input').get('value');
                    ftStartPos = tableTcols.item(6).one('input').get('value');
                    ftEndPos = tableTcols.item(7).one('input').get('value');
                    ftMinItems = tableTcols.item(8).one('input').get('value');
                    ftMaxItems = tableTcols.item(9).one('input').get('value');
                    bpWeight = tableTcols.item(10).one('input').get('value');
                    cset1Size = tableTcols.item(11).one('input').get('value');
                    cset2InitialRandom = tableTcols.item(12).one('input').get('value');
                    cset2Random = tableTcols.item(13).one('input').get('value');
                    itemWeight = tableTcols.item(16).one('input').get('value');
                    abilityOffset = tableTcols.item(17).one('input').get('value');
                    selectionAlgorithm = tableTcols.item(18).one('select').get('value');
                    cset1Order = tableTcols.item(19).one('select').get('value');
                    rcAbilityWeight = tableTcols.item(22).one('input').get('value');
                    abilityWeight = tableTcols.item(23).one('input').get('value');
                    precisionTargetNotMetWeight = tableTcols.item(24).one('input').get('value');
                    precisionTargetMetWeight = tableTcols.item(25).one('input').get('value');
                    precisionTarget = tableTcols.item(26).one('input').get('value');
                    adaptiveCut = tableTcols.item(27).one('input').get('value');
                    tooCloseSEs = tableTcols.item(28).one('input').get('value');
                    terminationMinCount = tableTcols.item(29).one('input').get('checked') ? "True" : "False";
                    terminationOverallInfo = tableTcols.item(30).one('input').get('checked') ? "True" : "False";
                    terminationRCInfo = tableTcols.item(31).one('input').get('checked') ? "True" : "False";
                    terminationTooClose = tableTcols.item(32).one('input').get('checked') ? "True" : "False";
                    terminationFlagsAnd = tableTcols.item(33).one('input').get('checked') ? "True" : "False";

                    if (startAbility == null || startAbility.length == 0
                            || startInfo == null || startInfo.length == 0
                            || minItems == null || minItems.length == 0
                            || maxItems == null || maxItems.length == 0
                            || ftStartPos == null || ftStartPos.length == 0
                            || ftEndPos == null || ftEndPos.length == 0
                            || ftMinItems == null || ftMinItems.length == 0
                            || ftMaxItems == null || ftMaxItems.length == 0
                            || bpWeight == null || bpWeight.length == 0
                            || cset1Size == null || cset1Size.length == 0
                            || cset2InitialRandom == null || cset2InitialRandom.length == 0
                            || cset2Random == null || cset2Random.length == 0
                            || itemWeight == null || itemWeight.length == 0
                            || abilityOffset == null || abilityOffset.length == 0
                            || selectionAlgorithm == null || selectionAlgorithm == 0
                            || cset1Order == null || cset1Order.length == 0
                            || rcAbilityWeight == null || rcAbilityWeight.length == 0
                            || abilityWeight == null || abilityWeight.length == 0
                            || precisionTargetNotMetWeight == null || precisionTargetNotMetWeight.length == 0
                            || precisionTargetMetWeight == null || precisionTargetMetWeight.length == 0
                            || terminationMinCount == null || terminationMinCount.length == 0
                            || terminationOverallInfo == null || terminationOverallInfo.length == 0
                            || terminationRCInfo == null || terminationRCInfo.length == 0
                            || terminationTooClose == null || terminationTooClose.length == 0
                            || terminationFlagsAnd == null || terminationFlagsAnd.length == 0) {
                        validationError = "All fields are required.";
                    } else if (String(startAbility).search(regExDecimal) == -1
                            || String(startInfo).search(regExDecimal) == -1 || String(minItems).search(regExInteger) == -1
                            || String(maxItems).search(regExInteger) == -1 || String(ftStartPos).search(regExInteger) == -1
                            || String(ftEndPos).search(regExInteger) == -1 || String(ftMinItems).search(regExInteger) == -1
                            || String(ftMaxItems).search(regExInteger) == -1 || String(bpWeight).search(regExDecimal) == -1
                            || String(cset1Size).search(regExInteger) == -1 || String(cset2InitialRandom).search(regExInteger) == -1
                            || String(cset2Random).search(regExInteger) == -1 || String(itemWeight).search(regExInteger) == -1
                            || String(abilityOffset).search(regExInteger) == -1 || String(rcAbilityWeight).search(regExDecimal) == -1
                            || String(abilityWeight).search(regExDecimal) == -1 || String(precisionTargetNotMetWeight).search(regExDecimal) == -1
                            || String(precisionTargetMetWeight).search(regExDecimal) == -1) {
                        validationError = "An invalid value was found. Please check your input values.";
                    }
                });
            }
             
            return validationError;
        },

        validateBlueprintSegmentStrand = function (tableTrow) {
            var validationError,
                tableTcols, startAbility, startInfo, minItems,
                maxItems, bpWeight, isStrictMax, adaptiveCut, scalar,
                
                regExInteger = /^\s*(\+|-)?\d+\s*$/,
                regExDecimal = /^\s*(\+|-)?((\d+(\.\d+)?)|(\.\d+))\s*$/;

            Y.one(_strTblBpSegStrand).one('tbody').all('tr').each( function () {
                tableTcols = this.all('td');
                startAbility = tableTcols.item(2).one('input').get('value');
                startInfo = tableTcols.item(3).one('input').get('value');
                minItems = tableTcols.item(4).one('input').get('value');
                maxItems = tableTcols.item(5).one('input').get('value');
                bpWeight = tableTcols.item(6).one('input').get('value');
                isStrictMax = tableTcols.item(7).one('input').get('checked') ? "True" : "False";
                adaptiveCut = tableTcols.item(8).one('input').get('value');
                scalar = tableTcols.item(9).one('input').get('value');
                abilityWeight = tableTcols.item(11).one('input').get('value');
                precisionTargetNotMetWeight = tableTcols.item(12).one('input').get('value');
                precisionTargetMetWeight = tableTcols.item(13).one('input').get('value');
                precisionTarget = tableTcols.item(14).one('input').get('value');

                if (startAbility == null || startAbility.length == 0
                        || startInfo == null || startInfo.length == 0
                        || minItems == null || minItems.length == 0
                        || maxItems == null || maxItems.length == 0
                        || bpWeight == null || bpWeight.length == 0
                        || isStrictMax == null || isStrictMax.length == 0
                        || abilityWeight == null || abilityWeight.length == 0
                        || precisionTargetNotMetWeight == null || precisionTargetNotMetWeight.length == 0
                        || precisionTargetMetWeight == null || precisionTargetMetWeight.length == 0
                        || precisionTarget == null || precisionTarget.length == 0) {
                    validationError = "All fields are required.";
                } else if (String(startAbility).search(regExDecimal) == -1
                        || String(startInfo).search(regExDecimal) == -1 || String(minItems).search(regExInteger) == -1
                        || String(maxItems).search(regExInteger) == -1 || String(bpWeight).search(regExDecimal) == -1
                        || (adaptiveCut != null && adaptiveCut.length > 0 && String(adaptiveCut).search(regExDecimal) == -1)
                        || (scalar != null && scalar.length > 0 && String(scalar).search(regExInteger) == -1)
                        || String(abilityWeight).search(regExDecimal) == -1 || String(precisionTargetNotMetWeight).search(regExDecimal) == -1
                        || String(precisionTargetMetWeight).search(regExDecimal) == -1 || String(precisionTarget).search(regExDecimal) == -1) {
                    validationError = "An invalid value was found. Please check your input values.";
                }
            });

            return validationError;
        },

        validateBlueprintSegmentContentLevel = function (tableTrow) {
            var validationError,
                tableTcols, minItems, maxItems, bpWeight, isStrictMax,

                regExInteger = /^\s*(\+|-)?\d+\s*$/,
                regExDecimal = /^\s*(\+|-)?((\d+(\.\d+)?)|(\.\d+))\s*$/;

            Y.one(_strTblBpSegContentLvl).one('tbody').all('tr').each( function () {
                
                tableTcols = this.all('td');
                minItems = tableTcols.item(2).one('input').get('value');
                maxItems = tableTcols.item(3).one('input').get('value');
                bpWeight = tableTcols.item(4).one('input').get('value');
                isStrictMax = tableTcols.item(5).one('input').get('checked') ? "True" : "False";

                if (minItems == null || minItems.length == 0
                        || maxItems == null || maxItems.length == 0
                        || bpWeight == null || bpWeight.length == 0
                        || isStrictMax == null || isStrictMax.length == 0) {
                    validationError = "All fields are required.";
                } else if (String(minItems).search(regExInteger) == -1 || String(maxItems).search(regExInteger) == -1 || String(bpWeight).search(regExDecimal) == -1) {
                    validationError = "An invalid value was found. Please check your input values.";
                }
            });

            return validationError;
        },

        
        /*
        *       EVENT HANDLERS
        */

        btnAlterTestItemProps_Click = function (e) {
            e.preventDefault();

            Y.SessionBlueprintItems.open(_sessionKey, Y.one(_strDdlSelectTest).get('value'), isAdaptiveTest(), _loadingPanel);
        },

        btnAlterItemSelectionParams_Click = function (e) {
            e.preventDefault();
            Y.SessionItemSelectionParams.open(_sessionKey, Y.one(_strDdlSelectTest).get('value'), currentSelectionAlgorithm(), _loadingPanel);
        },

        //convert table rows to editable
        btnEditBlueprintSegment_Click = function (e) {
        	e.preventDefault();

        	var tbodyBlueprintSegmentTable = Y.one(_strTblBpSeg).one('tbody'),
        	btnEditBlueprintSeg = Y.one(_strBtnEditBlueprintSegment),
        	btnSaveBlueprintSeg = Y.one(_strBtnSaveBlueprintSegment),
        	btnCancelBlueprintSeg = Y.one(_strBtnCancelBlueprintSegment);
        	//show the save/cancel buttons
        	btnEditBlueprintSeg.setStyle('display', 'none');
        	btnSaveBlueprintSeg.removeAttribute('style');
        	btnCancelBlueprintSeg.removeAttribute('style');
        	btnSaveBlueprintSeg.focus();

        	tbodyBlueprintSegmentTable.all('tr').each( function () {
        		editRowSegment(this);
        	});
        },
        btnEditBlueprintStrand_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintStrandTable = Y.one(_strTblBpSegStrand).one('tbody'),
                btnEditBlueprintStrand = Y.one(_strBtnEditBlueprintStrand),
                btnSaveBlueprintStrand = Y.one(_strBtnSaveBlueprintStrand),
                btnCancelBlueprintStrand = Y.one(_strBtnCancelBlueprintStrand);
                btnConvertEditBlueprintStrand = Y.one(_strBtnConvertBlueprintStrand),
                btnConvertSaveBlueprintStrand = Y.one(_strBtnConvertSaveBlueprintStrand),
                btnConvertCancelBlueprintStrand = Y.one(_strBtnConvertCancelBlueprintStrand);

            //show the save/cancel buttons
            btnEditBlueprintStrand.setStyle('display', 'none');
            btnSaveBlueprintStrand.removeAttribute('style');
            btnCancelBlueprintStrand.removeAttribute('style');
            btnConvertEditBlueprintStrand.setStyle('display', 'none');
            btnConvertSaveBlueprintStrand.setStyle('display', 'none');
            btnConvertCancelBlueprintStrand.setStyle('display', 'none');

            tbodyBlueprintStrandTable.all('tr').each( function () {
                editRowSegmentStrand(this);
            });
        },
        btnEditBlueprintContentLevel_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintContentLevelTable = Y.one(_strTblBpSegContentLvl).one('tbody'),
                btnEditBlueprintContentLevel = Y.one(_strBtnEditBlueprintContentLevel),
		        btnSaveBlueprintContentLevel = Y.one(_strBtnSaveBlueprintContentLevel),
                btnCancelBlueprintContentLevel = Y.one(_strBtnCancelBlueprintContentLevel);
		        btnConvertBlueprintContentLevel = Y.one(_strBtnConvertBlueprintContentLevel),
		        btnConvertSaveBlueprintContentLevel = Y.one(_strBtnConvertSaveBlueprintContentLevel),
                btnConvertCancelBlueprintContentLevel = Y.one(_strBtnConvertCancelBlueprintContentLevel);

            //show the save/cancel buttons
            btnEditBlueprintContentLevel.setStyle('display', 'none');
            btnSaveBlueprintContentLevel.removeAttribute('style');
            btnCancelBlueprintContentLevel.removeAttribute('style');
            btnConvertBlueprintContentLevel.setStyle('display', 'none');
            btnConvertSaveBlueprintContentLevel.setStyle('display', 'none');
            btnConvertCancelBlueprintContentLevel.setStyle('display', 'none');
            btnSaveBlueprintContentLevel.focus();

            tbodyBlueprintContentLevelTable.all('tr').each( function () {
                editRowSegmentContentLevel(this);
            });
        },
        btnConvertBlueprintStrandAsContentLevel_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintStrandTable = Y.one(_strTblBpSegStrand).one('tbody'),
                btnEditBlueprintStrand = Y.one(_strBtnEditBlueprintStrand),
                btnSaveBlueprintStrand = Y.one(_strBtnSaveBlueprintStrand),
                btnCancelBlueprintStrand = Y.one(_strBtnCancelBlueprintStrand);
                btnConvertEditBlueprintStrand = Y.one(_strBtnConvertBlueprintStrand),
                btnConvertSaveBlueprintStrand = Y.one(_strBtnConvertSaveBlueprintStrand),
                btnConvertCancelBlueprintStrand = Y.one(_strBtnConvertCancelBlueprintStrand);

            //show the cvsave/cvcancel buttons
            btnEditBlueprintStrand.setStyle('display', 'none');
            btnSaveBlueprintStrand.setStyle('display', 'none');
            btnCancelBlueprintStrand.setStyle('display', 'none');
            btnConvertEditBlueprintStrand.setStyle('display', 'none');
            btnConvertSaveBlueprintStrand.removeAttribute('style');
            btnConvertCancelBlueprintStrand.removeAttribute('style');
	        btnConvertSaveBlueprintStrand.focus();

	        tbodyBlueprintStrandTable.all('tr').each(function () {
	            convertRowSegmentStrandAsContentLevel(this);
            });
        },
	    btnConvertBlueprintContentLevelAsStrand_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintContentLevelTable = Y.one(_strTblBpSegContentLvl).one('tbody'),
                btnEditBlueprintContentLevel = Y.one(_strBtnEditBlueprintContentLevel),
				btnSaveBlueprintContentLevel = Y.one(_strBtnSaveBlueprintContentLevel),
                btnCancelBlueprintContentLevel = Y.one(_strBtnCancelBlueprintContentLevel),
				btnConvertBlueprintContentLevel = Y.one(_strBtnConvertBlueprintContentLevel),
				btnConvertSaveBlueprintContentLevel = Y.one(_strBtnConvertSaveBlueprintContentLevel),
				btnConvertCancelBlueprintContentLevel = Y.one(_strBtnConvertCancelBlueprintContentLevel);

            //show the cv_save/cv_cancel buttons
            btnEditBlueprintContentLevel.setStyle('display', 'none');
            btnSaveBlueprintContentLevel.setStyle('display', 'none');
            btnCancelBlueprintContentLevel.setStyle('display', 'none');
            btnConvertBlueprintContentLevel.setStyle('display', 'none');
            btnConvertSaveBlueprintContentLevel.removeAttribute('style');
            btnConvertCancelBlueprintContentLevel.removeAttribute('style');
            btnConvertSaveBlueprintContentLevel.focus();

            tbodyBlueprintContentLevelTable.all('tr').each( function () {
                convertRowSegmentContentLevelAsStrand(this);
            });
        },
        btnSaveBlueprintSegment_Click = function (e) {
        	e.preventDefault();

        	var validationError = validateBlueprintSegment(), // validate the blueprint segment params

        	cfgDeleteSessOppData = {
        		method: "POST",
        		data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
        		on: {
        			complete: function (id, xhr, args) {
        				_loadingPanel.hide();
        			},
        			success: function (id, xhr) {
        				var response = Y.JSON.parse(xhr.responseText),
        				k, isEmpty = true;
        				if (response.status == "success") {
        					//if delete was successful alter segment data
        					Y.one(_strTblBpSeg).one('tbody').all('tr').each( function () {
        						var currSegKey = this.one('td').getContent(), k;

        						if (_modifiedSegments[currSegKey]) {
        							saveAlterSegment(_sessionKey, _testKey, this);
        						} else {
        							var segmentKey = this.one('td').getContent(),
        							startAbility,
        							startInfo,
        							minItems,
        							maxItems,
        							ftStartPos,
        							ftEndPos,
        							ftMinItems,
        							ftMaxItems,
        							blueprintWeight,
        							cset1Size,
        							cset2InitialRandom,
        							cset2Random,
        							loadConfig,
        							updateConfig,
        							itemWeight,
        							abilityOffset,
        							selectionAlgorithm,
        							cset1Order,
        							rcAbilityWeight,
        							abilityWeight,
        							precisionTargetNotMetWeight,
        							precisionTargetMetWeight,
        							precisionTarget,
        							adaptiveCut,
        							tooCloseSEs,
        							terminationMinCount,
        							terminationOverallInfo,
        							terminationRCInfo,
        							terminationTooClose,
        							terminationFlagsAnd,
        							len;
        							len = _dataBpSegs.length;
        							for (var i = 0; i < len; i++) {
        								if (_dataBpSegs[i].SegmentKey == segmentKey) {
        									startAbility = _dataBpSegs[i].StartAbility;
        									startInfo = _dataBpSegs[i].StartInfo;
        									minItems = _dataBpSegs[i].MinItems;
        									maxItems = _dataBpSegs[i].MaxItems;
        									ftStartPos = _dataBpSegs[i].FtStartPos;
        									ftEndPos = _dataBpSegs[i].FtEndPos;
        									ftMinItems = _dataBpSegs[i].FtMinItems;
        									ftMaxItems = _dataBpSegs[i].FtMaxItems;
        									blueprintWeight = _dataBpSegs[i].BlueprintWeight;
        									cset1Size = _dataBpSegs[i].Cset1Size;
        									cset2InitialRandom = _dataBpSegs[i].Cset2InitialRandom;
        									cset2Random = _dataBpSegs[i].Cset2Random;
        									loadConfig = _dataBpSegs[i].LoadConfig;
        									updateConfig = _dataBpSegs[i].UpdateConfig;
        									itemWeight = _dataBpSegs[i].ItemWeight;
        									abilityOffset = _dataBpSegs[i].AbilityOffset;
        									selectionAlgorithm = _dataBpSegs[i].SelectionAlgorithm;
        									cset1Order = _dataBpSegs[i].Cset1Order;
        									rcAbilityWeight = _dataBpSegs[i].RCAbilityWeight;
        									abilityWeight = _dataBpSegs[i].AbilityWeight;
        									precisionTargetNotMetWeight = _dataBpSegs[i].PrecisionTargetNotMetWeight;
        									precisionTargetMetWeight = _dataBpSegs[i].PrecisionTargetMetWeight;
        									precisionTarget = _dataBpSegs[i].PrecisionTarget;
        									adaptiveCut = _dataBpSegs[i].AdaptiveCut;
        									tooCloseSEs = _dataBpSegs[i].TooCloseSEs;
        									terminationMinCount = _dataBpSegs[i].TerminationMinCount;
        									terminationOverallInfo = _dataBpSegs[i].TerminationOverallInfo;
        									terminationRCInfo = _dataBpSegs[i].TerminationRCInfo;
        									terminationTooClose = _dataBpSegs[i].TerminationTooClose;
        									terminationFlagsAnd = _dataBpSegs[i].TerminationFlagsAnd;
        									break;
        								}
        							}

        							readOnlyRowSegment(this, startAbility, startInfo, minItems, maxItems, ftStartPos, ftEndPos, ftMinItems, ftMaxItems, blueprintWeight, cset1Size, cset2InitialRandom, cset2Random, itemWeight, abilityOffset, selectionAlgorithm, cset1Order,
        									rcAbilityWeight, abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, adaptiveCut, tooCloseSEs, terminationMinCount, terminationOverallInfo, terminationRCInfo,
        									terminationTooClose, terminationFlagsAnd);
        						}

        						//if all modified segments have been saved, show the edit button and hide the rest
        						for (k in _modifiedSegments) {
        							isEmpty = false;
        							break;
        						}
        						if (isEmpty) {
        							//display edit button and hide rest
        							Y.one(_strBtnEditBlueprintSegment).removeAttribute('style');
        							Y.one(_strBtnSaveBlueprintSegment).setStyle('display', 'none');
        							Y.one(_strBtnCancelBlueprintSegment).setStyle('display', 'none');
        						}
        					});

        				} else {
        					alert("Failed: " + response.reason);
        				}
        			},
        			failure: function (id, xhr) {
        				alert("Failed to Delete Session Data");
        			}
        		}
        	};

            // validate all parameters
            if (validationError != null && validationError.length > 0) {
                alert(validationError);
                return;
            }

            // notify user that all opportunity data will be cleared if test properties are altered
            if (!confirm("All opportunity data will be cleared for the test.  Continue?"))
            {
                return;
            }

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionOppData", cfgDeleteSessOppData);
        },
        btnSaveBlueprintStrand_Click = function (e) {
            e.preventDefault();

            var validationError = validateBlueprintSegmentStrand(), // validate the blueprint segment params

            cfgDeleteSessOppData = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText), k, isEmpty = true;
                        if (response.status == "success") {
                            //if delete was successful alter segment data
                            Y.one(_strTblBpSegStrand).one('tbody').all('tr').each( function () {
                                var currStrndKey = this.one('td').next().getContent();
                
                                if (_modifiedStrands[currStrndKey]) {
                                    saveAlterSegmentStrand(_sessionKey, _testKey, this);
                                } else {
                                    var segmentKey = this.one('td').getContent(),
                                        strand = this.one('td').next().getContent(),
                                        startAbility,
                                        startInfo,
                                        minItems,
                                        maxItems,
                                        bpWeight,
                                        isStrictMax,
                                        adaptiveCut,
                                        scalar,
                                        abilityWeight,
                                        precisionTargetNotMetWeight,
                                        precisionTargetMetWeight,
                                        precisionTarget,
                                        isReportingCategory,
                                        len;
                                    len = _dataBpSegStrands.length;
                                    for (var i = 0; i < len; i++) {
                                        if (_dataBpSegStrands[i].SegmentKey == segmentKey && _dataBpSegStrands[i].Strand == strand) {
                                            startAbility = _dataBpSegStrands[i].StartAbility;
                                            startInfo = _dataBpSegStrands[i].StartInfo;
                                            minItems = _dataBpSegStrands[i].MinItems;
                                            maxItems = _dataBpSegStrands[i].MaxItems;
                                            bpWeight = _dataBpSegStrands[i].BlueprintWeight;
                                            isStrictMax = _dataBpSegStrands[i].IsStrictMax;
                                            adaptiveCut = _dataBpSegStrands[i].AdaptiveCut;
                                            scalar = _dataBpSegStrands[i].Scalar;
                                            abilityWeight = _dataBpSegStrands[i].AbilityWeight;
                                            precisionTargetNotMetWeight = _dataBpSegStrands[i].PrecisionTargetNotMetWeight;
                                            precisionTargetMetWeight = _dataBpSegStrands[i].PrecisionTargetMetWeight;
                                            precisionTarget = _dataBpSegStrands[i].PrecisionTarget;
                                            isReportingCategory = _dataBpSegStrands[i].IsReportingCategory;
                                            break;
                                        }
                                    }
                                    readOnlyRowSegmentStrand(this, startAbility, startInfo, minItems, maxItems, bpWeight, isStrictMax, adaptiveCut, scalar,
                                                             abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, isReportingCategory);
                                }

                                //if all modified segments have been saved, show the edit button and hide the rest
                                for (k in _modifiedStrands) {
                                    isEmpty = false;
                                    break;
                                }
                                if (isEmpty) {
                                    //display edit/convert buttons and hide rest
                                    Y.one(_strBtnEditBlueprintSegment).removeAttribute('style');
                                    Y.one(_strBtnSaveBlueprintSegment).setStyle('display', 'none');
                                    Y.one(_strBtnCancelBlueprintSegment).setStyle('display', 'none');
                                    Y.one(_strBtnConvertEditBlueprintSegment).removeAttribute('style');
                                    Y.one(_strBtnConvertSaveBlueprintSegment).setStyle('display', 'none');
                                    Y.one(_strBtnConvertCancelBlueprintSegment).setStyle('display', 'none');
                                }
                            });
                        } else {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };

            // validate all parameters before even clearing opp data
            if (validationError != null && validationError.length > 0) {
                alert(validationError);
                return;
            }

            // notify user that all opportunity data will be cleared if test properties are altered
            if (!confirm("All opportunity data will be cleared for the test.  Continue?"))
            {
                return;
            }

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionOppData", cfgDeleteSessOppData);
        },
        btnSaveBlueprintContentLevel_Click = function (e) {
            e.preventDefault();

            var validationError = validateBlueprintSegmentContentLevel(), // validate the blueprint segment params
            cfgDeleteSessOppData = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText), k, isEmpty = true;
                        if (response.status == "success") {
                            //if delete was successful alter content level data
                            Y.one(_strTblBpSegContentLvl).one('tbody').all('tr').each( function () {
                                var currCLKey = this.one('td').next().getContent();

                                if (_modifiedContentLevels[currCLKey]) {
                                    saveAlterSegmentContentLevel(_sessionKey, _testKey, this);
                                } else {
                                    var segmentKey = this.one('td').getContent(),
                                        contentLevelID = this.one('td').next().getContent(),
                                        minItems,
                                        maxItems,
                                        bpWeight,
                                        isStrictMax,
                                        isReportingCategory,
                                        len;
                                    len = _dataBpSegContentLvls.length;
                                    for (var i = 0; i < len; i++) {
                                        if (_dataBpSegContentLvls[i].SegmentKey == segmentKey && _dataBpSegContentLvls[i].ContentLevel == contentLevelID) {
                                            minItems = _dataBpSegContentLvls[i].MinItems;
                                            maxItems = _dataBpSegContentLvls[i].MaxItems;
                                            bpWeight = _dataBpSegContentLvls[i].BlueprintWeight;
                                            isStrictMax = _dataBpSegContentLvls[i].IsStrictMax;
                                            isReportingCategory = _dataBpSegContentLvls[i].IsReportingCategory;
                                            break;
                                        }
                                    }
                                    readOnlyRowSegmentContentLvl(this, minItems, maxItems, bpWeight, isStrictMax, isReportingCategory);
                                }
                            });
                        } else {
                            alert("Failed: " + response.reason);
                        }

                        //if all modified segments have been saved, show the edit button and hide the rest
                        for (k in _modifiedContentLevels) {
                            isEmpty = false;
                            break;
                        }
                        if (isEmpty) {
                            //display edit/convert button and hide rest
                            Y.one(_strBtnEditBlueprintSegment).removeAttribute('style');
                            Y.one(_strBtnSaveBlueprintSegment).setStyle('display', 'none');
                            Y.one(_strBtnCancelBlueprintSegment).setStyle('display', 'none');
                            Y.one(_strBtnConvertBlueprintContentLevel).removeAttribute('style');
                            Y.one(_strBtnConvertSaveBlueprintContentLevel).setStyle('display', 'none');
                            Y.one(_strBtnConvertCancelBlueprintContentLevel).setStyle('display', 'none');
			    
			    
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };

            if (validationError != null && validationError.length > 0) {
                alert(validationError);
                return;
            }

            // notify user that all opportunity data will be cleared if test properties are altered
            if (!confirm("All opportunity data will be cleared for the test.  Continue?"))
            {
                return;
            }

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionOppData", cfgDeleteSessOppData);
        },
        btnConvertSaveBlueprintStrandAsContentLevel_Click = function (e) {
            e.preventDefault();

            cfgDeleteSessOppData = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText), k, isEmpty = true;
                        if (response.status == "success") {
                            //if delete was successful alter segment data
                            Y.one(_strTblBpSegStrand).one('tbody').all('tr').each(function () {
                                var currStrndKey = this.one('td').next().getContent();

                                if (_modifiedStrands[currStrndKey]) {
                                    saveConvertStrandAsContentLevel(_sessionKey, _testKey, this);
                                } else {
                                    var segmentKey = this.one('td').getContent(),
                                        strand = this.one('td').next().getContent(),
                                        startAbility,
                                        startInfo,
                                        minItems,
                                        maxItems,
                                        bpWeight,
                                        isStrictMax,
                                        adaptiveCut,
                                        scalar,
                                        abilityWeight,
                                        precisionTargetNotMetWeight,
                                        precisionTargetMetWeight,
                                        precisionTarget,
                                        isReportingCategory,
                                        len;
                                    len = _dataBpSegStrands.length;
                                    for (var i = 0; i < len; i++) {
                                        if (_dataBpSegStrands[i].SegmentKey == segmentKey && _dataBpSegStrands[i].Strand == strand) {
                                            startAbility = _dataBpSegStrands[i].StartAbility;
                                            startInfo = _dataBpSegStrands[i].StartInfo;
                                            minItems = _dataBpSegStrands[i].MinItems;
                                            maxItems = _dataBpSegStrands[i].MaxItems;
                                            bpWeight = _dataBpSegStrands[i].BlueprintWeight;
                                            isStrictMax = _dataBpSegStrands[i].IsStrictMax;
                                            adaptiveCut = _dataBpSegStrands[i].AdaptiveCut;
                                            scalar = _dataBpSegStrands[i].Scalar;
                                            abilityWeight = _dataBpSegStrands[i].AbilityWeight;
                                            precisionTargetNotMetWeight = _dataBpSegStrands[i].PrecisionTargetNotMetWeight;
                                            precisionTargetMetWeight = _dataBpSegStrands[i].PrecisionTargetMetWeight;
                                            precisionTarget = _dataBpSegStrands[i].PrecisionTarget;
                                            isReportingCategory = _dataBpSegStrands[i].IsReportingCategory;
                                            break;
                                        }
                                    }
                                    readOnlyRowSegmentStrand(this, startAbility, startInfo, minItems, maxItems, bpWeight, isStrictMax, adaptiveCut, scalar,
                                                             abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, isReportingCategory);
                                }
                            });
                            //if all modified segments have been saved, show the edit button and hide the rest
                            for (k in _modifiedStrands) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                //display edit/convert buttons and hide rest
                                Y.one(_strBtnEditBlueprintSegment).removeAttribute('style');
                                Y.one(_strBtnSaveBlueprintSegment).setStyle('display', 'none');
                                Y.one(_strBtnCancelBlueprintSegment).setStyle('display', 'none');
                                Y.one(_strBtnConvertEditBlueprintSegment).removeAttribute('style');
                                Y.one(_strBtnConvertSaveBlueprintSegment).setStyle('display', 'none');
                                Y.one(_strBtnConvertCancelBlueprintSegment).setStyle('display', 'none');
                            }
                            window.location.reload(true);
                        } else {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };

            if (Object.keys(_modifiedStrands).length <= 0)
                return;

            // notify user that all opportunity data will be cleared if test properties are altered
            if (!confirm("All opportunity data will be cleared for the test.  Continue?")) {
                return;
            }

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionOppData", cfgDeleteSessOppData);
        },
        btnConvertSaveBlueprintContentLevelAsStrand_Click = function (e) {
            e.preventDefault();

            cfgDeleteSessOppData = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText), k, isEmpty = true;
                        if (response.status == "success") {
                            //if delete was successful alter content level data
                            Y.one(_strTblBpSegContentLvl).one('tbody').all('tr').each(function () {
                                var currCLKey = this.one('td').next().getContent();

                                if (_modifiedContentLevels[currCLKey]) {
                                    saveConvertContentLevelAsStrand(_sessionKey, _testKey, this);
                                } else {
                                    var segmentKey = this.one('td').getContent(),
                                        contentLevelID = this.one('td').next().getContent(),
                                        minItems,
                                        maxItems,
                                        bpWeight,
                                        isStrictMax,
                                        isReportingCategory,
                                        len;
                                    len = _dataBpSegContentLvls.length;
                                    for (var i = 0; i < len; i++) {
                                        if (_dataBpSegContentLvls[i].SegmentKey == segmentKey && _dataBpSegContentLvls[i].ContentLevel == contentLevelID) {
                                            minItems = _dataBpSegContentLvls[i].MinItems;
                                            maxItems = _dataBpSegContentLvls[i].MaxItems;
                                            bpWeight = _dataBpSegContentLvls[i].BlueprintWeight;
                                            isStrictMax = _dataBpSegContentLvls[i].IsStrictMax;
                                            isReportingCategory = _dataBpSegContentLvls[i].IsReportingCategory;
                                            break;
                                        }
                                    }
                                    readOnlyRowSegmentContentLvl(this, minItems, maxItems, bpWeight, isStrictMax, isReportingCategory);
                                }
                            });
                            //if all modified segments have been saved, show the edit button and hide the rest
                            for (k in _modifiedContentLevels) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                //display edit/convert button and hide rest
                                Y.one(_strBtnEditBlueprintSegment).removeAttribute('style');
                                Y.one(_strBtnSaveBlueprintSegment).setStyle('display', 'none');
                                Y.one(_strBtnCancelBlueprintSegment).setStyle('display', 'none');
                                Y.one(_strBtnConvertBlueprintContentLevel).removeAttribute('style');
                                Y.one(_strBtnConvertSaveBlueprintContentLevel).setStyle('display', 'none');
                                Y.one(_strBtnConvertCancelBlueprintContentLevel).setStyle('display', 'none');
                            }
                            window.location.reload(true);
                        } else {
                            alert("Failed: " + response.reason);
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };
            
            if (Object.keys(_modifiedContentLevels).length <= 0)
                return;

            // notify user that all opportunity data will be cleared if test properties are altered
            if (!confirm("All opportunity data will be cleared for the test.  Continue?")) {
                return;
            }

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionOppData", cfgDeleteSessOppData);
        },
        btnCancelBlueprintSegment_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintSegmentTable = Y.one(_strTblBpSeg).one('tbody'),
                isEmpty = true, k;
            //show the save/cancel buttons
            Y.one(_strBtnEditBlueprintSegment).removeAttribute('style');
            Y.one(_strBtnSaveBlueprintSegment).setStyle('display', 'none');
            Y.one(_strBtnCancelBlueprintSegment).setStyle('display', 'none');

            //if there are segments left over to modify and we are canceling these changes, validate the blueprint with the already saved values
            for (k in _modifiedSegments) {
                isEmpty = false;
                break;
            }
            if (!isEmpty) {
                ValidateBlueprint();
            }

            tbodyBlueprintSegmentTable.all('tr').each( function () {
                var segmentKey = this.one('td').getContent(),
                    startAbility,
                    startInfo,
                    minItems,
                    maxItems,
                    ftStartPos,
                    ftEndPos,
                    ftMinItem,
                    ftMaxItem,
                    blueprintWeight,
                    cset1Size,
                    cset2InitialRandom,
                    cset2Random,
                    loadConfig,
                    updateConfig,
                    itemWeight,
                    abilityOffset,
                    selectionAlgorithm,
                    cset1Order,
                    rcAbilityWeight,
                    abilityWeight,
                    precisionTargetNotMetWeight,
                    precisionTargetMetWeight,
                    precisionTarget,
                    adaptiveCut,
                    tooCloseSEs,
                    terminationMinCount,
                    terminationOverallInfo,
                    terminationRCInfo,
                    terminationTooClose,
                    terminationFlagsAnd,
                    len;
                len = _dataBpSegs.length;
                for (var i = 0; i < len; i++) {
                    if (_dataBpSegs[i].SegmentKey == segmentKey) {
                        startAbility = _dataBpSegs[i].StartAbility;
                        startInfo = _dataBpSegs[i].StartInfo;
                        minItems = _dataBpSegs[i].MinItems;
                        maxItems = _dataBpSegs[i].MaxItems;
                        ftStartPos = _dataBpSegs[i].FtStartPos;
                        ftEndPos = _dataBpSegs[i].FtEndPos;
                        ftMinItem = _dataBpSegs[i].FtMinItems;
                        ftMaxItem = _dataBpSegs[i].FtMaxItems;
                        blueprintWeight = _dataBpSegs[i].BlueprintWeight;
                        cset1Size = _dataBpSegs[i].Cset1Size;
                        cset2InitialRandom = _dataBpSegs[i].Cset2InitialRandom;
                        cset2Random = _dataBpSegs[i].Cset2Random;
                        loadConfig = _dataBpSegs[i].LoadConfig;
                        updateConfig = _dataBpSegs[i].UpdateConfig;
                        itemWeight = _dataBpSegs[i].ItemWeight;
                        abilityOffset = _dataBpSegs[i].AbilityOffset;
                        selectionAlgorithm = _dataBpSegs[i].SelectionAlgorithm;
                        cset1Order = _dataBpSegs[i].Cset1Order;
                        rcAbilityWeight = _dataBpSegs[i].RCAbilityWeight;
                        abilityWeight =_dataBpSegs[i].AbilityWeight;
                        precisionTargetNotMetWeight = _dataBpSegs[i].PrecisionTargetNotMetWeight;
                        precisionTargetMetWeight = _dataBpSegs[i].PrecisionTargetMetWeight;
                        precisionTarget = _dataBpSegs[i].PrecisionTarget;
                        adaptiveCut = _dataBpSegs[i].AdaptiveCut;
                        tooCloseSEs = _dataBpSegs[i].TooCloseSEs;
                        terminationMinCount = _dataBpSegs[i].TerminationMinCount;
                        terminationOverallInfo = _dataBpSegs[i].TerminationOverallInfo;
                        terminationRCInfo = _dataBpSegs[i].TerminationRCInfo;
                        terminationTooClose = _dataBpSegs[i].TerminationTooClose;
                        terminationFlagsAnd = _dataBpSegs[i].TerminationFlagsAnd;
                        break;
                    }
                }
                
                this.removeAttribute('style');
                readOnlyRowSegment(this, startAbility, startInfo, minItems, maxItems, ftStartPos, ftEndPos, ftMinItem, ftMaxItem, blueprintWeight, cset1Size, cset2InitialRandom, cset2Random, itemWeight, abilityOffset, selectionAlgorithm, cset1Order,
                                    rcAbilityWeight, abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, adaptiveCut, tooCloseSEs, terminationMinCount, terminationOverallInfo, terminationRCInfo,
                                     terminationTooClose, terminationFlagsAnd);
            });
        },
        btnCancelBlueprintStrand_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintStrandTable = Y.one(_strTblBpSegStrand).one('tbody'),
                isEmpty = true;
            //hide the save/cancel buttons
            Y.one(_strBtnEditBlueprintStrand).removeAttribute('style');
            Y.one(_strBtnSaveBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnCancelBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnConvertBlueprintStrand).removeAttribute('style');
            Y.one(_strBtnConvertSaveBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnConvertCancelBlueprintStrand).setStyle('display', 'none');

            //if there are segments left over to modify and we are canceling these changes, validate the blueprint with the already saved values
            for (k in _modifiedSegments) {
                isEmpty = false;
                break;
            }
            if (!isEmpty) {
                ValidateBlueprint();
            }
            
            tbodyBlueprintStrandTable.all('tr').each( function () {
                var segmentKey = this.one('td').getContent(),
                    strand = this.one('td').next().getContent(),
                    startAbility,
                    startInfo,
                    minItems,
                    maxItems,
                    blueprintWeight,
                    isStrictMax,
                    adaptiveCut,
                    scalar,
                    abilityWeight,
                    precisionTargetNotMetWeight,
                    precisionTargetMetWeight,
                    precisionTarget,
                    isReportingCategory,
                    len;
                len = _dataBpSegStrands.length;
                for (var i = 0; i < len; i++) {
                    if (_dataBpSegStrands[i].SegmentKey == segmentKey && _dataBpSegStrands[i].Strand == strand) {
                        startAbility = _dataBpSegStrands[i].StartAbility;
                        startInfo = _dataBpSegStrands[i].StartInfo;
                        minItems = _dataBpSegStrands[i].MinItems;
                        maxItems = _dataBpSegStrands[i].MaxItems;
                        blueprintWeight = _dataBpSegStrands[i].BlueprintWeight;
                        isStrictMax = _dataBpSegStrands[i].IsStrictMax;
                        adaptiveCut = _dataBpSegStrands[i].AdaptiveCut;
                        scalar = _dataBpSegStrands[i].Scalar;
                        abilityWeight = _dataBpSegStrands[i].AbilityWeight;
                        precisionTargetNotMetWeight = _dataBpSegStrands[i].PrecisionTargetNotMetWeight;
                        precisionTargetMetWeight = _dataBpSegStrands[i].PrecisionTargetMetWeight;
                        precisionTarget = _dataBpSegStrands[i].PrecisionTarget;
                        isReportingCategory = _dataBpSegStrands[i].IsReportingCategory;
                        break;
                    }
                }

                this.removeAttribute('style');
                readOnlyRowSegmentStrand(this, startAbility, startInfo, minItems, maxItems, blueprintWeight, isStrictMax, adaptiveCut, scalar,
                                        abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, isReportingCategory);
            });
        },
        btnCancelBlueprintContentLevel_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintContentLevelTable = Y.one(_strTblBpSegContentLvl).one('tbody'),
                isEmpty = true;

            //show the edit/convert buttons
            Y.one(_strBtnEditBlueprintContentLevel).removeAttribute('style');
            Y.one(_strBtnSaveBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnCancelBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnConvertBlueprintContentLevel).removeAttribute('style');
            Y.one(_strBtnConvertSaveBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnConvertCancelBlueprintContentLevel).setStyle('display', 'none');

            //if there are segments left over to modify and we are canceling these changes, validate the blueprint with the already saved values
            for (k in _modifiedSegments) {
                isEmpty = false;
                break;
            }
            if (!isEmpty) {
                ValidateBlueprint();
            }

            tbodyBlueprintContentLevelTable.all('tr').each( function () {
                var segmentKey = this.one('td').getContent(),
                    contentLevelID = this.one('td').next().getContent(),
                    minItems,
                    maxItems,
                    bpWeight,
                    isStrictMax,
                    isReportingCategory,
                    len;
                len = _dataBpSegContentLvls.length;
                for (var i = 0; i < len; i++) {
                    if (_dataBpSegContentLvls[i].SegmentKey == segmentKey && _dataBpSegContentLvls[i].ContentLevel == contentLevelID) {
                        minItems = _dataBpSegContentLvls[i].MinItems;
                        maxItems = _dataBpSegContentLvls[i].MaxItems;
                        bpWeight = _dataBpSegContentLvls[i].BlueprintWeight;
                        isStrictMax = _dataBpSegContentLvls[i].IsStrictMax;
                        isReportingCategory = _dataBpSegContentLvls[i].IsReportingCategory;
                        break;
                    }
                }

                this.removeAttribute('style');
                readOnlyRowSegmentContentLvl(this, minItems, maxItems, bpWeight, isStrictMax, isReportingCategory);
            });
        },
        btnConvertCancelBlueprintStrandAsContentLevel_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintStrandTable = Y.one(_strTblBpSegStrand).one('tbody'),
                isEmpty = true;
            //hide the save/cancel buttons
            Y.one(_strBtnEditBlueprintStrand).removeAttribute('style');
            Y.one(_strBtnSaveBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnCancelBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnConvertBlueprintStrand).removeAttribute('style');
            Y.one(_strBtnConvertSaveBlueprintStrand).setStyle('display', 'none');
            Y.one(_strBtnConvertCancelBlueprintStrand).setStyle('display', 'none');

            //if there are segments left over to modify and we are canceling these changes, validate the blueprint with the already saved values
            for (k in _modifiedSegments) {
                isEmpty = false;
                break;
            }
            if (!isEmpty) {
                ValidateBlueprint();
            }

            tbodyBlueprintStrandTable.all('tr').each(function () {
                var segmentKey = this.one('td').getContent(),
                    strand = this.one('td').next().getContent(),
                    startAbility,
                    startInfo,
                    minItems,
                    maxItems,
                    blueprintWeight,
                    isStrictMax,
                    adaptiveCut,
                    scalar,
                    abilityWeight,
                    precisionTargetNotMetWeight,
                    precisionTargetMetWeight,
                    precisionTarget,
                    isReportingCategory,
                    len;
                len = _dataBpSegStrands.length;
                for (var i = 0; i < len; i++) {
                    if (_dataBpSegStrands[i].SegmentKey == segmentKey && _dataBpSegStrands[i].Strand == strand) {
                        startAbility = _dataBpSegStrands[i].StartAbility;
                        startInfo = _dataBpSegStrands[i].StartInfo;
                        minItems = _dataBpSegStrands[i].MinItems;
                        maxItems = _dataBpSegStrands[i].MaxItems;
                        blueprintWeight = _dataBpSegStrands[i].BlueprintWeight;
                        isStrictMax = _dataBpSegStrands[i].IsStrictMax;
                        adaptiveCut = _dataBpSegStrands[i].AdaptiveCut;
                        scalar = _dataBpSegStrands[i].Scalar;
                        abilityWeight = _dataBpSegStrands[i].AbilityWeight;
                        precisionTargetNotMetWeight = _dataBpSegStrands[i].PrecisionTargetNotMetWeight;
                        precisionTargetMetWeight = _dataBpSegStrands[i].PrecisionTargetMetWeight;
                        precisionTarget = _dataBpSegStrands[i].PrecisionTarget;
                        isReportingCategory = _dataBpSegStrands[i].IsReportingCategory;
                        break;
                    }
                }

                this.removeAttribute('style');
                readOnlyRowSegmentStrand(this, startAbility, startInfo, minItems, maxItems, blueprintWeight, isStrictMax, adaptiveCut, scalar,
                                        abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget, isReportingCategory);
            });
        },
        btnConvertCancelBlueprintContentLevelAsStrand_Click = function (e) {
            e.preventDefault();

            var tbodyBlueprintContentLevelTable = Y.one(_strTblBpSegContentLvl).one('tbody'),
                isEmpty = true;

            //show the edit/convert buttons
            Y.one(_strBtnEditBlueprintContentLevel).removeAttribute('style');
            Y.one(_strBtnSaveBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnCancelBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnConvertBlueprintContentLevel).removeAttribute('style');
            Y.one(_strBtnConvertSaveBlueprintContentLevel).setStyle('display', 'none');
            Y.one(_strBtnConvertCancelBlueprintContentLevel).setStyle('display', 'none');

            //if there are segments left over to modify and we are canceling these changes, validate the blueprint with the already saved values
            for (k in _modifiedSegments) {
                isEmpty = false;
                break;
            }
            if (!isEmpty) {
                ValidateBlueprint();
            }

            tbodyBlueprintContentLevelTable.all('tr').each(function () {
                var segmentKey = this.one('td').getContent(),
                    contentLevelID = this.one('td').next().getContent(),
                    minItems,
                    maxItems,
                    bpWeight,
                    isStrictMax,
                    isReportingCategory,
                    len;
                len = _dataBpSegContentLvls.length;
                for (var i = 0; i < len; i++) {
                    if (_dataBpSegContentLvls[i].SegmentKey == segmentKey && _dataBpSegContentLvls[i].ContentLevel == contentLevelID) {
                        minItems = _dataBpSegContentLvls[i].MinItems;
                        maxItems = _dataBpSegContentLvls[i].MaxItems;
                        bpWeight = _dataBpSegContentLvls[i].BlueprintWeight;
                        isStrictMax = _dataBpSegContentLvls[i].IsStrictMax;
                        isReportingCategory = _dataBpSegContentLvls[i].IsReportingCategory;
                        break;
                    }
                }

                this.removeAttribute('style');
                readOnlyRowSegmentContentLvl(this, minItems, maxItems, bpWeight, isStrictMax, isReportingCategory);
            });
        },


        ddlSelectTest_Change = function (e) {
            e.preventDefault();

            _testKey = e.currentTarget.get('value');
            getTestBlueprint(_sessionKey, e.currentTarget.get('value'));
        },

        ddlSelectSegment_Change = function (e) {
            e.preventDefault();

            updateBlueprintStrandTable();
            updateBlueprintContentLevelTable();
            //update the ddlSelectStrand
        },

        ddlSelectStrand_Change = function (e) {
            e.preventDefault();

            updateBlueprintContentLevelTable();
        };


        return {
            init: init
        };
    } ();
}, '0.0.1', { requires: ["node", "io", "sessionblueprintitems", "sessionitemselectionparams" ] });

YUI({
    modules: {
        sessionblueprintitems: {
            fullpath: '../scripts/WebSim/SessionBlueprintItems.js',
            require: ['node', 'io']
        },
        sessionitemselectionparams : {
            fullpath: '../scripts/WebSim/SessionItemSelectionParams.js',
            require: ['node', 'io']
        }
    }
}).use("node", "io", "dump", "json-parse", "session_blueprint", "panel", function (Y) {
    var sessionBlueprint = Y.Session_Blueprint;
    sessionBlueprint.init();
});