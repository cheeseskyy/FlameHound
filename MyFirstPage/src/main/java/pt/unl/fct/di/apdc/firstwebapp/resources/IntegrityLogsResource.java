package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.Enums.LogOperation;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.LogType;

public class IntegrityLogsResource {
	
	private static final Logger LOG = Logger.getLogger(BackEndResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public static void insertNewLog(LogOperation operation, String[] args, LogType type, String username) {
		if(type.equals(LogType.Occurrency))
			addNewOccurrencyLog(args,operation, username);
		if(type.equals(LogType.User))
			addNewUserLog(args, operation, username);
		if(type.equals(LogType.Report))
			addNewReportLog(args, operation, username);
		if(type.equals(LogType.Other))
			addNewOtherLog(args, operation, username);
	}

	private static void addNewOtherLog(String[] args, LogOperation operation, String username) {
		
	}

	private static void addNewReportLog(String[] args, LogOperation operation, String username) {

	}

	private static void addNewUserLog(String[] args, LogOperation operation, String username) {
		
	}

	private static void addNewOccurrencyLog(String[] args, LogOperation operation, String username) {
		Transaction txn = datastore.beginTransaction();
		Transaction txn2 = datastore.beginTransaction();
		String newLog = String.format("User %s did a %s operation on Occurrency %s in %s\n",
				username, operation.toString(), args[0], (new Date()).toString());
		Key adminLogs = KeyFactory.createKey("OperationLogs", "Logs");
		try {
			Entity logs = datastore.get(txn, adminLogs);
			logs.setProperty("logText", (String)logs.getProperty("logText") + newLog);
			datastore.put(txn,logs);
		}catch(EntityNotFoundException e) {
			Entity logs = new Entity(adminLogs);
			logs.setProperty("logText", newLog);
			datastore.put(txn, logs);
		}
		txn.commit();
		
		Key adminLogsHash = KeyFactory.createKey("OperationLogs", "LogsHash");
		try {
			Entity logs = datastore.get(txn2, adminLogsHash);
			logs.setProperty("logHash", DigestUtils.sha512Hex((String)logs.getProperty("logHash") + newLog));
			datastore.put(txn2, logs);
		}catch(EntityNotFoundException e) {
			Entity logs = new Entity(adminLogsHash);
			logs.setProperty("logHash", DigestUtils.sha512Hex(newLog));
			datastore.put(txn2,logs);
		}
		txn2.commit();
	}
}