<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Événements</title>
    <link rel="stylesheet" href="/styles/style.css" />
</head>
<body>
<div class="container">
    <c:if test="${not empty sessionScope.user}">
        <p class="welcome">
            👋 Bonjour <strong>${sessionScope.user.nom}</strong> |
            <a href="${pageContext.request.contextPath}/logout" class="link">Se déconnecter</a>
        </p>
    </c:if>

    <h2>Liste des événements</h2>

    <c:if test="${not empty msg}">
        <p class="success">${msg}</p>
    </c:if>

    <table class="table">
        <tr>
            <th>Titre</th>
            <th>Lieu</th>
            <th>Date</th>
            <th>Prix</th>
            <th>Places restantes</th>
            <th>Action</th>
        </tr>

        <c:forEach var="event" items="${events}">
            <tr>
                <td>${event.titre}</td>
                <td>${event.lieu}</td>
                <td>${event.dateEvenement}</td>
                <td>${event.prixBase}</td>
                <td>${event.nbPlacesRestantes}</td>
                <td>
                    <c:if test="${isOrganisateur}">
                        <a href="${pageContext.request.contextPath}/events/create?id=${event.id}" class="btn small">Modifier</a>
                    </c:if>
                    <c:if test="${not isOrganisateur}">
                        <c:if test="${event.nbPlacesRestantes > 0}">
                            <form action="${pageContext.request.contextPath}/reservations/create" method="post" class="inline-form">
                                <input type="hidden" name="eventId" value="${event.id}"/>
                                <input type="number" name="nbPlaces" value="1" min="1" max="${event.nbPlacesRestantes}"/>
                                <button type="submit" class="btn small">Réserver</button>
                            </form>
                        </c:if>
                        <c:if test="${event.nbPlacesRestantes == 0}">
                            Complet
                        </c:if>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>

    <c:if test="${isOrganisateur}">
        <p><a href="${pageContext.request.contextPath}/events/create" class="btn">➕ Créer un événement</a></p>
    </c:if>

    <c:if test="${not isOrganisateur}">
        <p><a href="${pageContext.request.contextPath}/reservations/history" class="btn">Mes réservations</a></p>
    </c:if>
</div>
</body>
</html>
