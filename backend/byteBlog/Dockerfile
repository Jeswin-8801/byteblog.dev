FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /byteBlog
COPY . .
RUN mvn clean install -DskipTests

CMD mvn spring-boot:run