window.onload = function() {
	drawLayout();
	assignEvents();
	getContextMenuJobs();
	ajaxRequestSession();
	
    //when ever any tab is clicked this method will be call
	$("#id-imath-headTabsFile").on("click", "a", function (e) {
        e.preventDefault();
        $(this).tab('show');
        $currentTab = $(this);
        resizeFileEditor($(this));
    });
    
	$("#id-imath-headTabsConsole").on("click", "a", function (e) {
        e.preventDefault();
        $(this).tab('show');
        currentTabConsole = $(this);
        resizeConsoleTab($(this));
    });

	$.tablesorter.addParser({
	    id: "orderdate",
	    is: function (s, table, cell) {
	        return true;
	    },
	    format: function (s, table, cell, cellIndex) {
	    	s = s.split('/');
	    	var tweenty = "20";
	    	var full_year = tweenty.concat(s[2]);
	    	s[2] = full_year;
	    	s = s.join('/');
	        return new Date(s).getTime() || '';
	    },
	    type: "numeric"
	});
	    	
	$("#jobsXML").tablesorter({ 
        // pass the headers argument and assing a object
		dateFormat: "mmddyyyy",
        headers: {                
            0: { sorter: false}, 
            1: { sorter: "orderdate"}                     
        } 
    });
};


//Global variables
var mathLanguageCode;				// Default math language of the user
var mathFunc;						// List of math functions available by the user
var urlConsole;						// The url connection for the math console
var filesIdOpen = new Array();		// Just to control whether an open file is doubleclicked again
var filesPagination = new Array();	// To control the pagination of the loaded file.
									// If filesIdOpen[i] = idFile, then filesPagination[i] = P, where P is hte current pagination

var filesIdOpenPlot = new Array();	// For plot csv files control
var cmInstance = new Array();		// If filesIdOpen[i] = idFIle, the code mirror instance for the idFile will
									// be in cmInstance[i]
var filesIdOpenTab = new Array(); 	// If filesIdOpen[i] = idFIle, the tab in which the file is open can be found
									// at filesIdOpenTab[i];
var filesIdOpenTabPlot = new Array(); 	// If filesIdOpen[i] = idFIle, the tab in which the file is open can be found
// at filesIdOpenTab[i];

var globalTabCounting = 0;			// The global counting tab, for indexing...
var filesId = new Array();

var plotMap = new Object();			// The mapping between DOM id and csvplot object...
var descStatisticsMap = new Object(); // The mapping between DOM id and descStatisticsPlot object

var itemToCopy;				// Item's Id (either file or directory) already copied and to be pasted though the contextual menu.

var globalTabCountConsole = 0; 		// The global counting tab for indexing consoles
var globalIdConsole = 0; 			// The id associated to a new console, it must be incremented
var consolesIdOpenTabIndex = new Array();
var openTabIndexIdNotebook = new Array();
var mapNotebookIdFileId = {};

var projectName;
var currentTabConsole = null;

function resizeFileEditor(tab) {
	var id = tab.attr("href");
    var he = getWindowHeight() - getTopOffset(id) - getOffsetBottom()-37;    
    var idFile;
    if (id.substring(1,5)==='plot'){
    	idFile=getIdFromTabPlotName(id.substr(1));
    }
    else{
    	idFile=getIdFromTabName(id.substr(1));
        var myCodeMirror = getCodeMirrorInstance(idFile);
    	myCodeMirror.setSize(null,(he)+"px");
    	myCodeMirror.getScrollerElement().style.heigth = he+"px";
    	myCodeMirror.refresh();	
    }   
    
}

function resizeConsoleTab(tab) {
	var nameTab = tab.attr("href");
    var he = getWindowHeight() - getTopOffset(nameTab) - getOffsetBottom();
    $(nameTab + ' iframe:last').height(he);
	$(nameTab).height(he);
}

function drawLayout() {
	$( "#remoteTree" ).height(getWindowHeight()/2);
	addFakeTab();
}

function addFakeTab() {
	var aux = $("<div id='id-imath-fake-tab'></div>");
	$("#tabsFile").append(aux);
	var he = getWindowHeight() - getTopOffset("#id-imath-fake-tab") - getOffsetBottom();
	aux.height(he);
}

function removeFakeTab() {
	$("#id-imath-fake-tab").remove();
}

function getOffsetBottom() {
	//return 80;
	return 40;
}

function setProperHeight(selector) {
	var gheight = $("id-imath-content").height();
	$(selector).height(gheight);
}

function getProperHeight() {
	var gheight = $(window).height()-100;
	return gheight;
}

function getWindowHeight() {
	return getTopOffset("#id-imath-footer");
	//return $(window).height();
}

// Offset with respect document
function getTopOffset(selector) {
	var x = $(selector).offset();
	return x.top;
}

function assignEvents() {
	$("#imath-id-refresh-files").click(function () {
		ajaxGetFiles();
	});
	
	$("#imath-id-refresh-jobs").click(function () {
		ajaxGetJobs();
	});
	
	$("#imath-id-new-console").click(function() { 
		chooseConsole();
		//newNotebook();    		
	});
	
	$("#editor-resize-full").click(function(){
		increaseEditorSize();
	});
	
	$("#editor-resize-small").click(function(){
		displayInitialLayout("editor");
	});
	
	$("#console-resize-full").click(function(){
		increaseConsoleSize();
	});
	
	$("#console-resize-small").click(function(){
		displayInitialLayout("console");
	});
		
}

function increaseEditorSize(){
	$("#editor-section").switchClass( "col-sm-5", "col-sm-10");
	$("#console-section").hide();
	$("#div-editor-resize-full").hide();
	$("#div-editor-resize-small").show();
}

function increaseConsoleSize(){
	$("#editor-section").hide();
	$("#console-section").switchClass( "col-sm-5", "col-sm-10");	
	$("#div-console-resize-full").hide();
	$("#div-console-resize-small").show();
}

function displayInitialLayout(from){
	
	if(from == "editor"){
		$("#editor-section").switchClass( "col-sm-10", "col-sm-5");
		$("#console-section").show();
		$("#div-editor-resize-full").show();
		$("#div-editor-resize-small").hide();
	}
	else{
		$("#console-section").switchClass( "col-sm-10", "col-sm-5");
		$("#editor-section").show();
		$("#div-console-resize-full").show();
		$("#div-console-resize-small").hide();
	}
	
}

function placeWaiting(classid) {
	$("."+ classid).append("<div class='overlay " + classid+"X" + "'></div><div class='loading-img " + classid+"X" + "'></div>");
}

function unplaceWaiting(classid) {
	$("." + classid+"X").remove();
}

/**
 * The function that requests a session for the user and initializes the math console 
 * and the initial load.
 */

function ajaxRequestSession() {
	$.ajax({
        url: "rest/session_service/newSession/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(host) {
        	ajaxGetUserInfo();
        	ajaxGetUserMathFunctions();
        	ajaxGetJobs();
        	conectToDefaultConsole(host);
        },
        error: function(error) {
            console.log("error requestion for a new session -" + error.status);
        }
    });
}

function conectToDefaultConsole(host) {
	window.hostGlobal=host;
}
var id = setInterval(function() {
	if (! (typeof window.hostGlobal=== 'undefined')) {
		host = window.hostGlobal;
		urlConsole = 'http://'+host['url']+':' + host['port'];		
		if (isReady(host['port'])) {
			clearInterval(id);
			newDefaultNotebook();
		}
	}
}, 2000);

// Check if a url is ready
function isReady(port) {
    //var encodedURL = encodeURIComponent(url);
    var isValid = false;
    var urlCall = "rest/session_service/isConsoleReady/"+port; 
    $.ajax({
      url: urlCall,
      type: "get",
      async: false,
      success: function() {
        isValid = true;
      },
      error: function(){
        isValid = false;
      }
    });
    return isValid;
}

function ajaxGetUserInfo(){
	$.ajax({
        url: "rest/user_service/getUserInfo/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(user) {
        	projectName = user['rootName'];
        	$(".projectname").html(projectName);
        },
        error: function(error) {
            console.log("error getting project info - " + error.status);
        }
    });
	
}

//Functions to show simple dialogs and form dialogs

function showFormDialog() {
	var key = arguments[0];
	var extra = [];
	for (var i = 1; i < arguments.length; i++) {
	    extra.push(arguments[i]);
	}
	var form_tpl = eval("form_tpl_" + key + "("+ extra +")");
	showDialog(form_tpl.content, form_tpl.title, form_tpl.buttons);
}


function showDialog(content, title, buttons) {
	
	$("#contentPopup").html(content);
	$("#id-imath-title-dialog").html(title);
	$("#id-imath-buttons-dialog").html("");
	if (buttons.Cancel) {
		$("#id-imath-buttons-dialog").append('<button id="id-imath-cancel-button-select" type="button" class="btn" data-dismiss="modal"><i class="fa fa-times"></i> Cancel</button>');
		$("#imath-id-cancel-button-select").click(buttons.Cancel);
	}
	if (buttons.Submit) {
		$("#id-imath-buttons-dialog").append('<button id="id-imath-submit-button-select" type="button" class="btn btn-danger" data-dismiss="modal"><i class="fa fa-check-square-o"></i> Submit</button>');
		$("#id-imath-submit-button-select").click(buttons.Submit);
	}
	if (buttons.OK) {
		$("#id-imath-buttons-dialog").append('<button id="id-imath-ok-button-select" type="button" class="btn btn-danger" data-dismiss="modal"><i class="fa fa-check-square-o"></i> OK</button>');
		$("#id-imath-OK-button-select").click(buttons.OK);
	}
	$("#dialogPopup").modal();
	//$("#imath-id-conf-message").modal();
}


function chooseConsole(){
	var title = "Choose a console type";
	
	var content = ""
		+ "<div class='block'>"
		+ "<div class='centered'>"
		+ "<button id='imath-id-buton-new-console-python' type='button' class='consolePythonButton' onclick='runConsole(\"python\")'>"
		+ "<i>  New Python Console</i>"
		+ "</button>"
		+ "<button id='imath-id-buton-new-console-r' type='button' class='consoleRButton' onclick='runConsole(\"r\")'>"
		+ "<i>  New R Console</i>"
		+ "</button>"
		+ "<button id='imath-id-buton-new-console-octave' type='button' class='consoleOctaveButton' onclick='runConsole(\"octave\")'>"
		+ "<i>  New Octave Console</i>"
		+ "</button>"
		+ "</div>"
		+ "</div>"
	
	var buttons = {
			Cancel : function() {
				$("#dialogPopup").modal('hide');
			},
			
	};	
	
	showDialog(content, title, buttons);		

}

function runConsole(type){
	setTimeout(function(){
		$("#dialogPopup").modal('hide');
		},350);
	newNotebook(type);  
	
}

window.addEventListener("beforeunload", function (e) {
	  // Before closing the app, we call the logout servlet
	  var urlCall = "logout"; 
	  $.ajax({
		  url: urlCall,
	      type: "get",
	      async: false,
	      success: function() {
	        isValid = true;
	      },
	      error: function(){
	        isValid = false;
	      }
	   });	   
	 
	  return; // To not show any confirmation message                             
});
