FROM amazoncorretto:21-alpine
VOLUME /tmp
VOLUME /files
EXPOSE 8080

# Копируем файл, используя его точное имя из вашего терминала
ARG JAR_FILE=target/Diplom_Backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} project.jar

ENTRYPOINT ["java", "-jar", "/project.jar", "--spring.profiles.active=prod"]