<html>
<body>
<%
try {
	request.login(request.getParameter("j_username"), request.getParameter("j_password"));
	session = request.getSession();
	response.sendRedirect("indexNew.jsp"); 
} catch(ServletException e) {
    // Login failed
    response.sendRedirect("loginerror.html");
}
%>
</body>
</html>
