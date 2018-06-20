package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.UserInfo;

@Path("/session")
public class SessionResource extends HttpServlet{
	
	private static final Response FORBIDDEN = Response.status(Status.FORBIDDEN).build();
	private static final Response INTERNALSE = Response.status(Status.INTERNAL_SERVER_ERROR).build();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final Logger LOG = Logger.getLogger(SessionResource.class.getName());
	private final Gson g = new Gson();
	private static DropBoxResource dbIntegration = new DropBoxResource();	
	public SessionResource() {}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		RequestDispatcher r = request.getRequestDispatcher("pages/homePage.html");
		r.forward(request, response);
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
