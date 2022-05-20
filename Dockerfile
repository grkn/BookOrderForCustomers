FROM alpine:latest
ENV JAVA_HOME="/usr/lib/jvm/default-jvm/"
RUN apk add openjdk11
ENV PATH=$PATH:${JAVA_HOME}/bin
COPY /target/BookOrderForCustomers-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Dspring.data.mongodb.uri=mongodb://mongo:27017/bookorderforcustomers","-jar","app.jar"]
