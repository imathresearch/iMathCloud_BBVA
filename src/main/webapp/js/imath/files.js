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
			
			if(file['dir'] == null){ //ROOT
				aux = "<li><span id='" + genIdFileContextMenu(file['id'], file['name']) + "' class='" + classSharing + " "+ genClassFileContextMenu(file['id'])  + "'>" + projectName + " <i>" + userName + "</i> </span>";
			}
			else{
				aux = "<li><span id='" + genIdFileContextMenu(file['id'], file['name']) + "' class='" + classSharing + " "+ genClassFileContextMenu(file['id'])  + "'>" + file['name'] + " <i>" + userName + "</i> </span>";
			
			}
			aux = aux + "<ul id='filedir_" + file['id'] + "'></ul>";
			aux = aux + "</li>";
		}
		else {
			// The case we find a regular file
			switch(file['type']) {
				case 'py':
					aux = "<li><span class='file_py'>";
					break;
				case 'ipynb':
					aux = "<li><span class='file_ipynb'>";
					break;
				default:
					aux = "<li><span class='file'>";
			}
						
			aux = aux + "<a id = '" + genIdFileContextMenu(file['id'], file['name']) + "' class='" + genClassFileContextMenu(file['id']) + " " + genClassFileContextMenu(file['id']) + "_l" + "' href='#'>" +file['name']+ "</a>";
			aux = aux + "</span></li>";
		}
		
		var trig = 'right'; 
		if (file['dir']==null) { //ROOT
			if (shareZone==0) rootElement = genIdFileContextMenu(file['id'], file['name']);
			$( treeView ).append(aux);
			$.contextMenu({
		        selector: '.' + genClassFileContextMenu(file['id']) , 
		        trigger: trig,
		        delay: 1,
		        autoHide: true,
		        zIndex: 99,
		        callback: function(key, options) {
		        	var a = options.$trigger.attr("id");
					var b = a.split("__");
		        	executeMenu(key,b[1], b[2]);
		        },
		        
		        items: genContextMenu(file['type'], shareZone, file['sharingState'], true) 
		    });
		} else {						
			var trig = 'right'; 
			//if (file['type'] == "dir") {
			//	trig = 'right'; 
			//}
			$.contextMenu({
		        selector: '.' + genClassFileContextMenu(file['id']) , 	
		        trigger: trig,
		        zIndex:10,
		        callback: function(key, options) {
		        	var a = options.$trigger.attr("id");
					var b = a.split("__");
		        	executeMenu(key,b[1], b[2]);
		        },
		        items: genContextMenu(file['type'], shareZone, file['sharingState']) 
		    });
			trig = 'left';
			if (file['type'] != "dir") {
				$.contextMenu({
			        selector: '.' + genClassFileContextMenu(file['id']) + "_l", 
			        trigger: trig,
			        zIndex:10,
			        callback: function(key, options) {
			        	var a = options.$trigger.attr("id");
						var b = a.split("__");
			        	executeMenu(key,b[1], b[2]);
			        },
			        items: genContextMenu(file['type'], shareZone, file['sharingState']) 
			    });
			}
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
        	var aux = $("#"+rootElement)
		    .clone()    //clone the element
		    .children() //select all the children
		    .remove()   //remove all the children
		    .end()  //again go back to selected element
		    .text();
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
		$( "#remoteTree" ).height(getWindowHeight()/2);
	}
	//$( "#remoteTreeShared" ).remove();
	//$( "#remoteTreeContent").append('<ul id="remoteTreeShared" class="filetree"></ul>');
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
	    	$("#dialogPopup").modal('hide');
	    },350);
	};

	funcError = function(error) {
		console.log("error uploading files - " + error.status);
	};
		

	var buttons = {
		Cancel : function() {
			$("#dialogPopup").modal('hide');
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
				$("#dialogPopup").modal('hide');
				},350);
		}
	};

	funcError = function(error) {
		var cause = "Error deleting file ";
        showFileErrorDialog(name_file_dir, cause);
	};
		

	var buttons = {
		Cancel : function() {
			$("#dialogPopup").modal('hide');
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
				$("#dialogPopup").modal('hide');
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
		Cancel : function() { $("#dialogPopup").modal('hide');},
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

function renameFileSyn(idFile, newName){
	var data = new FormData();
	data.append("idFile", idFile);
	data.append("newName", newName);
	
	var success = false;
	$.ajax({
		url : "rest/beta/api/data/rename", // TODO: This URL must be generated instead of hard-coded.
		cache : false,
		data : data,
		dataType : "json",
		type : "POST",
		async: false,

		processData : false,
		contentType : false,

		success: function(data) {    	
	    	success = true;
	    },
	    error: function(){
	    	console.log("on error renameFileSync");    	  
	    },
	});
	
	return success;
	
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


function copyItem(id) {
	itemToCopy = id;
}

function pasteItem(destiny) {
    
    action = "copy";
	url = "rest/file_service/pasteItem/"+userName + "/" + action + "/" + itemToCopy + "/" + destiny;
    $.ajax({
        url: url,
        cache: false,
        dataType: "json",
        type: "POST",
        async: false,
        success: function(ans) {
            console.log(ans);
        },
        error: function(error) {
            console.log("Error on pasting - " + error.status);
        }
    });

    refreshFilesTree();
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


function ajaxGetFiles() {
	placeWaiting("imath-waiting-files");
	$.ajax({
        url: "rest/file_service/getFiles/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(files) {
        	//alert(JSON.stringify(files));
        	fillRemoteFiles(files, "#remoteTree",0);
        	$( "#remoteTree" ).treeview();
			unplaceWaiting("imath-waiting-files");
        },
        error: function(error) {
        	unplaceWaiting("imath-waiting-files");
            console.log("error loading files - " + error.status);
        }
    });
}


// for legacy
function getFiles() {
	ajaxGetFiles();
}

function getFilesSyn(){
	
	var listFiles = null;
	$.ajax({
        url: "rest/file_service/getFiles/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        async: false,
        success: function(files) {
        	listFiles = files;
        },
        error: function(error) {
            console.log("error loading syn files - " + error.status);
        }
    });
	
	return listFiles;	
}

/* NOT USED NOW!!!!
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
}*/

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
	
	if (globalTabCounting==0) {
		addFakeTab();
	}
	//var numOpenTabs = $('.tab-File').length;
	 
	/*
	if(numOpenTabs == 0){		
		var tabs = $( "#tabsFile" ).tabs();
		tabs.tabs( "refresh" );
		tabs.find( ".ui-tabs-nav" ).remove();		
		tabs.tabs( "refresh" );
	}*/
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
	
	var numOpenTabs = $('.tab-File').length;	
	/*
	if(numOpenTabs == 0){		
		var tabs = $( "#tabsFile" ).tabs();
		tabs.tabs( "refresh" );
		tabs.find( ".ui-tabs-nav" ).remove();		
		tabs.tabs( "refresh" );
	}*/
	
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
                        showSaveFileNotification(idFile);
                        
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


function showSaveFileNotification (idFile) {
	
    
    var msg = " File saved ";
    var timeout = 2000;
    $( "#saveNotification_" +idFile ).text(msg);
    $( "#saveNotification_" +idFile ).attr("style", "display:inline-block;");
   
    if (timeout !== undefined) {
        this.timeout = setTimeout(function () {
        	$( "#saveNotification_" +idFile ).text('');
        	$( "#saveNotification_" +idFile ).attr("style", "display:none;");
        }, timeout);
    };
};

function getTabIdByIndex(index, selector) {
	var tabb = $(selector).eq(index);
	var tabId = tabb.attr("href");
	return tabId.substring(1);
}

function loadFile(idFile){
	if(isFileOpen(idFile)) {
		//showTab(idFile);
		var index = getTabIndex(idFile);
		var tabId = getTabIdByIndex(index, '.tab-File a');
		showTab(tabId);
		//$( "#tabsFile" ).tabs("option", "active", index );
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
	        		var idConsole = getActiveConsole();
	        		executeInConsole(code, idConsole);
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
		$( "#tabsFile" ).tabs("option", "active", index );
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
	//var tabTemplate = "<li class='tab-File'><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
	var tabTemplate = "<li class='tab-File'><a href='#{href}'> <button class='close closeTab'> x</button> #{label}</a></li>";
	var label = 'plot_'+data['name']; 
	var id = nameTab;
	var li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );
	
	var numOpenTabs = $('.tab-File').length;	
	//if(numOpenTabs == 0){
	//	$("#tabsFile").append('<ul></ul>');
	//}
	$('#id-imath-headTabsFile').append(li);
	//var tabs = $( "#tabsFile" ).tabs();
	//tabs.tabs( "refresh" );
	//tabs.find( ".ui-tabs-nav" ).append( li );

	var u = document.getElementById('tabsFile');
	var he = u.offsetHeight;
	var wi = u.offsetWidth;
	he = Math.round(he - he*0.4);
	wi = Math.round(wi);
	
	//htmlCode = "<div class=\"flot-container\" style=\"width:" + wi + "px;height:" + he + "px;\">";
	//htmlCode += "<div class=\"flot-placeholder\" id=\"plotDIV_" + nameTab + "\" style=\"width:" + wi + "px;height:" + he + "px;\"></div>";
	//htmlCode += "<div id=\"options_plotDIV_" + nameTab + "\"></div></div>";
	//tabs.append( "<div id='" + id + "' style='position: relative; width: 100%; height:100%; padding: 0;'><p>" + htmlCode + "</p></div>" );
	
	he = 400;
	htmlCode = "<div class=\"flot-container\" style=\"width:" + wi + "px;height:" + he + "px;\">";
	htmlCode += "<div class=\"flot-placeholder\" id=\"plotDIV_" + nameTab + "\" style=\"width:" + wi + "px;height:" + he + "px;\"></div>";
	htmlCode += "<div id=\"options_plotDIV_" + nameTab + "\"></div></div>";
	
	$("#tabsFile").append( "<div id='" + id + "' class='tab-pane' style='padding: 0;'><p>" + htmlCode + "</p></div>" );
	//tabs.tabs( "refresh" );
	
	switch(data['type']){
		case "csv":
			csvplot = new csvPlot(jQuery.csv()(content),"plotDIV_" + nameTab,'col');
			csvplot.addRadioOptions("lines","col");
			plotMap["plotDIV_" + nameTab] = csvplot;
			break;
			
		case "svg":
			$('#plotDIV_' + nameTab).html(content);
	}
	
	
	addTabInstancePlot(data['id'],globalTabCounting);
	showTab(id);
    registerCloseEvent();
	globalTabCounting++;
	//tabs.tabs( "refresh" );
	var index = getTabIndexPlot(data['id']);
	//$( "#tabsFile" ).tabs("option", "active", index);
	//tabs.tabs( "refresh" );
	
    

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
	var tabTemplate = "<li class='tab-File'><a href='#{href}'> <button class='close closeTab'> x</button> #{label}</a></li>";
	var label = data['name']; 
	var id = nameTab;
	var li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );
	removeFakeTab();
	
	//$('.tab-File').removeClass("active");
	var numOpenTabs = $('.tab-File').length;	
	//if(numOpenTabs == 0){
	//	$("#tabsFile").append('<ul></ul>');
	//}
	$('#id-imath-headTabsFile').append(li);
	
	//var tabs = $( "#tabsFile" ).tabs();
	//tabs.tabs( "refresh" );
	//tabs.find( ".ui-tabs-nav" ).append( li );
	
	var code="";
	for(var i=0; i<data['content'].length;i++) {
		if (i>0) {
			code = code + '\n';
		}
		code = code + data['content'][i];
	}
	
	htmlCode = "<div id=\"codeDIV_" + nameTab + "\"><textarea name=\"code_" + nameTab + "\">" + code + "</textarea></div>";
	htmlButtons = generateHTMLToolBarFile(data['id']);
	$("#tabsFile").append("<div id='" + id + "' class='tab-pane' style='position: relative; padding: 0;'><p>" + htmlButtons + htmlCode + "</p></div>");
	//tabs.append( "<div id='" + id + "' style='position: relative; width: 100%; height:100%; padding: 0;'><p>" + htmlButtons + htmlCode + "</p></div>" );
	generateToolBarFile(data['id']);
	//var u = document.getElementById("codeDIV_"+nameTab);
	var u = document.getElementById('tabsFile');
	
	//var he = getProperHeight(); //u.offsetHeight;
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
	
	showTab(id);
	// -37 for the htmlButtons
	var he = getWindowHeight() - getTopOffset("#"+id) - getOffsetBottom()-37;

    registerCloseEvent();
    
	var myCodeMirror = CodeMirror.fromTextArea(x,conf);
	myCodeMirror.setSize(null,(he)+"px");
	myCodeMirror.getScrollerElement().style.heigth = he+"px";
	myCodeMirror.refresh();	
	addCodeMirrorTabInstance(data['id'],myCodeMirror, globalTabCounting);
	globalTabCounting++;
    
	//tabs.tabs( "refresh" );
	
	//var index = getTabIndex(data['id']);
	//$( "#tabsFile" ).tabs("option", "active", index );
	//tabs.tabs( "refresh" );
}

function registerCloseEvent() {
	$(".closeTab").unbind( "click" );
    $(".closeTab").click(function () {
        //there are multiple elements which has .closeTab icon so close the tab whose close icon is clicked
        var tabContentId = $(this).parent().attr("href");
        $(this).parent().parent().remove(); //remove li of tab
        
        if ($('#id-imath-headTabsFile a:last').length>0){
        	$('#id-imath-headTabsFile a:last').tab('show'); 
        	currentTab=$('#id-imath-headTabsFile a:last');
        	resizeFileEditor($('#id-imath-headTabsFile a:last'));
        }
        
        $(tabContentId).remove(); //remove respective tab content
        var id = tabContentId.substring(1);
        if (id.substring(0,4)==='plot') {
			closeOpenFilePlot(getIdFromTabPlotName(id));
		}
		else {
			closeOpenFile(getIdFromTabName(id));
		}
    });
}

//shows the tab with passed content div id..paramter tabid indicates the div where the content resides
function showTab(tabId) {
    $('#id-imath-headTabsFile a[href="#' + tabId + '"]').tab('show');
}
//return current active tab
function getCurrentTab() {
    return currentTab;
}



// Shareing files capabilities - NOT USED NOW!!!!
/*
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
}*/