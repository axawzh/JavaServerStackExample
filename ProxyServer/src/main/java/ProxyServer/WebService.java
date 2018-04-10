package ProxyServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

@Path("/")
public class WebService {
    private final Logger LOGGER = LogManager.getLogger(getClass());

    @Inject
    @ConfigProperty(name = "server.address.echo")
    private String echoServerAddress;

    @Inject
    @ConfigProperty(name = "server.port.echo.http")
    private String echoServerPortHttp;

    @Inject
    @ConfigProperty(name = "server.port.echo.https")
    private String echoServerPortHttps;

    @Inject
    @ConfigProperty(name = "server.path.echo")
    private String echoServerPath;

    @Inject
    @ConfigProperty(name = "protocol")
    private String protocol;

    // Injected from system env
    @Inject
    @ConfigProperty(name = "TESTENV", defaultValue = "cannot retrieve env variable TESTENV")
    private String envTest;

    @Inject
    @ConfigProperty(name = "PROTOCOL", defaultValue = "null")
    private String envProtocol;

    @Inject
    @ConfigProperty(name = "ECHOADDRESS", defaultValue = "null")
    private String envEchoAddress;

    @Inject
    @ConfigProperty(name = "ECHOPORT", defaultValue = "null")
    private String envEchoPort;

    @Inject
    @ConfigProperty(name = "ECHOPATH", defaultValue = "null")
    private String envEchoPath;

    @GET
    @Path("/test")
    public String getTest() {
        return "Successful: " + envEchoAddress + envEchoPort;
    }

    @GET
    @Path("/testenv")
    public String getTestEnv() {
        return "Successful: " + envTest;
    }

    @GET
    @Path("/proxyecho")
    public Response proxyEcho() {
        LOGGER.debug("proxyEcho() called.");
        String address = null;
        if (isUsingEnvVariable()) {
            if ("https".equals(envProtocol)) {
                address = getTargetAddressHttps();
            }
            else if ("http".equals(envProtocol)){
                address = getTargetAddressHttp();
            }
            else {
                LOGGER.error("Invalid protocol, using default http");
                address = getTargetAddressHttp();
            }
            LOGGER.debug("target address: " + address);
        }

        Client client = ClientBuilder.newClient();
        try {
            LOGGER.debug("Getting from proxy");
            String responseStr = client.target(address)
                    .request()
                    .get(String.class);
            LOGGER.info("Result string: " + responseStr);

            return Response.ok(responseStr).build();
        }
        catch (ProcessingException e) {
            LOGGER.error("Error processing get request to echo");
            return Response.status(Response.Status.BAD_GATEWAY).build();
        }
        catch (Exception e2) {
            LOGGER.error("Other exception");
            e2.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        finally {
            client.close();
        }
    }

    private boolean isUsingEnvVariable() {
        if (envProtocol == null || envEchoPath == null || envEchoPort == null || envEchoAddress == null) {
            LOGGER.warn("Environment variable not defined, using local microprofile-config definitions.");
            return false;
        }
        return true;
    }

    private String getTargetAddressHttp() {
        if (isUsingEnvVariable()) {
            return "http://" + envEchoAddress + ":" + envEchoPort + envEchoPath;
        }
        else {
            return "http://" + echoServerAddress + ":" + echoServerPortHttp + echoServerPath;
        }
    }

    private String getTargetAddressHttps() {
        if (isUsingEnvVariable()) {
            return "https://" + envEchoAddress + ":" + envEchoPort + envEchoPath;
        }
        else {
            return "https://" + echoServerAddress + ":" + echoServerPortHttps + echoServerPath;
        }
    }

}