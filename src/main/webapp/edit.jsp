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
        <c:when test="${!empty requestScope.orderAndAvailableClients.first.number}">
            <input type="number" name="number" placeholder="Номер заказа"
                   value="${requestScope.orderAndAvailableClients.first.number}"
                   disabled/>
        </c:when>
        <c:otherwise>
            <input type="number" name="number" placeholder="Номер заказа"
                   value="${requestScope.orderAndAvailableClients.first.number}"/>
        </c:otherwise>
    </c:choose>
    <input type="date" name="date" placeholder="Дата"
           value="${requestScope.orderAndAvailableClients.first.date}"/>
    <input type="number" name="total" placeholder="Сумма заказа"
           value="${requestScope.orderAndAvailableClients.first.priceTotal}"/>
    <ul>
        <c:forEach items="${requestScope.orderAndAvailableClients.second}" var="client">
            <c:choose>
                <c:when test="${requestScope.orderAndAvailableClients.first.client.id eq client.id}}">
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