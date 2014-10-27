var MSG_EXECUTE_CODE = "EXE";
var MSG_EXECUTE_CODE_R = "EXR";		// message to execute the sent code to R console
var MSG_DEFAULT_LANGUAGE = "LAN";
var MSG_DEFAULT_USER_ENVIRONMENT = "ENV";
var MSG_PORTCONSOLE = "PRT";

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
	//console.log("setEnvironmentVariable " + str);
	sendMessage(MSG_DEFAULT_USER_ENVIRONMENT, str + ";" + userName, idConsole);
}

function setPORTConsole(idConsole){
	sendMessage(MSG_PORTCONSOLE, host['port'], idConsole);
}

function sendMessage(msgType, content, idConsole) {
	//console.log("sendMessage" + msgType + content);
	//console.log("sendMessage" + urlConsole);
	iframeObj = document.getElementById("interactive_math-" + idConsole);
	var win = iframeObj.contentWindow;
	
	var destination = 'http://' + host['url'] + ":8080";
	win.postMessage(msgType + content, destination);
	//win.postMessage(msgType + content, urlConsole);
}

function goToConsole(idConsole) {
    iframeObj = document.getElementById("interactive_math-" + idConsole);
    divTabs = iframeObj.parentNode.parentNode;
    $("#" + divTabs.id + " a")[0].click(); // here we click to the first tab. We guess it is the console always.
}

function newConsole(notebookId, notebookName){
	
	//console.log("New console");
	
	if(!isNotebookOpen(notebookId)){
		var idConsole = globalIdConsole;	
		consolesIdOpenTabIndex[idConsole] = globalTabCountConsole;
		openTabIndexIdNotebook[globalTabCountConsole] = notebookId;
		globalIdConsole ++;
		globalTabCountConsole ++;
		
		var nameTab = buildNotebookName(idConsole);
		
		if (idConsole == 0){ // Default console
			var tabTemplate = "<li><a href='#{href}'>#{label}</a></li>";
		}
		else{
			var tabTemplate = "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
		}
		
		var label = notebookName; 
		var id = nameTab;
		var li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );
		 
		var tabs = $( "#tabsConsole" ).tabs();
		tabs.find( ".ui-tabs-nav" ).append( li );	
		tabs.append("<div id='" +nameTab + "'style='padding: 0;'><iframe id='interactive_math-" + idConsole + "' class='interactive_math' width='100%' frameborder='0' scrolling='no'></iframe></div>");
		
		tabs.tabs( "refresh" );
		$( "#tabsConsole" ).tabs("option", "active",consolesIdOpenTabIndex[idConsole] );
		
		
		var callBE = 'http://' + host['url'] + ":8080"+ '/iMathCloud/rest/notebook_service/getNotebook/' + notebookId + "/" + host['port'];
		$( "#interactive_math-" + idConsole).attr('src', callBE);
		//$( "#interactive_math-" + idConsole).attr('src',urlConsole +'/' + notebookId);								
		
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
			setPORTConsole(idConsole);
		});
				
	}
	
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
	delete openTabIndexIdNotebook[associatedTab];
	delete consolesIdOpenTabIndex[idConsole];
	globalTabCountConsole--;	
}	

function isNotebookOpen(idNotebook){	
	var index = openTabIndexIdNotebook.indexOf(idNotebook);
		
	if (index == -1){
		return false;
	}
	return true;	
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


function openNotebook(idFile){	
	$.ajax({
        url: "rest/file_service/getFile/"+ userName + "/" + idFile ,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(file) {        	
        	fileNameExt = file['name'];
        	id = file['id'];
        	directory_path = file['absolutePath'];   	
        	var notebooks = getNotebookList();
        	var len = notebooks.length;
        	fileName = fileNameExt.substr(0, fileNameExt.lastIndexOf('.')) || fileNameExt;
      	  	for (var i=0; i<len; i++) {      		  
      		  if( fileName == notebooks[i].name){
      			  break;
      		  }      		  
      	  	}  
      	  	var notebookId = notebooks[i].notebook_id;
      	  	var notebookName = notebooks[i].name;
      	    newConsole(notebookId, notebookName);          	
        },
        error: function(error) {
        	var cause = "File not found ";
            showFileErrorDialog("", cause);
        }
    });
}

function getNotebookList(){	
	host = window.hostGlobal; 
    var listNotebooks = null;
    
    $.ajax({
      url: "rest/notebook_service/getNotebookList/"+host['url']+"/"+host['port'],
      type: "GET",
      async: false,
      success: function(notebooks) {    	
    	  /*console.log("on success getNotebookList");
    	  var len = notebooks.length;
    	  for (var i=0; i<len; i++) {
    		  var notebook_id = notebooks[i].notebook_id;
    		  var nbname = notebooks[i].name;
    		  var kernel = notebooks[i].kernel_id;
    		  console.log("Notebook " + notebook_id + " name " + nbname + " kernel " + kernel);
    	  }*/
    	  listNotebooks = notebooks;
      },
      error: function(){
    	  console.log("on error getNotebookList");       
      }
    });
    
    return listNotebooks;
}

function createNotebook(){	
	host = window.hostGlobal;	    
    var notebookId = null;
    
    $.ajax({
      url: "rest/notebook_service/newNotebook/"+host['url']+"/"+host['port'] ,
      type: "GET",
      async: false,
      success: function(data) {    	   	  
    	  notebookId = data;
      },
      error: function(){
    	  console.log("on error new notebook");    	  
      },
    });
    
    return notebookId;	
}


function newNotebook(){	
	var notebookId = createNotebook();
	var listNotebooks = getNotebookList();
		
	var len = listNotebooks.length;	
	for (var i=0; i<len; i++) {      		  
		if( notebookId == listNotebooks[i].notebook_id){
			  break;
		}      		  
	}  
	  	
	var notebookName = listNotebooks[i].name;
	newConsole(notebookId, notebookName);		
}

function newDefaultNotebook(){	
	var defaultNotebookName = "iMathConsole";
	
	// 1. Check if the default notebook exists
	var listNotebooks = getNotebookList();
	var len = listNotebooks.length;
	var found = false;
	var notebookId;
	var notebookName;
	for(var i = 0; i < len; i++){
		if(defaultNotebookName == listNotebooks[i].name){
			found = true;
			notebookId = listNotebooks[i].notebook_id;
			notebookName = listNotebooks[i].name;
			break;
		}  
	}	
		
	if (found){ // 2. The default notebook exists, so a console is open using it
		//console.log("The notebook exists");
		//console.log("Name " + notebookName);
		newConsole(notebookId, notebookName);
	}
	else{ // 3. The default notebook does not exist
		
		// 3.1. Create a new notebook
		notebookId = createNotebook();
		
		// 3.2 Get the list of notebook to get the name of the recently created notebook
		listNotebooks = getNotebookList();
		len = listNotebooks.length;
		for(var i = 0; i < len; i++){
			if( notebookId == listNotebooks[i].notebook_id){
				  break;
			}  
		}
		notebookName = listNotebooks[i].name;
		notebookName = notebookName + ".ipynb";
		
		// 3.3. Get the files associated with the user with the aim of obtaining the id of
		// file of the notebook previously created.
		// Once the file is located, it is renamed using the name of the default notebook
		var listFiles = getFilesSyn();		
		len = listFiles.length;
		for(var i = 0; i < len; i++){
			if(notebookName == listFiles[i].name){
				break;
			}
		}
		
		var notebookFileId = listFiles[i].id;
		renameFileSyn(notebookFileId, "iMathConsole.ipynb");
		
		// 3.4 When the file associated to the notebook is renamed, the notebook id changes.
		// So, it is necessary to get again the list of notebook and find that notebook with the default name
		listNotebooks = getNotebookList();	
		len = listNotebooks.length;
		for(var i = 0; i < len; i++){
			if( "iMathConsole" == listNotebooks[i].name){
				  break;
			}  
		}
		notebookId = listNotebooks[i].notebook_id;
		notebookName = listNotebooks[i].name;
		
		// 3.5 Finally the console is open with the new notebook
		newConsole(notebookId, notebookName);
		
	}	
}