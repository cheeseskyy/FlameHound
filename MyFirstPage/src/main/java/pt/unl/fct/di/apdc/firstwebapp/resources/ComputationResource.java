
package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.objects.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;

@Path("/utils")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ComputationResource {

	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private static final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	private static long validLogin = 0;;

	public ComputationResource() {
	} // nothing to be done here

	@POST
	@Path("/validLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public static Response validLogin(SessionInfo session) {
		if (session.tokenId.equals("0")) {
			LOG.warning("User is not logged in");
			return Response.status(Status.FORBIDDEN).build();
		}
			
		Transaction txn = datastore.beginTransaction();
		Transaction txn2 = datastore.beginTransaction();
		Key userKey = KeyFactory.createKey("User", session.username);
		String role;
		try {
			LOG.info("Attempt to get user: " + session.username);
			Entity user = datastore.get(txn, userKey);
			LOG.info("Got user");
			role = (String)user.getProperty("role");
			if(System.currentTimeMillis() - validLogin  < 60000) {
				txn.commit();
				txn2.commit();
				return Response.ok(g.toJson(role)).build();
			}
			if (!user.getProperty("TokenKey").equals(session.tokenId)) {
				LOG.info("Wrong token for user " + session.username);
				txn.commit();
				txn2.commit();
				return Response.status(Status.FORBIDDEN).build();
			}
			LOG.info("Correct token for use " + session.username);

			Key timeoutKey = KeyFactory.createKey("timeout", session.username);
			LOG.info("Got timeoutKey");
			Entity timeout = datastore.get(txn2, timeoutKey);
			LOG.info("Got Timeout");
			long lastOp = (long) timeout.getProperty("lastOp");
			LOG.info("timeout is Long");
			if (System.currentTimeMillis() - lastOp > 20 * 60 * 1000) {
				LOG.info("Timed out");
				user.setProperty("TokenExpirationDate", "");
				user.setProperty("TokenCreationDate", "");
				user.setProperty("TokenKey", 0);
				datastore.put(txn, user);
				txn.commit();
				txn2.commit();
				return Response.status(Status.FORBIDDEN).build();
			}
			LOG.info("Didn't time out");
			timeout.setProperty("lastOp", System.currentTimeMillis());
			datastore.put(txn2, timeout);
			txn.commit();
			txn2.commit();
			validLogin = System.currentTimeMillis();
			return Response.ok(g.toJson(role)).build();
		} catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate username: " + session.username);
			txn.rollback();
			txn2.rollback();
			return Response.status(Status.FORBIDDEN).build();
		} catch(Exception e) {
			LOG.info(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		finally {
			if (txn.isActive() || txn2.isActive()) {
				LOG.info("Transactions still active");
				txn.rollback();
				txn2.rollback();
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
	
	@GET
	@Path("/solveLogs")
	public static Response solveLogs() {
		Query q = new Query("OperationLogs");
		PreparedQuery pQ = datastore.prepare(q);
		ArrayList<Entity> list = new ArrayList<Entity>(pQ.asList(FetchOptions.Builder.withDefaults()));
		for(int i = 0; i<list.size(); i++) {
			Entity log = list.get(i);
			log.setProperty("date", System.currentTimeMillis() + i);
			String text = (String)log.getProperty("logText");
			if(text != null && text.contains("did"))
				log.setProperty("logHash", "null");
			else
				log.setProperty("logText", "null");
			datastore.put(log);
		}
		return Response.ok().build();
	}
	
	@GET
	@Path("/integrityLogs")
	public static Response checkLogIntegrity(){
		
		Query q = new Query("OperationLogs").addSort("date", SortDirection.ASCENDING);
		PreparedQuery pQ = datastore.prepare(q);
		ArrayList<Entity> list = 
				new ArrayList<Entity>(pQ.asList(FetchOptions.Builder.withDefaults()));
		String newLine = "";
		String hex = "";
		for(int i = 0; i < list.size(); i++) {
			newLine = (String) list.get(i).getProperty("logText");
			newLine.replaceAll("\n", "");
			if(!newLine.equals("null")) {
				LOG.info(newLine);
				hex = DigestUtils.sha512Hex(hex+newLine);
			}
				
		}
		Transaction txn2 = datastore.beginTransaction();
		Key adminLogsHash = KeyFactory.createKey("OperationLogs", "LogsHash");
		Entity logs = null;
		try {
			logs = datastore.get(txn2, adminLogsHash);
		}catch(EntityNotFoundException e) {
		}
		txn2.commit();
		
		String logsHash = (String) logs.getProperty("logHash");
		
		LOG.info(hex);
		LOG.info(logsHash);
		
		if(hex.equals(logsHash))
			return Response.ok().build();
		return Response.status(Status.UNAUTHORIZED).build();
	}
}