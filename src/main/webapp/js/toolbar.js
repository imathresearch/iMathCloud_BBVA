function executeMenu(key,id, fileName) {
	console.log(key);
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


function genContextMenu(type, shareZone, sharingState, isRoot) {
	// shareZone= 0-> own files. shareZone=1-> other users files. 
	// sharingState = {'YES' | 'NO'}
	var stdFileOperations = '';
	stdFileOperations +=' "download": {"name": "Download as zip", "icon": "ui-icon-play"},';
	stdFileOperations +=' "rename": {"name": "Rename", "icon": "ui-icon-play"},';
	stdFileOperations +=' "delete": {"name": "Delete", "icon": "ui-icon-play"},';
	
	var stdDirOperations = "";
	stdDirOperations += '"newDirectory": {"name": "New directory", "icon": "ui-icon-play"},';
	stdDirOperations +=' "newFile": {"name": "New file", "icon": "ui-icon-play"},';
	stdDirOperations +=' "addFiles": {"name": "Upload files", "icon": "ui-icon-play"}}';
	
	var out;
	switch(type) {
		case "csv":
			out = '{ "edit": {"name": "Edit", "icon": "edit"}, ';
			out += stdFileOperations;
			out += '"plot": {';
			out += '    "name": "Plot",'; 
			out += '	"items": {';
			out += '		"descstats": {"name": "Descriptive Statistics", "icon": "ui-icon-cancel"},';
			out += '		"linreg": {"name": "Linear Regression", "icon": "ui-icon-image"}';
			out += '	}';
			out += '},';
		    out +=' "sep1": "---------", ';
		    out += mathFunc + '}';	
			break;
		case "py":
		case "r":
			out = '{ "edit": {"name": "Edit", "icon": "edit"}, ';
			out += stdFileOperations;
			out +=' "sep1": "---------", ';
		    out +=' "con": {"name": "Run on Console", "icon": "ui-icon-circle-triangle-e"},';
		    out +=' "job": {"name": "Run as Job", "icon": "ui-icon-play"} }';
			break;
		case "svg":
			out = '{ "descstats": {"name": "Plot", "icon": "ui-icon-image"},';
			out += stdFileOperations;
			break;
		case "dir":
			if (shareZone==0 && sharingState == 'NO') {
				out = '{ "share": {"name": "Share Folder", "icon": "ui-icon-image"}, ';
				out +=' "sep1": "---------", ';
				if(isRoot){
					out +=' "download": {"name": "Download as zip", "icon": "ui-icon-play"},';
					out += stdDirOperations;			
				}
				else{
					out += stdFileOperations;
					out += stdDirOperations;
									
				}
			} else if(shareZone==0 && sharingState == 'YES') {
				out = '{ "share": {"name": "Add Users", "icon": "ui-icon-image"},';
				out += '"share": {"name": "Sharing Options", "icon": "ui-icon-image"},';
				out +=' "sep1": "---------", ';
				out += '"unshare": {"name": "Unshare Folder", "icon": "ui-icon-image"},';
				out +=' "sep2": "---------", ';
				out += stdFileOperations;
				out += stdDirOperations;
			} else if(shareZone == 1) {
				out = '{ "shareopt": {"name": "Sharing Options", "icon": "ui-icon-image"}}';
			}
			break;
		default:
			out = '{ "edit": {"name": "Edit", "icon": "edit"}, ';
			out +=' "download": {"name": "Download as zip", "icon": "ui-icon-play"},';
			out +=' "rename": {"name": "Rename", "icon": "ui-icon-play"},';
			out +=' "delete": {"name": "Delete", "icon": "ui-icon-play"}}';
	} 
    return JSON.parse(out);
}

function generateHTMLToolBarFile(idFile) {
	var html = '<div id="toolbarFile_' + idFile + '" class="ui-widget-header ui-corner-all">';
	html += '<small>';
	html += '<button id="saveFileButton_' + idFile + '">Save file</button>';
	html += '<button id="executeConFileButton_' + idFile + '">Execute in console </button>';
	html += '<button id="executeJobFileButton_' + idFile + '">Execute as a job </button>';
	html += '</small></div>';
	return html;
}

function generateToolBarFile(idFile) {
	$( "#saveFileButton_"+idFile).button({
		text: false,
		icons: { primary: "ui-icon-disk" }
	});
	$( "#executeConFileButton_"+idFile).button({
		text: false,
		icons: { primary: "ui-icon-play" }
	});
	$( "#executeJobFileButton_"+idFile).button({
		text: false,
		icons: { primary: "ui-icon-circle-triangle-e" }
	});
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
		executeInConsole(str);
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
