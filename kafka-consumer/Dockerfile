# Usamos una imagen base con JDK 21 de Eclipse Temurin
FROM eclipse-temurin:21-jdk-alpine-3.21

# 1. Copiar el certificado a una ruta temporal
COPY opensearch_cert.pem /tmp/opensearch_cert.pem

# 2. Importar el certificado al almacén de Java (cacerts)
RUN keytool -importcert \
    -noprompt \
    -alias opensearch \
    -file /tmp/opensearch_cert.pem \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit && \
    rm /tmp/opensearch_cert.pem

# Copiamos el jar generado
COPY target/kafka-consumer-1.0.0.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]