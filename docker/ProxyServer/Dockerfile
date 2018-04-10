FROM java:8
EXPOSE 12001
WORKDIR /app
ADD ./launcher-1.0.1.jar /app
ADD ./ProxyServer.war /app
ENV TESTENV TestEnvWorks
ENV PROTOCOL http
ENV ECHOADDRESS 172.17.0.4
ENV ECHOPORT 12002
ENV ECHOPATH /v1/doecho
CMD ["java","-jar","launcher-1.0.1.jar", "--http-listener", "12001", "--https-listener", "13001", "--deploy","ProxyServer.war"]

