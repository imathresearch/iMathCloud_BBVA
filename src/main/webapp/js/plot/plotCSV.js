/**
 * csvPlot.js
 * 
 * The javascript object that contains the basic plotting options for plain csv data
 */


/**************
 * The Object definition
 */

function csvPlot(data, idPlot, mode) {
	this.data = data;			// The data, as an array of arrays of values (coming from csv file)
	this.idPlot = idPlot;		// The DOM id element where to plot the data
	this.dataVar = null;		// The current data plot in the format of flot
	this.typePlot = null;		// The current data Type plot for each serie
	this.jsonData = null;		// The current json data object structure ofthe current plot
	this.mode = mode;
	
	//** The public methods
	this.plotDataSeries = plotDataSeries;		
	this.plotAll = plotAll;
	this.addRadioOptions = addRadioOptions;
	this.replot=replot;
	
/** 
 * The basic plot function.
 * Data must be placed in  array of pairs [x,y]. For example, serie d1 = [[1,1],[2,4],[3,8]],
 * serie d2 = [[1,1],[2,2],[3,3]]. Then to plot the data both series in lines, in a DIV id 'idStuff' we could do
 * plotDataSeries(['d1','d2'],'idStuff',['lines','lines'])
 * 
 * @param dataVar An array of arrays of pairs 
 * @param idPlot The DOM identifier (usually a DIV tag) that will host the plot
 * @param typePlot An array of String indicating the type of the serie {'lines','bars', 'points'
 */
	function plotDataSeries(dataVar, typePlot) {
		
		
		var jsonVar = '[';
		for(i=0; i<dataVar.length; i++) {
			if (i>0) {
				jsonVar +=',';
			}
			var str = '{"label":"'+ this.mode + (i+1)+'", "data":'+ JSON.stringify(dataVar[i]);
			if (typePlot[i] == "boxplot") {
				str +="}";
			} else {
				str += ',"'+typePlot[i] + '":{"show":"true"}}';
			}
			jsonVar += str;
		}
		jsonVar += ']'; 
		this.jsonData = JSON.parse(jsonVar);
		
		if (typePlot[0] == "boxplot") {
			$.plot("#"+this.idPlot, this.jsonData, {
				series:{boxplot:{show: true}}, 
				grid: { backgroundColor: { colors: [ "#fff", "#eee" ] }}
			});
		} else {
			$.plot("#"+this.idPlot, this.jsonData, {
				grid: { backgroundColor: { colors: [ "#fff", "#eee" ] }}
			});
		}
		this.dataVar = dataVar;
		
		
	};
	
	function addRadioOptions(typePlot, mode) {
		var htmlOptions = '<br><br><form id="' + 'radio_'+this.idPlot+ '" action="">';
		htmlOptions += '<input class="' + 'radio_'+this.idPlot+'" type="radio" name="type" value="lines" onclick="plotMap[\''+this.idPlot+'\'].plotAll(\'lines\')" ' + placeChecked(typePlot,'lines') + '>Lines';
		htmlOptions += '<input class="' + 'radio_'+this.idPlot+'" type="radio" name="type" value="bars" onclick="plotMap[\''+this.idPlot+'\'].plotAll(\'bars\')" ' + placeChecked(typePlot,'bars') + '>Bars';
		htmlOptions += '<input class="' + 'radio_'+this.idPlot+'" type="radio" name="type" value="points" onclick="plotMap[\''+this.idPlot+'\'].plotAll(\'points\')" ' + placeChecked(typePlot,'points') + '>Points';
		htmlOptions += '<input class="' + 'radio_'+this.idPlot+'" type="radio" name="type" value="boxplot" onclick="plotMap[\''+this.idPlot+'\'].plotAll(\'boxplot\')" ' + placeChecked(typePlot,'boxplot') + '>Boxplot';
		htmlOptions += '</form>';
		
		htmlOptions += '<form id="' + 'radio2_'+this.idPlot+ '" action="">';
		htmlOptions += '<input class="' + 'radio2_'+this.idPlot+'" type="radio" name="colfil" value="col" onclick="plotMap[\''+this.idPlot+'\'].mode=\'col\'; plotMap[\''+this.idPlot+'\'].replot()" ' + placeChecked(mode,'col') + '>By comlumns';
		htmlOptions += '<input class="' + 'radio2_'+this.idPlot+'" type="radio" name="colfil" value="fil" onclick="plotMap[\''+this.idPlot+'\'].mode=\'fil\'; plotMap[\''+this.idPlot+'\'].replot()" ' + placeChecked(mode,'fil') + '>By rows';
		htmlOptions += '</form>';
		
		$("#options_"+this.idPlot).append(htmlOptions);
		this.mode = mode;
		this.plotAll(typePlot);
	};
	
	function replot() {
		this.plotAll(this.typePlot);
	};
	/**
	 * Plot all data (from columns 0 to size-1) in the same type 
	 * 
	 * @param data Non-empty two dimensional
	 * @param idPlot
	 * @param typePlot
	 */
	function plotAll(typePlot) {
		this.typePlot=typePlot;
		var iLen = this.data.length;
		if (iLen>0) {
			var jLen = this.data[0].length;
			var dataVar = getStandardSerie(0,iLen,0,jLen,this.data, this.mode);
			var typeArr = new Array();
			for(i=0;i<jLen;i++) {
				typeArr[i]=typePlot;
			}
			this.plotDataSeries(dataVar,typeArr);
		}
		
	};
};
	
	
/**
 * Return an array of arrays of pairs ready to be plotted. 
 * @param iFrom The data gathered will be from iFrom
 * @param iTo until iTo (not included)
 * @param data The entry data. We assume we have an array of arrays of values
 */
function getStandardSerie(iFrom, iTo, jFrom, jTo, data, mode) {
	var ret = new Array();
	if (mode=='col') {
		for(j=0;j<jTo-jFrom;j++) {
			ret[j] = new Array();
			ret[j]=[];
		}
		for(i=iFrom; i< iTo; i++) {
			for(j=jFrom;j<jTo-jFrom;j++) {
				ret[j-jFrom].push([i-iFrom,data[i][j]]);
			}
		}
	}
	else {
		for(i=0;i<iTo-iFrom;i++) {
			ret[i] = new Array();
			ret[i]=[];
		}
		for(i=iFrom; i< iTo; i++) {
			for(j=jFrom;j<jTo-jFrom;j++) {
				ret[i-iFrom].push([j-jFrom,data[i][j]]);
			}
		}
	}
	
	return ret;
}

function placeChecked(typePlot,typeToCompare) {
	if (typePlot==typeToCompare) {
		return "checked";
	}
	return "";
}