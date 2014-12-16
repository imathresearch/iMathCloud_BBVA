<%@ page import="java.security.Principal" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
    <head>
        <meta charset="UTF-8">
        <title>iMath Connect</title>
        <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
        <!-- bootstrap 3.0.2 -->
        <link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
        <!-- font Awesome -->
        <link href="css/font-awesome.min.css" rel="stylesheet" type="text/css" />
        <!-- Ionicons -->
        <link href="css/ionicons.min.css" rel="stylesheet" type="text/css" />
        <!-- Morris chart -->
        <link href="css/morris/morris.css" rel="stylesheet" type="text/css" />
        <!-- jvectormap -->
        <link href="css/jvectormap/jquery-jvectormap-1.2.2.css" rel="stylesheet" type="text/css" />
        <!-- Date Picker -->
        <link href="css/datepicker/datepicker3.css" rel="stylesheet" type="text/css" />
        <!-- Daterange picker -->
        <link href="css/daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css" />
        <!-- bootstrap wysihtml5 - text editor -->
        <link href="css/bootstrap-wysihtml5/bootstrap3-wysihtml5.min.css" rel="stylesheet" type="text/css" />
        <!-- Theme style -->
        <link href="css/AdminLTE.css" rel="stylesheet" type="text/css" />
		<link rel="shortcut icon" href="images/favicon.ico">
		
		<link href="css/jquery.treeview.css" rel="stylesheet">
		<link href="css/jquery.contextMenu.css" rel="stylesheet"> 
		<link rel='stylesheet' href='css/codemirror.css'></link>
  		<link rel='stylesheet' href='css/eclipse.css'></link>
  		<link href="css/start/jquery-ui-1.10.0.custom.css" rel="stylesheet">  	
  		  		
	  	<link href="css/table.sorter.css" rel="stylesheet" type="text/css" />
  		
        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
          <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
        <![endif]-->
        
        
        <style>
        
        
		.wrapImgProfile {
			width: 250px;
			height: 268px; 
			overflow: hidden;
			background: #fff;
			margin: 10px;
			text-align: center;
			line-height: 150px;			
			position: relative
		}
		
		.wrapImgTable {
			width: 32px;
			height: 32px; 
			overflow: hidden;
			background: #fff;
			margin: 10px;
			text-align: center;
			line-height: 150px;			
			position: relative
		}
		
		.wrapImgUserAccount {
			width: 90px;
			height: 90px; 
			overflow: hidden;
			background: #3c8dbc;
			margin: 10px;
			text-align: center;
			line-height: 150px;			
			position: relative
		
		}
		
		.wrapImgUserState {
			width: 45px;
			height: 45px; 
			overflow: hidden;
			margin: 10px;
			text-align: center;
			line-height: 150px;			
			position: relative		
		}
		
		.wrapImgProfile img, .wrapImgTable img, .wrapImgUserAccount img, .wrapImgUserState img {
			max-width: 100%;
			max-height: 100%;
			vertical-align: middle;
			margin:0;
			position: absolute;
    		top: 50%;
    		left: 50%;
    		margin-right: -50%;
    		transform: translate(-50%, -50%)
		
		}
		
		td.profile {
		    padding: 8px;		    
		    display: inline-block;
		    margin: 0;
		    border: 0;
		    width: 250px;
		}
		
		td.spaced {
			margin-left: 40px;
		}
	
		.CodeMirror {
 			font-size: 10pt;   			
		}
		
		.nav-tabs-color{
			background: #3c8dbc;		
		}
		
		.nav-tabs-custom > .nav-tabs > li.header-color{
			color: white;
		}
		
		.nav-tabs-custom > .nav-tabs > li:not(.active) > a:hover, .nav-tabs-custom > .nav-tabs > li:not(.active) > a:focus, .nav-tabs-custom > .nav-tabs > li:not(.active) > a:active {
			border-color: transparent;
			color: black;
		}
        
        .nav-tabs-custom > .nav-tabs > li:not(.active) > a {
			border-color: transparent;
			color: white;
		}
		
		

		table.table-job{
			width: 100%;
			table-layout: fixed;
			font-size:8pt;		
		}
		
				
		th.jobDes{
			width: 65%
		}
		
		th.jobDate{
			width: 35%;
		}
		
		th, td {    		
    		overflow: hidden;
    		word-wrap:break-word    	
		}
		.footer {
			position: absolute;
    		bottom: 0;
		}
		body, html {
			height:100%;
			overflow: hidden;
		}
		
		.filetree {
		    overflow-y: auto;
    		overflow-x: auto;
		}
		
		.consolePythonButton{
			background-color: #FFFFFF;
			background: url(css/images/python-icon.png) no-repeat 10px center;
			border-color: #367fa9;
			border-radius: 10px;
			border-width: 5px;
			margin: 0px 3px;
			padding:  8px 12px;
			padding-left: 30px;
			color: #3c8dbc;
			font-size: 18px;
			font-style: normal;
			margin-right: 50px
		}
		
		.consoleRButton{
			background-color: #FFFFFF;
			background: url(css/images/r-icon.png) no-repeat 10px center;
			border-color: #367fa9;
			border-radius: 10px;
			border-width: 5px;
			margin: 0px 3px;
			padding:  8px 12px;
			padding-left: 30px;
			color: #3c8dbc;
			font-size: 18px;
			font-style: normal;
			margin-left: 50px;
			width: 200px;
		}
        </style>
        
    </head>
    <body class="skin-blue">
        <!-- header logo: style can be found in header.less -->
        <div class="wrapper row-offcanvas row-offcanvas-left">
            <!-- Right side column. Contains the navbar and content of the page -->
                <!-- Main content -->
                <section id="id-imath-content" class="content">

                    <!-- Main row -->
                    <div id="id-imath-content" class="row imath-main-row">
 						<section class="col-sm-2 connectedSortable">
 							<!-- Box Own projects -->
 							<div class="box box-solid box-primary imath-waiting-creation imath-waiting-files">
                                <div class="box-header">
                                    <h3 class="box-title"><i class="fa fa-folder-o"></i>&nbsp;&nbsp;&nbsp; Files</h3>
                                    <div class="box-tools pull-right">
										<div class="btn-group" data-toggle="btn-toggle">
											<button id="imath-id-refresh-files" type="button" class="btn btn-primary active"><i class="fa fa-refresh"></i></button>
										</div>
									</div>
                                </div><!-- /.box-header -->
                                <div class="box-body">
									<small id="remoteTreeContent">
										<ul id="remoteTree" class="filetree">
										</ul>
									</small>
                                </div><!-- /.box-body -->
                            </div><!-- /.box Own projects-->
                            
                            <!-- Box Jobs -->
                            <div class="box box-solid box-primary imath-waiting-jobs">
                                <div class="box-header">
                                    <h3 class="box-title"><i class="fa fa-bar-chart-o"></i>&nbsp;&nbsp;&nbsp; Jobs</h3>
                                    <div class="box-tools pull-right">
	                                    <div class="btn-group" data-toggle="btn-toggle">
											<button id="imath-id-refresh-jobs" type="button" class="btn btn-primary active"><i class="fa fa-refresh"></i></button>
										</div>
									</div>
                                </div><!-- /.box-header -->
                                <form role="form">
                                	<div id="id-imath-box-jobs" class="box-body">
                                		<div class="form-group">
		                                	<select id="selectJobState" class="form-control" style="font-size:10pt" onchange="filterJobState()" onclick="$(this).focus()">
						  						<option value="ALL">All jobs</option>
						  						<option value="RUNNING">Running</option>  							
						  						<option value="CANCELLED">Cancelled</option>
						  						<option value="FINISHED_OK">Finished OK</option>
						  						<option value="FINISHED_ERROR">Finished ERROR</option>
											</select>
										</div>
										<div id="divjobTable" style="width:100%; overflow-y:auto;" class="form-group">
		                                   	<table id="jobsXML" class="tablesorter table table-bordered table-job jobs-table">
		                                    	<thead>
													<tr>
														<!--th class="jobState"> </th>
														<th class="jobId">#</th-->
														<th class="jobDes">Description</th>
														<th class="jobDate">Started</th>
														<!--th>%</th-->
													</tr>
												</thead>
												<tbody id="jobsTBODY">
							 					</tbody>
		                                    </table>
	                                    </div>
									</div>
								</form>
                                
                            </div>
 						</section> 

 						<section id="editor-section" class="col-sm-5 connectedSortable"> 						 					 						
                            <div id="id-imath-box-editor" class="nav-tabs-custom nav-tabs-color box box-solid box-primary">

 						
	                            <ul id="id-imath-headTabsFile" class="nav nav-tabs pull-right">
	                            	<li class="pull-left header header-color">
	                            		<i class="fa fa-edit"></i>Editor	                            																			
	                            	</li>
	                            	<div id ="div-editor-resize-full" class="box-tools pull-right">
											<div class="btn-group" data-toggle="btn-toggle">
												<button id="editor-resize-full" type="button" style="border:0;box-shadow: none" class="btn btn-primary active"><i class="fa fa-expand"></i></button>
											</div>
									</div>
									<div id ="div-editor-resize-small" class="box-tools pull-right" style="display:none">
											<div class="btn-group" data-toggle="btn-toggle">
												<button id="editor-resize-small" type="button" style="border:0;box-shadow:none" class="btn btn-primary active"><i class="fa fa-compress"></i></button>
											</div>
									</div>
    	                        </ul>
                                <div id="tabsFile" class="tab-content span4">
                                </div>
                            </div>
 						</section>

 						<section id="console-section" class="col-sm-5 connectedSortable"> 

                            <div id="id-imath-box-console" class="nav-tabs-custom nav-tabs-color box-solid box-primary">
								<ul id="id-imath-headTabsConsole" class="nav nav-tabs pull-right">
                            		<li class="pull-left header header-color">
                            			<i class="fa fa-desktop"></i>Console 
                            			<div class="btn-group" data-toggle="btn-toggle">
											<button id="imath-id-new-console" type="button" style="border:0;box-shadow: none" class="btn btn-primary active"><i class="fa fa-plus"></i> </button>
										</div>
                            		</li>
                            		<div id ="div-console-resize-full"  class="box-tools pull-right">
											<div class="btn-group" data-toggle="btn-toggle">
												<button id="console-resize-full" type="button" style="border:0;box-shadow: none" class="btn btn-primary active"><i class="fa fa-expand"></i></button>
											</div>
									</div>
									<div id ="div-console-resize-small"  class="box-tools pull-right" style="display:none">
											<div class="btn-group" data-toggle="btn-toggle">
												<button id="console-resize-small" type="button" style="border:0;box-shadow: none" class="btn btn-primary active"><i class="fa fa-compress"></i></button>
											</div>
									</div>	
   	                        	</ul>
                               	<div id="tabsConsole" class="tab-content span4">
                               	</div>
                            </div>
 						</section>
                   	</div><!-- /.row (main row) -->
                </section><!-- /.content -->
        </div><!-- ./wrapper -->
		<div id="id-imath-footer" class="footer"></div>
		<!-- COMPOSE MESSAGE MODAL FOR CONFIRMATIONS-->
        <div class="modal fade" id="imath-id-conf-message" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title"><i class="fa fa-warning danger"></i> Confirmation! </h4>
					</div>
					<div class="modal-body">
						<p class="imath-conf-message"></p>
					</div>
					<div class="modal-footer clearfix">
						<button id="imath-id-ok-button-select" type="button" class="btn btn-danger" data-dismiss="modal"><i class="fa fa-times"></i> OK</button>
						<button id="imath-id-cancel-button-select" type="button" class="btn" data-dismiss="modal"><i class="fa fa-times"></i> Cancel</button>
					</div>
				</div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->

		<!-- COMPOSE MESSAGE MODAL FOR ERROR MESSAGES-->
        <div class="modal fade" id="imath-id-error-message-col" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title"><i class="fa fa-warning danger"></i> Error</h4>
					</div>
					<div class="modal-body">
						<p class="imath-error-message"></p>
					</div>
					<div class="modal-footer clearfix">
						<button id="imath-id-cancel-button-select" type="button" class="btn btn-danger" data-dismiss="modal"><i class="fa fa-times"></i> OK</button>
					</div>
				</div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->

		<div id="dialogPopup" class="modal fade" tabindex="-1" role="dialog">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title"><i class="fa fa-warning danger"></i>
							<span id="id-imath-title-dialog"></span>
						</h4>
					</div>
					<div id="contentPopup" class="modal-body">
					</div>
					<div id="id-imath-buttons-dialog" class="modal-footer clearfix">
					</div>	 
				</div>
			</div>
		</div>

        <!-- jQuery 2.0.2 -->
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
        <!-- jQuery UI 1.10.3 -->
        <script src="js/jquery-ui-1.10.3.min.js" type="text/javascript"></script>
        <!-- Bootstrap -->
        <script src="js/bootstrap.min.js" type="text/javascript"></script>
        <!-- Sparkline -->
        <script src="js/plugins/sparkline/jquery.sparkline.min.js" type="text/javascript"></script>
        <!-- jvectormap -->
        <script src="js/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js" type="text/javascript"></script>
        <script src="js/plugins/jvectormap/jquery-jvectormap-world-mill-en.js" type="text/javascript"></script>
        <!-- daterangepicker -->
        <script src="js/plugins/daterangepicker/daterangepicker.js" type="text/javascript"></script>
        <!-- datepicker -->
        <script src="js/plugins/datepicker/bootstrap-datepicker.js" type="text/javascript"></script>
        <!-- iCheck -->
        <script src="js/plugins/iCheck/icheck.min.js" type="text/javascript"></script>
		<script src="js/plugins/bootstrap-wysihtml5/bootstrap3-wysihtml5.all.min.js" type="text/javascript"></script>
        <!-- AdminLTE App -->
        <script src="js/AdminLTE/app.js" type="text/javascript"></script>
		<script src="js/plugins/ionslider/ion.rangeSlider.min.js" type="text/javascript"></script>
        <!-- AdminLTE dashboard demo (This is only for demo purposes) -->
        <script src="js/AdminLTE/dashboard.js" type="text/javascript"></script>
        <script src="js/AdminLTE/demo.js" type="text/javascript"></script>

		<!-- External libs -->
		<script src="js/libs/jquery.contextMenu.js" type="text/javascript"></script>
		<script src="js/libs/jquery-ui-1.10.0.custom.min.js" type="text/javascript"></script>
		<script src="js/libs/jquery.treeview.js" type="text/javascript"></script>
		<script src="js/libs/jquery.treeview.edit.js" type="text/javascript"></script>
		<script src="js/libs/codemirror.js" type="text/javascript"></script>
		<script src='js/libs/python.js'></script>
  		<script src='js/libs/r.js'></script>
  		<script src="js/flotcharts/jquery.flot.js" type="text/javascript"></script>
		<script src="js/flotcharts/jquery.flot.errorbars.js" type="text/javascript"></script>
		<script src="js/flotcharts/jquery.flot.boxplot.js" type="text/javascript"></script>
		<script src="js/libs/jquery.csv.min.js" type="text/javascript"></script>
		<script src="js/libs/serializeObject.js" type="text/javascript"></script>
		<!-- iMath JS files -->
		<script src="js/imath/files.js" type="text/javascript"></script>
		<script src="js/imath/jobs.js" type="text/javascript"></script>
		<script src="js/imath/toolbar.js" type="text/javascript"></script>
		<script src="js/imath/plugins.js" type="text/javascript"></script>
		<script src="js/imath/plotCSV.js" type="text/javascript"></script>
		<script src="js/imath/notebook.js" type="text/javascript"></script>
        <script src="js/imath/initialization.js" type="text/javascript"></script>
        
        <script type="text/javascript" src="js/libs/jquery.tablesorter.min.js"></script>
        
		<script type="text/javascript">
			var userName = "<%= request.getUserPrincipal().getName() %>";
		</script>
    </body>
	
</html>
