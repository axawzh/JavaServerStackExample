# Java echo server stack example

## Overview
An example stack using Java CDI, MicroProfile Config, and Jax-rs

The stack consists 3 Webapps:
- `FrontServer`, the entry point of client browser
- `ProxyServer`, proxies GET request from `FrontServer` to `EchoServer`
- `EchoServer`, return a string as response

A typical workflow: 
1. User calls GET to `FrontServer` using browser or Postman or any REST tools.
2. `FrontServer` receives the request from user, calls GET to `ProxyServer`.
3. `ProxyServer` receives the request from `FrontServer`, calls GET to `EchoServer`.
4. `EchoServer` receives the request from `ProxyServer`, return string response.
5. `ProxyServer` receives the string, return string response.
6. `FrontServer` receives the string, return string to user.

Microprofile Config is used for injecting target server address, port, and path. There are 2 sources from where the injection takes value:
- Environment variable
- Webapp's `microprofile-config.properties` file

Environment variable has higher priority over properties file.

All 3 webapps can be run in docker environment. Dockerfile under respective folders of `/docker/` helps to set the environment variables of the container.

Please note that so far all webapps are run in default docker bridge network, therefore the IP addresses of the webapps are hardcoded in Dockerfiles. If running in user-defined bridge, can replace IP address with image `NAMES` which is set when starting the image. The Dockerfiles that need to be changed are:
- `ENV PROXYADDRESS` in Dockerfile for FrontServer. This environment variable provides the address of proxy server to front server.
- `ENV ECHOADDRESS` in Dockerfile for ProxyServer. This environment variable provides the address of echo server to proxy server.

## Usage
1. Run `mvn clean install` under root path (where `EchoServerBuilderAll` pom exists).
2. Copy the respective `.war` file from sub-projects to the `docker/{respective folder}`.
3. Copy the compiled launcher-1.0.1.jar into respective `docker/{respective folder}`, so the final folder structure is like:

        root
        |
        |--FrontServer
        |        |--Dockerfile
        |        |--FrontServer.war
        |        |--launcher-1.0.1.jar
        |--ProxyServer    
        |        |--Dockerfile
        |        |--ProxyServer.war
        |        |--launcher-1.0.1.jar
        |--EchoServer    
                 |--Dockerfile
                 |--EchoServer.war
                 |--launcher-1.0.1.jar
        
4. cd to `docker/FrontServer`, run `docker build -t frontserver .`.
5. cd to `docker/ProxyServer`, run `docker build -t proxyserver .`.
6. cd to `docker/EchoServer`, run `docker build -t echoserver .`.
7. Run following command to bring up all three webapps:
    - `docker run -p 8080:8080 frontserver`
    - `docker run proxyserver`
    - `docker run echoserver`
8. After bring up all three containers, open browser and go to `http://0.0.0.0:8080/v1/echo`, if `Here we go!` is returned, the server stack is working properly.

## REST Path
### FrontServer
`/v1/echo`: actual echo logic, calls `ProxyServer` using `{ProxyServer address}/v1/proxyecho`.

### ProxyServer
`/v1/proxyecho`: called by `FrontServer`, calls `EchoServer` using `{EchoServer address}/v1/doecho`.

### EchoServer
`/v1/doecho`: called by `ProxyServer`, returns string
