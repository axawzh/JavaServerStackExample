package EchoServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class WebService {
    @GET
    @Path("/doecho")
    public String doEcho() {
        return "Here we go!";
    }
}
