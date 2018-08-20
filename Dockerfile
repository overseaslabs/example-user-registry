#build image
FROM gradle:jdk10 as builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build

#run image
FROM openjdk:10-jre-slim

LABEL vendor="Overseas Labs Limited" \
      vendor.website="http://overseaslsbs.com" \
      description="User registry microservice" \
      project="Example project" \
      tag="overseaslabs/example-ureg"

EXPOSE 8080

COPY --from=builder /home/gradle/src/build/distributions/code-boot.tar /app/
WORKDIR /app
RUN tar -xvf code-boot.tar
WORKDIR /app/code-boot/bin

CMD ["/app/code-boot/bin/code"]