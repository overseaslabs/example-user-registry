##########################
# BUILD IMAGE
##########################

FROM openjdk:10 AS BUILDER

#make the project dir
ENV APP_HOME=/proj
RUN mkdir -p  $APP_HOME
WORKDIR $APP_HOME

#copy only the gradle build files first
#the dependencies will be resolved only if they change, otherwise they will be taken from the docker cache
COPY build.gradle settings.gradle gradlew $APP_HOME/
COPY gradle $APP_HOME/gradle
RUN ./gradlew resolveDependencies --continue

#now copy the project itself
#building it will not cause redownloading the dependencies now
COPY . .

RUN ./gradlew build

##########################
# RUNTIME IMAGE
##########################

FROM openjdk:10-jre-slim

LABEL vendor="Overseas Labs Limited" \
      vendor.website="http://overseaslsbs.com" \
      description="User registry microservice" \
      project="Example project" \
      tag="overseaslabs/example-ureg"

EXPOSE 8080

COPY --from=BUILDER /proj/build/distributions/code-boot.tar /app/
WORKDIR /app
RUN tar -xvf code-boot.tar
WORKDIR /app/code-boot/bin

CMD ["/app/code-boot/bin/code"]