package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.google.apphosting.datastore.DatastoreV4.PropertyFilter;

import org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.MessageData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.RegisterData;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource extends HttpServlet{

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final String DEFAULT_MESSAGE =
			"Obrigado por se registar na nossa aplicação!\n\n"
			+ "No entanto falta apenas um passo rápido para terminar o seu registo e poder aceder a todas as funcionalidades da nossa aplicação\n"
			+ "Carregue no seguinte link para confirmar a sua conta:\n"
			+ "%s \n"
			+ "Cumprimentos,\n"
			+ "FlameHound Team";
	private final Gson g = new Gson();
	
	public RegisterResource() { } //Nothing to be done here...

	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		RequestDispatcher r = request.getRequestDispatcher("pages/register.html");
		r.forward(request, response);
	}
	
	@POST
	@Path("/v3")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response registerUserV3(RegisterData data) {
		LOG.info("Attempt to register user: " + data.username);
		String valid = data.validRegistration();
		if(!valid.equals("ok")) {
			return Response.status(Status.BAD_REQUEST).entity(valid).build();
		}
		
		Transaction txn = datastore.beginTransaction();
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("User", data.username);
			Entity user;
			Filter propertyFilter =
				    new FilterPredicate("email", FilterOperator.EQUAL, data.email);
			Query q = new Query("User").setFilter(propertyFilter);
			PreparedQuery pQ = datastore.prepare(q);
			Iterator<Entity> it = pQ.asIterator();
			if(!it.hasNext())
				throw new EntityNotFoundException(userKey);
			user = datastore.get(userKey);
			txn.rollback();
			return Response.status(Status.UNAUTHORIZED).build(); 
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("User", data.username);
			user.setProperty("user_name", data.name);
			user.setProperty("email", data.email);
			user.setProperty("role", data.role);
			user.setProperty("homeNumber", data.homeNumber);
			user.setProperty("phoneNumber", data.phoneNumber);
			user.setProperty("address", data.address);
			user.setProperty("nif", data.nif);
			user.setProperty("cc", data.cc);
			user.setProperty("user_pwd", DigestUtils.sha512Hex(data.password));
			user.setProperty("TokenKey", "");
			user.setProperty("TokenCreationDate", "");
			user.setProperty("TokenExpirationDate", "");
			user.setUnindexedProperty("user_creation_time", new Date());
			datastore.put(txn,user);
			LOG.info("User registered " + data.username);
			txn.commit();
			Key userStatsKey = KeyFactory.createKey("userAppStats", data.username);
			Transaction txn2 = datastore.beginTransaction();
			Entity userStatsE = new Entity(userStatsKey);
			userStatsE.setProperty("upvotes", 0);
			userStatsE.setProperty("downvotes", 0);
			userStatsE.setProperty("occurrenciesPosted", 0);
			userStatsE.setProperty("occurrenciesConfirmed", 0);
			datastore.put(txn2, userStatsE);
			txn2.commit();
			
			sendConfirmationEmail(data);
			return Response.ok().entity(g.toJson(new MessageData("Success"))).build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	public void sendConfirmationEmail(RegisterData data){
	    Properties prop = new Properties();
	    Session session = Session.getDefaultInstance(prop,null);
	    try{    
	        Message msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress("noreply@my-first-project-196314.appspotmail.com", "FlameHound Team"));	        LOG.info("1");
	        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(data.email, "Sr./Sra. "+data.name));
	        msg.setSubject("Account Confirmation");
	        msg.setText(String.format(DEFAULT_MESSAGE, Utilities.generateID()));
	        Transport.send(msg);
	        LOG.info("Email sent");
	    } catch (AddressException e) {
	        LOG.info("Exception 1");
	    } catch (MessagingException e) {
	    	 LOG.info("Exception 2");
	    	 LOG.warning(e.getMessage());
	    } catch (UnsupportedEncodingException e) {
	    	 LOG.info("Exception 3");
	    }
	}
	


}