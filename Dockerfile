FROM maven:3.8.6-openjdk-11-slim as builder
WORKDIR /app

# Building the SDK.
COPY pom.xml .
COPY src/main/resources src/main/resources
RUN mvn verify clean -f pom.xml
COPY src ./src
RUN mvn clean install -DskipTests=true

# Building the Demo app.
COPY web/pom.xml web/pom.xml
RUN mvn verify clean -f web/pom.xml
COPY web ./web
RUN mvn clean install war:war -DskipTests=true -f web/pom.xml

FROM tomcat:9.0.68

COPY --from=builder /app/web/target/web-1.0.0 /usr/local/tomcat/webapps/ROOT

## Enforcer configuration json file is located at:
## /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/src/main/resources/enforcer_config.json
COPY web/src/main/resources/ /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/src/main/resources

EXPOSE 8080

CMD ["catalina.sh", "run"]