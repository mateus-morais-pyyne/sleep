FROM openjdk:11

WORKDIR /app

COPY build.gradle gradlew settings.gradle ./
COPY gradle/ gradle/

RUN ./gradlew wrapper

COPY src/ src

RUN ./gradlew build

ENTRYPOINT ["java","-jar","build/libs/sleep-1.0.0-SNAPSHOT.jar"]
