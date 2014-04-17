<%@ page import="java.security.Principal" %>
<%@ page import="java.util.Date" %>

<!-- apps/frontend/templates/layoutimath.php -->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Big Cloud</title>
	

        <link rel="stylesheet" href="bootstrap-integration/css/bootstrap.min.css" />
        <link href="http://cdn.kendostatic.com/2014.1.318/styles/kendo.common-bootstrap.min.css" rel="stylesheet" />
        <link href="http://cdn.kendostatic.com/2014.1.318/styles/kendo.bootstrap.min.css" rel="stylesheet" />
        <link href="http://cdn.kendostatic.com/2014.1.318/styles/kendo.dataviz.min.css" rel="stylesheet" />
        <link href="http://cdn.kendostatic.com/2014.1.318/styles/kendo.dataviz.bootstrap.min.css" rel="stylesheet" />

        <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
        <script src="http://cdn.kendostatic.com/2014.1.318/js/kendo.all.min.js"></script>
        <script src="http://cdn.kendostatic.com/2014.1.318/js/kendo.timezones.min.js"></script>


        <script src="bootstrap-integration/js/examples.js"></script>
        <script src="js/jobs.js" type="text/javascript"></script>
        
        <link rel="stylesheet" href="bootstrap-integration/styles.css" />
  </head>
  <body>
         <header>
            <div class="container" class="row">
                <h2 class="col-sm-3 hidden-sm">SMEs &hearts; BIG DATA</h2>
                <h2 class="col-sm-3 visible-sm">SMEs &hearts;<br/> BIGDATA</h2>

                <div id="configurator-wrap" class="col-sm-9 hidden-xs">
                    <div id="configurator" class="row">
                        <label class="col-sm-4">
                        	<div class="description">Last connection</div>
                        	<h1 style="font-size: 15px; padding-top: 0px" ><%= new Date() %></h1>
                        </label>

                        <label class="col-sm-4">
                            <div class="description">Current connection</div>
                            <h1 style="font-size: 15px;padding-top: 0px" ><%= new Date() %></h1>
                        </label>

                        <label class="col-sm-4">
                            <div class="description">User</div>
                            <h1 style="font-size: 21px; padding-top: 0px" ><%= request.getUserPrincipal().getName() %></h1>
                            
                        </label>
                    </div>
                </div>
            </div>
        </header>
  
  	<div id="example" class="container">
  		<section class="well">
        	<h2 class="ra-well-title">¡Congratulations!</h2>
            <p>You are one of our valuable beta testers. Feel free to try it out.</p>
            <p>This site runs on the top of iMath Cloud, the awarded Cloud platform for Big Data.</p>
            <p>We would greatly appreciate if you would complete this survey.</p>
        </section>
        <ul id="menu">
            <li><a href="#service">Services</a></li>
            <li><a href="#resources">Resources</a></li>
            <li><a href="#faq">FAQ</a></li>
        </ul>
  	 	<div class="row clearfix">
			<div class="col-lg-4">
				<section id="service" class="well">
					<h2 class="ra-well-title">Sales Prediction Service</h2>
					<div class="row">
						<div class="col-lg-5 col-sm-2">
							<img src="bootstrap-integration/images/service_avatar.jpg" class="ra-avatar img-responsive" />
	                	</div>
						<div class="col-lg-7 col-sm-2">
							<span class="ra-first-name">Big Data Service</span>
							<span class="ra-last-name">Sales Predictions</span>
							<div class="ra-position">Demo service for beta release </div>
						</div>
	                </div>
				</section>
			</div>
			<div class="col-lg-8">
				<div id="tabstrip" class="ra-section">
					<ul>
						<li class="k-state-active"><span class="km-icon revenue"></span><span class="hidden-xs">Revenue</span></li>
						<li><span class="km-icon spd"></span><span class="hidden-xs">Sales / day</span></li>
						<li><span class="km-icon spr"></span><span class="hidden-xs">Sales / region</span></li>
					</ul>
					<div><div id="revenue"></div></div>
					<div><div id="sales-per-day"></div></div>
					<div><div id="sales-per-region"></div></div>
				</div>
			</div>
		</div>
	    
	    
	    <div class="row clearfix">
			<div class="col-lg-4">
				<section id="service2" class="well">
					<h2 class="ra-well-title">Tweeter Service Alert</h2>
					<div class="row">
						<div class="col-lg-5 col-sm-2">
							<img src="bootstrap-integration/images/service_tweeter.png" class="ra-avatar img-responsive" />
	                	</div>
						<div class="col-lg-7 col-sm-2" style="height:350px">
							<span class="ra-first-name">Big Data Service</span>
							<span class="ra-last-name">Tweeter Alert</span>
							<div class="ra-position">Demo service for beta release </div>
						</div>
	                </div>
				</section>
			</div>
			<div class="col-lg-8">
				<div id="tabstrip2" class="ra-section">
					<ul>
						<li class="k-state-active"><span class="km-icon"></span><span class="hidden-xs">Mood Analysis</span></li>
						<li><span class="km-icon"></span><span class="hidden-xs">Notifications</span></li>
					</ul>
					<div><div id="radial-words-mood"></div></div>
					<div><div id="notifications">
		              	<div id="appendto" class="k-block" style="float:left;width:500px;height: 150px;margin: 1em 0;overflow: auto;"></div>
		              	<div id="appendto2" class="k-block" style="float:left;width:500px;height: 150px;margin: 1em 0;overflow: auto;"></div>
		              	<br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>
		              	<span id="staticNotification"></span>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row clearfix">
			<div class="col-lg-4">
				<section id="resources" class="well">
					<h2 class="ra-well-title">Executed Resources</h2>
					<div class="row">
						<div class="col-lg-5 col-sm-2">
							<img src="bootstrap-integration/images/process_running.jpg" class="ra-avatar img-responsive" />
	                	</div>
						<div class="col-lg-7 col-sm-2" style="height:350px">
							<span class="ra-first-name">Resources</span>
							<span class="ra-last-name">Background Processes</span>
							<div class="ra-position">Jobs running in iMathCloud </div>
						</div>
	                </div>
				</section>
			</div>
			<div class="col-lg-8">
				<table id="exec-table" class="ra-section" border="0">
					<colgroup>
	                    <col style="width:50px"/>
	                    <col style="width:100px"/>
	                    <col style="width:400px" />
	                    <col style="width:200px" />
	                    <col style="width:200px" />
	                </colgroup>
					<thead>
						<tr>
							<th> </th>
							<th>Job#</th>
							<th>Description</th>
							<th>Started</th>
							<th>% Completion</th>
						</tr>
					</thead>
					<tbody id="jobsTBODY">
 					</tbody>
				</table>
			</div>
		</div>
		
	  	<section id="faq" class="well">
			<h2 class="ra-well-title"><abbr title="Frequently Asked Questions">FAQ</abbr></h2>
		        <ul id="panelbar" class="ra-well-overlay">
					<li class="k-state-active">
						What is BigCloud?
						<div>
							<p>BigCloud is a web platform that runs over iMath Cloud and that compiles a set of big data services.</p>
							<p><a href="http://localhost:8080/com.iMathCloud">Access to iMath Cloud vi</a> and do it yourself!</p>
						</div>
					</li>
				</ul>
		</section>
	</div>
	<div id="dialogPopup">
	<div id="contentPopup"></div>
</div>
	<footer>Copyright &copy; 2014 <a href="http://www.imathresearch.com">iMath Research S.L.</a>. All rights reserved.</footer>

<!-- The initial filling stuff of the interactive math prototype -->
<script>
var userName = "<%= request.getUserPrincipal().getName() %>";
$("#menu").kendoMenu();

function resizeTabStripContent() {
    kendo.resize("#tabstrip");
    kendo.resize("#tabstrip2");
}
getJobs(false);

$("#exec-table").kendoGrid({
    height: 430,
    scrollable: true,
    sortable: true,
    filterable: true,
});

$("#tabstrip").kendoTabStrip({
    animation: {
        open: { effects: "fadeIn" }
    },
    activate: resizeTabStripContent
});

$("#tabstrip2").kendoTabStrip({
    animation: {
        open: { effects: "fadeIn" }
    },
    activate: resizeTabStripContent
});

$("#panelbar").kendoPanelBar();
// resize nested charts when window resizes
$(window).resize(resizeTabStripContent);

function initCharts(theme) {
    $(".k-tabstrip .k-content .k-chart").empty().each(function() {
        $(this).removeClass(".k-chart").removeData();
    });
    createChart();
    $("#sales-per-day").kendoChart({
        transitions: false,
        theme: theme,
        chartArea: {
            margin: {
                right: 10
            },
            background: "transparent"
        },
        legend: {
            visible: false
        },
        seriesDefaults: {
            type: "verticalBullet"
        },
        axisDefaults: {
            categories: ["Oct 1", "Oct 2", "Oct 3", "Oct 4", "Oct 5", "Oct 6",
                         "Oct 7", "Oct 8", "Oct 9", "Oct 10", "Oct 11", "Oct 12",
                         "Oct 13", "Oct 14", "Oct 15", "Oct 16", "Oct 17", "Oct 18",
                         "Oct 19", "Oct 20", "Oct 21", "Oct 22", "Oct 23", "Oct 24",
                         "Oct 25", "Oct 26", "Oct 27", "Oct 28", "Oct 29", "Oct 30", "Oct 31",
                         "Nov 1", "Nov 2", "Nov 3", "Nov 4", "Nov 5", "Nov 6",
                         "Nov 7", "Nov 8", "Nov 9", "Nov 10", "Nov 11", "Nov 12",
                         "Nov 13", "Nov 14", "Nov 15", "Nov 16", "Nov 17", "Nov 18",
                         "Nov 19", "Nov 20", "Nov 21", "Nov 22", "Nov 23", "Nov 24",
                         "Nov 25", "Nov 26", "Nov 27", "Nov 28", "Nov 29", "Nov 30",],
            line: {
                visible: false
            }
        },
        series: [{
            name: "All Products",
            data: [[1903, 5000], [2398, 5250], [1893, 5500], [3452, 5750], [6567, 6000], [5892, 6250], [2354, 6500],
                    [7238, 6750], [11574, 7000], [6392, 7250], [8932, 7500], [9320, 7750], [7894, 8000],
                    [9456, 8250], [12745, 8500], [16705, 8750], [19802, 9000], [15076, 9250], [17892, 9500],
                    [12983, 9750], [9034, 10000], [8902, 10250], [7893, 10500], [14562, 10750], [10235, 11000],
                    [23901, 11250], [17892, 11500], [11982, 11750], [1093, 12000], [4598, 12250], [3457, 12500],
                    [6092, 12750], [7892, 13000], [14562, 13250], [13200, 13500], [16502, 13750], [18902, 14000],
                    [16702, 14250], [10946, 14500], [12093, 14750], [19704, 15000], [17903, 15250], [17892, 15500],
                    [16783, 15750], [9845, 16000], [22904, 16250], [8934, 16500], [12983, 16750], [22876, 17000],
                    [20981, 17250], [21873, 17500], [24981, 17750], [23873, 18000], [18376, 18000], [21783, 18000],
                    [15672, 18000], [19456, 18000], [17998, 18000], [21673, 18000], [18234, 18000],[26932, 18000]
                   ]
        }],
        valueAxis: {
            labels: {
                step: 2
            },
            plotBands: [{
                from: 10000, to: 20000, color: "#ff0000", opacity: .05
            }]
        },
        categoryAxis: {
            labels: {
                visible: false
            },
            majorGridLines: {
                visible: false
            }
        },
        tooltip: {
            visible: true
        }
    });

    $("#sales-per-region").kendoChart({
        transitions: false,
        theme: theme,
        legend: {
            visible: false
        },
        chartArea: {
            margin: {
                top: 10,
                right: 8,
                bottom: 0,
                left: 10
            },
            background: "transparent"
        },
        seriesDefaults: {
            type: "area",
            area: {
                line: {
                    style: "smooth"
                }
            },
            stack: true
        },
        axisDefaults: {
            categories: ["Oct 1", "Oct 2", "Oct 3", "Oct 4", "Oct 5", "Oct 6",
                         "Oct 7", "Oct 8", "Oct 9", "Oct 10", "Oct 11", "Oct 12",
                         "Oct 13", "Oct 14", "Oct 15", "Oct 16", "Oct 17", "Oct 18",
                         "Oct 19", "Oct 20", "Oct 21", "Oct 22", "Oct 23", "Oct 24",
                         "Oct 25", "Oct 26", "Oct 27", "Oct 28", "Oct 29", "Oct 30", "Oct 31",
                         "Nov 1", "Nov 2", "Nov 3", "Nov 4", "Nov 5", "Nov 6",
                         "Nov 7", "Nov 8", "Nov 9", "Nov 10", "Nov 11", "Nov 12",
                         "Nov 13", "Nov 14", "Nov 15", "Nov 16", "Nov 17", "Nov 18",
                         "Nov 19", "Nov 20", "Nov 21", "Nov 22", "Nov 23", "Nov 24",
                         "Nov 25", "Nov 26", "Nov 27", "Nov 28", "Nov 29", "Nov 30",],
            line: {
                visible: false
            }
        },
        series: [{
            name: "Barcelona",
            data: [345, 823, 672, 1200, 3456, 2901, 800, 4562,
                    6721, 2453, 4521, 6712, 2891, 2932, 6721,
                    7812, 3742, 7812, 7892, 8912, 4525, 5682,
                    2453, 7562, 2435, 6781, 7891, 8991, 200,
                    2000, 1892, 4891, 4352, 1234, 7891, 3921,
                    8912, 6781, 8787, 8991, 5782, 10982, 12634,
                    6891, 1892, 14512, 4561, 2573, 9347, 7792,
                    8826, 7935, 19234, 7724, 9001, 8764, 13562,
                    15671, 18924, 10992, 3646]
        },{
            name: "Badalona",
            data: [524, 782, 891, 901, 2123, 278, 892, 872, 2003,
                    2010, 2831, 809, 1093, 2003, 3007, 5009, 4006,
                    3005, 5002, 2032, 3094, 2893, 3456, 4213, 4567,
                    6538, 4652, 1234, 234, 756, 234, 432, 678, 4567,
                    2347, 4623, 6578, 4662, 1040, 1909, 3495, 2398,
                    2654, 5842, 3556, 6233, 2613, 3452, 6772, 2553,
                    4772, 7889, 2374, 7856, 2345, 4563, 3456, 1436,
                    1123, 3445, 3456]
        },{
            name: "L'Hospitalet",
            data: [1034, 793, 330, 1351, 988, 2713, 662, 1804, 2850,
                    1929, 1580, 1799, 3910, 4521, 3017, 3884, 12054,
                    4259, 4998, 2039, 1415, 327, 1984, 2787, 3233,
                    10582, 5349, 1757, 659, 1842, 1331, 769, 2862,
                    8761, 2962, 7958, 3412, 5259, 1119, 1193, 10427,
                    4523, 2604, 4050, 4397, 2159, 1760, 6958, 6757,
                    10636, 8275, 9157, 2265, 2796, 10437, 2345, 2438,
                    891, 1626, 3797, 19830]
        }],
        valueAxis: {
            labels: {
                step: 2
            }
        },
        categoryAxis: {
            labels: {
                visible: false
            },
            majorGridLines: {
                visible: false
            }
        },
        tooltip: {
            visible: true,
            template: "#= series.name # <br /> #= category #: #= value #"
        }
    });

    $("#revenue").kendoChart({
        transitions: false,
        theme: theme,
        legend: {
            visible: false
        },
        chartArea: {
            margin: {
                top: 10,
                right: 5,
                bottom: 0,
                left: 10
            },
            background: "transparent"
        },
        seriesDefaults: {
            type: "column",
            style: "smooth",
            stack: true,
            width: 2,
            markers: {
                visible: false
            }
        },

        axisDefaults: {
            categories: ["Oct 1", "Oct 2", "Oct 3", "Oct 4", "Oct 5", "Oct 6",
                         "Oct 7", "Oct 8", "Oct 9", "Oct 10", "Oct 11", "Oct 12",
                         "Oct 13", "Oct 14", "Oct 15", "Oct 16", "Oct 17", "Oct 18",
                         "Oct 19", "Oct 20", "Oct 21", "Oct 22", "Oct 23", "Oct 24",
                         "Oct 25", "Oct 26", "Oct 27", "Oct 28", "Oct 29", "Oct 30", "Oct 31",
                         "Nov 1", "Nov 2", "Nov 3", "Nov 4", "Nov 5", "Nov 6",
                         "Nov 7", "Nov 8", "Nov 9", "Nov 10", "Nov 11", "Nov 12",
                         "Nov 13", "Nov 14", "Nov 15", "Nov 16", "Nov 17", "Nov 18",
                         "Nov 19", "Nov 20", "Nov 21", "Nov 22", "Nov 23", "Nov 24",
                         "Nov 25", "Nov 26", "Nov 27", "Nov 28", "Nov 29", "Nov 30",],
            line: {
                visible: false
            }
        },
        series: [{
            name: "Barcelona",
            data: [686.55, 1637.77, 1337.28, 2388, 6877.44, 5772.99, 1592, 9078.38,
                    13374.79, 4881.47, 8996.79, 13356.88, 5753.09, 5834.68, 13374.79,
                    15545.88, 7446.58, 15545.88, 15705.08, 17734.88, 9004.75, 11307.18,
                    4881.47, 15048.38, 4845.65, 13494.19, 15703.09, 17892.09, 398, 3980,
                    3765.08, 9733.09, 8660.48, 2455.66, 15703.09, 7802.79, 17734.88,
                    13494.19, 17486.13, 17892.09, 11506.18, 21854.18, 25141.66, 13713.09,
                    3765.08, 28878.88, 9076.39, 5120.27, 18600.53, 15506.08, 17563.74,
                    15790.65, 38275.66, 15370.76, 17911.99, 17440.36, 26988.38, 31185.29,
                    37658.76, 21874.08, 7255.54]
        },{
            name: "Badalona",
            data: [1199.96, 1790.78, 2040.39, 2063.29, 4861.67, 636.62, 2042.68, 1996.88,
                    4586.87, 4602.9, 6482.99, 1852.61, 2502.97, 4586.87, 6886.03, 11470.61,
                    9173.74, 6881.45, 11454.58, 4653.28, 7085.26, 6624.97, 7914.24, 9647.77,
                    10458.43, 14972.02, 10653.08, 2825.86, 535.86, 1731.24, 535.86, 989.28,
                    1552.62, 10458.43, 5374.63, 10586.67, 15063.62, 10675.98, 2381.6, 4371.61,
                    8003.55, 5491.42, 6077.66, 13378.18, 8143.24, 14273.57, 5983.77, 7905.08,
                    15507.88, 5846.37, 10927.88, 18065.81, 5436.46, 17990.24, 5370.05,
                    10449.27, 7914.24, 3288.44, 2571.67, 7889.05, 7914.24]
        },{
            name: "L'Hospitalet",
            data: [1540.66, 1181.57, 491.7, 2012.99, 1472.12, 4042.37, 986.38, 2687.96, 4246.5,
                    2874.21, 2354.2, 2680.51, 5825.9, 6736.29, 4495.33, 5787.16, 17960.46,
                    6345.91, 7447.02, 3038.11, 2108.35, 487.23, 2956.16, 4152.63, 4817.17,
                    15767.18, 7970.01, 2617.93, 981.91, 2744.58, 1983.19, 1145.81, 4264.38,
                    13053.89, 4413.38, 11857.42, 5083.88, 7835.91, 1667.31, 1777.57, 15536.23,
                    6739.27, 3879.96, 6034.5, 6551.53, 3216.91, 2622.4, 10367.42, 10067.93,
                    15847.64, 12329.75, 13643.93, 3374.85, 4166.04, 15551.13, 3494.05, 3632.62,
                    1327.59, 2422.74, 5657.53, 29546.7]
        }],
        valueAxis: {
            labels: {
                step: 2,
                template: "$#= value #"
            },
            plotBands: [{
                from: 30000, to: 50000, color: "#a7c9e6", opacity: .3
            }]
        },
        categoryAxis: {
            labels: {
                visible: false
            },
            majorGridLines: {
                visible: false
            }
        },
        tooltip: {
            visible: true,
            template: "#= series.name # <br /> #= category #: $#= value #"
        }
    });
}

function createChart() {
    $("#radial-words-mood").kendoChart({
        title: {
            text: "Mood Analysis [0: Very negative, 50: Neutral, 100: Very POsitive]"
        },
        legend: {
            position: "bottom"
        },
        seriesDefaults: {
            type: "radarLine"
        },
        series: [{
            name: "Average Mood Points",
            data: [16, 65, 21, 75, 10, 49, 80, 16, 10, 90, 67, 76, 91, 25, 12]
        },{
            name: "Current Mood Points (1 week) ",
            data: [64, 85, 97, 27, 16, 26, 35, 32, 26, 17, 10, 7, 19, 5]
        }],
        categoryAxis: {
            categories: ["Santander", "JP Morgan", "La Caixa", "Credit Suisse",
                "Goldman Sachs", "Bankia", "Catalunya Caixa", "UBS",
                "Banesto", "Banc Sabadell", "BBVA", "Deutsche Bank",
                "Barclays", "Citigroup", "Bankinter"]
        },
        valueAxis: {
            labels: {
                format: "{0}"
            }
        },
        tooltip: {
            visible: true,
            format: "{0} ptn"
        }
    });
}

var categories = ["Santander", "JP Morgan", "La Caixa", "Credit Suisse",
              "Goldman Sachs", "Bankia", "Catalunya Caixa", "UBS",
              "Banesto", "Banc Sabadell", "BBVA", "Deutsche Bank",
              "Barclays", "Citigroup", "Bankinter"];
              
initCharts("bootstrap");
var staticNotification = $("#staticNotification").kendoNotification({
    appendTo: "#appendto",
    button: true
}).data("kendoNotification");

var staticNotification2 = $("#staticNotification").kendoNotification({
    appendTo: "#appendto2",
    button: true
}).data("kendoNotification");

timer = setTimeout("newNotification()", 2000);

function newNotification() {
	var ind=Math.floor(Math.random()*15);
	var text = categories[ind];
	var value = Math.floor(Math.random()*100);
	var d = new Date();
	var fullText = kendo.toString(d, 'HH:MM:ss.') + " - " + text + ": " + value;
	if(value >= 50) {
    	staticNotification.show(fullText);
    	var container = $(staticNotification.options.appendTo);
    	container.scrollTop(container[0].scrollHeight);
	} else {
		staticNotification2.show(fullText, "error");
    	var container = $(staticNotification.options.appendTo);
    	container.scrollTop(container[0].scrollHeight);
	} 
		
    
    var milis = Math.floor(Math.random()*4000);
    timer = setTimeout("newNotification()", milis);
}

function showDialog(content, title, buttons) {
	$("#contentPopup").html(content);
	//$("#dialogPopup").dialog('option', 'title', title);
	
	//$("#dialogPopup").dialog({
	//	'buttons':buttons,
	//	'width':'auto',
	//	'height':'auto'
	//	});
	//$("#dialogPopup").dialog("open");

	var win = $("#dialogPopup").kendoWindow({
        width: 'auto',
        title: title,
        visible: false,
        actions: [
            "Pin",
            "Minimize",
            "Maximize",
            "Close"
        ]
    }).data("kendoWindow");
	win.center();
	win.open();    
}

</script>
</body>
</html> 
