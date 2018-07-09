package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.LogOperation;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.LogType;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ReportData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserInfo;

@Path("/UM")
public class UMResource {

	private static final Logger LOG = Logger.getLogger(BackEndResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public Utilities ut = new Utilities();
	private static final long TTL = 5*1000*60; //5 mins
	Map<String, UserInfo> listCache = new ConcurrentHashMap<String, UserInfo>();
	private long lastUpdate = 0;
	
	
	@Path("/getUsers/{userId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers(@PathParam("userId") String userId, SessionInfo session) {
		Response r = ComputationResource.validLogin(session);
		if(r.getStatus() != 200 || !((String) r.getEntity()).contains("ADMIN"))
			return Response.status(Status.FORBIDDEN).build();
		
		if(System.currentTimeMillis() - TTL > lastUpdate)
			updateCache();
		
		if(userId.equals("all"))
			return Response.ok(g.toJson(listCache.values())).build();
		
		try {
			UserInfo user= listCache.get(userId);
			if( user == null)
				throw new EntityNotFoundException(null);
			return Response.ok(g.toJson(user)).build();
		}catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate user: " + userId);
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	private void updateCache() {
		LOG.info("Got general request, querying all reports");
		Query q = new Query("User");
		PreparedQuery pQ = datastore.prepare(q);
		Iterator<Entity> it = pQ.asIterator();
		LOG.info("Got all");
		LOG.info("Converting all to readable format");
		while(it.hasNext()) {
			Entity rep = it.next();
			listCache.put(rep.getKey().getName(), convertUserToUserInfo(rep));
		}
		lastUpdate = System.currentTimeMillis();
	}


	
	@SuppressWarnings("unchecked")
	private UserInfo convertUserToUserInfo(Entity user) {
		return new UserInfo(
				(String)user.getProperty("user_name"),
				(String)user.getKey().getName(),
				(String)user.getProperty("email"),
				(String)user.getProperty("homeNumber"),
				(String)user.getProperty("phoneNumber"),
				(String)user.getProperty("address"),
				(String)user.getProperty("nif"),
				(String)user.getProperty("cc")
				);
	}


	@Path("/delete/{userId}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteReport(@PathParam("userId") String userId, SessionInfo session) {
		Response r = ComputationResource.validLogin(session);
		if(r.getStatus() != 200 || !((String) r.getEntity()).contains("ADMIN"))
			return Response.status(Status.FORBIDDEN).build();
		
			listCache.remove(userId);
			Transaction txn = datastore.beginTransaction();
			LOG.info("Deleting user with id: " + userId);
			Key userKey = KeyFactory.createKey("User", userId);
			datastore.delete(userKey);
			txn.commit();
			LOG.info("User Deleted");
			IntegrityLogsResource.insertNewLog(LogOperation.Delete, new String[]{userId}, LogType.User, session.username);
			updateCache();
			return Response.ok().build();
	}

}