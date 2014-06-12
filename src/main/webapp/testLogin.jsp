<html>
<body>
<%
request.login("ipinyolTest", "test");
session = request.getSession();
%>
<script>
window.location.href=".";
</script>
</body>
</html>
