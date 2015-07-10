YUI.add('sessionitemselectionparams', function (Y) {
    Y.SessionItemSelectionParams = function () {
        var _strManageItemSelectionParams = "#itemSelectionParams",
            _strDivModalContainer = "#modalContainer",
            _strWindowCloseEditParams = "#btnCloseEditParams",
            _strLblTestID = "#lblTestID",
            _strBtnEditParam = "#btnEditParam",
            _strBtnSaveParam = "#btnSaveParam",
            _strBtnCancelEditParam = "#btnCancelEditParam",
            _strEditParamTable = "#editParamTable",

            _editParamTblRow,
            _dataItemSelectionParams,
            _modifiedItemSelectionParams = {},
            _loadingPanel,
            _sessionKey,
            _testKey,
            _currentSelectionAlgorithm,
            _isEditable,

        open = function (sessionKey, testKey, currentSelectionAlgorithm, loadingPanel) {
            if ((sessionKey == null || sessionKey === '') || (testKey == null || testKey === '')) {
                alert('Session Key and Test Key required.');
                return;
            }

            _sessionKey = sessionKey;
            _testKey = testKey;
            _currentSelectionAlgorithm = currentSelectionAlgorithm;
            _isEditable = false;
            if (_currentSelectionAlgorithm === "adaptive2" || _currentSelectionAlgorithm === "adaptive") {
                _isEditable = true;
            }            
            _loadingPanel = loadingPanel;

            var tempTblTbody = Y.one(_strEditParamTable).one('tbody');
            if (_editParamTblRow == null) {
                _editParamTblRow = tempTblTbody.one('tr').cloneNode(true);
            }

            getItemSelectionParameters(sessionKey, testKey);

            Y.one(_strLblTestID).set('text', testKey);
            Y.on("click", btnWindowCloseEditParams_Click, _strWindowCloseEditParams);

            Y.one(_strManageItemSelectionParams).setStyle('display', 'block');
            Y.one(_strDivModalContainer).removeAttribute('style');
        },

        getItemSelectionParameters = function (sessionKey, testKey) {
            callback = {
                on: {
                    success: function (id, response) {
                        Y.log("RAW JSON DATA: " + response.responseText);
                        var parsedResponse,
                            itemSelectionParams = [],
                            lenItemSelectionParams;

                        try {
                            parsedResponse = Y.JSON.parse(response.responseText);
                        } catch (e) {
                            alert("Failed to parse response.");
                            return;
                        }

                        if (parsedResponse.status === "failed") {
                            alert(parsedResponse.reason);
                            return;
                        }

                        lenItemSelectionParams = parsedResponse.length;

                        for (var i = 0; i < lenItemSelectionParams; i++) {
                            itemSelectionParams.push(parsedResponse[i]);
                        }

                        _dataItemSelectionParams = Y.clone(itemSelectionParams);

                        updateItemSelectionParamsTable();

                        Y.on("click", btnEditParam_Click, _strBtnEditParam);
                        Y.on("click", btnSaveParam_Click, _strBtnSaveParam);
                        Y.on("click", btnCancelEditParam_Click, _strBtnCancelEditParam);
                    },
                    failure: function (id, response) {
                        alert('Failed to retrieve item selection parameters.');
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/GetItemSelectionParameters?sessionkey=" + sessionKey + "&testkey=" + testKey, callback);
        },

        btnWindowCloseEditParams_Click = function () {
            Y.one(_strManageItemSelectionParams).setStyle('display', 'none');
            Y.one(_strDivModalContainer).setStyle('display', 'none');

            Y.one(_strWindowCloseEditParams).detach();

            Y.one(_strBtnEditParam).detach();
            Y.one(_strBtnSaveParam).detach();
            Y.one(_strBtnCancelEditParam).detach();

            Y.fire('ManageTestBlueprint:refresh', {});
        },

        updateItemSelectionParamsTable = function () {
            var tableTbody = Y.one(_strEditParamTable).one('tbody'),
                lenItemSelectionParams = _dataItemSelectionParams.length,
                tableTrow,
                tableTcols,
                segmentKey,
                bpElementID,
                paramName,
                paramValue,
                label;

            if (_isEditable === true) {
                Y.one(_strBtnEditParam).removeAttribute('style');
            }
            else {
                Y.one(_strBtnEditParam).setStyle('display', 'none');
            }
            Y.one(_strBtnSaveParam).setStyle('display', 'none');
            Y.one(_strBtnCancelEditParam).setStyle('display', 'none');

            tableTbody.empty();
            for (var i = 0; i < lenItemSelectionParams; i++) {
            	segmentKey = _dataItemSelectionParams[i].SegmentKey;
                bpElementID = _dataItemSelectionParams[i].BpElementID;
                paramName = _dataItemSelectionParams[i].ParamName;
                paramValue = _dataItemSelectionParams[i].ParamValue;
                label = _dataItemSelectionParams[i].Label;

                tableTrow = _editParamTblRow.cloneNode(true);
                tableTcols = tableTrow.all('td');

                tableTcols.item(0).setContent(segmentKey);
                tableTcols.item(1).setContent(bpElementID);
                tableTcols.item(2).setContent(paramName);
                tableTcols.item(3).setContent(paramValue);
                tableTcols.item(4).setContent(label);

                tableTbody.appendChild(tableTrow);
            }
        },

        btnEditParam_Click = function (e) {
            e.preventDefault();

            var tbodyEditParamTable = Y.one(_strEditParamTable).one('tbody');

            Y.one(_strBtnEditParam).setStyle('display', 'none');
            Y.one(_strBtnSaveParam).removeAttribute('style');
            Y.one(_strBtnCancelEditParam).removeAttribute('style');
            Y.one(_strBtnEditParam).focus();

            tbodyEditParamTable.all('tr').each(function () {
                editRowItemSelectionParam(this);
            });
        },

        editRowItemSelectionParam = function (tableTrow) {
            var tableTcols = tableTrow.all('td');
            tableTcols.item(3).setContent(Y.Node.create('<input type=\"text\" size=\"15\" value=\"' + tableTcols.item(3).getContent() + '\" maxlength=\"20\" />'));

            tableTrow.all('input').on('change', function (e) {
                if (tableTcols.item(0).getContent() in _modifiedItemSelectionParams == false) {
                    _modifiedItemSelectionParams[tableTcols.item(0).getContent()] = {};
                }
                if (tableTcols.item(1).getContent() in _modifiedItemSelectionParams[tableTcols.item(0).getContent()] == false) {
                    _modifiedItemSelectionParams[tableTcols.item(0).getContent()][tableTcols.item(1).getContent()] = {};
                }
                _modifiedItemSelectionParams[tableTcols.item(0).getContent()][tableTcols.item(1).getContent()][tableTcols.item(2).getContent()] = "modified";
                tableTrow.setStyle('backgroundColor', '#ff9999');
            });
        },

        btnSaveParam_Click = function (e) {
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
                            Y.one(_strEditParamTable).one('tbody').all('tr').each(function () {
                                var segmentKey = this.one('td').getContent(),
                                    bpElementID = this.one('td').next().getContent(),
                                    paramName = this.one('td').next().next().getContent(),
                                    k, i, len = _dataItemSelectionParams.length; 

                                if (_modifiedItemSelectionParams[segmentKey] && 
                                		_modifiedItemSelectionParams[segmentKey][bpElementID] && 
                                		_modifiedItemSelectionParams[segmentKey][bpElementID][paramName]) {
                                    var bpElementType = "";
                                    for (i = 0; i < len; i++) {
                                        if (_dataItemSelectionParams[i].SegmentKey === segmentKey && 
                                        	_dataItemSelectionParams[i].BpElementID === bpElementID &&
                                            _dataItemSelectionParams[i].ParamName === paramName) {
                                            bpElementType = _dataItemSelectionParams[i].BpElementType;
                                            break;
                                        }
                                    }
                                    saveAlterItemSelectionParam(_sessionKey, _testKey, segmentKey, bpElementType, this);
                                } else {
                                    var paramValue;
                                    for (i = 0; i < len; i++) {
                                        if (_dataItemSelectionParams[i].SegmentKey === segmentKey &&
                                        	_dataItemSelectionParams[i].BpElementID === bpElementID &&
                                            _dataItemSelectionParams[i].ParamName === paramName) {
                                            paramValue = _dataItemSelectionParams[i].ParamValue;
                                            break;
                                        }
                                    }
                                    readOnlyRowItemSelectionParam(this, paramValue);
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

        saveAlterItemSelectionParam = function (sessionKey, testKey, segmentKey, bpElementType, tableTrow) {
            var tableTcols = tableTrow.all('td'),
                selectionAlgorithm = _currentSelectionAlgorithm,
                elementType = bpElementType, 
                segmentKey = tableTcols.item(0).getContent(),
                bpElementID = tableTcols.item(1).getContent(),
                paramName = tableTcols.item(2).getContent(),
                paramValue = tableTcols.item(3).one('input').get('value'),
            
            cfg = {
                method: "POST",
                data: "sessionkey=" + sessionKey +
                      "&testkey=" + testKey +
                      "&segmentkey=" + segmentKey +
                      "&selectionAlgorithm=" + selectionAlgorithm +
                      "&elementType=" + elementType +
                      "&bpElementID=" + bpElementID +
                      "&paramName=" + paramName +
                      "&paramValue=" + paramValue,
                on: {
                    success: function (id, xhr, arguments) {
                        var response = Y.JSON.parse(xhr.responseText),
                            k, isEmpty = true;
                        if (response.status === "success") {
                            delete _modifiedItemSelectionParams[segmentKey][bpElementID][paramName];                            
                            if (Object.keys(_modifiedItemSelectionParams[segmentKey][bpElementID]).length == 0) {
                                delete _modifiedItemSelectionParams[segmentKey][bpElementID];
                            }
                            if (Object.keys(_modifiedItemSelectionParams[segmentKey]).length == 0) {
                                delete _modifiedItemSelectionParams[segmentKey];
                            }
                            tableTrow.removeAttribute('style');
                            readOnlyRowItemSelectionParam(tableTrow, paramValue);

                            for (k in _modifiedItemSelectionParams) {
                                isEmpty = false;
                                break;
                            }
                            if (isEmpty) {
                                Y.one(_strBtnEditParam).removeAttribute('style');
                                Y.one(_strBtnSaveParam).setStyle('display', 'none');
                                Y.one(_strBtnCancelEditParam).setStyle('display', 'none');
                                // TODO: Validate 
                            }
                            var len = _dataItemSelectionParams.length;
                            for (var i = 0; i < len; i++) {
                                if (_dataItemSelectionParams[i].SegmentKey === segmentKey &&
                                		_dataItemSelectionParams[i].BpElementID === bpElementID && 
                                		_dataItemSelectionParams[i].ParamName === paramName) {
                                    _dataItemSelectionParams[i].ParamValue = paramValue;
                                    break;
                                }
                            }
                        }
                        else {
                            alert("Response status: " + response.status + " with reason: " + response.reason);
                        }
                    },
                    failure: function (id, response) {
                        alert("Async call to Save Item Selection Parameter failed!");
                    }
                }
            };

            Y.io("Services/WebSimXHR.ashx/AlterItemSelectionParameter", cfg);
        },

        btnCancelEditParam_Click = function (e) {
            if (e != null)
                e.preventDefault();
            var tbodyEditParamTable = Y.one(_strEditParamTable).one('tbody'),
                isEmpty = true,
                k;

            Y.one(_strBtnEditParam).removeAttribute('style');
            Y.one(_strBtnSaveParam).setStyle('display', 'none');
            Y.one(_strBtnCancelEditParam).setStyle('display', 'none');

            for (k in _modifiedItemSelectionParams) {
                isEmpty = false;
                break;
            }
            if (!isEmpty) {
                // TODO: Do Validation
            }

            tbodyEditParamTable.all('tr').each(function () {
                var segmentKey = this.one('td').getContent(),
                    bpElementID = this.one('td').next().getContent(),
                    paramName = this.one('td').next().next().getContent(),
                    paramValue,
                    len = _dataItemSelectionParams.length,
                    i;
                for (i = 0; i < len; i++) {
                    if (_dataItemSelectionParams[i].SegmentKey === segmentKey && 
                    		_dataItemSelectionParams[i].BpElementID === bpElementID &&
                            _dataItemSelectionParams[i].ParamName === paramName) {
                        paramValue = _dataItemSelectionParams[i].ParamValue;
                    }
                }

                this.removeAttribute('style');
                readOnlyRowItemSelectionParam(this, paramValue);
            });
        },

        readOnlyRowItemSelectionParam = function (tableTrow, paramValue) {
            var tableTcols = tableTrow.all('td'),                
	    segmentKey = tableTcols.item(0).getContent(),
            bpElementID = tableTcols.item(1).getContent(),
            paramName = tableTcols.item(2).getContent();
            tableTcols.item(3).setContent(paramValue);
            if (_modifiedItemSelectionParams[segmentKey]) {
            	if (_modifiedItemSelectionParams[segmentKey][bpElementID]) {
            		if (_modifiedItemSelectionParams[segmentKey][bpElementID][paramName]) {
                        delete _modifiedItemSelectionParams[segmentKey][bpElementID][paramName];
            		}
	                if (Object.keys(_modifiedItemSelectionParams[segmentKey][bpElementID]).length == 0) {
	                    delete _modifiedItemSelectionParams[segmentKey][bpElementID];
	                }
                }            	
                if (Object.keys(_modifiedItemSelectionParams[segmentKey]).length == 0) {
                    delete _modifiedItemSelectionParams[segmentKey];
                }
            }
        };

        return {
            open: open
        };
    }();
}, '0.0.1', { requires: ["node", "io"] });