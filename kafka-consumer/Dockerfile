# Usamos una imagen base con JDK
FROM openjdk:17-jdk-slim
# Copiamos el jar generado
COPY target/kafka-consumer-1.0.0.jar app.jar
# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]