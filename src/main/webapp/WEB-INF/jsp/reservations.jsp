<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Mes réservations</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
</head>
<body>
<div class="container">
    <h2>Historique des réservations</h2>

    <c:if test="${not empty msg}">
        <p class="success">${msg}</p>
    </c:if>

    <table class="table">
        <tr>
            <th>Événement</th>
            <th>Date de l'événement</th>
            <th>Nombre de places</th>
            <th>Statut</th>
            <th>Montant</th>
            <th>Action</th>
        </tr>
        <c:forEach var="r" items="${reservations}">
            <tr>
                <td>${r.evenement.titre}</td>
                <td>${r.evenement.dateEvenement}</td>
                <td>${r.nbPlaces}</td>
                <td>${r.statut}</td>
                <td>${r.montantTotal}</td>
                <td>
                    <c:if test="${r.statut == 'CONFIRMEE'}">
                        <form action="${pageContext.request.contextPath}/reservations/cancel" method="post" class="inline-form">
                            <input type="hidden" name="reservationId" value="${r.id}"/>
                            <button type="submit" class="btn small">Annuler</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>

    <p><a href="${pageContext.request.contextPath}/events" class="btn">Retour aux événements</a></p>
</div>
</body>
</html>
