package FrontServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Client;

@Path("/")
public class WebServices{
    private final Logger LOGGER = LogManager.getLogger(getClass());

    // Injected from local microprofile-config.properties
    @Inject
    @ConfigProperty(name = "server.address.proxy")
    private String proxyServerAddress;

    @Inject
    @ConfigProperty(name = "server.port.proxy.http")
    private String proxyServerPortHttp;

    @Inject
    @ConfigProperty(name = "server.port.proxy.https")
    private String proxyServerPortHttps;

    @Inject
    @ConfigProperty(name = "server.path.proxy")
    private String proxyServerPath;

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
    @ConfigProperty(name = "PROXYADDRESS", defaultValue = "null")
    private String envProxyAddress;

    @Inject
    @ConfigProperty(name = "PROXYPORT", defaultValue = "null")
    private String envProxyPort;

    @Inject
    @ConfigProperty(name = "PROXYPATH", defaultValue = "null")
    private String envProxyPath;

    @GET
    @Path("/test")
    public String getTest() {
        return "Successful: " + envProxyAddress + envProxyPort;
    }

    @GET
    @Path("/testenv")
    public String getTestEnv() {
        return "Successful: " + envTest;
    }

    @GET
    @Path("/echo")
    public Response get() {
        LOGGER.debug("get() called.");
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
            LOGGER.error("Error processing get request to proxy, address: " + address);
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
        if (envProtocol == null || envProxyPath == null || envProxyPort == null || envProxyAddress == null) {
            LOGGER.warn("Environment variable not defined, using local microprofile-config definitions.");
            return false;
        }
        return true;
    }

    private String getTargetAddressHttp() {
        if (isUsingEnvVariable()) {
            return "http://" + envProxyAddress + ":" + envProxyPort + envProxyPath;
        }
        else {
            return "http://" + proxyServerAddress + ":" + proxyServerPortHttp + proxyServerPath;
        }
    }

    private String getTargetAddressHttps() {
        if (isUsingEnvVariable()) {
            return "https://" + envProxyAddress + ":" + envProxyPort + envProxyPath;
        }
        else {
            return "https://" + proxyServerAddress + ":" + proxyServerPortHttps + proxyServerPath;
        }
    }
}
