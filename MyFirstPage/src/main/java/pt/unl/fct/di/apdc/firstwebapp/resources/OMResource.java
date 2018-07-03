package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.adminResources.OccurrencyManagement;
import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;

@Path("/oM")
public class OMResource {
	
	private static final Logger LOG = Logger.getLogger(BackEndResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public Utilities ut = new Utilities();
	
	@Path("/confirm/{ocID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response confirmOccurrency(@PathParam("ocID") String ocID, SessionInfo session) {
		if(ut.validAdminLogin(session).getStatus() == 200 || ut.validModeratorLogin(session).getStatus() == 200)
			return OccurrencyManagement.confirmOccurrency(datastore, ocID, LOG, session);
		else
			return Response.status(Status.FORBIDDEN).build();
	}

	@Path("/delete/{ocID}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteOccurrency(@PathParam("ocID") String ocID, SessionInfo session) {
		if(ut.validAdminLogin(session).getStatus() == 200)
			return OccurrencyManagement.deleteOccurrency(datastore, ocID, LOG, session);
		else
			return Response.status(Status.FORBIDDEN).build();
	}
	
	@Path("/update/{ocID}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOccurrency(@PathParam("ocID") String ocID, SessionInfo session) {
		if(ut.validAdminLogin(session).getStatus() == 200)
			return OccurrencyManagement.updateOccurrency(session, datastore, ocID, LOG);
		else
			return Response.status(Status.FORBIDDEN).build();
	}
	
}
