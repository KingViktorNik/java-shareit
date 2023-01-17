FROM amazoncorretto:11
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
#ARG JAR_FILE=target/*.jar
#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]
