<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Créer / Modifier un événement</title>
    <link rel="stylesheet" href="/styles/style.css" />
</head>
<body>
<div class="container">
    <h2><c:choose>
        <c:when test="${not empty event}">Modifier un événement</c:when>
        <c:otherwise>Créer un événement</c:otherwise>
    </c:choose></h2>

    <c:if test="${not empty msg}">
        <p class="error">${msg}</p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/events/create" class="form">
        <c:if test="${not empty event}">
            <input type="hidden" name="id" value="${event.id}" />
        </c:if>

        <label>Titre:</label>
        <input type="text" name="titre" value="${event.titre}" required/>

        <label>Description:</label>
        <input type="text" name="description" value="${event.description}" required/>

        <label>Date (ISO 2025-12-25T20:00):</label>
        <input type="text" name="date" value="${event.dateEvenement}" required/>

        <label>Lieu:</label>
        <input type="text" name="lieu" value="${event.lieu}" required/>

        <label>Nombre de places:</label>
        <input type="number" name="nbPlaces" value="${event.nbPlacesTotales}" required/>

        <label>Prix de base:</label>
        <input type="number" step="0.01" name="prix" value="${event.prixBase}" required/>

        <button type="submit" class="btn">
            <c:choose>
                <c:when test="${not empty event}">Modifier</c:when>
                <c:otherwise>Créer</c:otherwise>
            </c:choose>
        </button>
    </form>

    <p><a href="${pageContext.request.contextPath}/events" class="link">Annuler</a></p>
</div>
</body>
</html>
