function descStatisticsPlot(idDescStatistics, files, idJob) {
	this.files = files; // The list of generated files
	this.id = idDescStatistics;	// The DIV DOM id 
	this.idJob = idJob;		// The id Job that originated the result
	
	this.mean=null;		//File 'Mean'
	this.freq=null;		//File 'Frequency'
	this.stdev=null;	//File 'StandardDesviation'
	this.variance=null;	//File 'Variance'
	this.maximum = null;//File 'Maximum'
	this.minimum = null;//File 'Minimum'
	this.median = null;	//File 'Median'
	this.dataset = null;	//File 'Dataset'
	this.mode = null;	//File 'Mode'
	
	this.MEAN = 'Mean';
	this.FREQ = 'Frequency';
	this.STDEV = 'StandardDesviation';
	this.VARIANCE = 'Variance';
	this.MAXIMUM = 'Maximum';
	this.MINIMUM = 'Minimum';
	this.MEDIAN = 'Median';
	this.DATASET = 'Dataset';
	this.MODE = 'Mode';

	// The public methods
	this.loadDataAndPlot=loadDataAndPlot;
	this.loadSTDEV=loadSTDEV;
	this.loadMIN=loadMIN;
	this.loadMAX=loadMAX;
	this.loadMEDIAN=loadMEDIAN;
	this.loadBOXPLOT=loadBOXPLOT;
	
	this.loadMean=loadMean;
	this.findFileByName=findFileByName;
	this.plot=plot;
	this.plotMeanStDev=plotMeanStDev;
	this.plotMaxMin=plotMaxMin;
	this.plotBox=plotBox;
	
	/**
	 * Load all the data from the files and plot the result in the DIV section identified by this.id
	 * It will be a set of async ajax calls to retrieve file contents 
	 */
	function loadDataAndPlot() {
		this.loadMean(this.findFileByName(this.MEAN),this.id);
		this.loadBOXPLOT(this.findFileByName(this.DATASET),this.id);
	}

	function loadMIN(idFile, id) {
		getFileContent(idFile,
				function (fileDTO) {
					var obj = descStatisticsMap[id];
					obj.minimum = fileDTO;
					obj.plot();
				}, 
				function (error) {
					console.log("error opening file -" + error.status);
				});
	};
	
	function loadMAX(idFile, id) {
		getFileContent(idFile,
				function (fileDTO) {
					var obj = descStatisticsMap[id];
					obj.maximum = fileDTO;
					obj.loadMIN(obj.findFileByName(obj.MINIMUM),id);
					//descStatisticsMap[id].plot();
				}, 
				function (error) {
					console.log("error opening file -" + error.status);
				});
	};
	
	function loadSTDEV(idFile, id) {
		getFileContent(idFile,
				function (fileDTO) {
					var obj = descStatisticsMap[id];
					obj.stdev = fileDTO;
					obj.loadMAX(obj.findFileByName(obj.MAXIMUM),id);
					//descStatisticsMap[id].plot();
				}, 
				function (error) {
					console.log("error opening file -" + error.status);
				});
	};
	
	function loadMean(idFile, id) {
		getFileContent(idFile,
				function (fileDTO) {
					var obj = descStatisticsMap[id];
					obj.mean = fileDTO;
					obj.loadSTDEV(obj.findFileByName(obj.STDEV),id);
				}, 
				function (error) {
					console.log("error opening file -" + error.status);
				});
	};
	
	function loadMEDIAN(idFile, id) {
		getFileContent(idFile,
				function (fileDTO) {
					var obj = descStatisticsMap[id];
					obj.median = fileDTO;
				}, 
				function (error) {
					console.log("error opening file -" + error.status);
				});
	};
	
	
	function loadBOXPLOT(idFile, id) {
		getFileContent(idFile,
				function (fileDTO) {
					var obj = descStatisticsMap[id];
					obj.dataset = fileDTO;
				}, 
				function (error) {
					console.log("error opening file -" + error.status);
				});
	};
	
	
	function findFileByName(str) {
		var found = false;
		var i=0;
		while (!found && i<this.files.length) {
			found = (files[i]['name'].indexOf(str)!=-1);
			i=i+1;
		}
		if (found) {
			return files[i-1]['id'];
		}
	};
	
	function plot() {
		var u = document.getElementById('tabsFile');
		var he = u.offsetHeight;
		var wi = u.offsetWidth;
		he = Math.round(he - he*0.6);
		wi = Math.round(wi - wi*0.15);
		
		this.plotMeanStDev(he,wi);
		this.plotMaxMin(he,wi);
		this.plotBox(he,wi);
		
	};
	
	function plotMeanStDev(he,wi) {
		// Plot mean and stdev
		htmlCode = "<h2> MEAN and STDEV</h2>";
		htmlCode += "<div class=\"flot-container\" style=\"width:" + wi + "px;height:" + he + "px;\">";
		htmlCode += "<div class=\"flot-placeholder\" id=\"plotMEAN_STDEV_" + this.id + "\" style=\"width:" + wi + "px;height:" + he + "px;\"></div>";
		
		$("#"+this.id).append(htmlCode);
		var meanVal = this.mean['content'][0].split(',');
		var stdevVal = this.stdev['content'][0].split(',');
		
		var dataValsAll = new Array();
		var dataVals = new Array();
		var max;
		var min;
		for(i=0;i<meanVal.length;i++) {
			dataValsAll.push([i+1,meanVal[i],stdevVal[i]]);
			dataVals.push([i+1,meanVal[i]]);
			var cmax = parseFloat(meanVal[i]) + parseFloat(stdevVal[i]);
			var cmin = parseFloat(meanVal[i]) - parseFloat(stdevVal[i]);
			if(i==0) {
				max = cmax;
				min = cmin;
			}
			else {
				if (cmax>max) {
					max = cmax;
				}
				if (cmin <min) {
					min = cmin;
				}
			}
		}
		var idPlot = "plotMEAN_STDEV_" + this.id;
		
		var dataPoints = {
			radius: 0,
			errorbars: "y", 
			yerr: {show:true, upperCap: "-", lowerCap: "-", radius: 5}
		};
		var l1 = {color:"orange", bars: {show: true, align: "center"}, data: dataVals, label:"MEAN/STDEV"};
		var l2 = {color:"orange", points: dataPoints, data: dataValsAll};
		var dataPlot = [l1,l2];
			
		$.plot("#"+idPlot, dataPlot, {
			series: {lines: {show: false}},
			grid: { backgroundColor: { colors: [ "#fff", "#eee" ] }},
			yaxis: {
				min: min-0.2*min,
				max: max+0.2*max
			}
		});
	};
	
	function plotMaxMin(he,wi) {
		// Plot mean and stdev
		htmlCode = "<h2> Maximum and Minimum</h2>";
		htmlCode += "<div class=\"flot-container\" style=\"width:" + wi + "px;height:" + he + "px;\">";
		htmlCode += "<div class=\"flot-placeholder\" id=\"plotMax_MIN_" + this.id + "\" style=\"width:" + wi + "px;height:" + he + "px;\"></div>";
		var idPlot = "plotMax_MIN_" + this.id;
		
		$("#"+this.id).append(htmlCode);
		var meanVal = this.mean['content'][0].split(',');
		var maxVal = this.maximum['content'][0].split(',');
		var minVal = this.minimum['content'][0].split(',');
		
		var dataMeanVal = new Array();
		var dataMaxVal = new Array();
		var dataMinVal = new Array();
		var max;
		var min;
		for(i=0;i<meanVal.length;i++) {
			dataMeanVal.push([i+1,meanVal[i]]);
			dataMaxVal.push([i+1,maxVal[i]]);
			dataMinVal.push([i+1,minVal[i]]);
			var cmax = parseFloat(maxVal[i]);
			var cmin = parseFloat(minVal[i]);
			if(i==0) {
				max = cmax;
				min = cmin;
			}
			else {
				if (cmax>max) {
					max = cmax;
				}
				if (cmin <min) {
					min = cmin;
				}
			}
		}
		
		var l1 = {points: { show: true }, lines: {show: true}, data: dataMaxVal, label:"MAX"};
		var l2 = {points: { show: true }, lines: {show: true}, data: dataMeanVal, label:"MEAN"};
		var l3 = {points: { show: true }, lines: {show: true}, data: dataMinVal, label:"MIN"};
		var dataPlot = [l1,l2, l3];
			
		$.plot("#"+idPlot, dataPlot, {
			series: {lines: {show: false}},
			grid: { backgroundColor: { colors: [ "#fff", "#eee" ] }},
			yaxis: {
				min: min-0.2*min,
				max: max+0.2*max
			}
		});
	};
	
	function plotBox(he,wi) {
		// Plot Box-Plot
		htmlCode = "<h2> Box-plot</h2>";
		htmlCode += "<div class=\"flot-container\" style=\"width:" + wi + "px;height:" + he + "px;\">";
		htmlCode += "<div class=\"flot-placeholder\" id=\"plotBOX_" + this.id + "\" style=\"width:" + wi + "px;height:" + he + "px;\"></div>";
		var idPlot = "plotBOX_" + this.id;
		
		$("#"+this.id).append(htmlCode);
//		var datasetVal = this.dataset['content'][0].split(',');
//		var dataDatasetVal = new Array();

		var d1 = [[-5],[-10],[10],[2],[1],[20],[30]];
		var d2 = [[-4],[-20],[4],[12],[30]];
		var dataPlot = [[[0.2985],[0.7099],[0.1952],[1.0408],[0.8748],[1.2405],[0.2225],[0.1864],[0.7311],[1.9436],[0.2249],[1.7635],[0.6074],[0.6062],[0.7332],[0.1127],[0.0623],[0.0962],[0.4398],[0.9411]]];//[d1,d2];		
		$.plot("#"+idPlot, dataPlot, {
			series:{boxplot:{show: true}},
			grid: { backgroundColor: { colors: [ "#fff", "#eee" ] }}
		});
	};
	
};


