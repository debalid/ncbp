<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
<div class="container">
    <div class="page-header">
        <c:choose>
            <c:when test="${!empty requestScope.orderAndAvailableClients.first.number}">
                <h1>Редактирование</h1>
            </c:when>
            <c:otherwise>
                <h1>Создание</h1>
            </c:otherwise>
        </c:choose>
    </div>

    <form method="post" action="<c:url value="/orders/save/"/>">
        <div class="form-group input-group">
            <span class="input-group-addon">Номер заказа</span>
            <c:choose>
                <c:when test="${!empty requestScope.orderAndAvailableClients.first.number}">
                    <input type="number" name="number" placeholder="Номер заказа" class="form-control"
                           value="${requestScope.orderAndAvailableClients.first.number}"
                           readonly/>
                </c:when>
                <c:otherwise>
                    <input type="number" name="number" placeholder="Номер заказа" class="form-control"
                           value="${requestScope.orderAndAvailableClients.first.number}"/>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="form-group input-group">
            <span class="input-group-addon">Дата заказа</span>
            <input type="date" name="date" placeholder="Дата" class="form-control"
                   value="${requestScope.orderAndAvailableClients.first.date}"/>
        </div>

        <div class="form-group input-group">
            <span class="input-group-addon">Сумма заказа</span>
            <input type="number" name="total" placeholder="Сумма заказа" class="form-control"
                   value="${requestScope.orderAndAvailableClients.first.priceTotal}"/>
            <span class="input-group-addon"><i class="fa fa-rub"></i></span>
        </div>


        <div class="form-group input-group">
            <span class="input-group-addon">Клиент</span>
            <select class="form-control" name="clientId" id="clientId">
                <li>
                    <option value="null">
                        Нет клиента
                    </option>
                </li>
                <c:forEach items="${requestScope.orderAndAvailableClients.second}" var="client">
                    <c:choose>
                        <c:when test="${requestScope.orderAndAvailableClients.first.client.id eq client.id}">
                            <li>
                                <option value="${client.id}" selected>
                                        ${client.title}, ${client.phone}
                                </option>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li>
                                <option value="${client.id}">
                                        ${client.title}, ${client.phone}
                                </option>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-default">Сохранить</button>
    </form>

</div>
</body>
</html>