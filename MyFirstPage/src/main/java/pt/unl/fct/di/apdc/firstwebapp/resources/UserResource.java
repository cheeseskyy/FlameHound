package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyFlags;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.UserRoles;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AdminInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AdminRegisterInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyStatsData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ReportInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserStatsData;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UserResource extends HttpServlet {

	/**
	 * 
	 */
	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(UserResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final long TTLE = 5*60*1000; //TTL Entity 5 mins
	private static final long TTLS = 5*60*1000; //TTL Stats 2 mins
	
	private static DropBoxResource dbIntegration = new DropBoxResource();
	
	private long lastUpdateEntity = 0;
	private long lastUpdateStats = 0;

	private String username = null;
	private Entity userEntity = null;
	private UserStatsData userStats = null;
	
	
	public UserResource() {
	} // Nothing to be done here...

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		RequestDispatcher r = request.getRequestDispatcher("pages/login.html");
		r.forward(request, response);
	}
	
	
	@POST
	@Path("/report/{userReported}/{ocId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reportUser(@PathParam("ocId") String ocID, @PathParam("userReported") String usernameR, ReportInfo session) {
		Response r = validLogin(new SessionInfo(session.username, session.tokenId));
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		Key userReportKey = KeyFactory.createKey("UserReport", usernameR + "_" + System.currentTimeMillis());
		Transaction txn = datastore.beginTransaction();
		try {
			Entity userReport = new Entity(userReportKey);
			userReport.setIndexedProperty("ReporterInfo", session.username);
			userReport.setUnindexedProperty("Description", session.description);
			userReport.setIndexedProperty("ReportOc", ocID);
			datastore.put(txn ,  userReport);
			txn.commit();
		}catch(Exception e) {
			LOG.warning(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return Response.ok().build();
	}
	
	@POST
	@Path("/vote/{operation}/{ocId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response voteOc(SessionInfo session, @PathParam("ocId") String ocID, @PathParam("operation") String operation) {
		Entity user;
		Response r = validLogin(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		user = (Entity) r.getEntity();
		
		Key likedOcKey = KeyFactory.createKey(user.getKey(), "likedOc", user.getKey().getName()); 
		
		Transaction txn = datastore.beginTransaction();
		try {
			Entity likedOcs = datastore.get(txn, likedOcKey);
			
			@SuppressWarnings("unchecked")
			List<String> likedOcList = (ArrayList<String>) likedOcs.getProperty("occurrencies");
			
			likedOcList.add(ocID);
			
			likedOcs.setProperty("occurrencies", likedOcList);
			
			txn.commit();
		}catch(EntityNotFoundException e) {
			List<String> likedOcList = new ArrayList<String>();
			likedOcList.add(ocID);
			Entity likedOcs = new Entity(likedOcKey);
			likedOcs.setProperty("ocurrencies", likedOcList);
			
			datastore.put(txn, likedOcs);
			txn.commit();
		}catch(Exception e) {
			LOG.warning(e.getMessage());
			return Response.status(Status.FORBIDDEN).build();
		}finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return updateRelatedStats(ocID, operation);
	}
	
	@POST
	@Path("/saveProfileImage/{extension}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFileDropbox(byte[] file, @PathParam("extension") String ext) {
		if(username == null)
			return Response.status(Status.FORBIDDEN).build();
		String uuid = Utilities.generateID();
		LOG.info("Uploading image");
		try {
			dbIntegration.putFile(uuid, file , ext);
			LOG.info("Uploaded image with id "+uuid);
	} catch (IOException | DbxException e) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	} catch (Exception e) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}
		Transaction txn = datastore.beginTransaction();
		Key profileKey = KeyFactory.createKey("ProfilePicture", username);
		try {
			Entity profilePicE = datastore.get(txn, profileKey);
			profilePicE.setUnindexedProperty("Picture", uuid+"."+ext);
			datastore.put(txn, profilePicE);
		}catch(EntityNotFoundException e) {
			Entity profilePicE = new Entity(profileKey);
			profilePicE.setUnindexedProperty("Picture", uuid+"."+ext);
			datastore.put(txn, profilePicE);
		}
		txn.commit();
		return Response.ok().build();
	}
	
	@POST
	@Path("/getImage/{imageID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFileDropbox(SessionInfo session, @PathParam("imageID") String imageID){
		Response r = validLogin(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		byte[] file;		
		try {
			LOG.info("Getting image: " + imageID);
			int point = imageID.indexOf('.');
			String name = imageID.substring(0, point);
			String ext = imageID.substring(name.length()+1);
			file = dbIntegration.getFile(name, ext);
			LOG.info("Found file");
			if(file == null)
				return Response.status(Status.NOT_FOUND).build();			
		}catch(Exception e) {
			LOG.warning(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(file).build();
	}
	
	private Response updateRelatedStats(String ocID, String operation) {
		Transaction txn = datastore.beginTransaction();
		Transaction txn2 = datastore.beginTransaction();
		
		Key ocStatsKey = KeyFactory.createKey("OccurrencyStats", ocID);
		Key ocKey = KeyFactory.createKey("Occurrency", ocID);
		
		Entity occurrency = null;
		try {
			occurrency = datastore.get(txn2, ocKey);
		} catch (EntityNotFoundException e1) {
		}
		txn2.commit();
		String username = (String) occurrency.getProperty("user");
		
		try {
			Entity occurrencyStats = datastore.get(txn, ocStatsKey);
			occurrencyStats = updateStats(operation, occurrencyStats);
			datastore.put(txn, occurrencyStats);
			txn.commit();
		}catch(EntityNotFoundException e) {
			Entity occurrencyStats = new Entity(ocStatsKey);
			occurrencyStats.setProperty("user", username);
			occurrencyStats.setProperty("upvotes", 0);
			occurrencyStats.setProperty("downvotes", 0);
			occurrencyStats = updateStats(operation, occurrencyStats);
			txn.commit();
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
		return Response.ok().build();
	}

	private Entity updateStats(String operation, Entity occurrencyStats) {
		if(operation.equals("upvote"))
			occurrencyStats.setProperty("upvotes",
					(long) occurrencyStats.getProperty("upvotes") + 1);
		if(operation.equals("downvote"))
			occurrencyStats.setProperty("upvotes",
					(long) occurrencyStats.getProperty("downvotes") + 1);
		
		return occurrencyStats;
	}

	@POST
	@Path("/getRole")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRole(SessionInfo session) {
		Entity user;
		Response r = validLogin(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		user = (Entity) r.getEntity();
		
		return Response.ok(g.toJson(user.getProperty("role"))).build();

	}
	
	public Response validLogin(SessionInfo session) {
		if (session.tokenId.equals("0")) {
			LOG.warning("User is not logged in");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		Transaction txn2 = datastore.beginTransaction();
		Key userKey = KeyFactory.createKey("User", session.username);
		try {
			LOG.info("Attempt to get user: " + session.username);
			Entity user = datastore.get(txn, userKey);
			LOG.info("Got user");
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
			if (System.currentTimeMillis() - lastOp > 10 * 60 * 1000) {
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
			if(this.username == null)
				this.username = session.username;
			if(this.userEntity == null || System.currentTimeMillis() - lastUpdateEntity < TTLE) {
				this.userEntity = user;
				lastUpdateEntity = System.currentTimeMillis();
			}
			
			return Response.ok(user).build();
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
	
	@POST
	@Path("/getAddress")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAddress(SessionInfo session) {
		
		Entity user;
		Response r = validLogin(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		user = (Entity) r.getEntity();
		
		return Response.ok(g.toJson(user.getProperty("address"))).build();
		
	}
	
	@POST
	@Path("/getStats")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserStatistics(SessionInfo session) {
		Response r = validLogin(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		if(userStats == null || System.currentTimeMillis() - lastUpdateStats > TTLS) {
			
			UserStatsData uD = calculateUserStats(session.username);
			
			Key userStatsKey = KeyFactory.createKey("userAppStats", session.username);
			Transaction txn = datastore.beginTransaction();
			try {
				Entity userStatsE = datastore.get(txn, userStatsKey);
				userStatsE.setProperty("upvotes", uD.upvotes);
				userStatsE.setProperty("downvotes", uD.downvotes);
				datastore.put(txn, userStatsE);
				UserStatsData userStats = new UserStatsData(
						(long) userStatsE.getProperty("upvotes"),
						(long) userStatsE.getProperty("downvotes"),
						(long) userStatsE.getProperty("occurrenciesPosted"),
						(long) userStatsE.getProperty("occurrenciesConfirmed"));
				
				this.userStats = userStats;
				lastUpdateStats = System.currentTimeMillis();
				txn.commit();
				return Response.ok().entity(g.toJson(userStats)).build();
			} catch (EntityNotFoundException e) {
				LOG.warning("Could not find stats for user: " + session.username);
				return Response.status(Status.NOT_FOUND).build();
			} finally {
				if (txn.isActive()) {
					txn.rollback();
					return Response.status(Status.INTERNAL_SERVER_ERROR).build();
				}
			}
		}
		
		return Response.ok().entity(g.toJson(userStats)).build();
	}
	
	private UserStatsData calculateUserStats(String username) {
		UserStatsData data = new UserStatsData();
		Filter propertyFilter =
			    new FilterPredicate("user", FilterOperator.EQUAL, username);
		Query q = new Query("OccurrencyStats").setFilter(propertyFilter);
		PreparedQuery pQ = datastore.prepare(q);
		List<Entity> list = pQ.asList(FetchOptions.Builder.withDefaults());
		
		data.occurrenciesPosted = list.size();
		long upvotes = 0;
		long downvotes = 0;
		for(int i = 0; i<list.size(); i++) {
			Entity e = list.get(i);
			upvotes += (long) e.getProperty("upvotes");
			downvotes += (long) e.getProperty("downvotes");
		}
		data.downvotes = downvotes;
		data.upvotes = upvotes;
		return data;
	}

	@POST
	@Path("/updateProfile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfile(SessionInfo session){
		Response r = validLogin(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		Entity user = (Entity) r.getEntity();
		Transaction txn = datastore.beginTransaction();
		@SuppressWarnings("unchecked")
		Iterator<String> it = ((List<String>) session.getArgs().get(0)).iterator();
		while(it.hasNext()) {
			String param = it.next();
			String[] line = param.split(":");
			LOG.info("Updating parameter " + line[0].trim() + " with value " + line[1].trim());
			user.setProperty(line[0].trim(), line[1].trim());
		}
		datastore.put(txn, user);
		txn.commit();
		userEntity = user;
		return Response.ok().build();
	}
	
	
	@POST
	@Path("/getUserInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserInfo(SessionInfo session) {
		
		Entity userE;
		Response r = validLogin(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		userE = (Entity) r.getEntity();
		
		String name = (String) userE.getProperty("user_name");
		String uN = userE.getKey().toString();
		uN = uN.substring(6, uN.length()-2);
		String email = (String) userE.getProperty("email");
		String hN = (String) userE.getProperty("homeNumber");
		String pN = (String) userE.getProperty("phoneNumber");
		String add = (String) userE.getProperty("address");
		String nif = (String) userE.getProperty("nif");
		String cc = (String) userE.getProperty("cc");
		UserInfo user = new UserInfo(name,uN,email,hN,pN,add,nif,cc);
		return Response.ok(user).build();
	}

}