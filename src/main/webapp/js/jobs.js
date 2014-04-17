function runJob(idFile){
	
//	Console.log("Inside runJob");
	
	if (typeof ParamDTO === "undefined") {
		ParamDTO = generateEmptyDTO();
	}
	
	//ParamDTOS = JSON.stringify(ParamDTO);//(typeof ParamDTO == 'undefined'  || ParamDTO == '') ?  '' : JSON.stringify(ParamDTO);
	//console.log("ParamDTO: " + ParamDTOS);
	
	$.ajax({
        url: "rest/jobpython_service/submitJob/" + userName + "/" + idFile ,// + "/" + ParamDTO,// +"/5",
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
		data : ParamDTO,
        type: "POST",
        success: function(host) {
        	refreshJobsTable();
        	refreshFilesTree();
        },
        error: function(error) {
        	refreshJobsTable();
            console.log("Possible error submitting Python job -" + error.status);
        },
    });	
	
	
}



function getJobs(b) {
	$.ajax({
        url: "rest/job_service/getJobs/" + userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(jobs) {
			fillJobs(jobs);
			if (b) {
				$( "#jobsXML" ).footable();
			}
			setDefaultLanguage(mathLanguageCode);
        },
        error: function(error) {
            console.log("error loading files - " + error.status);
        }
    });
}


function fillJobs(jobs) {
	for(var i=0; i<jobs.length; i++) {
		job = jobs[i];
		var date = new Date(job['startDate']);
		var aux = "<tr>";
		aux = aux +"<td>" + getImageJobStatus(job['state']) + "</td>";
		aux = aux + "<td><a onclick='showJobStatus(\""+ job['id'] + "\")' + >" + job['id'] + "</a></td>";
		aux = aux + "<td><a onclick='showJobStatus(\""+ job['id'] + "\")' + >" + job['description'] + "</a></td>";
		aux = aux + '<td data-value="' + job['startDate'] + '">' + date + '</td>';		
		aux = aux + '<td data-value="' + jobPercentCompletion(job) + '">' + jobPercentCompletion(job) + '</td>';
		aux = aux + "</tr>";
		$( "#jobsTBODY" ).append(aux);
	}
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
	var aux;
	switch(status) {
		case "RUNNING":
			aux = '<img src="css/images/job_running.png" alt="Running">';
			break;
		case "FINISHED_OK":
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
		
		//JSON format {"files":[["", "src", "file1.txt], ["", "file2.txt"]], "dirs": [["", "src"], ["", "src", "new_dir"]]}
		//JSON.parse can throw an exception
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
		}
		
		
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
	showDialog(aux);
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
	 
	var tabs = $( "#tabs" ).tabs();
	tabs.find( ".ui-tabs-nav" ).append( li );

	tabs.append( "<div id='" + id + "' style='position: relative; width: 100%; height:100%; padding: 0; overflow : auto;'></div>" );
	
	var obj = new descStatisticsPlot(id,files,idJob);
	descStatisticsMap[id] = obj;
	obj.loadDataAndPlot();
	
	tabs.tabs( "refresh" );
	
	globalTabCounting++;
	//addTabInstancePlot(data['id'],globalTabCounting);
	$( "#tabs" ).tabs("option", "active", globalTabCounting);
	tabs.tabs( "refresh" );
}

function refreshJobsTable() {
	$("#jobsTBODY").remove();
	var aux = '<tbody id="jobsTBODY"></tbody>';
	$("#jobsXML").append(aux);
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

