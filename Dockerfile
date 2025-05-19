# Etapa de construcción (build)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución (run)
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Pasa las propiedades de conexión como variables de entorno si quieres sobreescribirlas en tiempo de ejecución
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport"

CMD ["java", "-jar", "app.jar"]
