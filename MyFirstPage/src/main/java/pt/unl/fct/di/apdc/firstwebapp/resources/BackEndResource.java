package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.Enums.LogOperation;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.LogType;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.UserRoles;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AdminRegisterInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.MessageData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ModeratorRegisterInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.RegisterData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.WorkerRegisterInfo;

@Path("/_be/_admin")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class BackEndResource extends HttpServlet {

	/**
	 * 
	 */
	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(BackEndResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public long validLogin = 0;
	public BackEndResource() {
	} // Nothing to be done here...

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		RequestDispatcher r = request.getRequestDispatcher("pages/login.html");
		r.forward(request, response);
	}
	
	@POST
	@Path("/addAdmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNewAdmin(AdminRegisterInfo info) {
		Response r = ComputationResource.validLogin(new SessionInfo(info.registerUsername, info.tokenId));
		if(r.getStatus() != 200 || !((String)r.getEntity()).contains("ADMIN"))
			return Response.status(Status.FORBIDDEN).build(); 
		LOG.fine("Attempt to register admin: " + info.username);
		if(info.username == null || info.password == null)
			return Response.status(Status.BAD_REQUEST).build();
		Transaction txn = datastore.beginTransaction();
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("UserAdmin", info.username);
			datastore.get(userKey);
			txn.rollback();
			return Response.status(Status.CONFLICT).entity("Username").build(); 
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("UserAdmin", info.username);
			user.setProperty("adminPermission", info.registerUsername);
			user.setProperty("creationTime", new Date());
			datastore.put(txn,user);
			LOG.info("Admin registered " + info.username);
			txn.commit();
			RegisterResource.registerUserV3(new RegisterData(info.name, info.username, info.email, "ADMIN", "","","","","",info.password, info.password));
			IntegrityLogsResource.insertNewLog(LogOperation.ADD, new String[]{"addAdmin with username: ", info.username}, LogType.Other, info.registerUsername);
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/addModerator")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNewModerator(ModeratorRegisterInfo info) {
		Response r = ComputationResource.validLogin(new SessionInfo(info.registerUsername, info.tokenId));
		if(r.getStatus() != 200 || !((String)r.getEntity()).contains("ADMIN"))
			return Response.status(Status.FORBIDDEN).build(); 
		LOG.fine("Attempt to register moderator: " + info.username);
		if(info.username == null || info.password == null || info.entity == null)
			return Response.status(Status.BAD_REQUEST).build();
		Transaction txn = datastore.beginTransaction();
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("UserModerator", info.username);
			datastore.get(userKey);
			txn.rollback();
			return Response.status(Status.CONFLICT).entity("Username").build(); 
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("UserModerator", info.username);
			user.setProperty("adminPermission", info.registerUsername);
			user.setProperty("creationTime", new Date());
			user.setProperty("entity", info.entity);
			datastore.put(txn,user);
			LOG.info("Moderator registered " + info.username);
			txn.commit();
			IntegrityLogsResource.insertNewLog(LogOperation.ADD, new String[]{"addMod with username: ", info.username}, LogType.Other, info.registerUsername);
			RegisterResource.registerUserV3(new RegisterData(info.name, info.username, info.email, "MODERATOR", "","","","","",info.password, info.password));
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/addWorker")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNewWorker(WorkerRegisterInfo info) {
		
		Response r1 = ComputationResource.validLogin(new SessionInfo(info.registerUsername, info.tokenId));
		if(r1.getStatus() != 200 || !((String) r1.getEntity()).contains("ADMIN")|| !((String) r1.getEntity()).contains("MODERATOR")) {
		}
		
		LOG.fine("Attempt to register worker: " + info.username);
		if(info.username == null || info.password == null|| info.entity == null)
			return Response.status(Status.BAD_REQUEST).build();
		Transaction txn = datastore.beginTransaction();
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("UserWorker", info.username);
			datastore.get(userKey);
			txn.rollback();
			return Response.status(Status.CONFLICT).entity("Username").build(); 
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("UserWorker", info.username);
			user.setProperty("ocurrenciesTreated", 0);
			user.setProperty("approvalRate", 0);
			user.setProperty("disapprovalRate", 0);
			user.setProperty("adminPermission", info.registerUsername);
			user.setProperty("creationTime", new Date());
			user.setProperty("entity", info.entity);
			datastore.put(txn,user);
			LOG.info("Worker registered " + info.username);
			txn.commit();
			IntegrityLogsResource.insertNewLog(LogOperation.ADD, new String[]{"addWorker with username: ", info.username}, LogType.Other, info.registerUsername);
			RegisterResource.registerUserV3(new RegisterData(info.name, info.username, info.email, "ADMIN", "","","","","",info.password, info.password));
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLogin(LoginData data, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context HttpHeaders headers) throws ServletException, IOException {
		Response r = doAdminLogin(data, request, response, headers);
		if(r.getStatus() == 200)
			return r;
		else
			if(r.getStatus() == 404)
				return doModLogin(data, request, response, headers);
		return r;
	}
	
	@POST
	@Path("/validLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLogin(SessionInfo session) {
		if(System.currentTimeMillis() - validLogin < 60000)
			return Response.ok().build();
		Response r = ComputationResource.validLogin(session);
		return r;
	}
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLogout(String username) {
			Transaction txn = datastore.beginTransaction();
			Key userKey = KeyFactory.createKey("UserAdmin", username);
			try {
				Entity user = datastore.get(txn, userKey);
				user.setProperty("TokenKey","");
				user.setProperty("TokeCreationDate", "");
				user.setProperty("TokenExpirationDate","");
				
				datastore.put(txn,user);
				txn.commit();
			} catch (EntityNotFoundException e) {
				userKey = KeyFactory.createKey("UserModerator", username);
				try {
					Entity user = datastore.get(txn, userKey);
					user.setProperty("TokenKey","");
					user.setProperty("TokeCreationDate", "");
					user.setProperty("TokenExpirationDate","");
					
					datastore.put(txn,user);
					txn.commit();
				} catch (EntityNotFoundException e2) {
					return Response.status(Status.FORBIDDEN).build();
				}
		}
		return Response.ok().build();
	}
	
	
	@POST
	@Path("/loginAdmin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doAdminLogin(LoginData data, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context HttpHeaders headers) throws ServletException, IOException {
		LOG.info("Attempt to login admin user: " + data.username);

		Transaction txn = datastore.beginTransaction();
		Key userKey = KeyFactory.createKey("UserAdmin", data.username);
		try {
			Entity user = datastore.get(userKey);
			LOG.info("Got AdminUser");
			// Obtain the user login statistics
			Query ctrQuery = new Query("AdminUserStats").setAncestor(userKey);
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
	
	@POST
	@Path("/loginModerator")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doModLogin(LoginData data, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context HttpHeaders headers) throws ServletException, IOException {
		LOG.info("Attempt to login admin user: " + data.username);

		Transaction txn = datastore.beginTransaction();
		Key userKey = KeyFactory.createKey("UserModerator", data.username);
		try {
			Entity user = datastore.get(userKey);
			LOG.info("Got AdminUser");
			// Obtain the user login statistics
			Query ctrQuery = new Query("ModeratorUserStats").setAncestor(userKey);
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
	
	@Path("/getLogs")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLogs(SessionInfo session) {
		Response r = ComputationResource.validLogin(session);
		if(r.getStatus() != 200 || !g.fromJson((String) r.getEntity(), String.class).equals("ADMIN"))
			return r;
		Transaction txn = datastore.beginTransaction();
		Key adminLogs = KeyFactory.createKey("OperationLogs", "Logs");
		String logText = null;
		try {
			Entity logs = datastore.get(txn, adminLogs);
			logText = (String) logs.getProperty("logText");
			txn.commit();
		}catch(EntityNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		Response integrity = ComputationResource.checkLogIntegrity();
		if(logText != null && integrity.getStatus() == 200)
			return Response.ok(new MessageData(logText)).build();
		else
			return Response.ok(new MessageData("Logs have been tampered with!\n" + logText)).build();
	}
}