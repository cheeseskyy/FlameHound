package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.LinkedList;
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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.Location;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.OccurrencyData;
import pt.unl.fct.di.apdc.firstwebapp.util.OccurrencyTypes;
import pt.unl.fct.di.apdc.firstwebapp.util.SessionInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.UserInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;

@Path("/occurrency")
public class OccurrencyResource extends HttpServlet{
	
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final Logger LOG = Logger.getLogger(OccurrencyResource.class.getName());
	private final Gson g = new Gson();
	private static DropBoxResource dbIntegration = new DropBoxResource();
	
	public OccurrencyResource() {}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		RequestDispatcher r = request.getRequestDispatcher("pages/occurrencyForm.html");
		r.forward(request, response);
	}
	
	
	@POST
	@Path("/occurrencyByUser/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOcurrencyByUsername(SessionInfo session, @PathParam("username") String username) {
		if(username == null)
			return Response.status(Status.BAD_REQUEST).build();
		Response r = checkIsLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
			LOG.info("Preparing filter");
			Filter propertyFilter =
			    new FilterPredicate("user", FilterOperator.EQUAL, username);
			LOG.info("Preparing query");
			Query q = new Query("Occurrency").setFilter(propertyFilter);
			LOG.info("Executing query");
			PreparedQuery pQ = datastore.prepare(q);
			LOG.info("Iterator");
			List<Entity> it = pQ.asList(FetchOptions.Builder.withDefaults());
			return Response.ok().entity(g.toJson(it)).build();
	}
	
	@POST
	@Path("/occurrencyByType/{type}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOcurrencyByType(SessionInfo session, @PathParam("type") String type) {
		if(OccurrencyTypes.valueOf(type) == null)
			return Response.status(Status.BAD_REQUEST).build();
		Response r = checkIsLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return Response.status(Status.FORBIDDEN).build();
			LOG.info("Preparing filter");
			Filter propertyFilter =
			    new FilterPredicate("type", FilterOperator.EQUAL, type);
			LOG.info("Preparing query");
			Query q = new Query("Occurrency").setFilter(propertyFilter);
			LOG.info("Executing query");
			PreparedQuery pQ = datastore.prepare(q);
			LOG.info("Iterator");
			List<Entity> it = pQ.asList(FetchOptions.Builder.withDefaults());
			return Response.ok().entity(g.toJson(it)).build();
	}

	private Response checkIsLoggedIn(SessionInfo session) {
		if(session.tokenId.equals("0")) {
			LOG.warning("User is not logged in");
			 Response.status(Status.FORBIDDEN).build();
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
			return Response.ok(user).build();
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
	
	@POST
	@Path("/saveOccurrency")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveOccurrency(OccurrencyData data) {
		Transaction txn = datastore.beginTransaction();
		LOG.info("Generating ID");
		String uuid = Utilities.generateID();
		try {
			String[] coords = data.getLocation().substring(1, data.getLocation().length()-2).split(",");
			Entity occurrency = new Entity("Occurrency", uuid);
			occurrency.setUnindexedProperty("title", data.getTitle());
			occurrency.setUnindexedProperty("description", data.getDescription());
			occurrency.setIndexedProperty("user", data.getUser());
			occurrency.setProperty("locationLat", coords[0].substring(6).trim());
			occurrency.setProperty("locationLong", coords[1].substring(6).trim());
			occurrency.setProperty("type", data.getType().toString());
			occurrency.setProperty("creationTime", System.currentTimeMillis());
			occurrency.setProperty("imagesID", data.getMediaURI());
			datastore.put(txn, occurrency);
			LOG.info("Put Occurrency");
			txn.commit();
		}catch (Exception e) {
			
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return Response.ok(g.toJson(uuid)).build();
	}
	
	@POST
	@Path("/saveImage/{extension}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFileDropbox(byte[] file, @PathParam("extension") String ext, @PathParam("ocID") String ocID) {
		String uuid = Utilities.generateID();
		Transaction txn = datastore.beginTransaction();
		LOG.info("Uploading image");
		try {
			dbIntegration.putFile(uuid, file , ext);
			Entity occurrency = new Entity("OccurrencyImage", uuid);
			occurrency.setUnindexedProperty("extension", ext);
			datastore.put(txn, occurrency);
			txn.commit();
			LOG.info("Uploaded image with id "+uuid);
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
		return Response.ok(g.toJson(uuid)).build();
	}
	
	@POST
	@Path("getImage/{extension}/{imageID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFileDropbox(SessionInfo session, @PathParam("imageID") String imageID, @PathParam("extension") String ext){
		Response r = checkIsLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		byte[] file;
		try {
			file = dbIntegration.getFile(imageID, ext);
			if(file == null)
				return Response.status(Status.NOT_FOUND).build();
			
		}catch(Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		return Response.ok(file).build();
	}
	
	@GET
	@Path("getOccurrency/{ocID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOccurrency(SessionInfo session, @PathParam("ocID") String ocurrencyID) {
		
		Response r = checkIsLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		
		Transaction txn = datastore.beginTransaction();
		Key ocKey = KeyFactory.createKey("Ocurrency", ocurrencyID);
		try {
			LOG.info("Attempt to get ocurrency: " + ocurrencyID);
			Entity ocurrency = datastore.get(ocKey);
			LOG.info("Got ocurrency");
			txn.commit();
			return Response.ok(ocurrency).build();
		}catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate ocurrency: " + ocurrencyID);
			return Response.status(Status.NOT_FOUND).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}
