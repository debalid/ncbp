<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        <h1>Заказы</h1>
    </div>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>Номер заказа</th>
            <th>Дата</th>
            <th>Сумма заказа</th>
            <th>Клиент</th>
            <th>Телефон</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="order" items="${requestScope.orders}">
            <tr>
                <td>
                        ${order.number}
                </td>
                <td>
                        ${order.date}
                </td>
                <td>
                    <i class="fa fa-rub"></i>
                        ${order.priceTotal}
                </td>
                <td>
                        ${order.client.title}
                </td>
                <td>
                        ${order.client.phone}
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <form class="form-inline" action="">
        <div class="input-group">
            <input name="number" type="text" class="form-control" placeholder="Содержимое номера"/>
        </div>
        <div class="input-group">
            <input name="clientTitle" type="text" class="form-control" placeholder="Содержимое имени"/>
        </div>
        <input type="submit" value="OK"/>
    </form>
</div>

<!-- Bootstrap js cdn -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
        integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS"
        crossorigin="anonymous">
</script>
</body>
</html>
