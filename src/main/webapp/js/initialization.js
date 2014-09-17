$(document).ready( function() {
		layout = $("body").layout({
			west__size:			'20%'
		,	east__size:			'40%'
		,	center__minWidth:	'40%'
		,	spacing_closed:		16
		,	initClosed:			false
		,	maskContents:		true // IMPORTANT - enable iframe masking - all panes in this case
		//,	onopen:				loadIframePage // same callback for ALL borderPanes
		,	east__resizeWhileDragging: false	// slow with a page full of iframes!
		, north: {
			spacing_open:			1			// cosmetic spacing
		,	togglerLength_open:		0			// HIDE the toggler button
		,	togglerLength_closed:	-1			// "100%" OR -1 = full width of pane
		,	resizable: 				false
		,	slidable:				false
		//	override default effect
		,	fxName:					"none"
		}
		});
//		$('.ui-accordion').bind('accordionchange', function(event, ui) {
//			  ui.newHeader; // jQuery object, activated header
//			  ui.oldHeader; // jQuery object, previous header
//			  ui.newPanel; // jQuery object, activated content
//			  ui.oldPanel; // jQuery object, previous content
//			});
		//layout.close("east");	// We close the east panel since it is not functional now.
		//layout.close("north");
		$( "#tabsFile" ).tabs();
		$( "#tabsConsole" ).tabs();
		$( "#menu_west1" ).accordion({
        	collapsible: true, active: 0, heightStyle: "fill",  animate: { duration: 100},
        	activate: function(event, ui) { manageWestAccordion(); }
    	});
		$( "#menu_west3" ).accordion({
    		collapsible: true, active: 0, heightStyle: "fill", animate: { duration: 100},
    		activate: function(event, ui) { manageWestAccordion(); }
		});
		
    	$( "#menu_east1" ).accordion({
        	collapsible: true, active: true, heightStyle: "fill", active: 0
    	});
    	$( "#menu_east2" ).accordion({
        	collapsible: true, active: true, heightStyle: "fill"
    	});
    	
    	$( "#dialogPopup" ).dialog({
    		autoOpen: false,
    		show: {
    			effect: "blind",
    		 	duration: 250
    		},
    		hide: {
    		 	effect: "explode",
    		 	duration: 500
    		},
    		buttons: {
    			Ok: function() {
    				$( this ).dialog( "close" );
    			}
    		}
    	});
    	$( "#sharingOptions" ).dialog({
    		autoOpen: false,
    		height: 400,
    		width: 800,
    		show: {
    			effect: "blind",
    		 	duration: 250
    		},
    		hide: {
    		 	effect: "explode",
    		 	duration: 500
    		},
    		buttons: {
    			'Close': function() {
    				$( this ).dialog( "close" );
    			}
    		}
    	});
    	// Tool bar for tree files
		$( "#refreshTreeButton" ).button({
			text: false,
			icons: { primary: "ui-icon-refresh" }
		});
		$( "#newDirectoryButton" ).button({
			text: false,
			icons: { primary: "ui-icon-folder-collapsed" }
		});
		$( "#newFileButton" ).button({
			text: false,
			icons: { primary: "ui-icon-document" }
		});
		$( "#copyButton" ).button({
			text: false,
			icons: { primary: "ui-icon-copy" }
		});
		$( "#pasteButton" ).button({
			text: false,
			icons: { primary: "ui-icon-script" }
		});
		$( "#deleteButton" ).button({
			text: false,
			icons: { primary: "ui-icon-trash" }
		});
		
		//Tool bar for jobs
		$( "#refreshJobButton" ).button({
			text: false,
			icons: { primary: "ui-icon-refresh" }
		});
		$( "#viewJobButton" ).button({
			text: false,
			icons: { primary: "ui-icon-contact" }
		});
		$( "#plotJobButton" ).button({
			text: false,
			icons: { primary: "ui-icon-image" }
		});
		$( "#runJobButton" ).button({
			text: false,
			icons: { primary: "ui-icon-play" }
		});
		$( "#pauseJobButton" ).button({
			text: false,
			icons: { primary: "ui-icon-pause" }
		});
		$( "#cancelJobButton" ).button({
			text: false,
			icons: { primary: "ui-icon-stop" }
		});
		$( "#deleteJobButton" ).button({
			text: false,
			icons: { primary: "ui-icon-cancel" }
		});
    	//$('table').footable();
    	
    	$("#refreshJobButton").click(function() { 
    		refreshJobsTable();
    	});
    	
    	$("#refreshTreeButton").click(function() { 
    		refreshFilesTree();
    	});
    	
    	$("#newNotebookButton").click(function() { 
    		newNotebook();    		
    	});
    	    	
    	
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
    	
    	$.tablesorter.addParser({
    	    id: "orderdate",
    	    is: function (s, table, cell) {
    	        return true;
    	    },
    	    format: function (s, table, cell, cellIndex) {
    	    	console.log("RUNNING PARSER");
    	        return new Date(s).getTime() || '';
    	    },
    	    type: "numeric"
    	});
    	    	
    	$("#jobsXML").tablesorter({ 
            // pass the headers argument and assing a object 
            headers: {                
                0: { sorter: false}, 
                1: { sorter: false},
                2: { sorter: false},
                3: { sorter: 'orderdate'},
                4: { sorter: false} 
            } 
        });
   	
    	$.contextMenu({
	        selector: '#jobsTBODY tr', 
	        callback: function(key, options) {
	        	var a = options.$trigger.attr("id");
	        	var b = a.split("__");
	        	executeMenuJob(key,b[1]);
	        	console.log("after executeMenuJob")		          
	        	//var m = "clicked: " + key + ". No ID found.";
	        	//if (b[1])
	        	//	m = "clicked: " + key + ". With ID: " + b[1] + ".";
	            //	window.console && console.log(m) || alert(m); 
	        },
	        items: {
	          "stop": {name: "Terminate", disabled: function(key, options)  {
		          var a = options.$trigger.attr("id");
			      var id_job = a.split("__")[1];
			      return !(jobsTable[id_job.toString()] == "RUNNING" || jobsTable[id_job.toString()] == "PAUSED");
		            
		          }},
	          "remove": {name: "Delete", disabled: function(key, options)  {
	        	  var a = options.$trigger.attr("id");
		          var id_job = a.split("__")[1];
		          return (jobsTable[id_job.toString()] == "RUNNING" || jobsTable[id_job.toString()] == "PAUSED");
	            
	          }},
	           
	        }
	    });
    	
		requestSession();
		
		
		
	});



// Global variables
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


/**
 * The function that requests a session for the user and initializes the math console 
 * and the initial load.
 */
function requestSession() {
	$.ajax({
        url: "rest/session_service/newSession/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(host) {
        	conectToDefaultConsole(host);
        	getUserInfo();
        },
        error: function(error) {
            console.log("error updating table -" + error.status);
        }
    });
}

function getUserInfo(){
	$.ajax({
        url: "rest/user_service/getUserInfo/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(user) {
        	projectName = user['rootName'];
        	$("#projectName").text("Project " + projectName);
        	console.log("Project name");
        	console.log(projectName);
        },
        error: function(error) {
            console.log("error updating table -" + error.status);
        }
    });
	
	
}

// keeps trying to connect to the console every second.
// When it gets it, stops the deamon.
// this is done because the console may take some time to start!
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
			/*
			var notebookId = createNotebook();						
			var listNotebooks = getNotebookList();			
			
			var len = listNotebooks.length;
			for(var i = 0; i < len; i++){
				if( notebookId == listNotebooks[i].notebook_id){
					  break;
				}  
			}			
			var notebookName = listNotebooks[i].name;
			notebookName = notebookName + ".ipynb";
			
			var listFiles = getFilesSyn();
			
			len = listFiles.length;
			for(var i = 0; i < len; i++){
				if(notebookName == listFiles[i].name){
					break;
				}
			}
			
			var notebookFileId = listFiles[i].id;
			renameFileSyn(notebookFileId, "iMathConsole.ipynb");
			
			listNotebooks = getNotebookList();	
			len = listNotebooks.length;
			for(var i = 0; i < len; i++){
				if( "iMathConsole" == listNotebooks[i].name){
					  break;
				}  
			}	
			
			notebookId = listNotebooks[i].notebook_id;
			
			console.log("urlConsole");
			console.log(urlConsole);
			$( "#interactive_math-0" ).attr('src',urlConsole +'/' + notebookId);
	    	var u = document.getElementById('tabsConsole');
	    	var he = u.offsetHeight;
	    	$( "#interactive_math-0" ).height(he-70);
	    	mathLanguageCode = host['mathLanguage']['consoleCode'];	    	
			getUserMathFunctions();
			consolesIdOpenTabIndex[globalIdConsole] = globalTabCountConsole;
			globalIdConsole++;
			globalTabCountConsole++;
			$('iframe#interactive_math-0').load(function() {
				var env_var = "/iMathCloud/" + userName;
				setEnvironmentVariable(env_var, 0);
				setDefaultLanguage(mathLanguageCode, 0);
			});
			*/
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

function manageWestAccordion() {
	
	var activeJobs = $( "#menu_west3" ).accordion( "option", "active" );
	var activeFiles = $( "#menu_west1" ).accordion( "option", "active" );
	var u = document.getElementById('tabsFile');
	var he = u.offsetHeight;
	
	if (activeJobs===0 && activeFiles === false) {
		$("#divJobs").height( he-100 );
		//$( "#menu_west3" ).accordion("refresh");
	}
	if (activeJobs===0 && activeFiles===0) {
		var heHalf = Math.round((he-100)/2);
		$("#divJobs").height( heHalf );
		$("#divRemoteFiles").height( heHalf );
		//$( "#menu_west1" ).accordion("refresh");
		//$( "#menu_west3" ).accordion("refresh");
	}
	if (activeJobs===false && activeFiles===0) {
		$("#divRemoteFiles").height( he-100 );
		//$( "#menu_west1" ).accordion("refresh");
	}

}

// Functions to show simple dialogs and form dialogs

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
	$("#dialogPopup").dialog('option', 'title', title);
	
	$("#dialogPopup").dialog({
		'buttons':buttons,
		'width':'auto',
		'height':'auto'
		});
	$("#dialogPopup").dialog("open");
}




window.addEventListener("beforeunload", function (e) {
	
	  // Before closing the tab, we call the logout servlet
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
	 
	  //return false; // To not show any confirmation message                             
});
