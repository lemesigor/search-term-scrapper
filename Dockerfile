FROM maven:3.6.3-jdk-14

ADD . /usr/src/searchterm
WORKDIR /usr/src/searchterm
EXPOSE 4567
ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]