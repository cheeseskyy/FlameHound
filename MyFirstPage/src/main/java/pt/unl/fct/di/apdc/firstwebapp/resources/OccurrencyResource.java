package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;

import com.dropbox.core.DbxException;

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
import com.google.gson.Gson;
import com.sun.research.ws.wadl.Application;
import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyFlags;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyTypes;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ImageData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.MessageData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyReadableData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyUpdateData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ZoneCoordsData;

@Path("/occurrency")
public class OccurrencyResource extends HttpServlet{
	
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final Logger LOG = Logger.getLogger(OccurrencyResource.class.getName());
	private final Gson g = new Gson();
	private static DropBoxResource dbIntegration = new DropBoxResource();
	private static final long TTL = 5*1000*60; //5 mins
	Map<String,OccurrencyReadableData> listCache = new ConcurrentHashMap<String, OccurrencyReadableData>();
	Map<String,byte[]> imageCache = new ConcurrentHashMap<String, byte[]>();
	private long lastUpdate = 0;
	
	public OccurrencyResource() {}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		RequestDispatcher r = request.getRequestDispatcher("pages/occurrencyForm.html");
		r.forward(request, response);
		updateCache();
	}
	
	@POST
	@Path("/occurrencyByZone")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOcurrencyByZone(ZoneCoordsData zone) {
		if(zone == null)
			return Response.status(Status.BAD_REQUEST).build();
		Response r = checkIsLoggedIn(new SessionInfo(zone.username, zone.tokenId));
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		
		if(System.currentTimeMillis() - TTL > lastUpdate)
			updateCache();
		List<OccurrencyReadableData> list = new ArrayList<OccurrencyReadableData>(listCache.values());
		for(int i = 0; i < list.size();) {
			String[] coords = list.get(i).location.split(",");
			long x = Long.parseLong(coords[0]);
			long y = Long.parseLong(coords[1]);
			
			if(y < zone.topY || y > zone.botY || x > zone.topX || x < zone.topY)
				list.remove(i);
			else
				i++;
		}
		return Response.ok().entity(g.toJson(list)).build();
	}
	
	@POST
	@Path("/searchByPrefixAndroid/{prefix}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getByPrefixAndroid(@PathParam("prefix") String prefix, SessionInfo[] info) {
		return getByPrefix(prefix, info[0]);
	}
	
	@POST
	@Path("/searchByPrefix/{prefix}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getByPrefix(@PathParam("prefix") String prefix, SessionInfo info) {
		Response r = checkIsLoggedIn(info);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		if(System.currentTimeMillis() - TTL > lastUpdate)
			updateCache();
		List<String> keys = new ArrayList<String>(listCache.keySet());
		for(int i = 0; i < keys.size();) {;
			if(!keys.get(i).startsWith(prefix))
				keys.remove(i);
			else
				i++;
		}
		return Response.ok().entity(g.toJson(keys)).build();
	}
	
	@POST
	@Path("/occurrencyByFlag/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOcurrencyByFlag(SessionInfo session, @PathParam("username") String flag ) {
		if(flag == null)
			return Response.status(Status.BAD_REQUEST).build();
		Response r = checkIsLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		
		if(System.currentTimeMillis() - TTL > lastUpdate)
			updateCache();
		
		List<OccurrencyReadableData> list = new ArrayList<OccurrencyReadableData>(listCache.values());
		for(int i = 0; i < list.size();) {
			if(!list.get(i).flag.toString().equals(flag))
				list.remove(i);
			else
				i++;
		}
		return Response.ok().entity(g.toJson(list)).build();
	}
	
	@POST
	@Path("/getByUserAndroid/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOcurrencyByUsernameAndroid(SessionInfo session[], @PathParam("username") String username) {
		return getOcurrencyByUsername(session[0], username);
	}
	
	@POST
	@Path("/getByUser/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOcurrencyByUsername(SessionInfo session, @PathParam("username") String username) {
		if(username == null)
			return Response.status(Status.BAD_REQUEST).build();
		Response r = checkIsLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		
		if(System.currentTimeMillis() - TTL > lastUpdate)
			updateCache();
		
		List<OccurrencyReadableData> list = new ArrayList<OccurrencyReadableData>(listCache.values());
		for(int i = 0; i < list.size();) {
			if(!list.get(i).user.equals(username))
				list.remove(i);
			else
				i++;
		}
		return Response.ok().entity(g.toJson(list)).build();
	}
	
	@POST
	@Path("/occurrencyByType/{type}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOcurrencyByType(SessionInfo session, @PathParam("type") String type) {
		OccurrencyTypes t;
		if((t = OccurrencyTypes.valueOf(type)) == null)
			return Response.status(Status.BAD_REQUEST).build();
		Response r = checkIsLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
		
		if(System.currentTimeMillis() - TTL > lastUpdate)
			updateCache();
		
		List<OccurrencyReadableData> list = new ArrayList<OccurrencyReadableData>(listCache.values());
		for(int i = 0; i < list.size();) {
			if(!list.get(i).type.equals(t))
				list.remove(i);
			else
				i++;
		}
		return Response.ok().entity(g.toJson(list)).build();
	}
	
	private Response checkIsLoggedIn(SessionInfo session) {
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
			if(!user.getProperty("TokenKey").equals(session.tokenId)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			LOG.info("Updating timeout");
			txn.commit();
			txn = datastore.beginTransaction();
			Key timeoutKey = KeyFactory.createKey("timeout", session.username);
			Entity timeout = datastore.get(timeoutKey);
			long lastOp = (long) timeout.getProperty("lastOp");
			if(System.currentTimeMillis() - lastOp > 10*60*1000) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			if(System.currentTimeMillis() - lastOp > 60*1000) {
				timeout.setProperty("lastOp", System.currentTimeMillis());
				datastore.put(txn, timeout);
			}
			LOG.info("User is logged in");
			txn.commit();
			return Response.ok(g.toJson(user)).build();
		}catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate username: " + session.username);
			return Response.status(Status.FORBIDDEN).build();
		} catch(Exception e){
			LOG.warning(e.getMessage());
			return Response.status(Status.FORBIDDEN).build();
		}
			finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		
	}
	
	@POST
	@Path("/saveOccurrency")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveOccurrency(OccurrencyData data) {
		Transaction txn = datastore.beginTransaction();
		Transaction txn2 = datastore.beginTransaction();
		LOG.info("Generating ID");
		String uuid = Utilities.generateID();
		try {
			LOG.info("Formatting coords");
			
			String aux = data.getLocation();
			LOG.info(aux);
			aux = aux.substring(1, aux.length()-2);
			String[] coords = aux.split(", ");
			LOG.info("Creating occurrency");
			Entity occurrency = new Entity("Occurrency", uuid);
			occurrency.setUnindexedProperty("title", data.getTitle());
			occurrency.setUnindexedProperty("description", data.getDescription());
			occurrency.setIndexedProperty("user", data.getUser());
			
			occurrency.setProperty("locationLat", coords[0].trim());
			occurrency.setProperty("locationLong", coords[1].trim());
			occurrency.setProperty("type", data.getType().toString());
			occurrency.setProperty("creationTime", System.currentTimeMillis());
			if(data.getMediaURI() != null && data.getMediaURI().size() > 0)
				occurrency.setProperty("imagesID", data.getMediaURI());
			else {
				List<String> mUri= data.mediaURI;
				mUri.add("default-occurrency.png");
				occurrency.setProperty("imagesID", mUri);
			}
			occurrency.setProperty("flag", OccurrencyFlags.unconfirmed.toString());
			occurrency.setProperty("worker", "");
			datastore.put(txn, occurrency);
			LOG.info("Put Occurrency");
			listCache.put(occurrency.getKey().toString(), convertOcToOcData(occurrency));
			
			Key userStatsKey = KeyFactory.createKey("userAppStats", data.getUser());
			Entity userStatsE = datastore.get(txn2, userStatsKey);
			long stat = ((long) userStatsE.getProperty("occurrenciesPosted"));
			userStatsE.setProperty("occurrenciesPosted", ++stat);
			datastore.put(txn2, userStatsE);
			txn.commit();
			txn2.commit();
		}catch (EntityNotFoundException e) {
			Key userStatsKey = KeyFactory.createKey("userAppStats", data.getUser());
			Entity userStatsE = new Entity(userStatsKey);
			userStatsE.setProperty("downvotes", 0);
			userStatsE.setProperty("upvotes", 0);
			userStatsE.setProperty("occurrenciesConfirmed", 0);
			userStatsE.setProperty("occurrenciesPosted", 1);
			datastore.put(txn2, userStatsE);
			txn2.commit();
			txn.commit();
		}catch (Exception e) {
			LOG.warning(e.getMessage());
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return Response.ok(g.toJson(new MessageData(uuid))).build();
	}
	
	@POST
	@Path("/getOccurrencyAndroid/{ocId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOccurrencyAndroid(SessionInfo[] session, @PathParam("ocId") String occurrencyID) {
		return getOccurrency(session[0], occurrencyID);
	}

	
	@POST
	@Path("/saveImageAndroid/{extension}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFileDropboxAndroid(ImageData img, @PathParam("extension") String ext) {
		Response r = uploadFileDropbox(img.image, ext);
		if(r.getStatus() != 200)
			return r;
		String id = (String) r.getEntity();
		return Response.ok(g.toJson(new MessageData(id))).build();
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
			LOG.info("Uploaded image with id "+uuid);
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
	
	@POST
	@Path("/getImage/{imageID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFileDropbox(SessionInfo session, @PathParam("imageID") String imageID){
		if(!session.username.equals("GETIMAGE")) {
			Response r = checkIsLoggedIn(session);
			if(r.getStatus() != Response.Status.OK.getStatusCode())
				return r;
		}
		byte[] file;		
		try {
			LOG.info("Getting image: " + imageID);
			int point = imageID.indexOf('.');
			String name = imageID.substring(0, point);
			String ext = imageID.substring(name.length()+1);
			file = dbIntegration.getFile(name, ext);
			LOG.info("Found file");
			imageCache.put(imageID, file);
			if(file == null)
				return Response.status(Status.NOT_FOUND).build();			
		}catch(Exception e) {
			LOG.warning(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(file).build();
	}
	
	@GET
	@Path("/getImageUri/{image}")
	@Produces("image/jpg")
	public Response getImageURI(@PathParam("image") String image) throws IOException {
		if(!imageCache.containsKey(image))
			downloadFileDropbox(new SessionInfo("GETIMAGE", "2167832"), image);
		if(!imageCache.containsKey(image))
			return Response.status(Status.NOT_FOUND).build();
		return Response.ok(imageCache.get(image)).build();
	}
	
	
	@POST
	@Path("/getImageAndroid/{imageID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response downloadFileDropboxAndroid(SessionInfo session, @PathParam("imageID") String imageID){
		Response r = downloadFileDropbox(session, imageID);
		if(r.getStatus() != 200)
			return r;
		byte[] image = r.readEntity(byte[].class);
		return Response.ok(g.toJson(new ImageData(image))).build();
	}
	
	public void updateCache() {
		LOG.info("Got general request, querying all occurrencies");
		Query q = new Query("Occurrency");
		PreparedQuery pQ = datastore.prepare(q);
		Iterator<Entity> it = pQ.asIterator();
		LOG.info("Got all");
		LOG.info("Converting all to readable format");
		while(it.hasNext()) {
			Entity oc = it.next();
			listCache.put(oc.getKey().getName(), convertOcToOcData(oc));
		}
		lastUpdate = System.currentTimeMillis();
	}
	
	@POST
	@Path("/getOccurrency/{ocID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOccurrency(SessionInfo session, @PathParam("ocID") String ocurrencyID) {
		
		Response r = checkIsLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		
		if(System.currentTimeMillis() - TTL > lastUpdate)
			updateCache();
		
		if(ocurrencyID.equals("all")) {
			return Response.ok(g.toJson(listCache.values())).build();
		}
		
		try {
			OccurrencyReadableData oc = listCache.get(ocurrencyID);
			if(oc == null)
				throw new EntityNotFoundException(null);
			return Response.ok(g.toJson(oc)).build();
		}catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate ocurrency: " + ocurrencyID);
			return Response.status(Status.NOT_FOUND).build();
		}
	}

	@SuppressWarnings("unchecked")
	private OccurrencyReadableData convertOcToOcData(Entity ocurrency) {
		String coordinates = ocurrency.getProperty("locationLat") + "," + ocurrency.getProperty("locationLong");
		OccurrencyReadableData oc = new OccurrencyReadableData(
				ocurrency.getKey().getName(),
				(String)ocurrency.getProperty("title"),
				(String)ocurrency.getProperty("description"),
				(String)ocurrency.getProperty("user"),
				coordinates,
				(String)ocurrency.getProperty("type"),
				(List<String>) ocurrency.getProperty("imagesID"),
				(String) ocurrency.getProperty("worker"));
		return oc;
	}
}
