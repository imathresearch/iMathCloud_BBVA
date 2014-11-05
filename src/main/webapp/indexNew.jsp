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
        
        </style>
        
    </head>
    <body class="skin-blue">
        <!-- header logo: style can be found in header.less -->
        <header class="header">
            <a href="indexNew.jsp" class="logo">
                <!-- Add the class icon to your logo image or logo icon to add the margining -->
                iMath CLOUD
            </a>
            <!-- Header Navbar: style can be found in header.less -->
            <nav class="navbar navbar-static-top" role="navigation">
                <!-- Sidebar toggle button-->
                <a href="#" class="navbar-btn sidebar-toggle" data-toggle="offcanvas" role="button">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>
                <div class="navbar-right">
                    <ul class="nav navbar-nav">
                    
                        <!-- Project Account: style can be found in dropdown.less -->
                        <li class="dropdown user user-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="glyphicon glyphicon-user"></i>
                                <span class="projectname"></span> <i class="caret"></i>
                            </a>
                            <ul class="dropdown-menu">
                                <!-- User image -->
                                <li class="user-header bg-light-blue">
                                    <div class="wrapImgUserAccount"><img id="userphoto" src="img/user-bg.png" class="img-circle userImg" alt="Project Image" /></div>
                                    <p class="projectname">-</p>
                                    <p class="projectcreationdate">-</p>
                                </li>
                                <!-- Menu Footer-->
                                <li class="user-footer">
                                    <div class="pull-right">
                                        <a href="logout" class="btn btn-primary active">Sign out</a>
                                    </div>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </nav>
        </header>
        
        <div class="wrapper row-offcanvas row-offcanvas-left">
            <!-- Right side column. Contains the navbar and content of the page -->
            <aside class="right-side">
                <!-- Content Header (Page header) -->
                <section class="content-header">
                    <h1 class="imath-title-menu">
                    </h1>
                    <ol class="breadcrumb">
                        <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
                        <li class="active">Dashboard</li>
                    </ol>
                </section>

                <!-- Main content -->
                <section class="content">

                    <!-- Main row -->
                    <div class="row imath-main-row">
 						<section class="col-lg-5 connectedSortable">
 							<!-- Box Own projects -->
 							<div class="box imath-waiting-creation imath-waiting-own-projects">
                                <div class="box-header">
                                    <h3 class="box-title"><i class="fa fa-bar-chart-o"></i>&nbsp;&nbsp;&nbsp; Files</h3>
                                    <div class="box-tools pull-right">
										<div class="btn-group" data-toggle="btn-toggle">
											<button id="imath-id-new-project-button-dashboard" type="button" class="btn btn-primary active">New</button>
										</div>
									</div>
                                </div><!-- /.box-header -->
                                <div class="box-body">
                                    <table class="table table-bordered imath-own-projects">
                                    </table>
                                </div><!-- /.box-body -->
                            </div><!-- /.box Own projects-->
                            
                            <!-- Box Collaborative projects -->
                            <div class="box imath-waiting-col-projects">
                                <div class="box-header">
                                    <h3 class="box-title"><i class="fa fa-bar-chart-o"></i>&nbsp;&nbsp;&nbsp; Jobs</h3>
                                </div><!-- /.box-header -->
                                <div class="box-body">
                                    <table class="table table-bordered imath-collaborations">
                                    </table>
                                </div><!-- /.box-body -->
                            </div><!-- /.box collaborations-->
 						</section> 
 						<section class="col-lg-7 connectedSortable">
 						    <!-- Box Own instances -->
                            <div class="box imath-waiting-own-instances">
                                <div class="box-header">
                                    <h3 class="box-title"><i class="fa fa-cog"></i>&nbsp;&nbsp;&nbsp; File Editor</h3>
                                </div><!-- /.box-header -->
                                <div class="box-body">
                                    <table class="table table-bordered imath-own-instances">
                                    </table>
                                </div><!-- /.box-body -->
                            </div><!-- /.box Own Instances-->
 						 
 							<!-- Box public instances -->
                            <div class="box imath-waiting-pub-instances">
                                <div class="box-header">
                                	<i class="fa-cop"></i> 
                                    <h3 class="box-title"><i class="fa fa-cog"></i>&nbsp;&nbsp;&nbsp; Console</h3>
                                </div><!-- /.box-header -->
                                <div class="box-body">
                                    <table class="table table-bordered imath-public-instances">
                                    </table>
                                </div><!-- /.box-body -->
                            </div><!-- /.box public instances-->
 						</section>
                   	</div><!-- /.row (main row) -->

                </section><!-- /.content -->
            </aside><!-- /.right-side -->
        </div><!-- ./wrapper -->

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

        <!-- The following modals permit to visualize user's profile, modify password and modify photograph -->


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

        <!-- AdminLTE for demo purposes -->
        <script src="js/AdminLTE/demo.js" type="text/javascript"></script>
		<script type="text/javascript">
			var userName = "<%= request.getUserPrincipal().getName() %>";
			alert(userName);
		</script>

    </body>
	
</html>