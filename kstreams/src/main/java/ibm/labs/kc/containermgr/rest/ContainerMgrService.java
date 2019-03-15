package ibm.labs.kc.containermgr.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibm.labs.kc.containermgr.dao.ContainerDAO;
import ibm.labs.kc.containermgr.streams.ContainerInventoryView;
import ibm.labs.kc.model.Container;

@Path("containers")
public class ContainerMgrService {
	 private static final Logger logger = LoggerFactory.getLogger(ContainerMgrService.class);
	 private ContainerDAO containerDAO;
	 
	 public ContainerMgrService(){
		 containerDAO = ContainerInventoryView.instance();
		 containerDAO.start();
	 }
	 
	@GET
    @Path("{Id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Query an order by id", description = "")
    @APIResponses(value = {
    @APIResponse(responseCode = "404", description = "Order not found", content = @Content(mediaType = "text/plain")),
    @APIResponse(responseCode = "200", description = "Order found", content = @Content(mediaType = "application/json")) })
	public Response getById(@PathParam("Id") String containerId) {
	    logger.info("ContainerMgrService.getById(" + containerId + ")");
	
	    Container c = containerDAO.getById(containerId);
	    if (c != null) {
	        return Response.ok().entity(c).build();
	    } else {
	        return Response.status(Status.NOT_FOUND).build();
	    }
	}
}