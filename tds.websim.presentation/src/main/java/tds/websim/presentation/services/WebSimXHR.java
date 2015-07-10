/*******************************************************************************
 * Educational Online Test Delivery System 
 * Copyright (c) 2014 American Institutes for Research
 *     
 * Distributed under the AIR Open Source License, Version 1.0 
 * See accompanying file AIR-License-1_0.txt or at
 * https://bitbucket.org/sbacoss/eotds/wiki/AIR_Open_Source_License
 ******************************************************************************/
package tds.websim.presentation.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.ObjectMapper;

import tds.websim.bll.model.ManageSimTask;
import tds.websim.bll.model.SetupSimTask;
import tds.websim.bll.model.WebSimUserTask;
import tds.websim.bll.tasks.IBLoadTask;
import tds.websim.model.BlueprintSegment;
import tds.websim.model.BlueprintSegmentContentLevel;
import tds.websim.model.BlueprintSegmentStrand;
import tds.websim.model.BlueprintValidationStatus;
import tds.websim.model.Clients;
import tds.websim.model.SessionLanguages;
import tds.websim.model.SessionTest;
import tds.websim.model.SessionTestBlueprint;
import tds.websim.model.SessionTestItems;
import tds.websim.model.SessionTests;
import tds.websim.model.Sessions;
import tds.websim.model.SimReport;
import tds.websim.model.SimulationErrors;
import tds.websim.model.Table;
import tds.websim.model.TableRow;
import tds.websim.model.WebSimUser;
import tds.websim.model.itemselectionparam.AdhocParams;
import tds.websim.model.itemselectionparam.ItemSelectionParamTemplate;
import tds.websim.model.itemselectionparam.ItemSelectionParams;
import tds.websim.model.simpublish.SimPubSession;
import tds.websim.model.simpublish.SimPubTestPackage;
import AIR.Common.Helpers._Ref;
import AIR.Common.Web.HttpHandlerBase;
import AIR.Common.data.ResponseData;
import TDS.Shared.Configuration.TDSSettings;
import TDS.Shared.Data.ReturnStatus;
import TDS.Shared.Exceptions.ReturnStatusException;
import TDS.Shared.Exceptions.TDSHttpException;
import TDS.Shared.Exceptions.TDSSecurityException;
import TDS.Shared.Web.UserCookie;

@Controller
@Scope ("prototype")
public class WebSimXHR extends HttpHandlerBase
{
  private static ConcurrentHashMap<String, AdhocParams> _adhocItemSelectionParameters = null;

  private static final Logger                           _logger                       = LoggerFactory.getLogger (WebSimXHR.class);

  @Autowired
  @Qualifier ("setupSimTask")
  private SetupSimTask                                  _setupSimTask;

  @Autowired
  @Qualifier ("webSimUserTask")
  private WebSimUserTask                                _webSimUserTask;

  @Autowired
  @Qualifier ("manageSimTask")
  private ManageSimTask                                 _manageSimTask;

  @Autowired
  @Qualifier ("ibLoadTask")
  private IBLoadTask                                    _ibLoadTask;

  @Autowired
  private TDSSettings                                   _tdsSettings                  = null;

  private WebSimUser                                    _user;
  private UserCookie                                    _userInfo;

  @PostConstruct
  public void init () {
    if (_adhocItemSelectionParameters == null)
    {
      _adhocItemSelectionParameters = new ConcurrentHashMap<String, AdhocParams> ();
      _adhocItemSelectionParameters.put ("adaptive", new AdhocParams ("adaptive"));
      _adhocItemSelectionParameters.put ("adaptive2", new AdhocParams ("adaptive2"));
      createItemSelectionParameterDefaults ();
    }
  }

  private SetupSimTask getSetupSimTask () {
    return this._setupSimTask;
  }

  private ManageSimTask getManageSimTask () {
    return this._manageSimTask;
  }

  private IBLoadTask getIBLoadTask () {
    return this._ibLoadTask;
  }

  private UserCookie getUserInfo () {
    if (this._userInfo == null)
      this._userInfo = new UserCookie (getCurrentContext (), _tdsSettings.getCookieName ("WebSim"));

    return this._userInfo;
  }

  public void getUser () {
    if (this._user == null) {
      this._user = new WebSimUser ();
      _webSimUserTask.loadUser (this._user, getUserInfo ());
    }
  }

  @RequestMapping (value = "GetSessions")
  @ResponseBody
  private Sessions getSessions (@RequestParam (value = "clientname", required = false) String clientname, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();

    // validate user is authorized
    // TODO:write a function to check form authentication cookie.
    // CheckAuthenticate method from HttpHandlerBase
    // TODO:if error, return a ReturnStatus
    if (!_user.isAuth ())
      throw new TDSSecurityException ();

    Sessions sessions = new Sessions ();

    if (checkStringsNotNullNotEmpty (clientname)) {
      sessions = this.getSetupSimTask ().getSessions (_user.getId (), clientname);

      if (sessions.size () < 1)
        sessions.setErrorMsg ("No Sessions found.");
    } else {
      sessions.setErrorMsg ("No Clientname found.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return sessions;
  }

  @RequestMapping (value = "GetSessionTests")
  @ResponseBody
  public SessionTests getSessionTests (@RequestParam (value = "sessionkey", required = false) String sessionKey, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();

    if (!_user.isAuth ())
      throw new TDSSecurityException ();

    SessionTests tests = new SessionTests ();

    if (checkStringsNotNullNotEmpty (sessionKey)) {
      tests = this.getSetupSimTask ().getSessionTests (sessionKey);

      if (tests.size () < 1)
        tests.setErrorMsg ("No Session Tests found.");
    } else {
      tests.setErrorMsg ("No Session Tests found.");
    }
    setStatus (response, HttpServletResponse.SC_OK);

    return tests;
  }

  @RequestMapping (value = "GetAddSessionTestList")
  @ResponseBody
  public SessionTests getAddSessionTestList (@RequestParam (value = "clientname", required = false) String clientName, @RequestParam (value = "sessiontype", required = false) String sessionType,
      @RequestParam (value = "sessionkey", required = false) String sessionKey, final @RequestParam (value = "sessionlanguage", required = false) String sessionLanguage, HttpServletResponse response)
      throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ())
      throw new TDSSecurityException ();

    SessionTests tests = new SessionTests ();

    if (!StringUtils.isEmpty (clientName)) {
      SessionTests existingTests = this.getSetupSimTask ().getSessionTests (sessionKey);
      tests = this.getSetupSimTask ().getSessionTestsToAdd (clientName, sessionType);

      // strip out tests that already exist & tests that do not match language
      for (final SessionTest existingTest : existingTests) {
        CollectionUtils.filter (tests, new Predicate ()
        {

          @Override
          public boolean evaluate (Object arg0) {
            SessionTest t = (SessionTest) arg0;
            return !StringUtils.equals (t.getAdminSubject (), existingTest.getAdminSubject ());
          }
        });
        // went with above in case there were multiple (i.e. languages)
        // tests.Remove(tests.Find(t => t.AdminSubject ==
        // existingTest.AdminSubject));
      }

      // filter test that do not match language
      CollectionUtils.filter (tests, new Predicate ()
      {

        @Override
        public boolean evaluate (Object arg0) {
          SessionTest t = (SessionTest) arg0;
          return StringUtils.equals (t.getLanguageCode (), sessionLanguage);
        }
      });

      if (tests.size () < 1)
        tests.setErrorMsg ("No Session Tests found.");
    } else {
      tests.setErrorMsg ("Clientname is required.");
    }
    setStatus (response, HttpServletResponse.SC_OK);
    return tests;
  }

  @RequestMapping (value = "GetClients")
  @ResponseBody
  public Clients getClients (HttpServletResponse response) throws TDSSecurityException {
    getUser ();
    if (!_user.isAuth ())
      throw new TDSSecurityException ();

    Clients clients = this.getSetupSimTask ().getUserClients (_user.getId ());

    if (clients.size () == 0)
      clients.setErrorMsg ("No Clients found.");

    setStatus (response, HttpServletResponse.SC_OK);
    return clients;
  }

  @RequestMapping (value = "GetItemTypes")
  @ResponseBody
  public List<String> getItemTypes (@RequestParam (value = "adminsubject", required = false) String adminSubject, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ())
      throw new TDSSecurityException ();

    List<String> itemTypes = this.getSetupSimTask ().getItemTypes (adminSubject);
    setStatus (response, HttpServletResponse.SC_OK);
    return itemTypes;
  }

  @RequestMapping (value = "GetDistinctLanguages")
  @ResponseBody
  private SessionLanguages getDistinctLanguages (@RequestParam (value = "clientname", required = false) String clientName, @RequestParam (value = "sessiontype", required = false) String sessionType,
      HttpServletResponse response) throws ReturnStatusException, TDSSecurityException {
    getUser ();
    if (!_user.isAuth ())
      throw new TDSSecurityException ();

    SessionLanguages sessLangs = new SessionLanguages ();

    setStatus (response, HttpServletResponse.SC_OK);
    if (checkStringsNotNullNotEmpty (clientName)) {
      sessLangs = this.getSetupSimTask ().getSessionLanguages (clientName, sessionType);

      return sessLangs;
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "Client name is required.", HttpServletResponse.SC_OK));
    }
  }

  @RequestMapping (value = "GetTestBlueprint")
  @ResponseBody
  public SessionTestBlueprint getTestBlueprint (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws ReturnStatusException, TDSSecurityException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SessionTestBlueprint bp = null;

    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      bp = this.getSetupSimTask ().getTestBlueprint (sessionKey, testKey);
      setStatus (response, HttpServletResponse.SC_OK);
      return bp;
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found.", HttpServletResponse.SC_OK));
    }
  }

  @RequestMapping (value = "GetTestBlueprintCSV")
  @ResponseBody
  public void getTestBlueprintCSV (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException, IOException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SessionTestBlueprint bp = null;

    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      bp = this.getSetupSimTask ().getTestBlueprint (sessionKey, testKey);

      setStatus (response, HttpServletResponse.SC_OK);
      response.setContentType ("text/csv");
      response.addHeader ("Content-Disposition", "attachment;filename=TestBlueprint.csv");

      PrintStream responseTW = new PrintStream (response.getOutputStream ());

      // write blueprint segment
      String strToWrite = "";
      responseTW.println ("Segments");
      strToWrite = StringUtils.join (new String[] { "SegmentID", "SegmentPosition", "StartAbility", "StartInfo", "MinItems", "MaxItems", "FtStartPos", "FtEndPos", "FtMinItems", "FtMaxItems",
          "formSelection", "BlueprintWeight", "Cset1Size", "Cset2InitialRandom", "Cset2Random", "LoadConfig", "UpdateConfig", "ItemWeight", "AbilityOffset", "SelectionAlgorithm", "Cset1Order",
          "RCAbilityWeight", "AbilityWeight", "PrecisionTargetNotMetWeight", "PrecisionTargetMetWeight", "PrecisionTarget", "AdaptiveCut", "TooCloseSEs", "TerminationMinCount",
          "TerminationOverallInfo", "TerminationRCInfo", "TerminationTooClose", "TerminationFlagsAnd" }, ',');
      responseTW.println (strToWrite);
      for (BlueprintSegment segment : bp.getBlueprintSegments ()) {
        strToWrite = StringUtils.join (
            new String[] {
                segment.getSegmentID (),
                "" + segment.getSegmentPosition (),
                "" + segment.getStartAbility (),
                "" + segment.getStartInfo (),
                "" + segment.getMinItems (),
                "" + segment.getMaxItems (),
                "" + segment.getFtStartPos (),
                "" + segment.getFtEndPos (),
                "" + segment.getFtMinItems (),
                "" + segment.getFtMaxItems (),
                segment.getFormSelection (),
                "" + segment.getBlueprintWeight (),
                "" + segment.getCset1Size (),
                "" + segment.getCset2InitialRandom (),
                "" + segment.getCset2Random (),
                "" + segment.getLoadConfig (),
                "" + segment.getUpdateConfig (),
                "" + segment.getItemWeight (),
                "" + segment.getAbilityOffset (),
                "" + segment.getSelectionAlgorithm (),
                "" + segment.getCset1Order (),
                "" + segment.getRcAbilityWeight (),
                "" + segment.getAbilityWeight (),
                "" + segment.getPrecisionTargetNotMetWeight (),
                "" + segment.getPrecisionTargetMetWeight (),
                "" + segment.getPrecisionTarget (),
                "" + segment.getAdaptiveCut (),
                "" + segment.getTooCloseSEs (),
                "" + segment.getTerminationMinCount (),
                "" + segment.getTerminationOverallInfo (),
                "" + segment.getTerminationRCInfo (),
                "" + segment.getTerminationTooClose (),
                "" + segment.getTerminationFlagsAnd () },
            ',');
        responseTW.println (strToWrite);
      }
      responseTW.println ('\n');
      responseTW.println ("Strands");
      strToWrite = StringUtils.join (new String[] { "SegmentKey", "Strand", "StartAbility", "StartInfo", "MinItems", "MaxItems", "BlueprintWeight", "IsStrictMax", "AdaptiveCut", "Scalar",
          "AbilityWeight", "PrecisionTargetNotMetWeight", "PrecisionTargetMetWeight", "PrecisionTarget" }, ',');
      responseTW.println (strToWrite);
      for (BlueprintSegmentStrand strand : bp.getBlueprintSegmentStrands ()) {
        strToWrite = StringUtils.join (new String[] { strand.getSegmentKey (), strand.getStrand (), "" + strand.getStartAbility (), "" + strand.getStartInfo (), "" + strand.getMinItems (),
            "" + strand.getMaxItems (), "" + strand.getBlueprintWeight (), "" + strand.getIsStrictMax (), "" + strand.getAdaptiveCut (), "" + strand.getScalar (), "" + strand.getAbilityWeight (),
            "" + strand.getPrecisionTargetNotMetWeight (), "" + strand.getPrecisionTargetMetWeight (), "" + strand.getPrecisionTarget () }, ',');
        responseTW.println (strToWrite);
      }
      responseTW.println ('\n');
      responseTW.println ("Content Levels");
      strToWrite = StringUtils.join (new String[] { "SegmentKey", "ContentLevelID", "MinItems", "MaxItems", "bpWeight", "isStrictMax" }, ',');
      responseTW.println (strToWrite);
      for (BlueprintSegmentContentLevel contLvl : bp.getBlueprintSegmentContentLevels ()) {
        strToWrite = StringUtils.join (
            new String[] { "" + contLvl.getSegmentKey (), "" + contLvl.getContentLevel (), "" + contLvl.getMinItems (), "" + contLvl.getMaxItems (), "" + contLvl.getBlueprintWeight (),
                "" + contLvl.getIsStrictMax () }, ',');
        responseTW.println (strToWrite);
      }

      responseTW.flush ();
    } else {
      // throw new ReturnStatusException (new ReturnStatus ("failed",
      // "No session key or test key found.", HttpServletResponse.SC_OK));
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

  }

  @RequestMapping (value = "GetTestItems")
  @ResponseBody
  public SessionTestItems getTestItems (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SessionTestItems testItems = null;

    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      testItems = this.getSetupSimTask ().getTestItems (sessionKey, testKey);
      setStatus (response, HttpServletResponse.SC_OK);
      return testItems;
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found.", HttpServletResponse.SC_OK));
    }
  }

  @RequestMapping (value = "CopySession")
  @ResponseBody
  public ReturnStatus copySession (@RequestParam (value = "fromsessionkey", required = false) String fromSessionKey, @RequestParam (value = "sessionname", required = false) String sessionName,
      @RequestParam (value = "sessiondescription", required = false) String sessionDescription, @RequestParam (value = "sessiontype", required = false) String sessionType, HttpServletResponse response)
      throws TDSSecurityException, ReturnStatusException {

    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;

    if (checkStringsNotNullNotEmpty (fromSessionKey, sessionName, sessionDescription)) {
      ret = this.getSetupSimTask ().copySession (fromSessionKey, sessionName, sessionDescription);
    } else {
      ret = new ReturnStatus ("failed", "Session Key and Session Name are required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "CreateSession")
  @ResponseBody
  public ReturnStatus createSession (@RequestParam (value = "language", required = false) String language, @RequestParam (value = "sessionName", required = false) String sessionName,
      @RequestParam (value = "sessiondescription", required = false) String sessionDescription, @RequestParam (value = "sessiontype", required = false) String sessionType,
      @RequestParam (value = "clientname", required = false) String clientName, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;

    // TODO Shiva: look at the commented out .NET line below! the casing is
    // different in the if condition vs. the one that is ultimately used
    // for getting the parameter value.

    // TODO Shiva: The HTTP parameters are case sensitive. We have to make sure
    // we're using the right one (by looking at those JavaScript code). And in
    // this case, "sessionName" is the right one. --- Tongliang LIU
    /*
     * if (!string.IsNullOrEmpty (CurrentContext.Request.Form["sessionname"]))
     * sessionName = CurrentContext.Request["sessionName"];
     */

    String userID = _user.getId ();

    // if (checkStringsNotNullNotEmpty (clientName, userID, sessionName,
    // language, iterations, opportunities, meanProficiency, sdProficiency,
    // strandCorrelation, grade, subject, sessionDescription, sessionType)) {
    if (checkStringsNotNullNotEmpty (clientName, userID, sessionName, language, sessionDescription, sessionType)) {
      // ret = task.CreateSession (clientName, userID, sessionName, language,
      // iterations, opportunities, meanProficiency, sdProficiency,
      // strandCorrelation, grade, subject, sessionDescription, sessionType);
      ret = this.getSetupSimTask ().createSession (clientName, userID, sessionName, language, sessionDescription, sessionType);
    } else {
      ret = new ReturnStatus ("failed", "Clientname, User ID, Session Name, and Language are required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "DeleteSession")
  @ResponseBody
  public ReturnStatus deleteSession (@RequestParam (value = "sessionkey", required = false) String sessionKey, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();

    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (!StringUtils.isEmpty (sessionKey)) {
      ret = this.getSetupSimTask ().deleteSession (sessionKey);
    } else {
      ret = new ReturnStatus ("failed", "Session Key is required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "DeleteSessionTest")
  @ResponseBody
  public ReturnStatus deleteSessionTest (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getSetupSimTask ().deleteSessionTest (sessionKey, testKey);
    } else {
      ret = new ReturnStatus ("failed", "Session Key is required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "DeleteSessionOppData")
  @ResponseBody
  public ReturnStatus deleteSessionOppData (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;

    if (checkStringsNotNullNotEmpty (sessionKey)) {
      if (checkStringsNotNullNotEmpty (testKey))
        ret = this.getSetupSimTask ().deleteSessionOppData (sessionKey, testKey);
      else
        ret = this.getSetupSimTask ().deleteSession (sessionKey);
    } else {
      ret = new ReturnStatus ("failed", "Session Key is required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "SetSessionDescription")
  @ResponseBody
  public ReturnStatus setSessionDescription (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "description", required = false) String description,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, description)) {
      ret = this.getSetupSimTask ().setSessionDescription (sessionKey, description);
    } else {
      ret = new ReturnStatus ("failed", "Unable to update the Session Description.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "AlterSessionTest")
  @ResponseBody
  public ReturnStatus alterSessionTest (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "iterations", required = false) String iterations, @RequestParam (value = "opportunities", required = false) String opportunities,
      @RequestParam (value = "meanproficiency", required = false) String meanProficiency, @RequestParam (value = "sdproficiency", required = false) String sdProficiency,
      @RequestParam (value = "strandcorrelation", required = false) String strandCorrelation, @RequestParam (value = "handscoreitemtypes", required = false) String handScoreItemTypes,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, iterations, opportunities,
        meanProficiency, sdProficiency, strandCorrelation)) {
      ret = this.getSetupSimTask ().alterSessionTest (sessionKey, testKey, iterations, opportunities,
          meanProficiency, sdProficiency, strandCorrelation, handScoreItemTypes);
    } else {
      ret = new ReturnStatus ("failed", "sessionkey, testkey, iterations, opportunities, meanproficiency, sdproficiency and strandcorrelation are all required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "AddSessionTests")
  @ResponseBody
  public List<ReturnStatus> addSessionTests (@RequestParam Map<String, String> formParams, @RequestParam (value = "sessionkey", required = false) String sessionKey,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {

    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SessionTests sessionTests = null;
    String theTests = parseOutSessionTests (formParams);

    try {
      ObjectMapper mapper = new ObjectMapper ();
      sessionTests = mapper.readValue (theTests, SessionTests.class);
    } catch (IOException e) {
      _logger.error (String.format ("Problem mapping pause request to SessionTests: %s", e.getMessage ()));
    }

    List<ReturnStatus> ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey) && sessionTests != null) {
      ret = this.getSetupSimTask ().addSessionTests (sessionKey, sessionTests);
    } else {
      ret = new ArrayList<ReturnStatus> ();
      ret.add (new ReturnStatus ("failed", "Session Tests are required."));
    }
    return ret;
  }

  private String parseOutSessionTests (Map<String, String> formParams) {
    Set<String> keys = formParams.keySet ();
    String thetests = null;
    for (String key : keys) {

      if (key.equalsIgnoreCase ("sessiontests")) {
        thetests = formParams.get (key);
        break;
      }
    }
    return thetests;
  }

  @RequestMapping (value = "AlterItemProperties")
  @ResponseBody
  public ReturnStatus alterItemProperties (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "segmentkey", required = false) String segmentKey, @RequestParam (value = "itemkey", required = false) String itemKey,
      @RequestParam (value = "isactive", required = false) String isActive, @RequestParam (value = "isrequired", required = false) String isRequired, HttpServletResponse response)
      throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, segmentKey, itemKey, isActive, isRequired)) {
      ret = this.getSetupSimTask ().alterItemProperties (sessionKey, testKey, segmentKey, itemKey, isActive, isRequired);
    } else {
      ret = new ReturnStatus ("failed", "sessionkey, testkey, segmentkey, isActive, isRequired are all required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "AlterItemGroupProperties")
  @ResponseBody
  public ReturnStatus alterItemGroupProperties (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "segmentkey", required = false) String segmentKey, @RequestParam (value = "groupid", required = false) String groupId,
      @RequestParam (value = "maxitems", required = false) String maxItems, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, segmentKey, groupId, maxItems)) {
      ret = this.getSetupSimTask ().alterItemGroupProperties (sessionKey, testKey, segmentKey, groupId, maxItems);
    } else {
      ret = new ReturnStatus ("failed", "sessionkey, testkey, segmentkey, group id, max items are all required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "AlterSegmentStrand")
  @ResponseBody
  public ReturnStatus alterSegmentStrand (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "segmentkey", required = false) String segmentKey, @RequestParam (value = "strand", required = false) String strand,
      @RequestParam (value = "minitems", required = false) String minItems, @RequestParam (value = "maxitems", required = false) String maxItems,
      @RequestParam (value = "bpweight", required = false) String bpWeight, @RequestParam (value = "isstrictmax", required = false) String isStrictMax,
      @RequestParam (value = "startability", required = false) String startAbility, @RequestParam (value = "startinfo", required = false) String startInfo,
      @RequestParam (value = "adaptivecut", required = false) String adaptiveCut, @RequestParam (value = "scalar", required = false) String scalar,
      @RequestParam (value = "abilityWeight", required = false) String abilityWeight, @RequestParam (value = "precisionTargetNotMetWeight", required = false) String precisionTargetNotMetWeight,
      @RequestParam (value = "precisionTargetMetWeight", required = false) String precisionTargetMetWeight, @RequestParam (value = "precisionTarget", required = false) String precisionTarget,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, segmentKey, strand, minItems,
        maxItems, bpWeight, isStrictMax, startAbility, startInfo, adaptiveCut, scalar, abilityWeight,
        precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget)) {
      ret = this.getSetupSimTask ().alterSegmentStrand (sessionKey, testKey, segmentKey, strand, minItems,
          maxItems, bpWeight, isStrictMax, startAbility, startInfo, adaptiveCut, scalar, abilityWeight,
          precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget);
    } else {
      ret = new ReturnStatus (
          "failed",
          "sessionKey, testKey, segmentKey, strand, minItems, maxItems, bpWeight, isStrictMax, startAbility, startInfo, abilityWeight, precisionTargetNotMetWeight, precisionTargetMetWeight, precisionTarget are all required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "AlterSegmentContentLevel")
  @ResponseBody
  public ReturnStatus alterSegmentContentLevel (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "segmentkey", required = false) String segmentKey, @RequestParam (value = "contentlevel", required = false) String contentLevel,
      @RequestParam (value = "minitems", required = false) String minItems, @RequestParam (value = "maxitems", required = false) String maxItems,
      @RequestParam (value = "bpweight", required = false) String bpWeight, @RequestParam (value = "isstrictmax", required = false) String isStrictMax, HttpServletResponse response)
      throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, segmentKey, contentLevel, minItems, maxItems, bpWeight, isStrictMax)) {
      ret = this.getSetupSimTask ().alterSegmentContentLevel (sessionKey, testKey, segmentKey, contentLevel, minItems, maxItems, bpWeight, isStrictMax);
    } else {
      ret = new ReturnStatus ("failed", "sessionKey, testKey, segmentKey, minItems, maxItems, bpWeight, isStrictMax.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "ChangeStrandAsContentLevel")
  @ResponseBody
  public ReturnStatus changeStrandAsContentLevel (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "segmentkey", required = false) String segmentKey, @RequestParam (value = "strand", required = false) String strand, HttpServletResponse response)
      throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, segmentKey, strand)) {
      ret = this.getSetupSimTask ().changeStrandAsContentLevel (sessionKey, testKey, segmentKey, strand);
    } else {
      ret = new ReturnStatus ("failed", "sessionKey, testKey, segmentKey, strand are all required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "ChangeContentLevelAsStrand")
  @ResponseBody
  public ReturnStatus changeContentLevelAsStrand (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "segmentkey", required = false) String segmentKey, @RequestParam (value = "contentLevel", required = false) String contentLevel, HttpServletResponse response)
      throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, segmentKey, contentLevel)) {
      ret = this.getSetupSimTask ().changeContentLevelAsStrand (sessionKey, testKey, segmentKey, contentLevel);
    } else {
      ret = new ReturnStatus ("failed", "sessionKey, testKey, segmentKey, contentLevel are all required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "AlterSegment")
  @ResponseBody
  public ReturnStatus alterSegment (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "segmentkey", required = false) String segmentKey, @RequestParam (value = "startability", required = false) String startAbility,
      @RequestParam (value = "startinfo", required = false) String startInfo, @RequestParam (value = "minitems", required = false) String minItems,
      @RequestParam (value = "maxitems", required = false) String maxItems, @RequestParam (value = "ftstartpos", required = false) String ftStartPos,
      @RequestParam (value = "ftendpos", required = false) String ftEndPos, @RequestParam (value = "ftminitems", required = false) String ftMinItems,
      @RequestParam (value = "ftmaxitems", required = false) String ftMaxItems, @RequestParam (value = "bpweight", required = false) String bpWeight,
      @RequestParam (value = "cset1size", required = false) String cSet1Size, @RequestParam (value = "cset2initialrandom", required = false) String cSet2InitialRandom,
      @RequestParam (value = "cset2random", required = false) String cSet2Random, @RequestParam (value = "itemweight", required = false) String itemWeight,
      @RequestParam (value = "abilityoffset", required = false) String abilityOffset, @RequestParam (value = "selectionAlgorithm", required = false) String selectionAlgorithm,
      @RequestParam (value = "cset1order", required = false) String cSet1Order, @RequestParam (value = "rcAbilityWeight", required = false) String rcAbilityWeight,
      @RequestParam (value = "abilityWeight", required = false) String abilityWeight, @RequestParam (value = "precisionTargetNotMetWeight", required = false) String precisionTargetNotMetWeight,
      @RequestParam (value = "precisionTargetMetWeight", required = false) String precisionTargetMetWeight, @RequestParam (value = "precisionTarget", required = false) String precisionTarget,
      @RequestParam (value = "adaptiveCut", required = false) String adaptiveCut, @RequestParam (value = "tooCloseSEs", required = false) String tooCloseSEs,
      @RequestParam (value = "terminationMinCount", required = false) String terminationMinCount, @RequestParam (value = "terminationOverallInfo", required = false) String terminationOverallInfo,
      @RequestParam (value = "terminationRCInfo", required = false) String terminationRCInfo, @RequestParam (value = "terminationTooClose", required = false) String terminationTooClose,
      @RequestParam (value = "terminationFlagsAnd", required = false) String terminationFlagsAnd, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, segmentKey, startAbility, startInfo, minItems, maxItems,
        ftStartPos, ftEndPos, ftMinItems, ftMaxItems, bpWeight, cSet1Size, cSet2InitialRandom, cSet2Random, itemWeight,
        abilityOffset, selectionAlgorithm, cSet1Order, rcAbilityWeight, abilityWeight, precisionTargetNotMetWeight,
        precisionTargetMetWeight, precisionTarget, adaptiveCut, tooCloseSEs, terminationMinCount, terminationOverallInfo,
        terminationRCInfo, terminationTooClose, terminationFlagsAnd)) {
      ret = this.getSetupSimTask ().alterSegment (sessionKey, testKey, segmentKey, startAbility, startInfo, minItems, maxItems,
          ftStartPos, ftEndPos, ftMinItems, ftMaxItems, bpWeight, cSet1Size, cSet2InitialRandom, cSet2Random, itemWeight,
          abilityOffset, selectionAlgorithm, cSet1Order, rcAbilityWeight, abilityWeight, precisionTargetNotMetWeight,
          precisionTargetMetWeight, precisionTarget, adaptiveCut, tooCloseSEs, terminationMinCount, terminationOverallInfo,
          terminationRCInfo, terminationTooClose, terminationFlagsAnd);
    } else {
      ret = new ReturnStatus ("failed", "sessionKey, testKey, segmentKey, contentLevel are all required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "GetItemSelectionParameters")
  @ResponseBody
  public ItemSelectionParams GetItemSelectionParameters (@RequestParam (value = "sessionkey", required = false) String sessionKey,
      @RequestParam (value = "testkey", required = false) String testKey, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }
    ItemSelectionParams itemSelectionParams;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      itemSelectionParams = this.getSetupSimTask ().getItemSelectionParameters (sessionKey, testKey);
      if (itemSelectionParams.size () < 1)
        itemSelectionParams.setErrorMsg ("No item selection parameters exists for this test");
    } else {
      itemSelectionParams = new ItemSelectionParams ();
      itemSelectionParams.setErrorMsg ("Session cannot be found");
    }
    setStatus (response, HttpServletResponse.SC_OK);
    return itemSelectionParams;
  }

  @RequestMapping (value = "AlterItemSelectionParameter")
  @ResponseBody
  public ReturnStatus AlterItemSelectionParameter (@RequestParam (value = "sessionkey", required = false) String sessionKey,
      @RequestParam (value = "testkey", required = false) String testKey,
      @RequestParam (value = "segmentkey", required = false) String segmentKey,
      @RequestParam (value = "selectionAlgorithm", required = false) String selectionAlgorithm,
      @RequestParam (value = "elementType", required = false) String elementType,
      @RequestParam (value = "bpElementID", required = false) String bpElementID,
      @RequestParam (value = "paramName", required = false) String paramName,
      @RequestParam (value = "paramValue", required = false) String paramValue,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }
    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey, selectionAlgorithm, elementType, paramName, paramValue) &&
        ((bpElementID != null && !bpElementID.isEmpty ()) || elementType.equalsIgnoreCase ("Test"))) {
      AdhocParams adhocParams = null;
      if (_adhocItemSelectionParameters.containsKey (selectionAlgorithm))
        adhocParams = _adhocItemSelectionParameters.get (selectionAlgorithm);
      _Ref<String> sMesg = new _Ref<String> ();
      if (adhocParams != null && adhocParams.meets (elementType, paramName, paramValue, sMesg))
        ret = this.getSetupSimTask ().alterItemSelectionParameter (sessionKey, testKey, segmentKey, bpElementID, paramName, paramValue);
      else
        ret = new ReturnStatus ("failed", sMesg.get ());
    } else {
      ret = new ReturnStatus ("failed", "sessionKey, testKey, selectionAlgorithm, elementType, paramName, paramValue are all required.");
    }
    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "RunSimulation")
  @ResponseBody
  public ReturnStatus runSimulation (@RequestParam (value = "sessionkey", required = false) String sessionKey, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey)) {
      ret = this.getManageSimTask ().runSimulation (sessionKey);
    } else {
      ret = new ReturnStatus ("failed", "Session Key is required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "PublishSession")
  @ResponseBody
  public ReturnStatus publishSession (@RequestParam (value = "sessionkey", required = false) String sessionKey, HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey)) {
      ret = this.getManageSimTask ().publishSession (sessionKey);
    } else {
      ret = new ReturnStatus ("failed", "Session Key is required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "ValidateBlueprint")
  @ResponseBody
  public BlueprintValidationStatus validateBlueprint (@RequestParam (value = "sessionkey", required = false) String sessionKey, HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    BlueprintValidationStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey)) {
      ret = this.getManageSimTask ().validateBlueprint (sessionKey);
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "Session Key is required."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "CancelSimulation")
  @ResponseBody
  public ReturnStatus cancelSimulation (@RequestParam (value = "sessionkey", required = false) String sessionKey, HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey)) {
      ret = this.getManageSimTask ().cancelSimulation (sessionKey);
    } else {
      ret = new ReturnStatus ("failed", "Session Key is required.");
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "GetSimulationErrors")
  @ResponseBody
  public SimulationErrors getSimulationErrors (@RequestParam (value = "sessionkey", required = false) String sessionKey, HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimulationErrors ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey)) {
      ret = this.getManageSimTask ().getSimulationErrors (sessionKey);
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "Session Key is required."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "GetReportSummaryStats")
  @ResponseBody
  public SimReport getReportSummaryStats (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportSummaryStats (sessionKey, testKey);
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "GetReportBPSummary")
  @ResponseBody
  public SimReport getReportBPSummary (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportBPSummary (sessionKey, testKey);
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "GetReportScores")
  @ResponseBody
  public SimReport getReportScores (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportScores (sessionKey, testKey);
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "GetReportFieldTestDistribution")
  @ResponseBody
  public SimReport getReportFieldTestDistribution (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportFieldTestDistribution (sessionKey, testKey);
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "GetReportItemDistribution")
  @ResponseBody
  public SimReport getReportItemDistribution (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportItemDistribution (sessionKey, testKey);
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
    return ret;
  }

  @RequestMapping (value = "GetReportScoresCSV")
  @ResponseBody
  public void getReportScoresCSV (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportScores (sessionKey, testKey);
      try {
        writeCsvReportToResponse (ret, response, "ReportScores.csv");
      } catch (IOException e) {
        throw new ReturnStatusException (new ReturnStatus ("failed", e.getMessage ()));
      }
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
  }

  @RequestMapping (value = "GetReportFieldTestDistributionCSV")
  @ResponseBody
  public void getReportFieldTestDistributionCSV (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportFieldTestDistribution (sessionKey, testKey);
      try {
        writeCsvReportToResponse (ret, response, "ReportItemDistribution.csv");
      } catch (IOException e) {
        throw new ReturnStatusException (new ReturnStatus ("failed", e.getMessage ()));
      }
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
  }

  @RequestMapping (value = "GetReportItemDistributionCSV")
  @ResponseBody
  public void getReportItemDistributionCSV (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportItemDistribution (sessionKey, testKey);
      try {
        writeCsvReportToResponse (ret, response, "ReportItemDistribution.csv");
      } catch (IOException e) {
        throw new ReturnStatusException (new ReturnStatus ("failed", e.getMessage ()));
      }
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
  }

  @RequestMapping (value = "GetReportOpportunities")
  @ResponseBody
  public void getReportOpportunities (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException, IOException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport ret = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      ret = this.getManageSimTask ().getReportOpportunities (sessionKey, testKey);
      writeCsvReportToResponse (ret, response, "OpportunitiesReport.csv");
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
  }

  @RequestMapping (value = "GetReportItems")
  @ResponseBody
  public void getReportItems (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException,
      ReturnStatusException, IOException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport report = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      report = this.getManageSimTask ().getReportItems (sessionKey, testKey);
      String msg = getReportFailedMessage (report);
      if (msg != null)
        writeString ("Failed: " + msg);
      else
        writeCsvReportToResponse (report, response, "ItemsReport.csv");
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
  }

  @RequestMapping (value = "GetReportFormDistributions")
  @ResponseBody
  public void getReportFormDistributions (@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException, IOException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    SimReport report = null;
    if (checkStringsNotNullNotEmpty (sessionKey, testKey)) {
      report = this.getManageSimTask ().getFormDistributions (sessionKey, testKey);
      String msg = getReportFailedMessage (report);
      if (msg != null)
        writeString ("Failed: " + msg);
      else
        writeCsvReportToResponse (report, response, "FormDistributions.csv");
    } else {
      throw new ReturnStatusException (new ReturnStatus ("failed", "No session key or test key found."));
    }

    setStatus (response, HttpServletResponse.SC_OK);
  }
  
  @RequestMapping (value = "GetTestPackage")
  @ResponseBody
  public void getTestPackage(@RequestParam (value = "sessionkey", required = false) String sessionKey, @RequestParam (value = "testkey", required = false) String testKey,
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException, IOException, TransformerException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }
    SimPubSession sess = this.getManageSimTask ().getSessionDataForPublish(sessionKey);
    if (sess == null)
      writeString("Error in getting session data");

    SimPubTestPackage pkg = this.getManageSimTask ().getSessionTestPackage(sessionKey, testKey);
    if (pkg.getErrorMsg () != null)
      writeString(pkg.getErrorMsg ());    
    else {
      // Update the test package using session information
      Document doc = pkg.getTestPackage ();
      sess.updateTestPackage(doc, testKey);

      response.setContentType ("application/xml");
      response.addHeader ("Content-Disposition", "attachment;filename=" + "Testpackage.xml");

      Transformer transformer = TransformerFactory.newInstance().newTransformer(); 
      StringWriter sw = new StringWriter(); 
      transformer.transform(new DOMSource(doc), new StreamResult(sw));      
      response.getOutputStream ().print (sw.toString ());      
    }
    setStatus (response, HttpServletResponse.SC_OK);
  }

  @RequestMapping (value = "LoadTestPackage")
  @ResponseBody
  public ReturnStatus loadTestPackage (@RequestParam (value = "testPackage", required = false) MultipartFile testPackage, 
      HttpServletResponse response) throws TDSSecurityException, ReturnStatusException {
    getUser ();
    if (!_user.isAuth ()) {
      throw new TDSSecurityException ();
    }

    ReturnStatus ret = null;    
    if (testPackage.isEmpty()) {
      throw new ReturnStatusException("File is empty") ;
    }

    try {
      InputStream in = testPackage.getInputStream();
      DocumentBuilderFactory documentBuildFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentBuildFactory.newDocumentBuilder();
      Document doc = documentBuilder.parse(in);
      doc.getDocumentElement().normalize();
      String testKey = getTestKey(doc);
      if (testKey == null)
        throw new ReturnStatusException("uniqueid / testkey cannot be found in the test package XML");
      Transformer transformer = TransformerFactory.newInstance().newTransformer(); 
      StringWriter sw = new StringWriter(); 
      transformer.transform(new DOMSource(doc), new StreamResult(sw));
      String xmlTestPackage = sw.toString ();
      ret = getIBLoadTask().loadConfig (testKey, xmlTestPackage);
      setStatus (response, HttpServletResponse.SC_OK);                   
    } catch (ReturnStatusException ex) {
      throw ex ;
    } catch (Exception e)  {
      throw new ReturnStatusException(e);
    }
    return ret;
  }

  @ExceptionHandler ({ TDSSecurityException.class })
  @ResponseBody
  public ResponseData<String> handleSecurityException (TDSSecurityException httpSecEx, HttpServletResponse response) {
    _logger.error (httpSecEx.getMessage (), httpSecEx);
    setStatus (response, httpSecEx.getHttpStatusCode ());
    return new ResponseData<String> (HttpServletResponse.SC_FORBIDDEN, httpSecEx.getMessage (), "");
  }

  @Override
  @ExceptionHandler ({ ReturnStatusException.class })
  @ResponseBody
  public ReturnStatus handleReturnStatusException (ReturnStatusException exp, HttpServletResponse response) {
    _logger.error (exp.getMessage (), exp);
    setStatus (response, exp.getReturnStatus ().getHttpStatusCode ());
    return exp.getReturnStatus ();
  }

  @ExceptionHandler ({ TDSHttpException.class })
  @ResponseBody
  public ResponseData<String> handleHttpException (TDSHttpException httpEx, HttpServletResponse response) {
    _logger.error (httpEx.getMessage (), httpEx);
    setStatus (response, httpEx.getHttpStatusCode ());
    return new ResponseData<String> (HttpServletResponse.SC_INTERNAL_SERVER_ERROR, httpEx.getMessage (), "");
  }

  @ResponseStatus (value = org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler (Exception.class)
  public @ResponseBody
  ResponseData<Object> handleAllOtherException (Exception e) {
    _logger.error (e.getMessage (), e);
    return new ResponseData<Object> (HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "A problem was encountered while processing the request. You will be logged out.", null);
  }

  @Override
  protected void onBeanFactoryInitialized () {
    // Nothing to do...
  }

  private static void setStatus (HttpServletResponse response, int statusCode) {
    response.setStatus (statusCode);
  }

  private static boolean checkStringsNotNullNotEmpty (String... strings) {
    for (int i = 0; i < strings.length; i++) {
      if (StringUtils.isEmpty (strings[i]))
        return false;
    }
    return true;
  }

  private static void writeCsvReportToResponse (SimReport report, HttpServletResponse response, String fileName) throws IOException {
    response.setContentType ("text/csv");
    response.addHeader ("Content-Disposition", "attachment;filename=" + fileName);

    // TODO: Shiva, what's the encoding for the CSV report output?
    BufferedWriter responseWriter = new BufferedWriter (new OutputStreamWriter (response.getOutputStream (), "UTF-8"));

    for (Table reportTable : report.getTables ()) {
      StringBuilder sb = new StringBuilder ();

      for (String tableHeader : reportTable.getTableHeaders ()) {
        sb.append (tableHeader);
        sb.append (",");
      }
      responseWriter.append (sb.toString ());
      responseWriter.newLine ();

      for (TableRow tableRow : reportTable.getTableRows ()) {
        sb = new StringBuilder ();
        for (String colVal : tableRow.getColVals ()) {
          sb.append (colVal);
          sb.append (",");
        }
        responseWriter.append (sb.toString ());
        responseWriter.newLine ();
      }
      responseWriter.newLine ();
    }

    responseWriter.flush ();
    responseWriter.close ();
  }

  private static String getReportFailedMessage (SimReport report) {
    Table table = report.getTables ().get (0);
    String status = table.getTableHeaders ().get (0);
    List<String> colVals = table.getTableRows ().get (0).getColVals ();
    if (status.equalsIgnoreCase ("status") && colVals.get (0).equalsIgnoreCase ("failed"))
      return colVals.get (1);
    else
      return null;
  }

  private void createItemSelectionParameterDefaults ()
  {
    try {
      List<ItemSelectionParamTemplate> dt = new ArrayList<ItemSelectionParamTemplate> ();
      for (AdhocParams adhocParams : _adhocItemSelectionParameters.values ())
        adhocParams.appendItemSelectionParameterDefaultRecords (dt);
      ReturnStatus response = this.getSetupSimTask ().addItemSelectionParameterDefaultRecords (dt);
      if (!response.getStatus ().equals ("success")) {
        _logger.error ("Creating item selection parameters template failed: %s", response.getReason ());
      }
    } catch (ReturnStatusException e) {
      _logger.error ("Creating item selection parameters template failed: %s", e.getMessage ());
    }
  }
  
  private String getTestKey(Document doc)
  {
    String testKey = null;
    Element elem = doc.getDocumentElement();
    if (elem != null){
      Node node = SimPubSession.getXmlNodeChild(elem, "identifier");
      if (node != null && node.hasAttributes ()) {
        Node attr = node.getAttributes ().getNamedItem ("uniqueid");
        if (attr != null)
          testKey = attr.getNodeValue ();
      }
    }
    return testKey;
  }
}
