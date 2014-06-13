<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta charset="UTF-8" />
    <title>iMath Cloud Login Page</title>
    <link href="css/demo.css" rel="stylesheet" type="text/css" />
	<link href="css/style2.css" rel="stylesheet" type="text/css" />
	<link href="css/animate-custom.css" rel="stylesheet" type="text/css" />
	<script src="js/libs/jquery-1.9.0.js" type="text/javascript"></script>
</head>

<br><br>

<body>
<div class="container">
<section>				
<div id="container_demo" >

<div id="wrapper">
	<a class="hiddenanchor" id="toregister"></a>
    <a class="hiddenanchor" id="tologin"></a>
	<div id="login" class="animate form">
    	<form  action="login.jsp" method="POST" autocomplete="on"> 
        	<h1>iMath Cloud - Log in</h1> 
            	<p> 
                	<label for="username" class="uname" data-icon="u" > Your email or username </label>
                    <input id="username" name="j_username" required="required" type="text" placeholder="myusername or mymail@mail.com"/>
                </p>
                <p> 
                	<label for="password" class="youpasswd" data-icon="p"> Your password </label>
                    <input id="password" name="j_password" required="required" type="password" placeholder="eg. X8df!90EO" /> 
                </p>
                <p class="keeplogin"> 
					<input type="checkbox" name="loginkeeping" id="loginkeeping" value="loginkeeping" /> 
					<label for="loginkeeping">Keep me logged in</label>
				</p>
                <p class="login button"> 
                	<input type="submit" value="Login" /> 
				</p>
                <!--p class="change_link">
					Not a member yet ?
					<a href="#toregister" class="to_register">Join us</a>
				</p-->
		</form>
	</div>
	<!-- div id="register" class="animate form">
    	<form  action="" autocomplete="on"> 
			<h1> Sign up </h1> 
            	<p> 
                	<label for="usernamesignup" class="uname" data-icon="u">Your username</label>
                    <input id="usernamesignup" name="usernamesignup" required="required" type="text" placeholder="mysuperusername690" />
				</p>
				<p> 
					<label for="emailsignup" class="youmail" data-icon="e" > Your email</label>
					<input id="emailsignup" name="emailsignup" required="required" type="email" placeholder="mysupermail@mail.com"/> 
				</p>
				<p> 
					<label for="passwordsignup" class="youpasswd" data-icon="p">Your password </label>
					<input id="passwordsignup" name="passwordsignup" required="required" type="password" placeholder="eg. X8df!90EO"/>
				</p>
				<p> 
					<label for="passwordsignup_confirm" class="youpasswd" data-icon="p">Please confirm your password </label>
					<input id="passwordsignup_confirm" name="passwordsignup_confirm" required="required" type="password" placeholder="eg. X8df!90EO"/>
				</p>
				<p class="signin button"> 
					<input type="submit" value="Sign up"/> 
				</p>
				<p class="change_link">  
					Already a member ?
					<a href="#tologin" class="to_register"> Go and log in </a>
				</p>
		</form>
	</div-->
</div>
</div>
</section>
</div>
</body>
</html>

<!-- 
<form action="j_security_check" method=post>
    <p><strong>Please Enter Your User Name: </strong>
    <input type="text" name="j_username" size="25">
    <p><p><strong>Please Enter Your Password: </strong>
    <input type="password" size="15" name="j_password">
    <p><p>
    <input type="submit" value="Submit">
    <input type="reset" value="Reset">
</form> -->