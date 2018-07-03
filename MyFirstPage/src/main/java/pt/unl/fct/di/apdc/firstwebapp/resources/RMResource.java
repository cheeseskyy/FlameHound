package pt.unl.fct.di.apdc.firstwebapp.resources;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.adminResources.ReportManagement;
import pt.unl.fct.di.apdc.firstwebapp.util.Utilities;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.ReportData;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;

@Path("/rM")
	public class RMResource {
		
		private static final Logger LOG = Logger.getLogger(BackEndResource.class.getName());
		private final Gson g = new Gson();
		private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		public Utilities ut = new Utilities();
		private static final long TTL = 5*1000*60; //5 mins
		Map<String, ReportData> listCache = new ConcurrentHashMap<String, ReportData>();
		private long lastUpdate = 0;
		
		
		@Path("/getReport/{repId}")
		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response getReports(@PathParam("repID") String repID, SessionInfo session) {
			Response r = ut.validAdminLogin(new SessionInfo(session.username, session.tokenId));
			if(r.getStatus() != 200)
				return Response.status(Status.FORBIDDEN).build(); 
			
			if(System.currentTimeMillis() - TTL > lastUpdate)
				updateCache();
			
			if(repID.equals("all"))
				return Response.ok(g.toJson(listCache.values())).build();
			
			try {
				ReportData rep = listCache.get(repID);
				if(rep == null)
					throw new EntityNotFoundException(null);
				return Response.ok(g.toJson(rep)).build();
			}catch (EntityNotFoundException e) {
				LOG.warning("Failed to locate report: " + repID);
				return Response.status(Status.NOT_FOUND).build();
			}
		}
		
		
		@Path("/getReportByReported/{reportedUsername}")
		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response getReportsByReported(@PathParam("reportedUsername") String reportedUsername, SessionInfo session) {
			Response r = ut.validAdminLogin(new SessionInfo(session.username, session.tokenId));
			if(r.getStatus() != 200)
				return Response.status(Status.FORBIDDEN).build(); 
			
			if(reportedUsername == null)
				return Response.status(Status.BAD_REQUEST).build();
			
			if(System.currentTimeMillis() - TTL > lastUpdate)
				updateCache();
			
			List<ReportData> list = new ArrayList<ReportData>(listCache.values());
			for(int i = 0; i < list.size();) {
				if(!list.get(i).reportedInfo.equals(reportedUsername))
					list.remove(i);
				else
					i++;
			}
			return Response.ok().entity(g.toJson(list)).build();
		}
		
		@Path("/getReportByReporter/{reporterUsername}")
		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response getReportsByReporter(@PathParam("reporterUsername") String reporterUsername, SessionInfo session) {
			Response r = ut.validAdminLogin(new SessionInfo(session.username, session.tokenId));
			if(r.getStatus() != 200)
				return Response.status(Status.FORBIDDEN).build(); 
			
			if(reporterUsername == null)
				return Response.status(Status.BAD_REQUEST).build();
			
			if(System.currentTimeMillis() - TTL > lastUpdate)
				updateCache();
			
			List<ReportData> list = new ArrayList<ReportData>(listCache.values());
			for(int i = 0; i < list.size();) {
				if(!list.get(i).reporterInfo.equals(reporterUsername))
					list.remove(i);
				else
					i++;
			}
			return Response.ok().entity(g.toJson(list)).build();
		}
		
		@Path("/getReportByOccurrency/{ocId}")
		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response getReportsByOccurrency(@PathParam("ocId") String ocId, SessionInfo session) {
			Response r = ut.validAdminLogin(new SessionInfo(session.username, session.tokenId));
			if(r.getStatus() != 200)
				return Response.status(Status.FORBIDDEN).build(); 
			
			if(ocId == null)
				return Response.status(Status.BAD_REQUEST).build();
			
			if(System.currentTimeMillis() - TTL > lastUpdate)
				updateCache();
			
			List<ReportData> list = new ArrayList<ReportData>(listCache.values());
			for(int i = 0; i < list.size();) {
				if(!list.get(i).ocID.equals(ocId))
					list.remove(i);
				else
					i++;
			}
			return Response.ok().entity(g.toJson(list)).build();
		}
		
		private void updateCache() {
			LOG.info("Got general request, querying all reports");
			Query q = new Query("UserReport");
			PreparedQuery pQ = datastore.prepare(q);
			Iterator<Entity> it = pQ.asIterator();
			LOG.info("Got all");
			LOG.info("Converting all to readable format");
			while(it.hasNext()) {
				Entity rep = it.next();
				listCache.put(rep.getKey().toString(), convertRepToRepData(rep));
			}
			lastUpdate = System.currentTimeMillis();
		}


		
		@SuppressWarnings("unchecked")
		private ReportData convertRepToRepData(Entity report) {
			String reportedUsername = report.getKey().getName();
			reportedUsername = reportedUsername.substring(0, reportedUsername.indexOf("_"));
			ReportData rep = new ReportData(
						(String) report.getProperty("ReporterInfo"),
						reportedUsername,
						(String) report.getProperty("ReportOc"),
						(String) report.getProperty("Description")
					);
			return rep;
		}


		@Path("/delete/{repID}")
		@DELETE
		@Consumes(MediaType.APPLICATION_JSON)
		public Response deleteReport(@PathParam("repID") String repID, SessionInfo session) {
			if(ut.validAdminLogin(session).getStatus() == 200) {
				listCache.remove(repID);
				return ReportManagement.deleteReport(datastore, repID, LOG);
			}
			else
				return Response.status(Status.FORBIDDEN).build();
		}
	}
