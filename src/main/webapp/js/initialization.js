$(document).ready( function() {
		layout = $("body").layout({
			west__size:			'25%'
		,	east__size:			'19%'
		,	center__minWidth:	'56%'
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
		layout.close("east");	// We close the east panel since it is not functional now.
		$( "#tabs" ).tabs();
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
    	
    	$( "#tabs" ).tabs().delegate( "span.ui-icon-close", "click", function() {
    		var panelId = $( this ).closest( "li" ).remove().attr( "aria-controls" );
    		var id = $( "#" + panelId ).remove().attr("id");
    		if (id.substring(0,4)==='plot') {
				closeOpenFilePlot(getIdFromTabPlotName(id));    				
			}
			else {
				closeOpenFile(getIdFromTabName(id));
			}
    		$( "#tabs" ).tabs().tabs( "refresh" );
    	});
    	
    	$( "#tabs" ).tabs().bind( "keyup", function( event ) {
    		if ( event.altKey && event.keyCode === $.ui.keyCode.BACKSPACE ) {
    			var panelId = tabs.find( ".ui-tabs-active" ).remove().attr( "aria-controls" );
    			var id = $( "#" + panelId ).remove().attr("id");
    			if (id.substring(0,4)==='plot') {
    				closeOpenFilePlot(getIdFromTabPlotName(id));    				
    			}
    			else {
    				closeOpenFile(getIdFromTabName(id));
    			}
        		
    			$( "#tabs" ).tabs().tabs( "refresh" );
    		}
    	});
    	
    	/*$("#jobsXML").tablesorter({ 
            // pass the headers argument and assing a object 
            headers: {                
                0: { sorter: false}, 
                1: { sorter: false},
                2: { sorter: false},
                3: { sorter: 'orderDate'},
                4: { sorter: false} 
            } 
        });*/ 
   	
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
        	conectToConsole(host);
        },
        error: function(error) {
            console.log("error updating table -" + error.status);
        }
    });
}

// keeps trying to connect to the console every second.
// When it gets it, stops the deamon.
// this is done because the console may take some time to start!
function conectToConsole(host) {
	window.hostGlobal=host;
}
var id = setInterval(function() {
	if (! (typeof window.hostGlobal=== 'undefined')) {
		host = window.hostGlobal;
		urlConsole = 'http://'+host['url']+':' + host['port'];
		if (isReady(host['url'], host['port'])) {
			clearInterval(id);
			$( "#interactive_math" ).attr('src',urlConsole +'/new');
	    	var u = document.getElementById('tabs');
	    	var he = u.offsetHeight;
	    	$( "#interactive_math" ).height(he-70);
	    	mathLanguageCode = host['mathLanguage']['consoleCode'];
			getUserMathFunctions();
			$('iframe#interactive_math').load(function() {
				var env_var = "/iMathCloud/" + userName;
				setEnvironmentVariable(env_var);
			});
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
	var u = document.getElementById('tabs');
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
