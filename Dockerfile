# ---------- Étape 1 : Build Maven ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compiler et packager le projet
RUN mvn clean package -DskipTests

# ---------- Étape 2 : Tomcat ----------
FROM tomcat:10.1-jdk21

# Supprimer les apps par défaut
RUN rm -rf /usr/local/tomcat/webapps/*

# Copier le WAR et l'extraire pour un déploiement complet
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Extraire le WAR pour servir les fichiers statiques immédiatement
RUN apt-get update && apt-get install -y unzip \
    && unzip /usr/local/tomcat/webapps/ROOT.war -d /usr/local/tomcat/webapps/ROOT \
    && rm /usr/local/tomcat/webapps/ROOT.war \
    && apt-get remove -y unzip \
    && apt-get autoremove -y \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# Lancer Tomcat
CMD ["catalina.sh", "run"]
