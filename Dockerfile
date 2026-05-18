FROM maven:3.8.6-openjdk-11-slim as builder
WORKDIR /app

# Build javax SDK only (demo app uses javax / Tomcat 9).
COPY pom.xml .
COPY perimeterx-sdk/pom.xml perimeterx-sdk/pom.xml
COPY perimeterx-sdk-jakarta/pom.xml perimeterx-sdk-jakarta/pom.xml
COPY perimeterx-sdk/src perimeterx-sdk/src
RUN mvn clean install -pl perimeterx-sdk -am -DskipTests=true -q

# Build the demo WAR (depends on perimeterx-sdk in local repo).
COPY web ./web
RUN mvn clean install war:war -DskipTests=true -f web/pom.xml -q

FROM tomcat:9.0.68

COPY --from=builder /app/web/target/ROOT /usr/local/tomcat/webapps/ROOT

## Remove the template config from the classpath so Utils.getEnforcerConfig() falls back to the
## filesystem path below, where Kubernetes mounts the real enforcer config at runtime.
RUN rm -f /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/enforcer_config.json

## Enforcer configuration json file is located at:
## /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/src/main/resources/enforcer_config.json
COPY web/src/main/resources/ /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/src/main/resources

## relaxedQueryChars allows fuzz/special characters (`, {, }, |, ^, \, [, ]) in query strings
## that Tomcat rejects by default per RFC 7230 strict mode.
RUN sed -i 's@protocol="HTTP/1.1"@protocol="HTTP/1.1" relaxedQueryChars="`{}|^\\[]"@' \
    /usr/local/tomcat/conf/server.xml

EXPOSE 8080

ENV CATALINA_OPTS="-Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true"

CMD ["catalina.sh", "run"]
