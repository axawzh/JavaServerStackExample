FROM java:8
EXPOSE 8080
WORKDIR /app
ADD ./launcher-1.0.1.jar /app
ADD ./FrontServer.war /app
ENV TESTENV TestEnvWorks
ENV PROTOCOL http
ENV PROXYADDRESS 172.17.0.3
ENV PROXYPORT 12001
ENV PROXYPATH /v1/proxyecho
CMD ["java","-jar","launcher-1.0.1.jar", "--http-listener", "8080","--https-listener", "8081", "--deploy","FrontServer.war"]

