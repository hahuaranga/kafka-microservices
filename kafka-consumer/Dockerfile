# Usamos una imagen base con JDK 21
FROM eclipse-temurin:21-jdk-slim

# Copiamos el jar generado
COPY target/kafka-consumer-1.0.0.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]