FROM eclipse-temurin:22 AS build

# Installa Maven manualmente
#RUN apt update && apt install -y curl \
#    && curl -fsSL https://downloads.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz | tar -xz -C /opt \
#    && ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/bin/mvn

WORKDIR /app
COPY . /app
RUN apt update && apt install -y maven


# Compila il progetto con Maven
RUN mvn clean package

# Usa OpenJDK 22 per eseguire l'applicazione
FROM eclipse-temurin:22 AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar /app/*.jar

# Espone la porta 5000
EXPOSE 42069

ENTRYPOINT ["java", "-jar", "/app/*.jar"]