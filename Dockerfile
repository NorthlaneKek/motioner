FROM maven:3.6-openjdk-11-slim as app
COPY . .
RUN mkdir /app && mvn package -DskipTests -Dmaven.wagon.http.ssl.allowall=true && cp ./target/motioner.jar /app && cp ./application.properties /app
CMD ["java", "-jar", "/app/motioner.jar"]