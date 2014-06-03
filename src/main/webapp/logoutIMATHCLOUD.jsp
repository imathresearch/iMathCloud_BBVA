<html>
<body>
<%
	if(session!=null) {
		session.invalidate();
	} else{
%>
    Logged Out Successfully....
<% }%>
<script>
window.location.href=".";
</script>
</body>
</html>
