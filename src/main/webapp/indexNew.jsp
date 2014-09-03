<%@ page import="java.security.Principal" %>

<!-- apps/frontend/templates/layoutimath.php -->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>iMathCloud - Your Math in the Cloud</title>
	<!--link rel="stylesheet" type="text/css" href="js/codebase/dhtmlx.css"></link-->
	<link href="css/start/jquery-ui-1.10.0.custom.css" rel="stylesheet">
	<link href="css/imath.css" rel="stylesheet">

	<!-- link href="css/jquery.dataTables_themeroller.css" rel="stylesheet"-->
	<link href="css/jquery.treeview.css" rel="stylesheet">
	<link href="css/jquery.contextMenu.css" rel="stylesheet">
	<link href="css/footable-0.1.css" rel="stylesheet" type="text/css" />
	<link href="css/table.sorter.css" rel="stylesheet" type="text/css" />
		
	<!-- External Libs -->
	<script src="js/libs/jquery-1.9.0.js" type="text/javascript"></script>
	<script src="js/libs/jquery-ui-1.10.0.custom.min.js" type="text/javascript"></script>
	<script src="js/libs/jquery.contextMenu.js" type="text/javascript"></script>
	<script src="js/libs/jquery.layout-latest.js" type="text/javascript"></script>
	<script src="js/libs/jquery.treeview.js" type="text/javascript"></script>
	<script src="js/libs/jquery.treeview.edit.js" type="text/javascript"></script>
	<!-- <script src="js/libs/data-generator.js" type="text/javascript"></script> --><!-- This file doesn't exist! --> 
	<script src="js/libs/footable-0.1.js" type="text/javascript"></script>
	<script src="js/flotcharts/jquery.flot.js" type="text/javascript"></script>
	<script src="js/flotcharts/jquery.flot.errorbars.js" type="text/javascript"></script>
	<script src="js/flotcharts/jquery.flot.boxplot.js" type="text/javascript"></script>
	<script src="js/libs/jquery.csv.min.js" type="text/javascript"></script>
	<script src="js/libs/serializeObject.js" type="text/javascript"></script>
    <script type="text/javascript" src="js/libs/jquery.tablesorter.min.js"></script>
     	
	<!-- Own Libs -->
	<script src="js/plot/plotCSV.js" type="text/javascript"></script>
	<script src="js/files.js" type="text/javascript"></script>
	<script src="js/jobs.js" type="text/javascript"></script>
	<script src="js/plugins.js" type="text/javascript"></script>
	<script src="js/toolbar.js" type="text/javascript"></script>
	<script src="js/notebook.js" type="text/javascript"></script>
	<script src="js/Plugin/desc.statistics.js" type="text/javascript"></script>
	<script src="js/initialization.js" type="text/javascript"></script>
	
	<style type="text/css">
			html, body {
			margin: 0px;
			padding: 0px;
			background:		#66C;	/* color page so it can be seen */
			font-family: Cambria, Palatino, "Palatino Linotype", "Palatino LT STD", Georgia, serif;
			color: #1d3c41;
			font-weight: 400;
			font-size: 15px;
		}

		.ui-layout-mask {
			background:	#C00 !important;
			opacity:	.20 !important;
			filter:		alpha;
		}
		.ui-layout-pane {
			color:			#000;
			background:		#EEE;
		}
		
		table.dataTable {
			margin: 0px;
		}
		
		iframe {
			padding:	0 !important; /* iframes should not have padding */
			overflow:	auto !important;
		}
		
		.ui-layout-pane { /* all 'panes' */ 
			border: 1px solid #BBB; 
		} 
		.ui-layout-pane-center { /* IFRAME pane */ 
			padding: 0;
			margin:  0;
		}
		 
		.ui-layout-pane-west { /* west pane */ 
			padding: 0; 
			margin:  0;
			background-color: #EEE !important;
			overflow: auto;
		} 

		.ui-layout-resizer { /* all 'resizer-bars' */ 
			background: #DDD; 
			} 
			
		.ui-layout-resizer-open:hover { /* mouse-over */
			background: #9D9; 
		}

		.ui-layout-toggler { /* all 'toggler-buttons' */ 
			background: #AAA; 
		} 
		.ui-layout-toggler-closed { /* closed toggler-button */ 
			background: #CCC; 
			border-bottom: 1px solid #BBB; 
		} 
		/*.ui-layout-toggler .content { /* toggler-text */ 
			/*font: 14px bold Verdana, Verdana, Arial, Helvetica, sans-serif;*/
		/*}*/
		
		.ui-layout-toggler:hover { /* mouse-over */ 
			background: #DCA; 
		} 
		.ui-layout-toggler:hover .content { /* mouse-over */ 
			color: #009; 
		}
		/* masks are usually transparent - make them visible (must 'override' default) */
		.ui-layout-mask {
			background:	#C00 !important;
			opacity:	.20 !important;
			filter:		alpha(opacity=20) !important;
		}
		
		.ui-accordion .ui-accordion-content {
			padding: 0;
		}
		.ui-tab .ui-tab-content {
			padding: 0;
		}
		.treeview ul {
    		background-color: transparent;
   		 	margin-top:4px;
		}	
		.toolbar {
			padding: 0px;
			display: inline-block;
		}
		#tabs li .ui-icon-close { float: left; margin: 0.4em 0.2em 0 0; cursor: pointer; }
		.CodeMirror {border-top: 1px solid black; border-bottom: 1px solid black;}
		
		.codrops-top {
    		line-height: 24px;
		    font-size: 11px;
		    /*background: none repeat scroll 0% 0% rgba(255, 255, 255, 0.4);*/
		    background: url(css/images/bg.jpg) repeat top left;
		    text-transform: uppercase;
		    z-index: 9999;
		    position: relative;
		    box-shadow: 1px 0px 2px rgba(0, 0, 0, 0.2);
		 }
		 
		.codrops-top span.right {
    		float: right;
		}
		
		.codrops-top a {
 		   	letter-spacing: 1px;
    		color: #333;
    		text-shadow: 0px 1px 1px #FFF;
		}
		.codrops-top span.right a {
		    float: left;
    		display: block;
		}
		
		/*Background image for the editor*/
		.bg-image {			
			background: #fcfdfd url(css/images/bg.png) 50% 50% no-repeat;
		}
		
		/*Background white for each tab of the editor, so the default editor image (above) does not appear*/
		.ui-tabs .ui-tabs-panel {
			background: #fcfdfd none;
		}

	</style>
	
  </head>
  
  <body>
  	<link rel='stylesheet' href='css/codemirror.css'></link>
  	<link rel='stylesheet' href='css/eclipse.css'></link>
  	<!--script src="js/layoutInteractiveMath.js" type="text/javascript"></script-->
  	<script src='js/libs/codemirror.js'></script>
  	<script src='js/libs/python.js'></script>
  	<script src='js/libs/r.js'></script>


<!--  The static top bar for logout and other methods-->
 

	<div class="ui-layout-north" id="ui-layout-north">
		<div class="codrops-top">
    		<span class="right">
    			<button id="selfButton">Profile</button>
    			<button id="logoutButton">Log out</button>
    			<!-- a href="logoutIMATHCLOUD.jsp">
        			<strong>Logout</strong>
        		</a-->
			</span>
    		<div class="clr"></div>
    	</div>
	</div><!--/ Codrops top bar -->
	
	<!-- The initial filling stuff of the interactive math prototype -->            
	<!-- The left side panel, which contains file navigation system, team box access, and jobs-->
	<div class="ui-layout-west" id="ui-layout-west" style="width: 100%; height:100%;">
		<div id="menu_west1" style="margin: 0;">
			<h2>Remote Files</h2>
				<div id="divRemoteFiles" style="height:300px; overflow:auto">
					<div id="toolbarFiles" class="ui-widget-header ui-corner-all">
						<small>
						<button id="refreshTreeButton">Refresh tree</button>
						<button id="newNotebookButton" class="ui-widget ui-state-default ui-corner-all">New Notebook</button>
						<!-- <button id="uploadFileButton">Upload file</button>-->
						<!-- button id="newDirectoryButton">New directory</button-->
						<!-- button id="newFileButton">New File</button-->
						<!-- button id="copyButton">Copy</button-->
						<!-- button id="pasteButton">Paste</button-->
						<!-- button id="deleteButton">Delete</button-->
						</small>
					</div>
					<small id="remoteTreeContent">
						<ul id="remoteTree" class="filetree">
						</ul>
						<ul id="remoteTreeShared" class="filetree">
						</ul>
					</small>
				</div>
		</div>
		<div id="menu_west3">
			<h2>Submitted Jobs</h2>
				<div id = "divJobs" style="padding: 0;height:300px; overflow:auto"> 
					<div id="toolbarJobs" class="ui-widget-header ui-corner-all">
						<small>
						<button id="refreshJobButton">Refresh jobs list</button>
						<select id="selectJobState" class="ui-widget ui-state-default ui-corner-all" onchange="filterJobState()">
  							<option value="ALL">All jobs</option>
  							<option value="RUNNING">Running</option>  							
  							<option value="CANCELLED">Cancelled</option>
  							<option value="FINISHED_OK">Finished OK</option>
  							<option value="FINISHED_ERROR">Finished ERROR</option>
						</select>
						<!-- button id="viewJobButton">View job data</button-->
						<!-- button id="plotJobButton">Plot job output</button-->
						<!-- button id="runJobButton">Run job</button-->
						<!-- button id="pauseJobButton">Pause job</button-->
						<!-- button id="cancelJobButton">Cancel job</button-->
						<!-- button id="deleteJobButton">Delete job</button-->
						</small>
					</div>
					<table id="jobsXML" class="footable tablesorter" border="0">
					<thead>
						<tr>
							<th> </th>
							<th>Job#</th>
							<th>Description</th>
							<th>Started</th>
							<th>% Compl.</th>
						</tr>
					</thead>
					<tbody id="jobsTBODY">
 					</tbody>
					</table>
				</div>
			<!--/div-->
		</div>
	</div>

<!-- The central panel, which contains tabs, including interactive console-->
	<div class="ui-layout-center" id="ui-layout-center" style="width: 100%; height:100%;">
		<div id="tabsFile" class="bg-image" style="position: absolute; width: 100%; height:100%;">
			<ul>				
			</ul>				
		</div>
	</div>

<!-- The right panel, which contains functionalities. It is empty now-->

	<div class="ui-layout-east" id="ui-layout-east" style="width: 100%; height:100%;">
		<div id="tabsConsole" style="position: absolute; width: 100%; height:100%;">
			<ul>
				<!--li><a href="#tabsC-0">iMathConsole</a></li-->
			</ul>	
			<!-- div id="tabsC-0" style="padding: 0;">
				<iframe id="interactive_math-0" class="interactive_math" width="100%" frameborder="0" scrolling="no"></iframe>
			</div-->
		</div>
	</div>

	
	<!-- The pop up forms -->
<div id="dialogPopup">
<div id="contentPopup"></div>
</div>

<!-- The pop up form for the sharing options -->
<div id="sharingOptions">
<div id="contentSharingOptions"></div>
</div>

<div id="profilePopup">
<div id="profile" class="form">
	<form  action="javascript:void(0);" method="post" autocomplete="off" >
    	<h1> Profile - Change Password </h1> 
    	<p> 
            <label for="passwordsignup" class="youpasswd" data-icon="p">Your current password </label>
            <input id="passwordOld" name="passwordOld" required="required" type="password" placeholder="eg. X8df!90EO"/>
        </p>
        <p> 
            <label for="passwordsignup" class="youpasswd" data-icon="p">Your new password </label>
            <input id="passwordNew" name="passwordNew" required="required" type="password" placeholder="eg. X8df!90EO"/>
        </p>
        <p> 
            <label for="passwordsignup_confirm" class="youpasswd" data-icon="p">Please confirm your new password </label>
            <input id="passwordNewConf" name="passwordNewConf" required="required" type="password" placeholder="eg. X8df!90EO"/>
        </p>

        <div id="profileMsg" style="float:left"></div>
		<div style="float:right"> <input id="changePassButton" type="button" value="Change"/></div>
	</form>
</div>
</div>
<!-- a href="logoutIMATHCLOUD.jsp">
        			<strong>Logout</strong>
        		</a-->
<script type="text/javascript">

var userName = "<%= request.getUserPrincipal().getName() %>";
var rootElement = "";

$("#logoutButton").button({
	text: false,
	icons: { primary: "ui-icon-arrowthickstop-1-e" }
});
$("#selfButton").button({
	text: false,
	icons: { primary: "ui-icon-person" }
});
$("#logoutButton").click(function() { 
	window.location.href = "logout";
});

$("#selfButton").click(function() {
	$("#profilePopup").dialog();
	$("#profilePopup form")[0].reset()
	$("#profilePopup div#profileMsg").html("");
});

$("#changePassButton").click(function() {

    url = "changePassword";
    $("#profileMsg").html("");
	$.ajax({
        url: url,
        cache: false,
        data: JSON.stringify($("div#profile form").serializeObject()),
        type: "POST",
        success: function(data) {
            $("#profileMsg").html("<span style='color:green'>" + data + "</span>");
            setTimeout("$('#profilePopup').dialog('close')",1500);
        },
        error: function(data) {
        	$("#profileMsg").html("<span style='color:red'>" + data.responseText + "</span>");
        }
	});
});



</script>
  </body>
</html> 
