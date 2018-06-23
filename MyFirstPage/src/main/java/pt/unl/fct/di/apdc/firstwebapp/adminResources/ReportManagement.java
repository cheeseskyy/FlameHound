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

public class ReportManagement {
	
	
	public static Response deleteReport(DatastoreService datastore, String repID, Logger LOG) {
		Transaction txn = datastore.beginTransaction();
		LOG.info("Deleting report with id: " + repID);
		Key repKey = KeyFactory.createKey("Report", repID);
		datastore.delete(repKey);
		txn.commit();
		LOG.info("Report Deleted");
		return Response.ok().build();
	}
}