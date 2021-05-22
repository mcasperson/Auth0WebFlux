FROM adoptopenjdk:16-jdk AS BUILD_IMAGE
RUN apt-get update && apt-get install npm -y
ENV APP_HOME=/auth0webflux
WORKDIR $APP_HOME
COPY . .
RUN cd $APP_HOME/src/main/resources/static/; npm install
RUN ./gradlew --no-daemon build

FROM adoptopenjdk:16-jre
WORKDIR /root/
COPY --from=BUILD_IMAGE /auth0webflux/build/libs/webflux-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java","-jar","webflux-0.0.1-SNAPSHOT.jar"]