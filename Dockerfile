# ---------- Étape 1 : Build Maven ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Étape 2 : Tomcat ----------
FROM tomcat:10.1-jdk21

# Supprimer les apps par défaut
RUN rm -rf /usr/local/tomcat/webapps/*

# Déployer le WAR généré par Maven
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
