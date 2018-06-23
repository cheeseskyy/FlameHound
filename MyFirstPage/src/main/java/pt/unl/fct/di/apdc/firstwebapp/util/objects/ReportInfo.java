package pt.unl.fct.di.apdc.firstwebapp.util.objects;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;

public class ReportInfo {
	
	public String username;
	public String tokenId;
	public String description;
	
	public ReportInfo() {
	}
	
	public ReportInfo(String username, String tokenId, String description) {
		this.username = username;
		this.tokenId = tokenId;
		this.description = description;
	}
	
}
