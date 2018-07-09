package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ImageData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.MessageData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyStatsData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyUpdateData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ReportInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserStatsData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserUpdateData;

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
	private static final long TTLE = 5 * 60 * 1000; // TTL Entity 5 mins
	private static final long TTLS = 5 * 60 * 1000; // TTL Stats 2 mins

	private static DropBoxResource dbIntegration = new DropBoxResource();

	private long lastUpdateEntities = 0;
	private long lastUpdateImages = 0;

	private ConcurrentHashMap<String, byte[]> imageCache = new ConcurrentHashMap<String, byte[]>();
	private ConcurrentHashMap<String, Entity> entityCache = new ConcurrentHashMap<String, Entity>();

	public UserResource() {
	} // Nothing to be done here...

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		RequestDispatcher r = request.getRequestDispatcher("pages/login.html");
		r.forward(request, response);
	}

	@POST
	@Path("/report/{userReported}/{ocId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reportUser(@PathParam("ocId") String ocID, @PathParam("userReported") String usernameR,
			ReportInfo session) {
		Response r = validLogin(new SessionInfo(session.username, session.tokenId));
		if (r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		Key userReportKey = KeyFactory.createKey("UserReport", usernameR + "_" + System.currentTimeMillis());
		Transaction txn = datastore.beginTransaction();
		try {
			Entity userReport = new Entity(userReportKey);
			userReport.setIndexedProperty("ReporterInfo", session.username);
			userReport.setUnindexedProperty("Description", session.description);
			userReport.setIndexedProperty("ReportOc", ocID);
			datastore.put(txn, userReport);
			txn.commit();
		} catch (Exception e) {
			LOG.warning(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return Response.ok().build();
	}

	@POST
	@Path("/voteWorker/{operation}/{workerId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response voteWorker(SessionInfo session, @PathParam("operation") String op,
			@PathParam("workerId") String workerID) {
		Response r = validLogin(session);
		if (r.getStatus() != Response.Status.OK.getStatusCode())
			return r;

		Key workerKey = KeyFactory.createKey("UserWorker", workerID);
		Transaction txn = datastore.beginTransaction();
		try {
			Entity worker = datastore.get(workerKey);
			if (op.equals("upvote"))
				worker.setProperty("approvalRate", (long) worker.getProperty("approvalRate") + 1);
			if (op.equals("downvote"))
				worker.setProperty("disapprovalRate", (long) worker.getProperty("disapprovalRate") + 1);
			datastore.put(txn, worker);
			txn.commit();
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			txn.rollback();
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@POST
	@Path("/vote/{operation}/{ocId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response voteOc(SessionInfo session, @PathParam("ocId") String ocID,
			@PathParam("operation") String operation) {
		Entity user;
		Response r = validLogin(session);
		if (r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		user = (Entity) r.getEntity();

		if (operation.equals("upvote")) {
			Key likedOcKey = KeyFactory.createKey(user.getKey(), "likedOc", user.getKey().getName());

			Transaction txn = datastore.beginTransaction();
			try {
				Entity likedOcs = datastore.get(txn, likedOcKey);

				@SuppressWarnings("unchecked")
				List<String> likedOcList = (ArrayList<String>) likedOcs.getProperty("occurrencies");

				likedOcList.add(ocID);

				likedOcs.setProperty("occurrencies", likedOcList);

				txn.commit();
			} catch (EntityNotFoundException e) {
				List<String> likedOcList = new ArrayList<String>();
				likedOcList.add(ocID);
				Entity likedOcs = new Entity(likedOcKey);
				likedOcs.setProperty("ocurrencies", likedOcList);

				datastore.put(txn, likedOcs);
				txn.commit();
			} catch (Exception e) {
				LOG.warning(e.getMessage());
				return Response.status(Status.FORBIDDEN).build();
			} finally {
				if (txn.isActive()) {
					txn.rollback();
					return Response.status(Status.INTERNAL_SERVER_ERROR).build();
				}
			}
		}
		return updateRelatedStats(ocID, operation);
	}

	@POST
	@Path("/saveProfileImageAndroid/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFileDropboxAndroid(ImageData img, @PathParam("name") String name) {
		return uploadFileDropbox(img.image, name);
	}

	@POST
	@Path("/saveProfileImage/{name}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFileDropbox(byte[] file, @PathParam("name") String name) {
		LOG.info("Uploading image");
		LOG.info(name);
		String nameL;
		String extension = "jpg";
		try {
			LOG.info(name.substring(0, name.indexOf(".")) + " " + name.substring(name.indexOf(".") + 1));
			nameL = name.substring(0, name.indexOf("."));
			extension = name.substring(name.indexOf(".") + 1);
		} catch (IndexOutOfBoundsException e) {
			nameL = name;
		}
		String uuid = Utilities.generateID();
		try {
			dbIntegration.delete(uuid);
		} catch (Exception e) {
		}
		try {
			dbIntegration.putFile(uuid, file, extension);
		} catch (IOException | DbxException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} catch (Exception e) {
			LOG.info(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		Transaction txn = datastore.beginTransaction();
		Key profileImageKey = KeyFactory.createKey("ProfileImage", nameL);
		Entity profileImage = new Entity(profileImageKey);
		profileImage.setProperty("uri", uuid + "." + extension);
		datastore.put(txn, profileImage);
		txn.commit();
		return Response.ok(new MessageData("OK")).build();
	}

	@GET
	@Path("/getImageUri/{username}")
	@Produces("image/jpg")
	public Response getImageURI(@PathParam("username") String username) throws IOException, EntityNotFoundException {
		LOG.info("Getting image for username " + username);
		byte[] f;
		try {
			f = (byte[]) downloadFileDropbox(new SessionInfo("GETIMAGE", "2167832"), username).getEntity();
		} catch (EntityNotFoundException e) {
			f = (byte[]) downloadFileDropbox(new SessionInfo("GETIMAGE", "2167832"), "defaultP").getEntity();
		}
		return Response.ok(f).build();
	}

	@POST
	@Path("/getImage/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFileDropbox(SessionInfo session, @PathParam("username") String username)
			throws EntityNotFoundException {
		if (!session.username.equals("GETIMAGE")) {
			Response r = validLogin(session);
			if (r.getStatus() != Response.Status.OK.getStatusCode())
				return r;
		}
		if(imageCache.containsKey(username))
			return Response.ok(imageCache.get(username)).build();
		
		String imageID = "";
		Key profileKey = KeyFactory.createKey("ProfileImage", username);
		Entity picture = datastore.get(profileKey);
		imageID = (String) picture.getProperty("uri");
		byte[] file;
		String ext = "jpg";
		String name = imageID;
		try {
			LOG.info("Getting image: " + imageID);
			int point = imageID.indexOf('.');
			name = imageID.substring(0, point);
			ext = imageID.substring(name.length() + 1);
		} catch (IndexOutOfBoundsException e) {
		}
		try {
			file = dbIntegration.getFile(name, ext);
			LOG.info("Found file");
			imageCache.put(username, file);
			if (file == null)
				return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
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
		} catch (EntityNotFoundException e) {
			Entity occurrencyStats = new Entity(ocStatsKey);
			occurrencyStats.setProperty("user", username);
			occurrencyStats.setProperty("upvotes", 0);
			occurrencyStats.setProperty("downvotes", 0);
			occurrencyStats = updateStats(operation, occurrencyStats);
			txn.commit();
		} catch (Exception e) {
			LOG.info(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
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
		if (operation.equals("upvote"))
			occurrencyStats.setProperty("upvotes", (long) occurrencyStats.getProperty("upvotes") + 1);
		if (operation.equals("downvote"))
			occurrencyStats.setProperty("upvotes", (long) occurrencyStats.getProperty("downvotes") + 1);

		return occurrencyStats;
	}

	@POST
	@Path("/getRole")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRole(SessionInfo session) {
		Entity user;
		Response r = validLogin(session);
		if (r.getStatus() != Response.Status.OK.getStatusCode())
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
			if(!entityCache.containsKey(session.username) || System.currentTimeMillis() - lastUpdateEntities > TTLE)
				entityCache.put(session.username, user);

			return Response.ok(user).build();
		} catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate username: " + session.username);
			txn.rollback();
			txn2.rollback();
			return Response.status(Status.FORBIDDEN).build();
		} catch (Exception e) {
			LOG.info(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
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
		if (r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		user = (Entity) r.getEntity();

		return Response.ok(g.toJson(user.getProperty("address"))).build();

	}

	@POST
	@Path("/getStats/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserStatistics(SessionInfo session, @PathParam("username") String username) {
		Response r = validLogin(session);
		if (r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		
			UserStatsData uD = calculateUserStats(username);

			Key userStatsKey = KeyFactory.createKey("userAppStats", username);
			Transaction txn = datastore.beginTransaction();
			try {
				Entity userStatsE = datastore.get(txn, userStatsKey);
				userStatsE.setProperty("upvotes", uD.upvotes);
				userStatsE.setProperty("downvotes", uD.downvotes);
				datastore.put(txn, userStatsE);
				UserStatsData userStats = new UserStatsData((long) userStatsE.getProperty("upvotes"),
						(long) userStatsE.getProperty("downvotes"), (long) userStatsE.getProperty("occurrenciesPosted"),
						(long) userStatsE.getProperty("occurrenciesConfirmed"));
				txn.commit();
				return Response.ok().entity(g.toJson(userStats)).build();
			} catch (EntityNotFoundException e) {
				LOG.warning("Could not find stats for user: " + username);
				return Response.status(Status.NOT_FOUND).build();
			} finally {
				if (txn.isActive()) {
					txn.rollback();
					return Response.status(Status.INTERNAL_SERVER_ERROR).build();
				}
			}
	}

	private UserStatsData calculateUserStats(String username) {
		UserStatsData data = new UserStatsData();
		Filter propertyFilter = new FilterPredicate("user", FilterOperator.EQUAL, username);
		Query q = new Query("OccurrencyStats").setFilter(propertyFilter);
		PreparedQuery pQ = datastore.prepare(q);
		List<Entity> list = pQ.asList(FetchOptions.Builder.withDefaults());

		data.occurrenciesPosted = list.size();
		long upvotes = 0;
		long downvotes = 0;
		for (int i = 0; i < list.size(); i++) {
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
	public Response updateProfile(UserUpdateData info) {
		Response r = validLogin(new SessionInfo(info.username, info.tokenId));
		if (r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		Entity user = (Entity) r.getEntity();
		Transaction txn = datastore.beginTransaction();
		user.setProperty("user_name", info.name);
		user.setProperty("email", info.email);
		user.setProperty("homeNumber", info.homeNumber);
		user.setProperty("phoneNumber", info.phoneNumber);
		user.setProperty("address", info.address);
		user.setProperty("nif", info.nif);
		user.setProperty("cc", info.cc);
		user.setProperty("user_pwd", DigestUtils.sha512Hex(info.password));
		datastore.put(txn, user);
		txn.commit();
		entityCache.put(user.getKey().getName(), user);
		return Response.ok().build();
	}

	@POST
	@Path("/getUserInfo/{user}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserInfo(SessionInfo session, @PathParam("user") String username) {
		Entity userE;
		Response r = validLogin(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		
		if(username == null || username.equals(session.username) || username.equals("")) {
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
			return Response.ok(g.toJson(user)).build();
		}
		else {
			
			if(!entityCache.containsKey(username)) {
			Key userKey = KeyFactory.createKey("User", username);
				try {
					userE = datastore.get(userKey);
				} catch (EntityNotFoundException e) {
					return Response.status(Status.NOT_FOUND).build();
				}
			}else
				userE = entityCache.get(username);
			
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
			return Response.ok(g.toJson(user)).build();
		}
	}

	@PUT
	@Path("/updateOccurrency/{ocID}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOccurrency(OccurrencyUpdateData data, @PathParam("ocID") String ocID) {
		Response r = validLogin(new SessionInfo(data.username, data.tokenId));
		if (r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		Transaction txn = datastore.beginTransaction();
		Key ocKey = KeyFactory.createKey("Ocurrency", ocID);
		try {
			LOG.info("Attempt to get ocurrency: " + ocID);
			Entity occurrency = datastore.get(txn, ocKey);
			LOG.info("Got occurrency");
			if (!occurrency.getProperty("user").equals(data.username)) {
				txn.commit();
				Response.status(Status.FORBIDDEN).build();
			}

			LOG.info("Replacing values");
			String aux = data.getLocation();
			LOG.info(aux);
			aux = aux.substring(1, aux.length() - 2);
			String[] coords = aux.split(", ");
			LOG.info("Creating occurrency");
			occurrency.setUnindexedProperty("title", data.getTitle());
			occurrency.setUnindexedProperty("description", data.getDescription());
			occurrency.setIndexedProperty("user", data.getUser());

			occurrency.setProperty("locationLat", coords[0].trim());
			occurrency.setProperty("locationLong", coords[1].trim());
			occurrency.setProperty("type", data.getType().toString());
			occurrency.setProperty("creationTime", System.currentTimeMillis());
			occurrency.setProperty("imagesID", data.getMediaURI());
			occurrency.setProperty("flag", data.flag.toString());
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

}