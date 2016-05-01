<form>
    <input type="number" name="number" placeholder="Номер заказа"/>
    <input type="date" name="date" placeholder="Дата"/>
    <input type="number" name="total" placeholder="Сумма заказа"/>
    <ul>
        <c:forEach items="${requestScope.clients}" var="client">
            <li><input type="radio" value="${client.title}"/></li>
        </c:forEach>
    </ul>
    <input type="tel" name="phone" placeholder="Телефон"/>
</form>