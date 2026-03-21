FROM eclipse-temurin:17-jre

WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENV JAVA_OPTS=""
EXPOSE 9001

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

