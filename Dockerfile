# Usar imagen base de OpenJDK 11
FROM openjdk:11-jdk-slim

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven primero (para aprovechar cache)
COPY pom.xml .
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Crear directorio para logs
RUN mkdir -p /app/logs

# Exponer puerto (aunque la aplicación no es web, lo dejamos por si acaso)
EXPOSE 8080

# Comando por defecto
CMD ["java", "-cp", "target/classes:target/dependency/*", "com.fortunato.sistema.SistemaKioscoApp"]
