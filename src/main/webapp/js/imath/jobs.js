function runJob(idFile){
	
	$.ajax({
        url: "rest/jobpython_service/submitJob/" + userName + "/" + idFile ,
        cache: false,
        dataType: "json",
        type: "POST",
        success: function(job) {        	
        	refreshJobsTable();
        	//refreshFilesTree();    
        	updateJob(job['id']);       	
        },
        error: function(error) {
        	refreshJobsTable();
            console.log("Possible error submitting Python job -" + error.status);
        },
    });	
	
	
}

var lastUpdatedJobs = undefined;

function ajaxGetJobs() {
	placeWaiting("imath-waiting-jobs");
	$.ajax({
        url: "rest/job_service/getJobs/" + userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(jobs) {
        	$( "#jobsTBODY" ).empty();
        	lastUpdatedJobs = jobs;
        	filterJobState();
        	unplaceWaiting("imath-waiting-jobs");
			//fillJobs(jobs);
        	//$( "#jobsXML" ).footable();
			//setDefaultLanguage(mathLanguageCode);
        },
        error: function(error) {
        	unplaceWaiting("imath-waiting-jobs");
            console.log("error loading files - " + error.status);
        }
    });
}

// for legacy 
function getJobs() {
	ajaxGetJobs();
}
/*
function checkJobChangeState(jobs) {
	var i = 0;
	var found = false;
	while (!found && i<jobs.length) {
		var id = jobs[i]['id'];
		var state = jobs[i]['state'];
		
	}
	
	for(var i=0; i<jobs.length; i++) {
		if (lastUpdatedJobs[i]['state'] == state){
			filteredJobs.push(lastUpdatedJobs[i]);
		}
	}
}
*/
function updateJob(idJob){

	$.ajax({
        url: "rest/job_service/getJob/" + idJob,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(job) {
			processJobState(job);
        },
        error: function(error) {
            console.log("error getting job - " + error.status);
        }
    });
	
}

function processJobState(job){
		
	if (job['state'] != jobsTable[job['id'].toString()]){
		refreshJobsTable();
		refreshFilesTree();
	}
	
	//Polling for the state of the job
	if (job['state'] == "RUNNING" || job['state'] == "PAUSED"){
		setTimeout(function (){
			updateJob(job['id']);		
		}, 5*1000); //five seconds
	}
}

var jobsTable = new Array();

function fillJobs(jobs) {
	clearJobTable();
	$("#jobsTBODY").height(getProperHeight()/3-120);
	for(var i=0; i<jobs.length; i++) {
		job = jobs[i];
		console.log(job['startDate']);
		var date = new Date(job['startDate']);		
		var dateText = dateToNice(date);
		var description = job['description'].split('/');		
		var aux = "<tr id='" + genIdJobContextMenu(job['id'], job['state']) + "' class='" + genClassJobContextMenu(job['id']) + "' >";
		//aux = aux +"<td>" + getImageJobStatus(job['state']) + "</td>";
		aux = aux + "<td>" +  getImageJobStatus(job['state']) + "  " + "<a onclick='showJobStatus(\""+ job['id'] + "\")' + >" + job['id'] + "</a></td>";
		aux = aux + "<td>" + description[description.length-1] + "</td>";
		aux = aux + '<td data-value="' + job['startDate'] + '">' + dateText + '</td>';		
		//aux = aux + '<td data-value="' + jobPercentCompletion(job) + '">' + jobPercentCompletion(job) + '</td>';
		aux = aux + "</tr>";
		$( "#jobsTBODY" ).append(aux);
		jobsTable[job['id'].toString()] = job['state'];
		
	}
	
	$("#jobsXML").trigger('update')
}



function genIdJobContextMenu (jobId, state) {
	return "job__" + jobId;
}

function genClassJobContextMenu(jobId) {
	return "con_menujob_" + jobId;
}

function jobPercentCompletion(job){
	
	var percents = job['pcts']['perc'];
		
	
	if ( percents[0] == "NO INFO"){
		return percents[0];
	}
	else{
	
		var sum = 0;
		var p;
		for (x=0;x<percents.length;x++){
			p = parseFloat(percents[x]);
			sum = sum + p;
		}
	
		var total_percent = sum /(percents.length);
	
		return total_percent;
	}
	
}

function getImageJobStatus(status) {
	// TODO: Consider another status: CANCELLED. Not it is treated as FINISHED_OK
	var aux;
	switch(status) {
		case "RUNNING":
			aux = '<img src="css/images/job_running.png" alt="Running">';
			break;
		case "FINISHED_OK":
		case "CANCELLED":
			aux = '<img src="css/images/job_ok.png" alt="Finished ok">';
			break;
		case "FINISHED_ERROR":
			aux = '<img src="css/images/job_error.png" alt="Finished error">';
			break;	
		default:
			aux = '<img src="css/images/job_running.png" alt="other">';
	}
	return aux;
}

function showJobStatus(idJob) {
	$.ajax({
        url: "rest/job_service/getJob/" + idJob,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(job) {
			showJobDialog(job);
        },
        error: function(error) {
            console.log("error loading files - " + error.status);
        }
    });
}

function stopJob(idJob) {
	if (confirm("The job will be cancelled. Do you want to continue?")) {
		$.ajax({
	        url: "rest/beta/api/exec/stop/" + idJob,
	        cache: false,
	        dataType: "json",
	        type: "GET",
	        success: function(job) {
	        	refreshJobsTable();
	        },
	        error: function(error) {
	            console.log("error loading files - " + error.status);
	        }
	    });
	}
}


function removeJob(idJob){
	if (confirm("The job and all its generated files will be deleted. Do you want to continue?")){
		$.ajax({
	        url: "rest/job_service/removeJob/" + idJob,
	        cache: false,
	        type: "DELETE",
	        success: function() {
	        	refreshJobsTable();
	        	refreshFilesTree();
	        },
	        error: function(error) {
	            console.log("error deleting job - " + error);	            
	        }
	    });
	}
}


function filterJobState(){	
	var state = $("#selectJobState").val();
		
	filteredJobs = [];
	if(state == "ALL"){
		filteredJobs = lastUpdatedJobs;
	}
	else{
		for(var i=0; i<lastUpdatedJobs.length; i++) {
			if (lastUpdatedJobs[i]['state'] == state){
				filteredJobs.push(lastUpdatedJobs[i]);
			}
		}
	}
	
	fillJobs(filteredJobs);	
}

function clearJobTable(){
	$("#jobsTBODY").remove();
	var aux = '<tbody id="jobsTBODY"></tbody>';
	$("#jobsXML").append(aux);
}

function showJobDialog(job) {
	var aux;
	var startDate = new Date(job['startDate']);
	var endDate = null;
	if (job['endDate'] != null) {
		endDate = new Date(job['endDate']);
	}
	aux = "";
	aux += "<b>Job Id:		</b> " + job['id'] + "<br>";
	aux += "<b>Status: 		</b> " + getImageJobStatus(job['state']) + " <i>"+job['state']+"</i><br>";
	aux += "<b>Description:	</b>" + job['description']+"<br>";
	aux += "<b>Start Date:	</b> " + startDate + "<br>";
	aux += "<b>End Date: 	</b>";
	if (endDate != null) {
		aux += endDate;
	}
	aux += "<br>";
	
	if (job['jobResult']!=null && job['state']=='FINISHED_ERROR') {
		aux += "<hr>";
		aux += "<b>Error message</b><br>";
		aux += job['jobResult']['json'];
	}
	else if (job['jobResult']!=null && job['state']=='FINISHED_OK') {
		aux += "<hr>";
		if (job['outputFiles']!=null) {
			if (job['outputFiles'].length>0) {
				aux += "<b>Output file/s</b><br>";
			}
			for (var i =0; i < job['outputFiles'].length;i++) {
				file = job['outputFiles'][i];
				if (i>0) aux+= ", ";
				aux += file['name'];
			}
		}
		//JSON format {"files":[["", "src", "file1.txt], ["", "file2.txt"]], "dirs": [["", "src"], ["", "src", "new_dir"]]}
		//JSON.parse can throw an exception
		/*
		try{
			obj = JSON.parse(job['jobResult']['json']);
			//console.log("JSON parseado: " + obj);
			if (obj.files.length > 0){
				var f = "";
				for(x = 0; x < obj.files.length; x++)
					f += obj.files[x].join('/') + " ";
			
				aux += "<b>Output file/s</b><br>";
				aux += f  + "<br>";
			}
			
			if (obj.dirs.length > 0){
				var d = "";
				for(x = 0; x < obj.dirs.length; x++)
					d += obj.dirs[x].join('/') + " ";
		
				aux += "<b>Output directory/ies</b><br>";
				aux += d  + "<br>";		
			}
		}
		catch(e){		
			console.log("Excepcion throw in showJobDialog: " + e.message);
		}*/
		
		
		aux += "<hr>";
		aux += "<b>Percentages of completion</b><br>";
		aux += job['pcts']['perc'] + "<br>";
		aux += '<a href="#" onclick="plotJobResults('+ job['id'] + '); $(\'#dialogPopup\').dialog(\'close\')">Plot <img src="css/images/page_white_paste.png"></a>';
	}
	aux += "";
//	$("#contentPopup").remove();
//	$("#dialogPopup").append("<div id='contentPopup'>" + aux + "</div>");
//	$("#contentPopup").html(aux);
//	$("#dialogPopup").dialog("open");
	var content = aux;
	var title = "Job Info";
	var buttons = {
			OK : function() {
				$("#dialogPopup").modal('hide');
			}		
	};
	showDialog(content, title, buttons);
}

function plotJobResults(idJob) {
	$.ajax({
        url: "rest/job_service/getJobOutputFiles/" + idJob,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(files) {
        	if (files.length===1) {
        		file = files[0];
        		plotFile(file['id']);
        	}
        	else if (files.length>1) {
        		plotStatDescriptiveInTab(files,idJob);
        		// We assume it is a statistical descriptive stuff
        		// TODO: Still to be done
        	}
        },
        error: function(error) {
            console.log("error loading output files from job id - " + error.status);
        }
    });
}

function plotStatDescriptiveInTab(files,idJob) {
	
	var nameTab = 'plot_stat_desc_' + idJob;
	var tabTemplate = "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>";
	var label = 'Job ' + idJob + ": Stats"; 
	var id = nameTab;
	var li = $( tabTemplate.replace( /#\{href\}/g, "#" + id ).replace( /#\{label\}/g, label ) );
	 
	var tabs = $( "#tabsFile" ).tabs();
	tabs.find( ".ui-tabs-nav" ).append( li );

	tabs.append( "<div id='" + id + "' style='position: relative; width: 100%; height:100%; padding: 0; overflow : auto;'></div>" );
	
	var obj = new descStatisticsPlot(id,files,idJob);
	descStatisticsMap[id] = obj;
	obj.loadDataAndPlot();
	
	tabs.tabs( "refresh" );
	
	globalTabCounting++;
	//addTabInstancePlot(data['id'],globalTabCounting);
	$( "#tabsFile" ).tabs("option", "active", globalTabCounting);
	tabs.tabs( "refresh" );
}

function refreshJobsTable() {
	getJobs(false);
}


function form_tpl_linreg(idFile){

	//Form scheme
	var title = "Linear Regression";
	
	var content = "" +
			"<form id='formLinreg'>" +
			"	<fieldset id='columnsLinreg' style='float: left;width:300px;'></fieldset>" +
			"	<fieldset id='optionsLinreg' style='float: left;width:300px;'></fieldset>" +
			"	<div style='clear:both'></div>" +
			"</form>" +
			"<br/>" +
			"<div id='CSVTable'></div>";
	

	// Specific functions
	funcSuccess = function(dataOrig){
		
		// Manage data and its header
		manageLabels = function(data){
			var headerAux = data['content'].slice(0,1)[0];
			var contentAux = data['content'].slice(1,Infinity);
			var dataHeader = [];
			hasHeader = $("input#input_hasHeader").is(":checked");
			if (hasHeader){
				$.each(headerAux.split(','),function(index,value){
					dataHeader.push(value);
				});
			}else{
				//console.log([headerAux].concat(contentAux));
				contentAux = [headerAux].concat(contentAux);
				$.each(headerAux.split(','),function(index,value){
					dataHeader.push("column "+(index+1));
				});
			}
			
			var newData = contentAux;
		    return [dataHeader,newData];
		};
		var output = manageLabels(dataOrig);
		var dataHeader = output[0];
		var data = output[1];
		
		// Creation Form to set Columns to be user
		var legend = "<legend>Columns to be used:</legend>";
		var fields = "";
		$.each(dataHeader,function(index,value){
			fields = fields + "<input type=\"checkbox\" name=\"columns\" value=\""+index+"\" checked=\"checked\">"+value+"<br>";
		});
		$( "#columnsLinreg" ).html(legend + fields);
		
		
		// Creation Form to set the representation options		
		var legend = "<legend>Options:</legend>";
		var fields = "" +
				"<input id=\"input_hasHeader\" type=\"checkbox\" name=\"hasHeader\" value=\"1\" >First row is header<br/>" +
				"<input id=\"input_polinomialDegree\" type=\"input\" name=\"polinomialDegree\" value=\"1\">Polinomial degree<br/>";
		$( "#optionsLinreg" ).html(legend + fields);
		
		// Event handlers
		$("input#input_hasHeader").change(function () {
			var output = manageLabels(dataOrig);
			var dataHeader = output[0];
			var data = output[1];
			
			// Creation Form to set Columns to be user
			var legend = "<legend>Columns to be used:</legend>";
			var fields = "";
			$.each(dataHeader,function(index,value){
				fields = fields + "<input type=\"checkbox\" name=\"columns\" value=\""+index+"\" checked=\"checked\">"+value+"<br>";
			});
			$( "#columnsLinreg" ).html(legend + fields);
			
			dataHeader = dataHeader.join(',');
			var table = csvToTable(dataHeader,data);
			$( "#CSVTable" ).html(table);
			$( "#csvToTable" ).footable();
			
		});
		
		// Draw csvTable as a summary of data
		dataHeader = dataHeader.join(',');
		var table = csvToTable(dataHeader,data);
		$( "#CSVTable" ).html(table);
		$( "#csvToTable" ).footable();
		
	};
	
	funcError = function(error){console.log("error loading files - " + error.status);};
	var extraParams = {
			numLines: 5
	};
	getFileContent(idFile, funcSuccess, funcError, extraParams);
	
	
	// Buttons definition
	var buttons = {
	    Cancel: function() {
	        $( this ).dialog( "close" );
	    },
		"Ok": function() {
	        //$("#formLinreg").submit();
			
			key = "-11";//"PlotLinReg";
			
			//ParamDTO = $("#formLinreg").serializeObject();
			columns = $("input[name='columns']").serializeObject();
			hasHeader = $("input#input_hasHeader").is(":checked");
			polinomialDegree = $("input#input_polinomialDegree").val();
			ParamDTO = $.extend(columns,{"hasHeader":hasHeader},{"polinomialDegree":polinomialDegree});
			submitMathFunction(key,idFile,ParamDTO);
	        $( this ).dialog( "close" );
	    }
	};
	
	//Return the template
	return {
		"content":content,
		"title":title,
		"buttons":buttons
		};
}

var global_month_array = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

function numberToMonth(i) {
	ret = "";
	if (i>=0 && i <=11) {
		ret = global_month_array[i];
	}
	return ret; 
}

function getOrdinal(digitT) {
	ret = "th";
	if (digitT=="1") ret="st";
	if (digitT=="2") ret="nd";
	if (digitT=="3") ret="rd";
	return ret;
}

function dateToNice(date) {
	var year = date.getFullYear();
	var monthText = numberToMonth(date.getMonth());
	var day = date.getDate();
	dayText = day.toString();
	lastDigit = dayText[dayText.length-1];
	ord = getOrdinal(lastDigit);
	if (day==11) {
		ord = "th";
	}
	else {
		if (day==12) {
			ord = "th";
		}
	}
	var ret = monthText + " " + dayText + ord + " " + year;
	return ret;
}