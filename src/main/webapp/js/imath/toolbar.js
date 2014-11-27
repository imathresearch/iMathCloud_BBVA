function executeMenu(key,id, fileName) {
	switch(key) {
		case "edit":
			loadFile(id);
			break;
		case "descstats":
			plotFile(id);
			break;
		case "linreg":
			showFormDialog(key,id);
			break;
		case "con":
			executeFileInConsole(id);
			break;
		case "job":
			runJob(id);
			break;
		case "share":
			shareFile(id, fileName);
			break;
		case "addFiles":
			key = "uploadFiles";
			prepareFile(id, key);
			break;
		case "download":
			downloadFileDirectory(id);
			break;
		case "delete":
			key = "deleteFile";
			prepareFile(id, key);
			break;
		case "rename":
			key = "inputText";
			subkey = "renameFile" ;
			prepareFile(id, key);
			break;
		case "newDirectory":
			key = "inputText";
			subkey = "createDirectory";
			prepareFile(id, key);
			break;
		case "newFile":
			key = "inputText";
			subkey = "createFile";
			prepareFile(id,key);
			break;
		case "copy":
			key = "copy";
			//prepareFile(id,key);
			copyItem(id);
			break;
		case "paste":
			key = "paste";
			//prepareFile(id,key);
			pasteItem(id);
			break;
		case "openConsole":
			openNotebook(id);
			break;
		default:
			submitMathFunction(key,id);
	}	
}

function executeMenuJob(key, jobId) {
	
	switch(key) {
		case "stop":
			stopJob(jobId);
			break;
		case "remove":
			removeJob(jobId);
			break;
		default:
			console.log("The option to perform over a job is not known");
			break;
	}
}

function getContextMenuJobs() {
	$.contextMenu({
        selector: '#jobsTBODY tr', 
        callback: function(key, options) {
        	var a = options.$trigger.attr("id");
        	var b = a.split("__");
        	executeMenuJob(key,b[1]);
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
}

function genContextMenu(type, shareZone, sharingState, isRoot) {
	// shareZone= 0-> own files. shareZone=1-> other users files. 
	// sharingState = {'YES' | 'NO'}
	var stdFileOperations = '';	
	stdFileOperations +=' "rename": {"name": "Rename", "icon": "ui-icon-play"},';
	stdFileOperations +=' "delete": {"name": "Delete", "icon": "ui-icon-play"},';
	stdFileOperations +=' "copy": {"name": "Copy", "icon": "ui-icon-play"},';
	//stdFileOperations +=' "download": {"name": "Download as zip", "icon": "ui-icon-play"},';
	
	
	var stdDirOperations = "";
	stdDirOperations +=' "copy": {"name": "Copy", "icon": "ui-icon-play"},';
	stdDirOperations +=' "paste": {"name": "Paste", "icon": "ui-icon-play"},';
	stdDirOperations += ' "sep1": "---------", ';
	stdDirOperations += '"newDirectory": {"name": "New directory", "icon": "ui-icon-play"},';
	stdDirOperations +=' "newFile": {"name": "New file", "icon": "ui-icon-play"},';
	stdDirOperations += ' "sep2": "---------", ';	
	stdDirOperations +=' "addFiles": {"name": "Upload files", "icon": "ui-icon-play"},';
	
	var out;
	switch(type) {
		case "csv":
			out = '{ "edit": {"name": "Edit", "icon": "edit"}, ';
			out += stdFileOperations;
			out += ' "download": {"name": "Download as zip", "icon": "ui-icon-play"},';
			//out += '"plot": {';
			//out += '    "name": "Plot",'; 
			//out += '	"items": {';
			//out += '		"descstats": {"name": "Descriptive Statistics", "icon": "ui-icon-cancel"}';
			//out += '		"linreg": {"name": "Linear Regression", "icon": "ui-icon-image"}';
			//out += '	}';
			//out += '},';
		    out +=' "sep1": "---------", ';
		    out += mathFunc + '}';	
			break;
		case "py":
		case "r":
			out = '{ "edit": {"name": "Edit", "icon": "edit"}, ';
			out += stdFileOperations;
			out += ' "download": {"name": "Download as zip", "icon": "ui-icon-play"},';
			out +=' "sep1": "---------", ';
		    out +=' "con": {"name": "Run on Console", "icon": "ui-icon-circle-triangle-e"},';
		    out +=' "job": {"name": "Run as Job", "icon": "ui-icon-play"} }';
			break;
		case "svg":
			out = '{ "descstats": {"name": "Plot", "icon": "ui-icon-image"},';
			out += stdFileOperations;
			out += ' "download": {"name": "Download as zip", "icon": "ui-icon-play"},';
			break;
		case "dir":
			if (shareZone==0 && sharingState == 'NO') {
				//out = '{ "share": {"name": "Share Folder", "icon": "ui-icon-image"}, ';
				//out +=' "sep1": "---------", ';
				out = '{';
				if(isRoot){
					out += stdDirOperations;
					out +=' "download": {"name": "Download as zip", "icon": "ui-icon-play"}}';
								
				}
				else{
					out += stdFileOperations;
					out += stdDirOperations;
					out +=' "download": {"name": "Download as zip", "icon": "ui-icon-play"}}';
									
				}
			} else if(shareZone==0 && sharingState == 'YES') {
				//out = '{ "share": {"name": "Add Users", "icon": "ui-icon-image"},';
				//out += '"share": {"name": "Sharing Options", "icon": "ui-icon-image"},';
				//out +=' "sep1": "---------", ';
				out = '{';
				out += '"unshare": {"name": "Unshare Folder", "icon": "ui-icon-image"},';
				out +=' "sep2": "---------", ';
				out += stdFileOperations;
				out += stdDirOperations;
				out +=' "download": {"name": "Download as zip", "icon": "ui-icon-play"}}';
			} else if(shareZone == 1) {
				out = '{ "shareopt": {"name": "Sharing Options", "icon": "ui-icon-image"}}';
			}
			break;
		case "ipynb":
			out = '{ "edit": {"name": "Edit", "icon": "edit"}, ';
			out += stdFileOperations;
			out += ' "download": {"name": "Download as zip", "icon": "ui-icon-play"},';
			out +=' "sep1": "---------", ';
			out += ' "openConsole": {"name": "Open console", "icon": "ui-icon-play"}}';
			break;			
		default:
			out = '{ "edit": {"name": "Edit", "icon": "edit"}, ';			
			out +=' "rename": {"name": "Rename", "icon": "ui-icon-play"},';
			out +=' "delete": {"name": "Delete", "icon": "ui-icon-play"},';
			out +=' "copy": {"name": "Copy", "icon": "ui-icon-play"},';			
			out +=' "download": {"name": "Download as zip", "icon": "ui-icon-play"}}';
			
	} 
    return JSON.parse(out);
}

function generateHTMLToolBarFile(idFile) {
	var html = '<div id="toolbarFile_' + idFile + 'class="btn-group">';
	html += '<button id="saveFileButton_' + idFile + '" type="button" class="btn btn-default"><i class="fa fa-save"></i></button>';
	html += '<button id="executeConFileButton_' + idFile + '" type="button" class="btn btn-default"><i class="fa fa-play"></i></button>';
    html += '<button id="executeJobFileButton_' + idFile + '" type="button" class="btn btn-default"><i class="fa fa-play-circle"></i></button>';
    html += '<div id="saveNotification_' + idFile + '" class="notification ui-widget ui-widget-content ui-corner-all border-box-sizing" style="display: none;"></div>';
    html += '</div>';

    /*
	var html = '<div id="toolbarFile_' + idFile + '" class="ui-widget-header ui-corner-all">';
	html += '<small>';
	html += '<button id="saveFileButton_' + idFile + '">Save file</button>';
	html += '<button id="executeConFileButton_' + idFile + '">Execute in console </button>';
	html += '<button id="executeJobFileButton_' + idFile + '">Execute as a job </button>';
	//html += '</small>';
	html += '<div id="saveNotification_' + idFile + '" class="notification ui-widget ui-widget-content ui-corner-all border-box-sizing" style="display: none;"></div>';
	html += '</small></div>';
	*/
	return html;
}

function generateToolBarFile(idFile) {
	$("#saveFileButton_"+idFile).click(function() { 
		var content = new Array();
		var nameTab = buildTabName(idFile);
		var cmI = getCodeMirrorInstance(idFile);
		cmI.save();
		var str = cmI.getValue();
		var content = str.split("\n");
		saveFile(idFile, content);
	});
	
	$("#executeConFileButton_"+idFile).click(function() { 
		var content = new Array();
		var nameTab = buildTabName(idFile);
		var cmI = getCodeMirrorInstance(idFile);
		cmI.save();
		var str = cmI.getValue();
		var idConsole = getActiveConsole();
		executeInConsole(str, idConsole);
	});
	
	$("#executeJobFileButton_"+idFile).click(function() { 				
		runJob(idFile);
	});
}

function generateEastButtons(divId, functions) {
	//TODO attach code to the buttons
	var html='';
	for(var i=0; i<functions.length; i++) {
		func = functions[i];
		html += "<button id=\""+ divId + func['id']+ "\">" + func['shortName'] + "</button>";
	}
	$("#"+divId).append("<small>"+html+"</small>");
	for(var i=0; i<functions.length; i++) {
		func = functions[i];
		$("#" + divId + func['id']).button({
			test: true
		});
		$("#" + divId + func['id']).css({
			width: 130
		});
	}
}
