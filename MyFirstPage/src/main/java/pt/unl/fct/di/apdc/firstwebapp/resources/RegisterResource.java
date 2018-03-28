package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Key;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

@Path("/register")
public class RegisterResource {
	
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public RegisterResource() {}
	
	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistration(LoginData data) {
		Entity user = new Entity("User", data.username);
		user.setProperty("user_pwd", DigestUtils.shaHex(data.password));
		user.setUnindexedProperty("user_creation_time", new Date());
		datastore.put(user);
		LOG.info("User registered " + data.username);
		return Response.ok().build();
	}
	
	/*@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doRegistrationV2(RegisterData data) {
		if(!data.validRegistration()) {
			
		}
		else {
			Transaction txn = datastore.beginTransaction();
			try {
				 Key userKey = KeyFactory.createKey("User", data.username);
				 Entity user = datastore.get(userKey);
				
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}*/
	
}
