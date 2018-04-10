FROM java:8
EXPOSE 12002
WORKDIR /app
ADD ./launcher-1.0.1.jar /app
ADD ./EchoServer.war /app
ENV TESTENV TestEnvWorks
CMD ["java","-jar","launcher-1.0.1.jar", "--http-listener", "12002", "--https-listener", "13002", "--deploy","EchoServer.war"]

