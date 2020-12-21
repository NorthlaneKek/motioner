FROM maven:3.6-openjdk-11-slim as app
COPY . .
RUN mkdir /app && mvn package -DskipTests -Dmaven.wagon.http.ssl.allowall=true && cp ./target/motioner.jar /app && cp ./src/main/resources/application.properties /app && cp ./src/main/resources/schema.sql /app
CMD ["java", "-jar", "/app/motioner.jar"]