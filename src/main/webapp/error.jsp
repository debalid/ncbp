<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Error - ${requestScope.reason}
    </title>
    <!-- Bootstrap css cdn -->
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
          crossorigin="anonymous"/>
</head>
<body>
<h2>
    <c:choose>
        <c:when test="${!empty requestScope.reason}">
            ${requestScope.reason}
        </c:when>
        <c:otherwise>
            Неизвестная ошибка
        </c:otherwise>
    </c:choose>
</h2>
<c:if test="${!empty requestScope.reasonExplicit}">
    <h5>${requestScope.reasonExplicit}</h5>
</c:if>

<!-- Bootstrap js cdn -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
        integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS"
        crossorigin="anonymous">
</script>
</body>
</html>
