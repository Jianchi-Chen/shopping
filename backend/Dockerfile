FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies

COPY src src
COPY uploads uploads

EXPOSE 8080

CMD ["./gradlew", "bootRun", "--no-daemon"]
