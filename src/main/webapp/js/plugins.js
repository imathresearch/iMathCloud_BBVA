function getUserMathFunctions() {

	$.ajax({
        url: "rest/plugin_service/getMathFunctions/" + userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(functions) {
			mathFunc= fillMathFuncForMenu(functions);
			getFiles(true);
        },
        error: function(error) {
            console.log("error loading files - " + error.status);
        }
    });
}

function fillMathFuncForMenu(functions) {
	var out="";
	for(var i=0; i<functions.length; i++) {
		func = functions[i];
		var aux="";
		if (i>0) {
			aux = ",";
		}
		aux += '"' + func['id'] + '": {"name": "' + func['shortName']+ '", "icon": "paste"}'; 
		out += aux;
	}
	generateEastButtons("basicStatistics", functions);
	return out;
}



function submitMathFunction(key,idFile,ParamDTO) {
	// Only for contextual menu
	// TODO: handle parameter: Now, fixed to 5...
//	console.log("ParamDTO: " + ParamDTO);
	if (typeof ParamDTO === "undefined") {
		ParamDTO = generateEmptyDTO();
	}
	ParamDTOS = JSON.stringify(ParamDTO);//(typeof ParamDTO == 'undefined'  || ParamDTO == '') ?  '' : JSON.stringify(ParamDTO);
	console.log("ParamDTO: " + ParamDTOS);
	$.ajax({
        url: "rest/plugin_service/submitMathFunction/" + userName + "/" + key + "/" + idFile ,// + "/" + ParamDTO,// +"/5",
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
		data : ParamDTOS,
        type: "POST",
        success: function(job) {
        	refreshJobsTable();
        	updateJob(job['id']);  
        },
        error: function(error) {
        	refreshJobsTable();
            console.log("Possible error submitting Math job -" + error.status);
        }
    });	
}

function generateEmptyDTO() {
	ParamDTO = {"columns":[],"hasHeader":false,"polinomialDegree":"1"}
	return ParamDTO;
}