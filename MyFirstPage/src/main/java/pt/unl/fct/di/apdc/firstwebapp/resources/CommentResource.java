package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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

import org.apache.taglibs.standard.lang.jstl.test.Bean1;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.BackEndResource;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.CommentData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.CommentInfoData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.MessageData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ReportInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;

@Path("/social")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class CommentResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final DateFormat fmt = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy ");
	
	
	public CommentResource() {
	}
	
	//POST
	@Path("/{ocID}/post")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postComment(@PathParam("ocID") String ocID, CommentInfoData comment) {
		String postDate = fmt.format(new Date());
		Response r = ComputationResource.validLogin(new SessionInfo(comment.user, comment.tokenId));
		if(r.getStatus() != 200)
			return r;
		Key commentKey = KeyFactory.createKey("Comment", ocID+comment.user+postDate);
		Transaction txn = datastore.beginTransaction();
		Entity commentE = new Entity(commentKey);
		commentE.setProperty("username", comment.user);
		commentE.setProperty("comment", comment.comment);
		commentE.setProperty("date", postDate);
		commentE.setProperty("occurrency", ocID);
		commentE.setProperty("replyingTo", comment.replyingTo);
		commentE.setProperty("upvotes", 0);
		commentE.setProperty("downvotes", 0);
		datastore.put(txn,commentE);
		txn.commit();
		return Response.ok(new MessageData("OK")).build();
	}
	
	//GETALL
	@Path("/{ocID}/getAll")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getComments(@PathParam("ocID") String ocID, SessionInfo session) {
		Response r = ComputationResource.validLogin(new SessionInfo(session.username, session.tokenId));
		if(r.getStatus() != 200)
			return r;
		
		Query q = new Query("Comment").addFilter("occurrency", FilterOperator.EQUAL, ocID);
		
		PreparedQuery pQ = datastore.prepare(q);
		Iterator<Entity> it = pQ.asIterator();
		List<CommentData> list = new ArrayList<CommentData>();
		while(it.hasNext())
			list.add(convertCommentToCommentData(it.next()));
		return Response.ok(g.toJson(list)).build();
	}
	
	@Path("/{ocID}/getAllAndroid")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCommentsAndroid(@PathParam("ocID") String ocID, SessionInfo[] session) {
		return getComments(ocID, session[0]);
	}
	
	public CommentData convertCommentToCommentData(Entity comment){
			return new CommentData(
					(String)comment.getKey().getName(),
					(String)comment.getProperty("username"),
					(String)comment.getProperty("comment"),
					(String)comment.getProperty("replyingTo"),
					(String)comment.getProperty("date"),
					(long)comment.getProperty("upvotes"),
					(long)comment.getProperty("downvotes")
					);
	}
	
	//UPVOTE
	@Path("/upvote/{commentId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response upvoteComment(@PathParam("commentId") String commentId, SessionInfo session) {
		Transaction txn = datastore.beginTransaction();
		Key commentKey = KeyFactory.createKey("Comment", commentId);
		try {
			Entity comment = datastore.get(txn, commentKey);
			comment.setProperty("upvote", (long)comment.getProperty("upvote") + 1);
			datastore.put(txn, comment);
			txn.commit();
			return Response.ok().build();
		}catch(EntityNotFoundException e) {
			txn.rollback();
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	//DOWNVOTE
	@Path("/downvote/{commentId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response downvoteComment(@PathParam("commentId") String commentId, SessionInfo session) {
		Transaction txn = datastore.beginTransaction();
		Key commentKey = KeyFactory.createKey("Comment", commentId);
		try {
			Entity comment = datastore.get(txn, commentKey);
			comment.setProperty("downvote", (long)comment.getProperty("downvote") + 1);
			datastore.put(txn, comment);
			txn.commit();
			return Response.ok().build();
		}catch(EntityNotFoundException e) {
			txn.rollback();
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	//REPORT
	@POST
	@Path("/report/{userReported}/{commentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reportUser(@PathParam("ocId") String ocID, @PathParam("userReported") String usernameR, ReportInfo session) {
		Response r = ComputationResource.validLogin(new SessionInfo(session.username, session.tokenId));
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
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return Response.ok().build();
	}
	
	//DELETE
	@Path("/{commentID}/delete")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteComment(@PathParam("commentID") String commentID, SessionInfo session) {
		Response r = ComputationResource.validLogin(session);
		if(r.getStatus() != 200)
			return r;
		Key commentKey = KeyFactory.createKey("Comment", commentID);
		Transaction txn = datastore.beginTransaction();
		datastore.delete(txn, commentKey);
		return Response.ok().build();
	}
	
	//EDIT
	public Response editComment(@PathParam("commentID") String commentID, CommentInfoData comment) {
		String postDate = fmt.format(new Date());
		Response r = ComputationResource.validLogin(new SessionInfo(comment.user, comment.tokenId));
		if(r.getStatus() != 200)
			return r;
		Key commentKey = KeyFactory.createKey("Comment", commentID);
		Transaction txn = datastore.beginTransaction();
		try {
			Entity commentE = datastore.get(txn, commentKey);
			commentE.setProperty("comment", comment.comment);
			commentE.setProperty("date", postDate);
			commentE.setProperty("replyingTo", comment.replyingTo);
			datastore.put(txn, commentE);
			txn.commit();
			return Response.ok().build();
		}catch(EntityNotFoundException e) {
			txn.rollback();
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
}