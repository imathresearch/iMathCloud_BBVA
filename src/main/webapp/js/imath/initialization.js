window.onload = function() {
	assignEvents();
	getContextMenuJobs();
	ajaxRequestSession();
    //when ever any tab is clicked this method will be call
    
	$("#id-imath-headTabsFile").on("click", "a", function (e) {
        e.preventDefault();
        $(this).tab('show');
        $currentTab = $(this);
    });
    
	$("#id-imath-headTabsConsole").on("click", "a", function (e) {
        e.preventDefault();
        $(this).tab('show');
        currentTabConsole = $(this);
    });
	
    /*
	$( "#tabsFile" ).tabs().delegate( "span.ui-icon-close", "click", function() {
		var panelId = $( this ).closest( "li" ).remove().attr( "aria-controls" );
		var id = $( "#" + panelId ).remove().attr("id");
		if (id.substring(0,4)==='plot') {
			closeOpenFilePlot(getIdFromTabPlotName(id));
		}
		else {
			closeOpenFile(getIdFromTabName(id));
		}
		$( "#tabsFile" ).tabs().tabs( "refresh" );
	});
	
	$( "#tabsFile" ).tabs().bind( "keyup", function( event ) {
		if ( event.altKey && event.keyCode === $.ui.keyCode.BACKSPACE ) {
			var panelId = tabs.find( ".ui-tabs-active" ).remove().attr( "aria-controls" );
			var id = $( "#" + panelId ).remove().attr("id");
			if (id.substring(0,4)==='plot') {
				closeOpenFilePlot(getIdFromTabPlotName(id));
			}
			else {
				closeOpenFile(getIdFromTabName(id));
			}
			$( "#tabsFile" ).tabs().tabs( "refresh" );
		}
		
	});
	
	$( "#tabsConsole" ).tabs().delegate( "span.ui-icon-close", "click", function() {
		var panelId = $( this ).closest( "li" ).remove().attr( "aria-controls" );
		var id = $( "#" + panelId ).remove().attr("id");
		closeOpenConsole(getIdFromConsoleTabName(id));
		$( "#tabsConsole" ).tabs().tabs( "refresh" );
	});
	
	$( "#tabsConsole" ).tabs().bind( "keyup", function( event ) {
		if ( event.altKey && event.keyCode === $.ui.keyCode.BACKSPACE ) {
			var panelId = tabs.find( ".ui-tabs-active" ).remove().attr( "aria-controls" );
			var id = $( "#" + panelId ).remove().attr("id");
			closeOpenConsole(getIdFromConsoleTabName(id));
			$( "#tabsConsole" ).tabs().tabs( "refresh" );
		}
	});
	*/
	
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

var projectName;
var currentTabConsole = null;

function assignEvents() {
	$("#imath-id-refresh-files").click(function () {
		ajaxGetFiles();
	});
	
	$("#imath-id-refresh-jobs").click(function () {
		ajaxGetJobs();
	});
	
	$("#imath-id-new-console").click(function() { 
		newNotebook();    		
	});
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
		if (isReady(host['url'], host['port'])) {
			clearInterval(id);
			newDefaultNotebook();
		}
	}
}, 2000);

// Check if a url is ready
function isReady(host, port) {
    //var encodedURL = encodeURIComponent(url);
    var isValid = false;
    var urlCall = "rest/session_service/isConsoleReady/"+host+"/"+port; 
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
