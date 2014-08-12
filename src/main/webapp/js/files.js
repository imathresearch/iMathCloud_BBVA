function fillRemoteFiles(files, treeView, shareZone) {
	
	clearFileTree(shareZone);
	
	// shareZone = 0 -> owner files. shareZone = 1 -> files from others
	for(var i=0; i<files.length;i++) {
		var file = files[i];
		var userName = "";
		var aux;
		if (file['type']=='dir') {
			// The case we find a directory
			var classSharing = "folder";
			if(shareZone == 1) {
				classSharing = "folder_blue";
				if(file['dir']==-100000) {
					userName = " @" + file['userNameOwner'];
				}
			}
			if (file['sharingState'] == "YES") {
				classSharing = "folder_shared";
			}
			aux = "<li><span id='" + genIdFileContextMenu(file['id'], file['name']) + "' class='" + classSharing + " "+ genClassFileContextMenu(file['id'])  + "'>" + file['name'] + " <i>" + userName + "</i> </span>";
			aux = aux + "<ul id='filedir_" + file['id'] + "'></ul>";
			aux = aux + "</li>";
		}
		else {
			// The case we find a regular file
			if (file['type'] == 'py') {
				aux = "<li><span class='file_py'>";
			} else {
				aux = "<li><span class='file'>";
			}
			aux = aux + "<a id = '" + genIdFileContextMenu(file['id'], file['name']) + "' class='" + genClassFileContextMenu(file['id']) + "' href='#'>" +file['name']+ "</a>";
			aux = aux + "</span></li>";
		}
		
		if (file['dir']==null) {
			if (shareZone==0) rootElement = genIdFileContextMenu(file['id'], file['name']);
			$( treeView ).append(aux);
			$.contextMenu({
		        selector: '.' + genClassFileContextMenu(file['id']) , 
		        trigger: trig,
		        callback: function(key, options) {
		        	var a = options.$trigger.attr("id");
					var b = a.split("__");
		        	executeMenu(key,b[1], b[2]);
		        },
		        
		        items: genContextMenu(file['type'], shareZone, file['sharingState'], true) 
		    });
		} else {			
			var trig = 'left'; 
			if (file['type'] == "dir") {
				trig = 'right'; 
			}
			$.contextMenu({
		        selector: '.' + genClassFileContextMenu(file['id']) , 
		        trigger: trig,
		        callback: function(key, options) {
		        	var a = options.$trigger.attr("id");
					var b = a.split("__");
		        	executeMenu(key,b[1], b[2]);
		        },
		        items: genContextMenu(file['type'], shareZone, file['sharingState']) 
		    });
			$( "#filedir_" + file['dir'] ).append(aux);
		}
	}
	if(shareZone==0) getStorage();
	
}

function getStorage() {
	$.ajax({
        url: "rest/user_service/getCurrentStorage/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(storage) {
        	var curr = parseFloat(storage['currentStorage']);
        	var total = parseFloat(storage["totalStorage"]);
        	if (curr>total) alert("Your current storage is above your limit. Erase some data or contact info@imathresearch.com to extend your quota");
        	var aux = $("#"+rootElement).html();
        	aux = aux + "<b>(" + storage['currentStorage'] + "MiB of " + storage["totalStorage"] + "MiB)</b>";
        	$("#"+rootElement).html(aux);
        },
        error: function(error) {
        	var cause = "Error retrieving the storage";
            showFileErrorDialog("", cause);
        }
    });
}


function clearFileTree(shareZone){
	if (!shareZone){
		$( "#remoteTree" ).remove();
		$( "#remoteTreeContent").append('<ul id="remoteTree" class="filetree"></ul>');
	}
	$( "#remoteTreeShared" ).remove();
	$( "#remoteTreeContent").append('<ul id="remoteTreeShared" class="filetree"></ul>');
}

function genIdFileContextMenu (fileId, name) {
	return "file__" + fileId + "__" + name;
}

function genClassFileContextMenu(fileId) {
	return "con_menu_" + fileId;
}

function refreshFilesTree() {
	//$( "#remoteTree" ).remove();
	//$( "#remoteTreeContent").append('<ul id="remoteTree" class="filetree"></ul>');
	//$( "#remoteTreeShared" ).remove();
	//$( "#remoteTreeContent").append('<ul id="remoteTreeShared" class="filetree"></ul>');
	getFiles(false);
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
// Functions about uploading/downloading/deleting/renaming/creating files/directories functionality //
//////////////////////////////////////////////////////////////////////////////////////////////////////

function prepareFile(idFileDir, key){
	console.log("prepareFIle");
	
	$.ajax({
        url: "rest/file_service/getFile/"+userName + "/" + idFileDir ,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(file) {
        	selectedFile = file;
        	name_file_dir = file['name'];
        	idFile = file['id'];
        	directory_path = file['absolutePath'];   	
        	showFormDialog(key);
        	if(key == "uploadFiles"){
        		prepareUploadFile();
        	}
        	
        },
        error: function(error) {
        	var cause = "File not found ";
            showFileErrorDialog("", cause);
        }
    });
}


function prepareUploadFile(){
	files = undefined;
	$('input[type=file]').on('change', function(event){
		files = event.target.files;
	});
}

function form_tpl_uploadFiles() {
	var title = "Upload Files";

	var content = ""
			+ "<form id='formUploadFiles' >"
			+ "<p>"
			+ "Select a file : <input id='files' name='uploadedFile[]' type='file' size='50' multiple='multiple' />"
			+ "</p>"
			+ "</form>" + "<div id='response'></div>";


	funcSuccess = function(dataOrig) {
	    $("div#response").html("File(s) uploaded");
	    refreshFilesTree();
	    setTimeout(function(){
	    	$("#dialogPopup").dialog("close");
	    },350);
	};

	funcError = function(error) {
		console.log("error uploading files - " + error.status);
	};
		

	var buttons = {
		Cancel : function() {
			$("#dialogPopup").dialog("close");
		},
		Submit : function() {
			existentFiles = getFilesInfo(false,selectedFile.id);
			uploadFiles(files, selectedFile, funcSuccess, funcError);
		}
	};

	// Return the template
	return {
		"content" : content,
		"title" : title,
		"buttons" : buttons
	};
}

function uploadFiles(files, selectedFile, funcSuccess, funcError) {
	  
	if (files == undefined) {
		$("div#response").html("None files selected.");
	} else {
		 var data = new FormData();
		 if(selectedFile != undefined){
			 console.log("destinationDir " + selectedFile.absolutePath);
			data.append("destinationDir", selectedFile.absolutePath);
		 }
		 
		 var fileExist = false;
		 for (var i=0;i<files.length;i++) {
			 data.append("uploadedFile", files[i]);
			 $("div#response").html("Uploading . . .");
			 fileExist = fileAlreadyExist(files[i],existentFiles.getInfo());
			 if (fileExist) {
				 $("div#response").html("File already exist.");
				 break;
			 }
		 };
	
		 if (!fileExist) {
			$.ajax({
				url : "rest/beta/api/data/upload", // TODO: This URL must be generated instead of hard-coded.
				cache : false,
				data : data,
				dataType : "json",
				type : "POST",
		
				processData : false,
				contentType : false,
		
				success : funcSuccess,
				error : funcError
			});
		}
	 }
}

function fileAlreadyExist(uploadFile, existentFiles){
	var exist = false;
	for (var i=0;i<existentFiles.length;i++) {
		if (existentFiles[i].name == uploadFile.name 
				&& existentFiles[i].dir == selectedFile.id){
			exist = true;
			break;
		}
	 };
	 return exist;
};

function downloadFileDirectory(idFileDir){
	var hiddenIFrameID = 'hiddenDownloader',
    iframe = document.getElementById(hiddenIFrameID);
	if (iframe === null) {
	    iframe = document.createElement('iframe');
	    iframe.id = hiddenIFrameID;
	    iframe.style.display = 'none';
	    document.body.appendChild(iframe);
	}
	iframe.src = "rest/beta/api/data/download?idDownloadFile=" + idFileDir;
	
}


function form_tpl_deleteFile() {
	var title = "Delete File/Directory";

	var content = ""
			+ "<form id='formDeleteFiles' >"
			+ "<p>"
			+ "Do you want to delete "
			+ "<b>"+name_file_dir+"</b>"
			+ " ?"
			+ "</p>"
			+ "</form>" + "<div id='response'></div>";


	funcSuccess = function(dataOrig) {
				
		return_code = dataOrig['code'];
		if(return_code == 404){
			var cause = "Error deleting file ";
	        showFileErrorDialog(name_file_dir, cause);
		}
		else{
			$("div#response").html("Deleted");
			refreshFilesTree();
			setTimeout(function(){
				$("#dialogPopup").dialog("close");
				},350);
		}
	};

	funcError = function(error) {
		var cause = "Error deleting file ";
        showFileErrorDialog(name_file_dir, cause);
	};
		

	var buttons = {
		Cancel : function() {
			$("#dialogPopup").dialog("close");
		},
		Submit : function() {
			deleteFile(idFile, funcSuccess, funcError);
		}
	};

	// Return the template
	return {
		"content" : content,
		"title" : title,
		"buttons" : buttons
	};
}

function deleteFile(idFile, funcSuccess, funcError) {
	
	if (idFile == undefined) {
		$("div#response").html("None file selected to be deleted.");
	} else {
		 var data = new FormData();
		 data.append("idDeletedFile", idFile);
		 $("div#response").html("Deleting . . .");
		 
	
		$.ajax({
			url : "rest/beta/api/data/erase", // TODO: This URL must be generated instead of hard-coded.
			cache : false,
			data : data,
			dataType : "json",
			type : "POST",
	
			processData : false,
			contentType : false,
	
			success : funcSuccess,
			error : funcError
		});
	 }
}


function form_tpl_inputText() {
	
	console.log("SUBKEY " + subkey);
	switch(subkey) {
		case "renameFile":
			var form_title = "Rename File/Directory";
			var var_content = "New name ";
			var text_box = name_file_dir;
			var cause1 = "Cannot be renamed as ";
			var cause2 = "Error renaming file...";
			var ok_state = "Renamed";
			var func = renameFile;
			break;
		case "createDirectory":
			var title = "New Directory";
			var var_content = "Directory name ";
			var text_box = "";
			var cause1 = "Cannot create the directory ";
			var cause2 = "Error creating directory...";
			var ok_state = "Created";
			var func = createFile;
			break;
		case "createFile":
			var title = "New Empty File";
			var var_content = "File Name ";
			var text_box = "";
			var cause1 = "Cannot create the file ";
			var cause2 = "Error creating file...";
			var ok_state = "Created";
			var func = createFile;
			break;
		default:
			break;
	}
		
	var title = form_title;

	var content = ""
			+ "<form id='formInput' >"
			+ "<p>"
			+ var_content
			+ "<INPUT TYPE='text' NAME='inputbox' VALUE='" + text_box + "' onkeydown='if (event.keyCode == 13) {clickSubmit(); return false;}'/>"
			+ "</p>"
			+ "</form>" + "<div id='response'></div>";

	funcSuccess = function(dataOrig) {
		
		return_code = dataOrig['code'];
		if(return_code == 404){
			showFileErrorDialog(formInput.inputbox.value, cause1);
		}
		else{
			$("div#response").html(ok_state);
			refreshFilesTree();
			setTimeout(function(){
				$("#dialogPopup").dialog("close");
				},350);
		}
	};

	funcError = function(error) {
		showFileErrorDialog(name_file_dir, cause2);
	};
		
	var funcSubmit = function() {
		if(checkFileName(formInput.inputbox.value)){
			func(idFile, formInput.inputbox.value, funcSuccess, funcError);
		}
		else{
			var cause = "Not a valid name";
			showFileErrorDialog(formInput.inputbox.value, cause);
		}
	};
	
	var buttons = {
		Cancel : function() { $("#dialogPopup").dialog("close");},
		Submit : funcSubmit
	};

	// Return the template
	return {
		"content" : content,
		"title" : title,
		"buttons" : buttons
	};
}

// Just to simulate the click on the submit buton
// It is used when the user press RETURN 
function clickSubmit() {
	$(':button:contains("Submit")').click();
}
		
function renameFile(idFile, newName, funcSuccess, funcError) {
	
	if (idFile == undefined) {
		$("div#response").html("None file selected to be renamed.");
	} else {
		 var data = new FormData();
		 data.append("idFile", idFile);
		 data.append("newName", newName);
		 $("div#response").html("Renaming . . .");
	
		$.ajax({
			url : "rest/beta/api/data/rename", // TODO: This URL must be generated instead of hard-coded.
			cache : false,
			data : data,
			dataType : "json",
			type : "POST",
	
			processData : false,
			contentType : false,
	
			success : funcSuccess,
			error : funcError
		});
	 }
}

function checkFileName(name){
	if (name.length == 0 || name.search(/[\/\\\:\*\"\?\>\<\|\ ]/) != -1){
		return false;
	}	
	return true;
}

function showFileErrorDialog(nameFile, cause){
	var aux;
	aux = "";
	aux += '<p> <img src="css/images/error2.png" alt="Error">';
	aux += "    " + cause + " <b>" + nameFile + "</b> </p>";
	aux += "";

	showDialog(aux);
}

function createFile(idParentDir, fileName, funcSuccess, funcError ){
	
	if (idParentDir == undefined) {
		$("div#response").html("None directory selected to create a child directory.");
	} else {
		 var data = new FormData();
		 data.append("idParentDir", idParentDir);
		 if(subkey == "createDirectory"){
			 data.append("dirName", fileName);
			 $("div#response").html("Creating directory . . .");
		 }
		 if(subkey == "createFile"){
			 data.append("fileName", fileName);
			 $("div#response").html("Creating file . . .");
		 }
		 
	
		$.ajax({
			url : "rest/beta/api/data/createFile", // TODO: This URL must be generated instead of hard-coded.
			cache : false,
			data : data,
			dataType : "json",
			type : "POST",
	
			processData : false,
			contentType : false,
	
			success : funcSuccess,
			error : funcError
		});
	 }
}
///////////////////////////////////////////////////////////////////////////

function getFilesInfo(b, id) {
	url = "rest/file_service/getFiles/"+userName;
	url = (id == undefined) ? url : url + "/" + id;
	var existentFiles = undefined;
	$.ajax({
        url: url,
        cache: false,
        dataType: "json",
        type: "GET",
        async: false,
        success: function(files) {
        	existentFiles = files;
        },
        error: function(error) {
            console.log("error loading files - " + error.status);
        }
    });
	
	return {"getInfo" : function()
	    {
		if (existentFiles != undefined) return existentFiles;
	    }};
}


function getFiles(b) {
	$.ajax({
        url: "rest/file_service/getFiles/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(files) {
        	//alert(JSON.stringify(files));
        	fillRemoteFiles(files, "#remoteTree",0);
			$( "#remoteTree" ).treeview();
			$( "#menu_west1" ).accordion({clearStyle: true});
			$( "#menu_west1" ).accordion("refresh");
			if (b) {
				getJobs(true);
        	}
			getSharedFiles();
        },
        error: function(error) {
            console.log("error loading files - " + error.status);
        }
    });
}

function getSharedFiles() {
	$.ajax({
        url: "rest/file_service/getSharedFiles/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(files) {
			fillRemoteFiles(files,"#remoteTreeShared",1);
			$( "#remoteTreeShared" ).treeview();
        },
        error: function(error) {
            console.log("error loading shared files - " + error.status);
        }
    });
}

function isFile(idFile) {
	//var yes = false;
	//var cc = filesId.length;
	//var j = 0;
	//while (j<cc && !yes) {
	//	yes = (filesId[j]==idFile);
	//	j++; 
	//}
	return true;	// We always return true because only files will be allowed to be opened
}

function addCodeMirrorTabInstance(idFile,cmI,tabNum) {
	var yes = false;
	var cc = filesIdOpen.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpen[j]==idFile);
		j++; 
	}
	if (yes) {
		cmInstance[j-1] = cmI;
		filesIdOpenTab[j-1] = tabNum;
	}
}

function addTabInstancePlot(idFile,tabNum) {
	var yes = false;
	var cc = filesIdOpenPlot.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpenPlot[j]==idFile);
		j++; 
	}
	if (yes) {
		filesIdOpenTabPlot[j-1] = tabNum;
	}
}

function getCodeMirrorInstance(idFile) {
	var yes = false;
	var cc = filesIdOpen.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpen[j]==idFile);
		j++; 
	}
	if (!yes) {
		return null;
	}
	return cmInstance[j-1]; 
}

function getTabIndex(idFile) {
	var yes = false;
	var cc = filesIdOpen.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpen[j]==idFile);
		j++; 
	}
	if (!yes) {
		return null;
	}
	return filesIdOpenTab[j-1]; 
}

function getTabIndexPlot(idFile) {
	var yes = false;
	var cc = filesIdOpenPlot.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpenPlot[j]==idFile);
		j++; 
	}
	if (!yes) {
		return null;
	}
	return filesIdOpenTabPlot[j-1]; 
}

function isFileOpen(idFile) {
//Just to avoid calling to the server when doubleclick in a directory
	var yes = false;
	var cc = filesIdOpen.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpen[j]==idFile);
		j++; 
	}
	return yes;
}

function isFileOpenPlot(idFile) {
//Just to avoid calling to the server when doubleclick in a directory
	var yes = false;
	var cc = filesIdOpenPlot.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpenPlot[j]==idFile);
		j++; 
	}
	return yes;
}

function addOpenFile(idFile) {
	var yes = false;
	var cc = filesIdOpen.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpen[j]==-1);
		j++; 
	}
	if(yes) { j--; }
	filesIdOpen[j]=idFile;
	filesPagination[j] = 1;
}

// Returns the current pagination of a given open IdFile
function getPaginationFileId(idFile) {
	var cc = filesIdOpen.length;
	var j = 0;
	var found = false;
	while (j<cc && !found) {
		found = (filesIdOpen[j]==idFile);
		j++;
	}
	if (found) { 
		return filesPagination[j-1];
	}
	return 1;
}

//Sets the current pagination of a given open IdFile
function setPaginationFileId(idFile, pagination) {
	var cc = filesIdOpen.length;
	var j = 0;
	var found = false;
	while (j<cc && !found) {
		found = (filesIdOpen[j]==idFile);
		j++;
	}
	if (found) { 
		filesPagination[j-1] = pagination;
	}
}

function addOpenFilePlot(idFile) {
	var yes = false;
	var cc = filesIdOpenPlot.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpenPlot[j]==-1);
		j++; 
	}
	if(yes) { j--; }
	filesIdOpenPlot[j]=idFile;
}

function closeOpenFile(idFile) {
	var yes = false;
	var cc = filesIdOpen.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpen[j]==idFile);
		j++; 
	}
	if(yes) { 
		filesIdOpen[j-1] = -1;
		var pivot = filesIdOpenTab[j-1];
		decreaseTabIndex(pivot);
	}
	globalTabCounting--;
}

function closeOpenFilePlot(idFile) {
	var yes = false;
	var cc = filesIdOpenPlot.length;
	var j = 0;
	while (j<cc && !yes) {
		yes = (filesIdOpenPlot[j]==idFile);
		j++; 
	}
	if(yes) { 
		filesIdOpenPlot[j-1] = -1;
		var pivot = filesIdOpenTabPlot[j-1];
		decreaseTabIndex(pivot);
	}
	globalTabCounting--;
}

function decreaseTabIndex(pivot) {
	for (var i=0;i<filesIdOpenTabPlot.length;i++) {
		if(filesIdOpenTabPlot[i]>pivot) {
			filesIdOpenTabPlot[i]--;
		}
	}
	for (var i=0;i<filesIdOpenTab.length;i++) {
		if(filesIdOpenTab[i]>pivot) {
			filesIdOpenTab[i]--;
		}
	}
}


function saveFile(idFile, content){
	var pagination = getPaginationFileId(idFile);
	var url = "rest/file_service/saveFileContent/" + userName + "/" +idFile;
	if (pagination > 0) {
		url = "rest/file_service/saveFileContentPage/" + userName + "/" +idFile;
		url = url + "/" + pagination;
	} 
	
	$.ajax({
		url: url,
		cache: false,
		contentType: "application/json; charset=utf-8",
		dataType: "text json",
		type: "POST",
		data: JSON.stringify(content),
        async: false,
		success: function(dat) {
            var cm = getCodeMirrorInstance(idFile);
           
            var code="";
            var cursorAux = cm.getCursor();
            // We must reload everything until upToPage page.
            for (i=0; i<dat.upToPage; i++) {
                $.ajax({
                data: {page: i+1},
                url: "rest/file_service/getFileContent/" + userName + "/" + idFile,
                cache: false,
                type: "GET",
                async: false,
                success: function(fileDTO) {
                        if (code != "") {
                            code = '\n' + fileDTO['content'].join('\n');
                            cm.replaceRange(code, {line: Infinity});
                        } else {
                            code = fileDTO['content'].join('\n');
                            cm.setValue(code);
                        }
                        setPaginationFileId(idFile, i+1);
                },
                error: function(error) {
                    console.log("error opening file -" + error.status);
                }
                });
            }
            cm.setCursor(cursorAux);
		},
		error: function(error) {
			console.log("error saving file -" + error.responseText);
		}
	});
}


function loadFile(idFile){
	if(isFileOpen(idFile)) {
		var index = getTabIndex(idFile);
		$( "#tabs" ).tabs("option", "active", index );
	}
	else {
		if(isFile(idFile)){
			$.ajax({
				data: {page: 1},
		        url: "rest/file_service/getFileContent/" + userName + "/" + idFile,
		        cache: false,
		        dataType: "json",
		        type: "GET",
		        success: function(fileDTO) {
		        	addOpenFile(idFile);
		        	openFile(fileDTO);
		        },
		        error: function(error) {
		            console.log("error opening file -" + error.status);
		        }
		    });
    	}
    }
    		
};

function loadFileNextPage(idFile, cm) {
	if (isFile(idFile)) {
		var pagination = getPaginationFileId(idFile);
		if (pagination > 0) {		// If pagination for this file is 0, it means that the file is completely uloaded. 
			$.ajax({
				data: {page: pagination+1},
		        url: "rest/file_service/getFileContent/" + userName + "/" + idFile,
		        cache: false,
		        dataType: "json",
		        type: "GET",
                async: false,
		        success: function(fileDTO) {
		        	setPaginationFileId(idFile, pagination+1);
		        	attachNewData(fileDTO, cm);
		        },
		        error: function(error) {
		            console.log("error opening file -" + error.status);
		        }
		    });
		}
	}
	
}

function executeFileInConsole(idFile){
	if(isFile(idFile)){
		$.ajax({
	        url: "rest/file_service/getFileContent/" + userName + "/"+idFile,
	        cache: false,
	        dataType: "json",
	        type: "GET",
	        success: function(fileDTO) {
	        	var code ='';
	        	for(var i=0; i<fileDTO['content'].length;i++) {
	        		code = code + fileDTO['content'][i] + '\n';
	        	}
	        	if (fileDTO['type'] == 'r') { 
	        		executeInConsoleR(code);
	        	} else {
	        		executeInConsole(code);
	        	};
	        },
	        error: function(error) {
	            console.log("error opening file -" + error.status);
	        }
	    });
   	}
}


function plotFile(idFile){
	if(isFileOpenPlot(idFile)) {
		var index = getTabIndexPlot(idFile);
		$( "#tabs" ).tabs("option", "active", index );
	}
	else {
		$.ajax({
	        url: "rest/file_service/getFileContent/" + userName + "/"+idFile,
	        cache: false,
	        dataType: "json",
	        type: "GET",
	        success: function(fileDTO) {
	        	switch(fileDTO['type']){
	        		case "csv":
	        		case "svg":
	        			addOpenFilePlot(idFile);
	    	        	plotInTab(fileDTO);
	        			break;
	        	}
	        },
	        error: function(error) {
	            console.log("error opening file -" + error.status);
	        }
	    });
   	}
}

function getFileContent(idFile, funcSuccess, funcError, extraParams) {
	params = (typeof extraParams != 'undefined') ? $.param(extraParams) : '' ;
	$.ajax({
        url: "rest/file_service/getFileContent/" + userName + "/"+idFile,
        cache: false,
        data: params,
        dataType: "json",
        type: "GET",
        success: funcSuccess,
        error: funcError
    });
}

function csvToTable(header,content) {
	
	headerFormated = "<tr><th>" + header.split(',').join("</th><th>") + "</th></tr>";
	valsFormated = "";
	for(var i=0; i<content.length; i++) {
		vals = content[i];
		valsFormated = valsFormated +"<tr><td>"+ vals.split(',').join("</td><td>") +"</td></tr>";
	}
	return  "<table id='csvToTable' class='footable'>" +
			"<thead>" + headerFormated +"</thead>" +
			"<tbody>"+ valsFormated + "</tbody>" +
			"</table>";
};

function openFile(fileDTO) {
		switch(fileDTO['type']) {
			case 'csv':
				//openCSV(fileDTO);
				openCodeFile(fileDTO,"");
				break;
			case 'py':
				openCodeFile(fileDTO,"python");
				break;
			case 'r':
				openCodeFile(fileDTO,"r");
				break;
			default:
				openCodeFile(fileDTO,"");
				break;
		}	
}

function plotInTab(data){
	var content ='';
	for(var i=0; i<data['content'].length;i++) {
		content = content + data['content'][i] + '\n';
	}
	
	var nameTab = buildTabPlotName(data['id']);
	var tabTemplate = "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
	var label = 'plot_'+data['name']; 
	var id = nameTab;
	var li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );
	 
	var tabs = $( "#tabs" ).tabs();
	tabs.find( ".ui-tabs-nav" ).append( li );

	var u = document.getElementById('tabs');
	var he = u.offsetHeight;
	var wi = u.offsetWidth;
	he =Math.round(he - he*0.4);
	wi = Math.round(wi - wi*0.1);
	
	htmlCode = "<div class=\"flot-container\" style=\"width:" + wi + "px;height:" + he + "px;\">";
	htmlCode += "<div class=\"flot-placeholder\" id=\"plotDIV_" + nameTab + "\" style=\"width:" + wi + "px;height:" + he + "px;\"></div>";
	htmlCode += "<div id=\"options_plotDIV_" + nameTab + "\"></div></div>";
	tabs.append( "<div id='" + id + "' style='position: relative; width: 100%; height:100%; padding: 0;'><p>" + htmlCode + "</p></div>" );
	
	tabs.tabs( "refresh" );
	
	switch(data['type']){
		case "csv":
			csvplot = new csvPlot(jQuery.csv()(content),"plotDIV_" + nameTab,'col');
			csvplot.addRadioOptions("lines","col");
			plotMap["plotDIV_" + nameTab] = csvplot;
			break;
			
		case "svg":
			$('#plotDIV_' + nameTab).html(content);
	}
	
	globalTabCounting++;
	addTabInstancePlot(data['id'],globalTabCounting);
	$( "#tabs" ).tabs("option", "active", globalTabCounting);
	tabs.tabs( "refresh" );
}

function buildTabName(idFile) {
	return  'tab_file_'+idFile;
}

function buildTabPlotName(idFile) {
	return  'plot_tab_file_'+idFile;
}

function getIdFromTabPlotName(str) {
	return str.substring(14);
}

function getIdFromTabName(str) {
	return str.substring(9);
}

function attachNewData(data, cm) {
	//alert("in attachNewData");
	var code="";
	if (data['content'].length > 0) {
		for(var i=0; i<data['content'].length;i++) {
			
			code = code + '\n' + data['content'][i];
		}
		cm.replaceRange(code, {line: Infinity});
	} else {
		setPaginationFileId(data['id'], 0);	// This indicates that the file has been completely uploaded
	}
}

function openCodeFile(data, modeStr) {
	var nameTab = buildTabName(data['id']);
	var tabTemplate = "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
	var label = data['name']; 
	var id = nameTab;
	var li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );
	 
	var tabs = $( "#tabs" ).tabs();
	tabs.find( ".ui-tabs-nav" ).append( li );
	
	var code="";
	for(var i=0; i<data['content'].length;i++) {
		if (i>0) {
			code = code + '\n';
		}
		code = code + data['content'][i];
	}
	//alert(code);
	htmlCode = "<div id=\"codeDIV_" + nameTab + "\" style=\"width:100%; height:100%;\"><textarea name=\"code_" + nameTab + "\">" + code + "</textarea></div>";
	htmlButtons = generateHTMLToolBarFile(data['id']);
	tabs.append( "<div id='" + id + "' style='position: relative; width: 100%; height:100%; padding: 0;'><p>" + htmlButtons + htmlCode + "</p></div>" );
	generateToolBarFile(data['id']);
	//var u = document.getElementById("codeDIV_"+nameTab);
	var u = document.getElementById('tabs');
	var he = u.offsetHeight;
	var x = document.getElementsByName('code_'+nameTab)[0];
	
	var conf = {
			mode: modeStr, 
			lineNumbers: true, 
			value: x.value, 
			theme: "eclipse",
			onScroll: function(cm) {
				var r = cm.getScrollInfo(); 
				var totalHeight = r.height;
				var scrollTop = r.y;
				var scrollClient = r.clientHeight;
				
				var aux =totalHeight - scrollClient;
				var pctEnd = -1;
				if (aux > 0) {	// Here we prevent division by 0 and the case where aux is negative (if that possible?)
					pctEnd = scrollTop / aux * 100; 			// if pctEnd = 0, the scroll is in the top. If close to 100, is at the end of the scroll	
				} 
				
				if (pctEnd >= 95) {
					var idFile = data['id'];										// We get the idFile being edited
					loadFileNextPage(idFile, cm);								// We upload the next page of the file
				}
				//TODO
			}
	};
	var myCodeMirror = CodeMirror.fromTextArea(x,conf);
	myCodeMirror.setSize(null,(he-150)+"px");
	//myCodeMirror.getScrollerElement().style.heigth = he+"px";
	myCodeMirror.refresh();
	globalTabCounting++;
	addCodeMirrorTabInstance(data['id'],myCodeMirror, globalTabCounting);
	tabs.tabs( "refresh" );
	
	var index = getTabIndex(data['id']);
	$( "#tabs" ).tabs("option", "active", index );
	tabs.tabs( "refresh" );
}

// Shareing files capabilities

function shareFile(fileId, fileName) {
	$.ajax({
        url: "rest/file_service/getUsersShared/" +fileId,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(users) {
			showSharingOptions(fileId, users, fileName);
        },
        error: function(error) {
            console.log("error loading sharing options - " + error.status);
        }
    });
}

function showSharingOptions(fileId, users, fileName) {
	var table = "";
	$("#contentSharingOptions").remove();
	
	var input ="<form><fieldset>";
	input += "<label for='name'>User Name or eMail </label>";
	input += "<input type='text' name='txt_name' id='txt_name' class='text ui-widget-content ui-corner-all' />";
	input += "<label for='permission'> Permission</label>";
	input += "<select id='cmb_permission' class='text ui-widget-content ui-corner-all'><option value='READONLY'>Read only</option><option value='READWRITE'>Read write</option></select>";
	input += "<a href='#' onClick='addUserShared(" + fileId + ");'><img src='css/images/page_white_add.png' alt='Add User'></a>";
	input += "</fieldset></form>";
	
	table = '<table id="sharesXML" class="footable" border="0">';
	table += '<thead><th>UserId</th><th>Name</th><th>email</th><th>Permissions</th><th>Options</th></thead>';
	table += '<tbody id="shareFilesTBODY"></tbody></table>';
	
	$("#sharingOptions").append("<div id='contentSharingOptions'>" + input + table + "</div>");
	for(var i=0; i<users.length; i++) {
		user = users[i];
		addUserAtSharedTable(user, fileId);
	}
	
	$( "#sharesXML" ).footable();
	$("#sharingOptions").dialog("open");
	$("#sharingOptions").dialog("option", "title", "Share folder: '" + fileName + "'");
}

function addUserAtSharedTable(user, fileId) {
	var aux = "<tr id = '" +  genIdRowSharedTable(user['userName'], fileId) + "'>";
	aux = aux + "<td>" + user['userName'] + "</td>";
	aux = aux + "<td>" + user['name'] + "</td>";
	aux = aux + "<td>" + user['email'] + "</td>";
	aux = aux + "<td>" + user['permission'] + "</td>";
	aux = aux + "<td><a href='#' onClick='removeUserShared(\"" + user['userName'] + "\", " + fileId + ")'><img src='css/images/page_white_delete.png' alt='Delete'></a></td>";
	aux = aux + "</tr>";
	$( "#shareFilesTBODY" ).append(aux);
}

function genIdRowSharedTable(userName, fileId) {
	return userName + "_" + fileId; 
}

function removeUserShared(userName, fileId) {
	$.ajax({
        url: "rest/file_service/removeUserShared/" +fileId + "/" + userName, 
        cache: false,
        //dataType: "json",
        type: "POST",
        success: function() {
        	refreshFilesTree();
        	$("#"+genIdRowSharedTable(userName, fileId)).remove();
        },
        error: function(error) {
            console.log("Error removing the sharing option.");
        }
    });
}

function addUserShared(fileId) {
	var perm = $("#cmb_permission").val();
	var user = $("#txt_name").val(); 
	
	$.ajax({
        url: "rest/file_service/addUserShared/" +fileId + "/" + user + "/" + perm,
        cache: false,
        dataType: "json",
        type: "POST",
        success: function(user) {
        	if(user['name'] != null) {
        		addUserAtSharedTable(user,fileId);
        		refreshFilesTree();
        		$("#txt_name").val('');
        	} else {
        		alert("Error: user does not exists in the system");
        	}
        },
        error: function(error) {
            console.log("Error assigning user to the folder.");
        }
    });
}
