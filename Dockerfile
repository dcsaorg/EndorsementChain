FROM debian:buster

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get -y upgrade \
    && DEBIAN_FRONTEND=noninteractive apt-get -y install --no-install-recommends \
        openjdk-11-jre-headless \
    && rm -rf /var/lib/apt/lists/*

MAINTAINER nicolas@ange.dk
EXPOSE 8443

RUN mkdir /app
WORKDIR /app

COPY target/dcsa-endorsement-chain-1.jar .
COPY run-service .
COPY dbconfiguration.env .
CMD ["./run-service"]
