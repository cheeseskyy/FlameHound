package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
	
	@GET
	@Path("/time")
	public Response getCurrentTime() {
		LOG.fine("Replying to date request.");
		return Response.ok().entity(g.toJson(fmt.format(new Date()))).build();
	}
	
	@POST
	@Path("/compute")
	public Response executeComputeTask() {
		
		LOG.fine("Starting to execute computation taks");
		try {
			Thread.sleep(30*1000); //30s //5 min...
		} catch (Exception e) {
			LOG.logp(Level.SEVERE, this.getClass().getCanonicalName(), "executeComputeTask", "An exeption has ocurred", e);
			return Response.serverError().build();
		} //Simulates 60s execution
		
		Query q = new Query("User") ;
		PreparedQuery pq = datastore.prepare(q);  
		for (Entity result : pq.asIterable()) {   
	       long loginExpiration = (long) result.getProperty("TokenExpirationDate"); 
	       if(System.currentTimeMillis() > loginExpiration) {
	    	   Transaction txn = datastore.beginTransaction();
	    	   try {
	    		    result.setProperty("TokenKey", "");
					result.setProperty("TokenCreationDate", "");
					result.setProperty("TokenExpirationDate", "");
					datastore.put(result);
					txn.commit();
	   			}catch (Exception e) {
	   			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	   		} finally {
	   			if (txn.isActive()) {
	   				txn.rollback();
	   				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	   			}
	   		}
	       }
		}
		return Response.ok().build();
	}
	
	@GET
	@Path("/compute")
	public Response triggerExecuteComputeTask() {
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(TaskOptions.Builder.withUrl("/rest/utils/compute"));
		return Response.ok().build();

	}
	
	
}