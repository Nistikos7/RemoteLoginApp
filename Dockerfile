# Build stage
FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package

# Run stage
FROM tomcat:10.1-jdk17
COPY --from=build /app/target/RemoteLoginApp.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]