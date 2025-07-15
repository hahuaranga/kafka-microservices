# Usamos una imagen base con JDK 21 de Eclipse Temurin
FROM eclipse-temurin:21-jdk-alpine-3.21

# Copiamos el jar generado
COPY target/kafka-consumer-1.0.0.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]