var MSG_EXECUTE_CODE = "EXE";
var MSG_EXECUTE_CODE_R = "EXR";		// message to execute the sent code to R console
var MSG_DEFAULT_LANGUAGE = "LAN";
var MSG_DEFAULT_USER_ENVIRONMENT = "ENV";

function executeInConsole(str, idConsole) {
	sendMessage(MSG_EXECUTE_CODE, str, idConsole);
    //goToConsole(idConsole);
}

function executeInConsoleR(str, idConsole) {
	sendMessage(MSG_EXECUTE_CODE_R, str, idConsole);
    //goToConsole();
}

function setDefaultLanguage(str, idConsole) {
	sendMessage(MSG_DEFAULT_LANGUAGE, str, idConsole);
}

function setEnvironmentVariable(str, idConsole) {
	console.log("setEnvironmentVariable " + str);
	sendMessage(MSG_DEFAULT_USER_ENVIRONMENT, str + ";" + userName, idConsole);
}

function sendMessage(msgType, content, idConsole) {
	console.log("SENDMESSAGE");
	console.log(idConsole);
	console.log("sendMessage" + msgType + content);
	console.log("sendMessage" + urlConsole);
	iframeObj = document.getElementById("interactive_math-" + idConsole);
	var win = iframeObj.contentWindow;
	
	win.postMessage(msgType + content, urlConsole);
}

function goToConsole(idConsole) {
    iframeObj = document.getElementById("interactive_math-" + idConsole);
    divTabs = iframeObj.parentNode.parentNode;
    $("#" + divTabs.id + " a")[0].click(); // here we click to the first tab. We guess it is the console always.
}

function newConsole(){
	var idConsole = globalIdConsole;	
	consolesIdOpenTabIndex[idConsole] = globalTabCountConsole;
	globalIdConsole ++;
	globalTabCountConsole ++;
	
	var nameTab = buildNotebookName(idConsole);
	var tabTemplate = "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
	var label = "New Notebook"; 
	var id = nameTab;
	var li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );
	 
	var tabs = $( "#tabsConsole" ).tabs();
	tabs.find( ".ui-tabs-nav" ).append( li );	
	tabs.append("<div id='" +nameTab + "'style='padding: 0;'><iframe id='interactive_math-" + idConsole + "' class='interactive_math' width='100%' frameborder='0' scrolling='no'></iframe></div>");

	
	console.log("INDEX ACTIVE TAB");
	console.log(consolesIdOpenTabIndex[idConsole]);
	tabs.tabs( "refresh" );
	$( "#tabsConsole" ).tabs("option", "active",consolesIdOpenTabIndex[idConsole] );
	
			
	$( "#interactive_math-" + idConsole).attr('src',urlConsole +'/new');
	var u = document.getElementById('tabsConsole');
	var he = u.offsetHeight;
	$( "#interactive_math-" + idConsole).height(he-70);
	host = window.hostGlobal;
	mathLanguageCode = host['mathLanguage']['consoleCode'];
	getUserMathFunctions();
	$('iframe#interactive_math-' + idConsole).load(function() {
		var env_var = "/iMathCloud/" + userName;
		setEnvironmentVariable(env_var, idConsole);
		setDefaultLanguage(mathLanguageCode, idConsole);
	});
	
	
}

function buildNotebookName(idConsole) {
	return  'tabsC-'+idConsole;
}

function getIdFromConsoleTabName(str) {
	return str.substring(6);
}

function closeOpenConsole(idConsole){

	var associatedTab = consolesIdOpenTabIndex[idConsole];
	for (var i=0;i<consolesIdOpenTabIndex.length;i++) {
		if(consolesIdOpenTabIndex[i]>associatedTab) {
			consolesIdOpenTabIndex[i]--;
		}
	}
	delete consolesIdOpenTabIndex[idConsole];
	globalTabCountConsole--;
	
}	


function getActiveConsole(){
	
	var tabs = $( "#tabsConsole" ).tabs();
	tabs.tabs("refresh");
	var indexActiveConsole = tabs.tabs( "option", "active" );
	
	for (var key in consolesIdOpenTabIndex){
		if(consolesIdOpenTabIndex[key] == indexActiveConsole){
			return key;
		}
	}
}