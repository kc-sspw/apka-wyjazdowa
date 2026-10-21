FROM maven:3.8.5-openjdk-17 AS BUILD
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src/ src/
RUN mvn -B -f pom.xml clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
COPY --from=BUILD /build/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
