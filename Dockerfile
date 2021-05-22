FROM adoptopenjdk:16-jdk AS BUILD_IMAGE
ENV APP_HOME=/auth0webflux
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME
# copy build files
COPY build.gradle.kts settings.gradle.kts gradlew gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle
# download dependencies
RUN ./gradlew build --no-daemon --continue
# download npm dependencies
RUN apt-get update && apt-get install npm
RUN mkdir -p $APP_HOME/src/main/resources/static
COPY src/main/resources/static/package.json src/main/resources/static/package-lock.json $APP_HOME/src/main/resources/static/
RUN npm install
# compile app
COPY . .
RUN ./gradlew --no-daemon build

FROM adoptopenjdk:16-jre
WORKDIR /root/
COPY --from=BUILD_IMAGE /auth0webflux/build/libs/webflux-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java","-jar","webflux-0.0.1-SNAPSHOT.jar"]