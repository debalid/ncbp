<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String reason = (String) request.getAttribute("reason");
    String reasonExplicit = (String) request.getAttribute("reasonExplicit");
    if (reason == null) reason = "Неизвестная ошибка";
    request.removeAttribute("reason");
    request.removeAttribute("reasonExplicit");
%>
<html>
<head>
    <title>Error - <%= reason %></title>
    <!-- Bootstrap css cdn -->
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
          crossorigin="anonymous" />
</head>
<body>
<h2> <%= reason %> </h2>
<% if (reasonExplicit != null) { %>
    <h5> <%= reasonExplicit %> </h5>
<% } %>

<!-- Bootstrap js cdn -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
        integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS"
        crossorigin="anonymous">
</script>
</body>
</html>
