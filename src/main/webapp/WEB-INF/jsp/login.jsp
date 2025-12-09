<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Connexion</title>
    <link rel="stylesheet" href="/styles/style.css" />
</head>
<body>
<div class="container">
    <h2>Connexion</h2>

    <c:if test="${not empty msg}">
        <p class="error">${msg}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post" class="form">
        <label>Email:</label>
        <input type="email" name="email" required/>
        
        <label>Mot de passe:</label>
        <input type="password" name="motDePasse" required/>
        
        <button type="submit" class="btn">Se connecter</button>
    </form>
</div>
</body>
</html>
