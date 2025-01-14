# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM tomcat:10.1-jdk17-temurin-jammy
COPY --from=build /app/target/RemoteLoginApp-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Add PostgreSQL JDBC driver
ADD https://jdbc.postgresql.org/download/postgresql-42.6.0.jar /usr/local/tomcat/lib/

EXPOSE 8080
CMD ["catalina.sh", "run"]