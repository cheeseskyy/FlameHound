package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.dropbox.core.DbxException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;
import com.sun.research.ws.wadl.Application;

import pt.unl.fct.di.apdc.firstwebapp.util.Location;
import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyTypes;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserInfo;

@Path("/session")
public class MapResource extends HttpServlet{
	
	private static final Response FORBIDDEN = Response.status(Status.FORBIDDEN).build();
	private static final Response INTERNALSE = Response.status(Status.INTERNAL_SERVER_ERROR).build();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final Logger LOG = Logger.getLogger(MapResource.class.getName());
	private final Gson g = new Gson();
	private static DropBoxResource dbIntegration = new DropBoxResource();	
	public MapResource() {}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		RequestDispatcher r = request.getRequestDispatcher("pages/homePage.html");
		r.forward(request, response);
	}

	@POST
	@Path("/getAddress")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAddress(SessionInfo session) {
		
		Entity user;
		Response r = isLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		user = (Entity) r.getEntity();
		
		return Response.ok(g.toJson(user.getProperty("address"))).build();
		
	}

	
	
	@POST
	@Path("/getUserInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserInfo(SessionInfo session) {
		
		Entity userE;
		Response r = isLoggedIn(session);
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
	
	@POST
	@Path("/geocodeAddress")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response geocodeAddress(SessionInfo session) {
		
		Response r = isLoggedIn(session);
		if(r.getStatus() != Response.Status.OK.getStatusCode())
			return r;
		return Response.ok().build();
		
			
	}
	
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(String username) throws EntityNotFoundException {
		Transaction txn = datastore.beginTransaction();
		Key userKey = KeyFactory.createKey("User", username);
		try {
		Entity user = datastore.get(userKey);
		user.setProperty("TokenKey","");
		user.setProperty("TokeCreationDate", "");
		user.setProperty("TokenExpirationDate","");
		
		datastore.put(txn,user);
		txn.commit();
		return Response.ok().build();
		} catch (EntityNotFoundException e) {
			// Username does not exist
			LOG.warning("Failed logout attempt for username: " + username);
			return Response.status(Status.FORBIDDEN).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}	
	}
	
	private Response isLoggedIn(SessionInfo session) {
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
			txn = datastore.beginTransaction();
			Key timeoutKey = KeyFactory.createKey("timeout", session.username);
			Entity timeout = datastore.get(timeoutKey);
			long lastOp = (long) timeout.getProperty("lastOp");
			if(System.currentTimeMillis() - lastOp > 10*60*1000)
				return Response.status(Status.FORBIDDEN).build();
			timeout.setProperty("lastOp", System.currentTimeMillis());
			datastore.put(timeout);
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
	
	
	
}
