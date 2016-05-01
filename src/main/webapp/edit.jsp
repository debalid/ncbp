<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
    <!-- Bootstrap css cdn -->
    <link rel="stylesheet"
          href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
          crossorigin="anonymous"/>
    <!-- Font Awesome css cdn -->
    <link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.min.css">
</head>
<body>
<form>
    <c:choose>
        <c:when test="${!empty requestScope.order.number}">
            <input type="number" name="number" placeholder="Номер заказа" value="${requestScope.order.number}"
                   disabled/>
        </c:when>
        <c:otherwise>
            <input type="number" name="number" placeholder="Номер заказа" value="${requestScope.order.number}"/>
        </c:otherwise>
    </c:choose>
    <input type="date" name="date" placeholder="Дата" value="${requestScope.order.date}"/>
    <input type="number" name="total" placeholder="Сумма заказа" value="${requestScope.order.priceTotal}"/>
    <ul>
        <c:forEach items="${requestScope.availableClients}" var="client">
            <c:choose>
                <c:when test="${requestScope.order.client.title = client.title}}">
                    <li><input type="radio" value="${client.title}" checked/></li>
                </c:when>
                <c:otherwise>
                    <li><input type="radio" value="${client.title}"/></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </ul>
</form>
</body>
</html>