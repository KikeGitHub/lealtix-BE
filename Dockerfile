# Etapa 1: build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagen liviana para ejecutar
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Usar la variable de entorno SPRING_PROFILES_ACTIVE para seleccionar el profile.
# Por defecto la imagen usará 'local' pero puede sobreescribirse en tiempo de ejecución
ENV SPRING_PROFILES_ACTIVE=local
# Pasamos el profile al JVM para que Spring Boot cargue application-${profile}.properties
ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]
