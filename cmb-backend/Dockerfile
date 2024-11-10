FROM maven:3.9.9-amazoncorretto-23 as Builder
LABEL authors="stargazer"
COPY . /app
WORKDIR /app
RUN mvn clean install
RUN mvn package

FROM amazoncorretto:23-alpine3.20
RUN apk add chromium-chromedriver --no-cache 
COPY --from=Builder /app/target/combank-0.0.1-SNAPSHOT.jar /app/combank-0.0.1-SNAPSHOT.jar
RUN ls /app
WORKDIR /app
CMD ["java","-jar","combank-0.0.1-SNAPSHOT.jar","-Dspring.config.location=file:./application.yml"]

