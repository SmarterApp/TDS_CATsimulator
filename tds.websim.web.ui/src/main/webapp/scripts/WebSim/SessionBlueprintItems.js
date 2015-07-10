YUI.add('sessionblueprintitems', function (Y) {
    Y.SessionBlueprintItems = function () {
        var _strManageTestItem = "#testItemProp",
            _strDivModalContainer = "#modalContainer",
            _strBtnCloseManageTestItem = "#btnCloseTestItemProp",
        //item properties
            _strItemPropDiv = "#itemProp",
            _strItemLblTestID = "#lblItemTestID",
            _strDdlItem = "#editItem-key",
            _strDdlItemStrand = "#editItem-strand",
            _strDdlItemGroupID = "#editItem-groupID",
            _strTblItemProp = "#editItem-table",
            _strBtnItemProp = "#btnShowItemProps",
            _strBtnItemPropText = "#btnShowItemPropsText",
            _strTbItemSearch = "#editItem-search",
            _strBtnItemSearch = "#btnItemSearch",
            _strBtnEditItemProp = "#btnEditEditItem",
            _strBtnSaveItemProp = "#btnSaveEditItem",
            _strBtnCancelItemProp = "#btnCancelEditItem",
            _strDivIsRequiredAll = "#dvIsRequiredAll",
            _strCbIsRequiredAll = "#cbIsRequiredAll",
        //item group properties
            _strGroupPropDiv = "#groupProp",
            _strGroupLblTestID = "#lblGroupTestID",
            _strDdlGroup = "#editGroup-key",
            _strTblGroupProp = "#editGroup-table",
            _strBtnGroupProp = "#btnShowGroupProps",
            _strBtnGroupPropText = "#btnShowGroupPropsText",
            _strTbGroupSearch = "#editGroup-search",
            _strBtnGroupSearch = "#btnGroupSearch",
            _strBtnEditGroupProp = "#btnEditEditGroup",
            _strBtnSaveGroupProp = "#btnSaveEditGroup",
            _strBtnCancelGroupProp = "#btnCancelEditGroup",
        //alter item properties
            _strAlterTestItemPropDiv = "#alterTestItemProp",
            _strDdlAlterItemPropIsActive = "#alter-itemProp-isActive",
            _strDdlAlterItemPropIsRequired = "#alter-itemProp-isRequired",
            _strBtnAlterItemPropSave = "#alter-itemProp-save",
            _strBtnAlterItemPropCancel = "#alter-itemProp-cancel",
        //alter item group properties
            _strAlterTestGroupPropDiv = "#alterGroupProp",
            _strTbAlterGroupPropMaxItems = "#alter-grpProp-maxItems",
            _strBtnAlterGroupPropSave = "#alter-grpProp-save",
            _strBtnAlterGroupPropCancel = "#alter-grpProp-cancel",
        //copy of table rows
            _itemPropsTblRow,
            _groupPropsTblRow,
        //copy of data we have retrieved
            _dataItemProps,
            _dataGroupProps,
        //data needed for edits
            _sessionKey,
            _testKey,
            _isAdaptiveTest,
        //modified items/groups
            _modifiedGroupProps = {},
            _modifiedItemProps = {},
        //loading panel
            _loadingPanel,

        open = function (sessionKey, testKey, isAdaptiveTest, loadingPanel) {
            if ((sessionKey == null || sessionKey === '') || (testKey == null || testKey === '')) {
                alert('Session Key and Test Key required.');
                return;
            }

            _sessionKey = sessionKey;
            _testKey = testKey;
            _isAdaptiveTest = isAdaptiveTest;
            _loadingPanel = loadingPanel;

            //store table rows
            var tempTblTbody = Y.one(_strTblItemProp).one('tbody');
            if (_itemPropsTblRow == null) {
                _itemPropsTblRow = tempTblTbody.one('tr').cloneNode(true);
            }
            tempTblTbody.empty();
            tempTblTbody = Y.one(_strTblGroupProp).one('tbody');
            if (_groupPropsTblRow == null) {
                _groupPropsTblRow = tempTblTbody.one('tr').cloneNode(true);
            }
            tempTblTbody.empty();

            // clear all controls
            Y.one(_strTbGroupSearch).set('value', '');
            Y.one(_strTbItemSearch).set('value', '');

            //populate the controls
            getTestItems(sessionKey, testKey);

            //show the window
            Y.one(_strManageTestItem).setStyle('display', 'block');
            Y.one(_strDivModalContainer).removeAttribute('style');

            Y.one(_strItemLblTestID).set('text', testKey);
            Y.one(_strGroupLblTestID).set('text', testKey);

            // top button event handlers
            Y.on("click", btnItemProp_Click, _strBtnItemProp);
            Y.on("click", btnGroupProp_Click, _strBtnGroupProp);
            // search event handlers
            Y.on("click", btnItemSearch_Click, _strBtnItemSearch);
            Y.on("click", btnGroupSearch_Click, _strBtnGroupSearch);
            // close button
            Y.on("click", btnCloseAlterTestItem_Click, _strBtnCloseManageTestItem);
        },

        getTestItems = function (sessionKey, testKey) {
            callback = {
                on: {
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.responseText);
                        var parsedResponse,
                            dataItemProps = [],
                            dataGroupProps = [],
                            lenItemProps,
                            lenItemGroupProps;

                        try {
                            parsedResponse = Y.JSON.parse(response.responseText);
                        } catch (e) {
                            alert("Failed to parse response.");
                            return;
                        }

                        // check to make sure test items are retrieved
                        if (parsedResponse.status === "failed") {
                            alert(parsedResponse.reason);
                            return;
                        }

                        //grab the lengths of itemprops and itemgroupprops
                        lenItemProps = parsedResponse.ItemProperties.length;
                        lenItemGroupProps = parsedResponse.ItemGroupProperties.length;

                        for (var i = 0; i < lenItemProps; i++) {
                            dataItemProps.push(parsedResponse.ItemProperties[i]);
                        }

                        for (var i = 0; i < lenItemGroupProps; i++) {
                            dataGroupProps.push(parsedResponse.ItemGroupProperties[i]);
                        }

                        // deep copy the data to the module
                        _dataItemProps = Y.clone(dataItemProps);
                        _dataGroupProps = Y.clone(dataGroupProps);

                        // item properties controls
                        updateItemPropsDropdown();
                        updateItemPropsTable(Y.one(_strDdlItem).get('value'));
                        // item group properties controls
                        updateGroupPropsDropdown();
                        updateGroupPropsTable(Y.one(_strDdlGroup).get('value'));

                        // add eventhandlers for controls that have been populated by now
                        Y.on("change", ddlSegmentKey_Change, _strDdlItem);
                        Y.on("change", ddlItemStrand_Change, _strDdlItemStrand);
                        Y.on("change", ddlItemGroupID_Change, _strDdlItemGroupID);
                        Y.on("change", ddlGroupKey_Change, _strDdlGroup);
                        // add eventhandlers for edit/save/cancel items
                        Y.on("click", btnEditItemProp_Click, _strBtnEditItemProp);
                        Y.on("click", btnSaveItemProp_Click, _strBtnSaveItemProp);
                        Y.on("click", btnCancelItemProp_Click, _strBtnCancelItemProp);
                        // add eventhandlers for edit/save/cancel groups
                        Y.on("click", btnEditGroupProp_Click, _strBtnEditGroupProp);
                        Y.on("click", btnSaveGroupProp_Click, _strBtnSaveGroupProp);
                        Y.on("click", btnCancelGroupProp_Click, _strBtnCancelGroupProp);
                    },
                    failure: function (id, response) {
                        alert('Failed to retrieve Item and ItemGroup Properties failed.');
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetTestItems?sessionkey=" + sessionKey + "&testkey=" + testKey, callback);
        },

        //populate dropdowns
        updateItemPropsDropdown = function () {
            var ddlItemProps = Y.one(_strDdlItem),
                lenItemProps = _dataItemProps.length,
                temp = {}, i;

            ddlItemProps.empty();
            ddlItemProps.appendChild(Y.Node.create('<option value=\"ALL\">ALL</option>'));
            for (i = 0; i < lenItemProps; i++) {
                if (temp[_dataItemProps[i].SegmentKey] == null) {
                    temp[_dataItemProps[i].SegmentKey] = "used";
                    ddlItemProps.appendChild(Y.Node.create('<option value=\"' + _dataItemProps[i].SegmentKey + '\">' + _dataItemProps[i].SegmentKey + '</option>'));
                }
            }
            updateItemStrandDropdown();
            updateItemGroupIDDropdown();
        },
        updateItemStrandDropdown = function () {
            var ddlItemProps = Y.one(_strDdlItem),
                ddlItemStrand = Y.one(_strDdlItemStrand),
                lenItemProps = _dataItemProps.length,
                temp = {}, i;

            ddlItemStrand.empty();
            ddlItemStrand.appendChild(Y.Node.create('<option value=\"ALL\">ALL</option>'));
            for (i = 0; i < lenItemProps; i++) {
                if (temp[_dataItemProps[i].Strand] == null && (_dataItemProps[i].SegmentKey == ddlItemProps.get('value') || ddlItemProps.get('value') == "ALL")) {
                    temp[_dataItemProps[i].Strand] = "used";
                    ddlItemStrand.appendChild(Y.Node.create('<option value=\"' + _dataItemProps[i].Strand + '\">' + _dataItemProps[i].Strand + '</option>'));
                }
            }
        },
        updateItemGroupIDDropdown = function () {
            var ddlItemProps = Y.one(_strDdlItem),
                ddlItemStrand = Y.one(_strDdlItemStrand),
                ddlItemGroupID = Y.one(_strDdlItemGroupID),
                lenItemProps = _dataItemProps.length,
                temp = {}, i;

            ddlItemGroupID.empty();
            ddlItemGroupID.appendChild(Y.Node.create('<option value=\"ALL\">ALL</option>'));
            for (i = 0; i < lenItemProps; i++) {
                if (temp[_dataItemProps[i].GroupID] == null && (_dataItemProps[i].SegmentKey == ddlItemProps.get('value') || ddlItemProps.get('value') == "ALL")) {
                    temp[_dataItemProps[i].GroupID] = "used";
                    ddlItemGroupID.appendChild(Y.Node.create('<option value=\"' + _dataItemProps[i].GroupID + '\">' + _dataItemProps[i].GroupID + '</option>'));
                }
            }
        },
        updateGroupPropsDropdown = function () {
            var ddlGroupProps = Y.one(_strDdlGroup),
                lenGroupProps = _dataGroupProps.length,
                temp = {};

            ddlGroupProps.empty();
            for (var i = 0; i < lenGroupProps; i++) {
                if (temp[_dataGroupProps[i].SegmentKey] == null) {
                    temp[_dataGroupProps[i].SegmentKey] = "used";
                    ddlGroupProps.appendChild(Y.Node.create('<option value=\"' + _dataGroupProps[i].SegmentKey + '\">' + _dataGroupProps[i].SegmentKey + '</option>'));
                }
            }
        },

        //populate tables
        updateItemPropsTable = function (itemKeyToSearch, isSearch) {
            var tableTbody = Y.one(_strTblItemProp).one('tbody'),
                lenItemProps = _dataItemProps.length,
                ddlItemVal = Y.one(_strDdlItem).get('value'),
                ddlItemStrandVal = Y.one(_strDdlItemStrand).get('value'),
                ddlItemGroupIDVal = Y.one(_strDdlItemGroupID).get('value'),
                tableTrow,
                tableTcols,
                segmentKey,
                strand,
                groupID,
                itemKey,
                isActive,
                isRequired,
                isFieldTest;

            if (_isAdaptiveTest === true) {
                Y.one(_strBtnEditItemProp).removeAttribute('style');
            }
            else {
                Y.one(_strBtnEditItemProp).setStyle('display', 'none');
            }
            Y.one(_strBtnSaveItemProp).setStyle('display', 'none');
            Y.one(_strBtnCancelItemProp).setStyle('display', 'none');


            tableTbody.empty();
            for (var i = 0; i < lenItemProps; i++) {
                //if segmentkey matches dropdown value && (is a item key matching search || is not a search)
                if ((_dataItemProps[i].SegmentKey === ddlItemVal || ddlItemVal === "ALL")
                    && (_dataItemProps[i].Strand === ddlItemStrandVal || ddlItemStrandVal === "ALL")
                    && (_dataItemProps[i].GroupID === ddlItemGroupIDVal || ddlItemGroupIDVal === "ALL")
                    && ((isSearch && _dataItemProps[i].ItemKey.indexOf(itemKeyToSearch) >= 0) || !isSearch)) {

                    segmentKey = _dataItemProps[i].SegmentKey;
                    strand = _dataItemProps[i].Strand;
                    groupID = _dataItemProps[i].GroupID;
                    itemKey = _dataItemProps[i].ItemKey;
                    isActive = _dataItemProps[i].IsActive;
                    isRequired = _dataItemProps[i].IsRequired;
                    isFieldTest = _dataItemProps[i].IsFieldTest;

                    tableTrow = _itemPropsTblRow.cloneNode(true);
                    tableTcols = tableTrow.all('td');

                    tableTcols.item(0).setContent(segmentKey);
                    tableTcols.item(1).setContent(strand);
                    tableTcols.item(2).setContent(groupID);
                    tableTcols.item(3).setContent(itemKey);
                    tableTcols.item(4).setContent(isActive);
                    tableTcols.item(5).setContent(isRequired);
                    tableTcols.item(6).setContent(isFieldTest);

                    tableTbody.appendChild(tableTrow);
                }
            }
        },
        updateGroupPropsTable = function (groupIDToSearch, isSearch) {
            var tableTbody = Y.one(_strTblGroupProp).one('tbody'),
                lenGroupProps = _dataGroupProps.length,
                ddlGropuVal = Y.one(_strDdlGroup).get('value'),
                tableTrow,
                tableTcols,
                segmentKey,
                groupID,
                maxItems,
                activeItems;

            if (_isAdaptiveTest === true) {
                Y.one(_strBtnEditGroupProp).removeAttribute('style');
            }
            else {
                Y.one(_strBtnEditGroupProp).setStyle('display', 'none');
            }
            Y.one(_strBtnSaveGroupProp).setStyle('display', 'none');
            Y.one(_strBtnCancelGroupProp).setStyle('display', 'none');

            tableTbody.empty();
            for (var i = 0; i < lenGroupProps; i++) {
                if ((_dataGroupProps[i].SegmentKey === ddlGropuVal)
                    && ((isSearch && _dataGroupProps[i].GroupID.indexOf(groupIDToSearch) >= 0) || !isSearch)) {

                    segmentKey = _dataGroupProps[i].SegmentKey;
                    groupID = _dataGroupProps[i].GroupID;
                    maxItems = _dataGroupProps[i].MaxItems;
                    activeItems = _dataGroupProps[i].ActiveItems;

                    tableTrow = _groupPropsTblRow.cloneNode(true);
                    tableTcols = tableTrow.all('td');

                    tableTcols.item(0).setContent(segmentKey);
                    tableTcols.item(1).setContent(groupID);
                    tableTcols.item(2).setContent(maxItems);
                    tableTcols.item(3).setContent(activeItems);

                    tableTbody.appendChild(tableTrow);
                }
            }
        },

        // methods to convert rows to editable
        editRowItemProp = function (tableTrow) {
            var tableTcols = tableTrow.all('td');

            if (tableTcols.item(4).getContent() === "True") {
                tableTcols.item(4).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(4).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }
            if (tableTcols.item(5).getContent() == "True") {
                tableTcols.item(5).setContent(Y.Node.create('<input type=\"checkbox\" checked />'));
            } else {
                tableTcols.item(5).setContent(Y.Node.create('<input type=\"checkbox\" />'));
            }

            tableTrow.all('input').on('change', function (e) {
                _modifiedItemProps[tableTcols.item(3).getContent()] = "modified";
                tableTrow.setStyle('backgroundColor', '#ff9999');
            });
        },

        editRowGroupProp = function (tableTrow) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(2).setContent(Y.Node.create('<input type=\"text\" value=\"' + tableTcols.item(2).getContent() + ' \" />'));

            tableTrow.all('input').on('change', function (e) {
                _modifiedGroupProps[tableTcols.item(1).getContent()] = "modified";
                tableTrow.setStyle('backgroundColor', '#ff9999');
            });
        },

        readOnlyRowItemProp = function (tableTrow, IsActive, IsRequired) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(4).setContent(IsActive);
            tableTcols.item(5).setContent(IsRequired);

            delete _modifiedItemProps[tableTcols.item(3).getContent()];
        },

        readOnlyRowGroupProp = function (tableTrow, maxItems) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(2).setContent(maxItems);

            delete _modifiedGroupProps[tableTcols.item(1).getContent()];
        },

        saveAlterItemProp = function (sessionKey, testKey, tableTrow) {
            var tableTcols = tableTrow.all('td'),
                segmentKey = tableTcols.item(0).getContent(),
                itemKey = tableTcols.item(3).getContent(),
                isActive = tableTcols.item(4).one('input').get('checked') ? "True" : "False",
                isRequired = tableTcols.item(5).one('input').get('checked') ? "True" : "False",

            cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey + "&testkey=" + testKey + "&segmentkey=" + segmentKey + "&itemkey=" + itemKey
                    + "&isactive=" + isActive + "&isrequired=" + isRequired,
                on: {
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText),
                            k, isEmpty = true;
                        if (response.status === "success") {
                            // delete the key from the hashtable and hide the highlight
                            delete _modifiedItemProps[itemKey];
                            tableTrow.removeAttribute('style');
                            readOnlyRowItemProp(tableTrow, isActive, isRequired);

                            // if all modified itemproperties have been saved, show only the edit button
                            for (k in _modifiedItemProps) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                // display only edit button
                                Y.one(_strBtnEditItemProp).removeAttribute('style');
                                Y.one(_strBtnSaveItemProp).setStyle('display', 'none');
                                Y.one(_strBtnCancelItemProp).setStyle('display', 'none');

                                // hide the checkAllIsRequired control
                                Y.one(_strDivIsRequiredAll).setStyle('display', 'none');
                                Y.one(_strDivIsRequiredAll).detach();

                                // validate the blueprint as well
                                ValidateBlueprint();
                            }

                            //update local copy of the data
                            var len = _dataItemProps.length;
                            for (var i = 0; i < len; i++) {
                                if (_dataItemProps[i].SegmentKey == segmentKey && _dataItemProps[i].ItemKey == itemKey) {
                                    _dataItemProps[i].IsActive = isActive;
                                    _dataItemProps[i].IsRequired = isRequired;
                                    break;
                                }
                            }
                        }
                        else {
                            alert("Response status: " + response.status + " with reason: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Async call to Save Item Property failed!");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/AlterItemProperties", cfg);
        },

        saveAlterGroupProp = function (sessionKey, testKey, tableTrow) {
            var tableTcols = tableTrow.all('td'),
                segmentKey = tableTcols.item(0).getContent(),
                groupID = tableTcols.item(1).getContent(),
                maxItems = tableTcols.item(2).one('input').get('value'),

            cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey + "&testkey=" + testKey + "&segmentkey=" + segmentKey
                    + "&groupid=" + groupID + "&maxitems=" + maxItems,
                on: {
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText),
                            isEmpty = true, k;
                        if (response.status === "success") {
                            // delete the key from the hashtable and hide the highlight
                            delete _modifiedGroupProps[groupID];
                            tableTrow.removeAttribute('style');
                            readOnlyRowGroupProp(tableTrow, maxItems);

                            // if all modified itemproperties have been saved, show only the edit button
                            for (k in _modifiedGroupProps) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                // display only edit button
                                Y.one(_strBtnEditGroupProp).removeAttribute('style');
                                Y.one(_strBtnSaveGroupProp).setStyle('display', 'none');
                                Y.one(_strBtnCancelGroupProp).setStyle('display', 'none');

                                // validate the blueprint as well
                                ValidateBlueprint();
                            }

                            //update local copy of data
                            var len = _dataGroupProps.length;
                            for (var i = 0; i < len; i++) {
                                if (_dataGroupProps[i].SegmentKey == segmentKey && _dataGroupProps[i].GroupID == groupID) {
                                    _dataGroupProps[i].MaxItems = maxItems;
                                    break;
                                }
                            }
                        } else {
                            alert("Response status: " + response.status + " with reason: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Async call to Save Item Property failed!");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/AlterItemGroupProperties", cfg);
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

                            var url = document.URL, newUrl,
                                lenPathName, pathNames, newPathName = '';

                            //let's rebuild the correct url!
                            pathNames = window.location.pathname.substring(1).split('/');
                            lenPathName = pathNames.length;

                            newPathName = '/' + pathNames[0] + "/Reports/BlueprintValidation.aspx?&sessionkey=" + _sessionKey;

                            window.open(newPathName);
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/ValidateBlueprint", cfg);
        },

        close = function () {
            Y.one(_strManageTestItem).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');

            //detach events
            Y.one(_strBtnCloseManageTestItem).detach();
            Y.one(_strBtnGroupProp).detach();
            Y.one(_strBtnGroupSearch).detach();
            Y.one(_strBtnItemProp).detach();
            Y.one(_strBtnItemSearch).detach();
            Y.one(_strDdlGroup).detach();
            Y.one(_strDdlItem).detach();
            Y.one(_strDdlItemStrand).detach();
            Y.one(_strDdlItemGroupID).detach();

            Y.one(_strBtnEditGroupProp).detach();
            Y.one(_strBtnSaveGroupProp).detach();
            Y.one(_strBtnCancelGroupProp).detach();

            Y.one(_strBtnEditItemProp).detach();
            Y.one(_strBtnSaveItemProp).detach();
            Y.one(_strBtnCancelItemProp).detach();

            // call the event handlers for the cancel buttons. we want to make sure that the table gets set to the correct ui state before closing.  hack-y? maybe.
            btnCancelItemProp_Click();
            btnCancelGroupProp_Click();

            //fire update edit event
            Y.fire('ManageTestBlueprint:refresh', {
            //pass data here if needed
        });
    },

    //event handlers

    //display itemprop/groupprop
        btnItemProp_Click = function (e) {
            e.preventDefault();

            Y.one(_strItemPropDiv).removeAttribute('style');
            Y.one(_strGroupPropDiv).setStyle('display', 'none');

            //display the text instead of hyperlink
            Y.one(_strBtnItemProp).setStyle('display', 'none');
            Y.one(_strBtnItemPropText).removeAttribute('style');

            Y.one(_strBtnGroupProp).removeAttribute('style');
            Y.one(_strBtnGroupPropText).setStyle('display', 'none');
        },
        btnGroupProp_Click = function (e) {
            e.preventDefault();

            Y.one(_strGroupPropDiv).removeAttribute('style');
            Y.one(_strItemPropDiv).setStyle('display', 'none');

            Y.one(_strBtnItemProp).removeAttribute('style');
            Y.one(_strBtnItemPropText).setStyle('display', 'none');

            Y.one(_strBtnGroupProp).setStyle('display', 'none');
            Y.one(_strBtnGroupPropText).removeAttribute('style');
        },

    //change dropdowns
        ddlSegmentKey_Change = function (e) {
            // clear the itemkey search input
            Y.one(_strTbItemSearch).set('value', '');
            // update dropdowns
            updateItemStrandDropdown();
            updateItemGroupIDDropdown();
            // update table
            updateItemPropsTable(e.currentTarget.get('value'), false);
        },
        ddlItemStrand_Change = function (e) {
            // clear the itemkey search input
            Y.one(_strTbItemSearch).set('value', '');
            // update table
            updateItemPropsTable();
        },
        ddlItemGroupID_Change = function (e) {
            // clear the itemkey search input
            Y.one(_strTbItemSearch).set('value', '');
            // update table
            updateItemPropsTable();
        },
        ddlGroupKey_Change = function (e) {
            // clear the itemkey search input
            Y.one(_strTbGroupSearch).set('value', '');
            // update table
            updateGroupPropsTable(e.currentTarget.get('value'), false);
        },

    //search button clicked
        btnItemSearch_Click = function (e) {
            e.preventDefault();

            updateItemPropsTable(Y.one(_strTbItemSearch).get('value'), true);
        },
        btnGroupSearch_Click = function (e) {
            e.preventDefault();

            updateGroupPropsTable(Y.one(_strTbGroupSearch).get('value'), true);
        },

    //close link on top
        btnCloseAlterTestItem_Click = function (e) {
            e.preventDefault();

            close();
        },

    // event handlers for edit/save/cancel properties
        btnEditItemProp_Click = function (e) {
            e.preventDefault();

            var tbodyItemPropTable = Y.one(_strTblItemProp).one('tbody');

            Y.one(_strBtnEditItemProp).setStyle('display', 'none');
            Y.one(_strBtnSaveItemProp).removeAttribute('style');
            Y.one(_strBtnCancelItemProp).removeAttribute('style');

            // show the checkAll buttons
            Y.one(_strDivIsRequiredAll).removeAttribute('style');

            // add event listener
            Y.on('change', function (e) {
                var isChecked = e.target.get('checked');
                tbodyItemPropTable.all('tr').each(function () {
                    var tCols = this.all('td');
                    tCols.item(5).one('input').set('checked', isChecked);
                    _modifiedItemProps[tCols.item(3).getContent()] = "modified";
                });
            });

            tbodyItemPropTable.all('tr').each(function () {
                editRowItemProp(this);
            });
        },
        btnEditGroupProp_Click = function (e) {
            e.preventDefault();

            var tbodyGroupPropTable = Y.one(_strTblGroupProp).one('tbody');

            Y.one(_strBtnEditGroupProp).setStyle('display', 'none');
            Y.one(_strBtnSaveGroupProp).removeAttribute('style');
            Y.one(_strBtnCancelGroupProp).removeAttribute('style');

            tbodyGroupPropTable.all('tr').each(function () {
                editRowGroupProp(this);
            });
        },

        btnSaveItemProp_Click = function (e) {
            e.preventDefault();

            var cfgDeleteSessOppData = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status == "success") {
                            // if clear was successful, alter item props
                            Y.one(_strTblItemProp).one('tbody').all('tr').each(function () {
                                var currItemKey = this.one('td').next().next().next().getContent(), k;

                                if (_modifiedItemProps[currItemKey]) {
                                    saveAlterItemProp(_sessionKey, _testKey, this);
                                } else {
                                    var itemKey = this.one('td').next().next().next().getContent(),
                                        isActive,
                                        isRequired,
                                        isFieldTest,
                                        len = _dataItemProps.length, i;
                                    for (i = 0; i < len; i++) {
                                        if (_dataItemProps[i].ItemKey === itemKey) {
                                            isActive = _dataItemProps[i].IsActive;
                                            isRequired = _dataItemProps[i].IsRequired;
                                            break;
                                        }
                                    }

                                    readOnlyRowItemProp(this, isActive, isRequired);
                                }
                            });
                        } else {
                            alert("Failed with reason: " + response.reason);
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };

            // notify user that all opportunity data will be cleared
            if (!confirm("All opportunity data will be cleared for the test. Continue?")) {
                return;
            }

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionOppData", cfgDeleteSessOppData);
        },
        btnSaveGroupProp_Click = function (e) {
            e.preventDefault();

            var cfgDeleteSessOppData = {
                method: "POST",
                data: "sessionkey=" + _sessionKey + "&testkey=" + _testKey,
                on: {
                    complete: function (id, xhr, args) {
                        _loadingPanel.hide();
                    },
                    success: function (id, xhr) {
                        var response = Y.JSON.parse(xhr.responseText);
                        if (response.status == "success") {
                            // if clear was successful, alter item props
                            Y.one(_strTblGroupProp).one('tbody').all('tr').each(function () {
                                var currGroupKey = this.one('td').next().getContent(), k;

                                if (_modifiedGroupProps[currGroupKey]) {
                                    saveAlterGroupProp(_sessionKey, _testKey, this);
                                } else {
                                    var groupKey = this.one('td').next().getContent(),
                                        maxItems,
                                        len = _dataItemProps.length, i;
                                    for (i = 0; i < len; i++) {
                                        if (_dataGroupProps[i].GroupID === groupKey) {
                                            maxItems = _dataGroupProps[i].MaxItems;
                                            break;
                                        }
                                    }

                                    readOnlyRowGroupProp(this, maxItems);
                                }
                            });
                        } else {
                            alert("Failed with reason: " + response.reason);
                        }
                    },
                    failure: function (id, xhr) {
                        alert("Failed to Delete Session Data");
                    }
                }
            };

            // notify user that all opportunity data will be cleared
            if (!confirm("All opportunity data will be cleared for the test. Continue?")) {
                return;
            }

            _loadingPanel.show();
            Y.io("Services/WebSimXHR.ashx/DeleteSessionOppData", cfgDeleteSessOppData);
        },

        btnCancelItemProp_Click = function (e) {
            // this is called on close(), so make sure e is not undefined
            if (e != null)
                e.preventDefault();

            var tbodyItemPropTable = Y.one(_strTblItemProp).one('tbody'),
                isEmpty = true, k;

            //hide the save/cancel buttons
            Y.one(_strBtnEditItemProp).removeAttribute('style');
            Y.one(_strBtnSaveItemProp).setStyle('display', 'none');
            Y.one(_strBtnCancelItemProp).setStyle('display', 'none');

            // hide the checkAllIsRequired control
            Y.one(_strDivIsRequiredAll).setStyle('display', 'none');
            Y.one(_strCbIsRequiredAll).detach();

            // if no segments left over to modify, we are cancelling these changes and validating changes that were made
            for (k in _modifiedItemProps) {
                isEmpty = false;
                break;
            }
            if (!isEmpty) {
                ValidateBlueprint();
            }

            tbodyItemPropTable.all('tr').each(function () {
                var itemKey = this.one('td').next().next().next().getContent(),
                    isActive,
                    isRequired,
                    isFieldTest,
                    len = _dataItemProps.length, i;
                for (i = 0; i < len; i++) {
                    if (_dataItemProps[i].ItemKey === itemKey) {
                        isActive = _dataItemProps[i].IsActive;
                        isRequired = _dataItemProps[i].IsRequired;
                    }
                }

                this.removeAttribute('style');
                readOnlyRowItemProp(this, isActive, isRequired);
            });
        },
        btnCancelGroupProp_Click = function (e) {
            // this is called on close(), so make sure e is not undefined
            if (e != null)
                e.preventDefault();

            var tbodyGroupPropTable = Y.one(_strTblGroupProp).one('tbody'),
                isEmpty = true, k;

            // hide the save/cancel buttons
            Y.one(_strBtnEditGroupProp).removeAttribute('style');
            Y.one(_strBtnSaveGroupProp).setStyle('display', 'none');
            Y.one(_strBtnCancelGroupProp).setStyle('display', 'none');

            // if no segments left over to modify, we are cancelling these changes...etc
            for (k in _modifiedGroupProps) {
                isEmpty = false;
                break;
            }
            if (!isEmpty) {
                // hide the save/cancel buttons
                ValidateBlueprint();
            }

            tbodyGroupPropTable.all('tr').each(function () {
                var groupKey = this.one('td').next().getContent(),
                    maxItems,
                    len = _dataGroupProps.length, i;
                for (i = 0; i < len; i++) {
                    if (_dataGroupProps[i].GroupID === groupKey) {
                        maxItems = _dataGroupProps[i].MaxItems;
                        break;
                    }
                }

                this.removeAttribute('style');
                readOnlyRowGroupProp(this, maxItems);
            });
        };

    return {
        open: open
    }
} ();
}, '0.0.1', { requires: ["node", "io"] });