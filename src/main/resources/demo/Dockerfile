FROM maven:3.8.3-openjdk-17 AS demo-app
COPY ./ ./
RUN --mount=type=cache,target=/root/.m2 mvn clean -DskipTests package
EXPOSE 8081
CMD ["java", "-jar", "target/demo-0.0.1.jar"]