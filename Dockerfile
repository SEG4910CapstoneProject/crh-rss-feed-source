FROM maven:3.9-eclipse-temurin-21-alpine as buildNode

WORKDIR /project-build

COPY ./ /project-build

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21.0.2_13-jre-alpine

COPY --from=buildNode /project-build/target/rss-feed-source-task.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
