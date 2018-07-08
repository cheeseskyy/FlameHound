package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.google.appengine.api.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.BackEndResource;
import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.EventReadableInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.EventRegisterInfo;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.OccurrencyReadableData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;

@Path("/event")
public class EventResource {
	
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final Logger LOG = Logger.getLogger(OccurrencyResource.class.getName());
	private final Gson g = new Gson();
	public Map<String,EventReadableInfo> listCache = new ConcurrentHashMap<String, EventReadableInfo>();
	private long lastUpdate = 0;
	private static final long TTL = 5*1000*60; //5 mins

	public EventResource() {};
	
	@POST
	@Path("/addEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNewEvent(EventRegisterInfo event) {
		Response r = ComputationResource.validLogin(new SessionInfo(event.username, event.tokenId));
		if(r.getStatus() != 200)
			return r;
		
		String id = Utilities.generateID();
		Transaction txn = datastore.beginTransaction();
		Key eventKey = KeyFactory.createKey("Event", id);
		try {
			Entity eventE = datastore.get(txn, eventKey);
			txn.rollback();
			return Response.status(Status.CONFLICT).build();
		}catch(EntityNotFoundException e) {
			Entity eventE = new Entity(eventKey);
			eventE.setUnindexedProperty("title", event.title);
			eventE.setUnindexedProperty("description", event.description);
			eventE.setProperty("user", event.username);
			eventE.setUnindexedProperty("participants", new ArrayList<String>());
			eventE.setProperty("upvotes", 0);
			eventE.setProperty("downvote", 0);
			eventE.setProperty("date", event.date);
			eventE.setProperty("location", event.location);
			datastore.put(txn, eventE);
			txn.commit();
		}
		return Response.ok().build();
	}
	
	@POST
	@Path("/getEvent/{eventId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getEvent(@PathParam("eventId") String eventId, SessionInfo session) {
		Response r = ComputationResource.validLogin(session);
		if(r.getStatus() != 200)
			return r;
		
		if(System.currentTimeMillis() - TTL > lastUpdate)
			updateCache();
		
		if(eventId == null)
			return Response.status(Status.BAD_REQUEST).build();
		
		if(eventId.equals("all"))
			return Response.ok(g.toJson(listCache.values())).build();
		
		return Response.ok(g.toJson(listCache.get(eventId))).build();
	}
	
	public void updateCache() {
		LOG.info("Got general request, querying all occurrencies");
		Query q = new Query("Event");
		PreparedQuery pQ = datastore.prepare(q);
		Iterator<Entity> it = pQ.asIterator();
		LOG.info("Got all");
		LOG.info("Converting all to readable format");
		while(it.hasNext()) {
			Entity ev = it.next();
			listCache.put(ev.getKey().getName(), convertEvToEvData(ev));
		}
		lastUpdate = System.currentTimeMillis();
	}
	
	
	@SuppressWarnings("unchecked")
	private EventReadableInfo convertEvToEvData(Entity event) {
		EventReadableInfo ev = new EventReadableInfo(
				(String)event.getProperty("user"),
				(String)event.getProperty("title"),
				(String)event.getProperty("description"),
				(String)event.getProperty("location"),
				(String)event.getProperty("date"),
				(List<String>)event.getProperty("participants"),
				(long)event.getProperty("upvotes"),
				(long)event.getProperty("downvotes"));
		return ev;
	}

}