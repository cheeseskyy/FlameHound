package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.SessionInfo;

@Path("/utils")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ComputationResource {

	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	
	public ComputationResource() {} //nothing to be done here
	
	
	@POST
	@Path("/validLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validLogin(SessionInfo session) {
		if(session.tokenId.equals("0")) {
			LOG.warning("User is not logged in");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		Key userKey = KeyFactory.createKey("User", session.username);
		try {
			LOG.info("Attempt to get user: " + session.username);
			Entity user = datastore.get(userKey);
			LOG.info("Got user");
			if(!user.getProperty("TokenKey").equals(session.tokenId))
				return Response.status(Status.FORBIDDEN).build();
			txn.commit();
			return Response.ok().build();
		}catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate username: " + session.username);
			return Response.status(Status.FORBIDDEN).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		
	}
	
	@GET
	@Path("/time")
	public Response getCurrentTime() {
		LOG.fine("Replying to date request.");
		return Response.ok().entity(g.toJson(fmt.format(new Date()))).build();
	}
	
	@POST
	@Path("/compute/{username}")
	public void executeComputeTask(@PathParam("username") String username) {
		while(true) {
			Transaction txn = datastore.beginTransaction();
			LOG.fine("Starting to execute computation taks");
			try {
				Thread.sleep(60*1000*5);//30s //5 min...
				
				Key userKey = KeyFactory.createKey("User", username);
				LOG.info("Attempt to get user: " + username);
				Entity user = datastore.get(userKey);
				LOG.info("Got user");
				long expiration = Long.parseLong((String)user.getProperty("TokenExpirationDate"));
				if(expiration > System.currentTimeMillis()) {
					user.setProperty("TokenExpirationDate", "");
					user.setProperty("TokenCreationDate", "");
					user.setProperty("TokenKey", 0);
				}
				txn.commit();
				
			} catch (Exception e) {
				LOG.logp(Level.SEVERE, this.getClass().getCanonicalName(), "executeComputeTask", "An exeption has ocurred", e);
			} finally {
				txn.rollback();
			}
		}
	}	
	
	@GET
	@Path("/compute")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response triggerExecuteComputeTask(String username) {
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/rest/utils/compute/" + username));
		return Response.ok().build();

	}
	
	
}