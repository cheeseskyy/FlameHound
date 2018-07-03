package pt.unl.fct.di.apdc.firstwebapp.util.objects;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;

public class ReportData {
	
	public String reportId;
	public String reportedInfo;
	public String reporterInfo;
	public String description;
	public String ocID;
	
	public ReportData() {
	}
	
	public ReportData(String reportId, String reporterInfo, String reportedInfo, String ocID, String description) {
		this.reportId = reportId;
		this.reportedInfo = reportedInfo;
		this.reporterInfo = reporterInfo;
		this.description = description;
		this.ocID = ocID;
	}
	
}
