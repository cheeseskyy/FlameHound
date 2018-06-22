package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.dropbox.core.DbxException;
import com.google.api.client.util.store.DataStore;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.adminResources.OccurrencyManagement;
import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyFlags;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.UserRoles;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AdminInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AdminRegisterInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;

@Path("/_bo/_worker")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class BackOfficeResource extends HttpServlet {

	/**
	 * 
	 */
	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(BackOfficeResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public BackOfficeResource() {
	} // Nothing to be done here...

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		RequestDispatcher r = request.getRequestDispatcher("pages/login.html");
		r.forward(request, response);
	}
	
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
			Entity userN = datastore.get(userKey);
			if(!userN.getProperty("role").equals(UserRoles.WORKER.toString()))
				return Response.status(Status.FORBIDDEN).build();
			LOG.info("Got user");
			Key workerUserKey = KeyFactory.createKey("UserWorker", session.username);
			Entity user = datastore.get(workerUserKey);
			if(!user.getProperty("TokenKey").equals(session.tokenId))
				return Response.status(Status.FORBIDDEN).build();
			Key timeoutKey = KeyFactory.createKey("timeout", session.username);
			txn.commit();
			Transaction txn2 = datastore.beginTransaction();
			Entity timeout = datastore.get(txn2, timeoutKey);
			long lastOp = (long) timeout.getProperty("lastOp");
			if(System.currentTimeMillis() - lastOp > 5*60*1000) {
				user.setProperty("TokenExpirationDate", "");
				user.setProperty("TokenCreationDate", "");
				user.setProperty("TokenKey", 0);
				datastore.put(txn, user);
				txn.commit();
				txn2.commit();
				return Response.status(Status.FORBIDDEN).build();
			}
			timeout.setProperty("lastOp", System.currentTimeMillis());
			datastore.put(txn2, timeout);
			txn.commit();
			txn2.commit();
			return Response.ok().build();
		}catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate username: " + session.username);
			txn.rollback();
			return Response.status(Status.FORBIDDEN).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doAdminLogin(LoginData data, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context HttpHeaders headers) throws ServletException, IOException {
		LOG.info("Attempt to login admin user: " + data.username);

		Transaction txn = datastore.beginTransaction();
		Key userKey = KeyFactory.createKey("UserWorker", data.username);
		try {
			Entity user = datastore.get(userKey);
			LOG.info("Got AdminUser");
			// Obtain the user login statistics
			Query ctrQuery = new Query("WorkerUserStats").setAncestor(userKey);
			List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			Entity ustats = null;
			LOG.info("Logging stats");
			if(results != null) {
				if (results.isEmpty()) {
					ustats = new Entity("UserStats", user.getKey());
					ustats.setProperty("user_stats_logins", 0L);
					ustats.setProperty("user_stats_failed", 0L);
				} else {
					ustats = results.get(0);
				}
			}else {
				ustats = new Entity("UserStats", user.getKey());
				ustats.setProperty("user_stats_logins", 0L);
				ustats.setProperty("user_stats_failed", 0L);
			}

			String hashedPWD = (String) user.getProperty("password");
			if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				// Password correct
				LOG.info("Constructing logs");
				// Construct the logs
				Entity log = new Entity("AdminUserLog", user.getKey());
				log.setProperty("user_login_ip", request.getRemoteAddr());
				log.setProperty("user_login_host", request.getRemoteHost());
				log.setProperty("user_login_latlon", headers.getHeaderString("X-AppEngine-CityLatLong"));
				log.setProperty("user_login_city", headers.getHeaderString("X-AppEngine-City"));
				log.setProperty("user_login_country", headers.getHeaderString("X-AppEngine-Country"));
				log.setProperty("user_login_time", new Date());
				// Get the user statistics and updates it
				ustats.setProperty("user_stats_logins", 1L + (long) ustats.getProperty("user_stats_logins"));
				ustats.setProperty("user_stats_failed", 0L);
				ustats.setProperty("user_stats_last", new Date());

				// Batch operation
				List<Entity> logs = Arrays.asList(log, ustats);
				datastore.put(txn, logs);
				LOG.info("Put logs");
				// Return token
				AuthToken token = new AuthToken();
				token.setUsername(data.username);
				token.setCreationData(System.currentTimeMillis());
				token.setExpirationData(token.creationData + AuthToken.EXPIRATION_TIME);
				
				user.setProperty("TokenKey", token.tokenID);
				user.setProperty("TokenCreationDate", token.creationData);
				user.setProperty("TokenExpirationDate", token.expirationData);
				datastore.put(txn, user);
				LOG.info("User '" + data.username + "' logged in sucessfully.");
				Key timeoutKey = KeyFactory.createKey("timeout", data.username);
				Transaction txn2 = datastore.beginTransaction();
				Entity timeout = new Entity(timeoutKey);
				timeout.setProperty("lastOp", System.currentTimeMillis());
				datastore.put(txn2, timeout);
				txn.commit();
				txn2.commit();
				SessionInfo s = new SessionInfo(data.username, token.tokenID);
				return Response.ok(g.toJson(s)).build();
			} else {
				// Incorrect password
				ustats.setProperty("user_stats_failed", 1L + (long) ustats.getProperty("user_stats_failed"));
				datastore.put(txn, ustats);
				txn.commit();

				LOG.warning("Wrong password for username: " + data.username);
				return Response.status(Status.UNAUTHORIZED).build();
			}
		} catch (EntityNotFoundException e) {
			// Username does not exist
			txn.rollback();
			LOG.warning("Failed login attempt for username: " + data.username);
			return Response.status(Status.NOT_FOUND).build();
		} 
		catch(Exception e){
			LOG.warning(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
			finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
	
	/**
	 * Management Methods:
	 */
	
	@Path("/oM")
	public class WorkerOccurrencyManagement {

		private final Gson g = new Gson();
		private final DropBoxResource dbIntegration = new DropBoxResource();

		public WorkerOccurrencyManagement() {
		}
		
		
		@Path("tag/{ocID}")
		@PUT
		@Consumes(MediaType.APPLICATION_JSON)
		public Response tagOccurrency(@PathParam("ocID") String ocID, SessionInfo session) {
			Transaction txn = datastore.beginTransaction();
			Key ocKey = KeyFactory.createKey("Occurrency", ocID);
			try {
				LOG.info("Attempt to get ocurrency: " + ocID);
				Entity occurrency = datastore.get(txn, ocKey);
				LOG.info("Got occurrency");
				occurrency.setProperty("flag", OccurrencyFlags.solving);
				datastore.put(txn, occurrency);
				txn.commit();
				return Response.ok().build();
			} catch (EntityNotFoundException e) {
				LOG.warning("Failed to locate ocurrency: " + ocID);
				return Response.status(Status.NOT_FOUND).build();
			} finally {
				if (txn.isActive()) {
					txn.rollback();
					return Response.status(Status.INTERNAL_SERVER_ERROR).build();
				}
			}
		}
		
		@Path("/solve/{ocID}/{imageID}")
		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		public Response solveOccurrency(@PathParam("ocID") String ocID, @PathParam("imageID") String imageID) {
			Transaction txn = datastore.beginTransaction();
			try {
				LOG.info("Attempt to get ocurrency: " + ocID);
				Key solvedOcKey = KeyFactory.createKey("Occurrency", ocID);
				Entity solvedOccurrency = datastore.get(solvedOcKey);
				List<String> images = (ArrayList) solvedOccurrency.getProperty("imagesID");
				images.add(imageID);
				solvedOccurrency.setProperty("flag", OccurrencyFlags.solved.toString());
				datastore.put(txn, solvedOccurrency);
				txn.commit();
				return Response.ok().build();
			} catch (EntityNotFoundException e) {
				LOG.warning("Failed to locate ocurrency: " + ocID);
				return Response.status(Status.NOT_FOUND).build();
			} finally {
				if (txn.isActive()) {
					txn.rollback();
					return Response.status(Status.INTERNAL_SERVER_ERROR).build();
				}
			}
		}
		
		@POST
		@Path("/saveImage/{extension}")
		@Consumes(MediaType.APPLICATION_OCTET_STREAM)
		@Produces(MediaType.APPLICATION_JSON)
		public Response uploadFileDropbox(byte[] file, @PathParam("extension") String ext) {
			String uuid = Utilities.generateID();
			Transaction txn = datastore.beginTransaction();
			LOG.info("Uploading image");
			try {
				dbIntegration.putFile(uuid, file , ext);
				LOG.info("Uploaded image with id "+uuid+"."+ext);
				txn.commit();
		} catch (IOException | DbxException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
			return Response.ok(g.toJson(uuid+"."+ext)).build();
		}
		
	}
		

}