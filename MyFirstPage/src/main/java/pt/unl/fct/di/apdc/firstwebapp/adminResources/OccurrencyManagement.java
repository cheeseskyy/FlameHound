package pt.unl.fct.di.apdc.firstwebapp.adminResources;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.DatastoreService;

import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyFlags;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;

public class OccurrencyManagement {
	
	
	public static Response confirmOccurrency(DatastoreService datastore, String ocID, Logger LOG) {
		Transaction txn = datastore.beginTransaction();
		Key ocKey = KeyFactory.createKey("Occurrency", ocID);
		try {
			LOG.info("Attempt to get ocurrency: " + ocID);
			Entity occurrency = datastore.get(txn, ocKey);
			LOG.info("Got occurrency");
			occurrency.setProperty("flag", OccurrencyFlags.confirmed);
			datastore.put(txn, occurrency);
			txn.commit();
			return Response.ok().build();
		}catch(EntityNotFoundException e) {
			LOG.warning("Failed to locate ocurrency: " + ocID);
			return Response.status(Status.NOT_FOUND).build();
		}finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
	
	
	public static Response deleteOccurrency(DatastoreService datastore, String ocID, Logger LOG) {
		Transaction txn = datastore.beginTransaction();
		LOG.info("Deleting occurrency with id: " + ocID);
		Key ocKey = KeyFactory.createKey("Occurrency", ocID);
		datastore.delete(ocKey);
		txn.commit();
		LOG.info("Occurrency Deleted");
		return Response.ok().build();
	}
	
	
	public static Response updateOccurrency(SessionInfo session, DatastoreService datastore, String ocID, Logger LOG){
		Transaction txn = datastore.beginTransaction();
		Key ocKey = KeyFactory.createKey("Occurrency", ocID);
		try {
			LOG.info("Attempt to get ocurrency: " + ocID);
			Entity occurrency = datastore.get(txn, ocKey);
			LOG.info("Got occurrency");
			
			@SuppressWarnings("unchecked")
			Iterator<String> it = ((List<String>) session.getArgs().get(0)).iterator();
			while(it.hasNext()) {
				String param = it.next();
				String[] line = param.split(":");
				LOG.info("Updating parameter " + line[0].trim() + " with value " + line[1].trim());
				occurrency.setProperty(line[0].trim(), line[1].trim());
			}
			datastore.put(txn, occurrency);
			txn.commit();
			return Response.ok().build();
		}catch (EntityNotFoundException e) {
			LOG.warning("Failed to locate ocurrency: " + ocID);
			return Response.status(Status.NOT_FOUND).build();
		}finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}
}