FROM eclipse-temurin:21-jre
ENV VAADIN_PRODUCTION_MODE=true
COPY target/*.jar app.jar
EXPOSE 8070
ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "/app.jar"]
