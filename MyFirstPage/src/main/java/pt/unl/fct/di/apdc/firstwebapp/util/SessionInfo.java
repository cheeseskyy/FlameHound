package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;

public class SessionInfo {
	
	public String username;
	public String tokenId;
	private List<Object> arguments;
	
	public SessionInfo() {
	}
	
	public SessionInfo(String username, String tokenId) {
		this.username = username;
		this.tokenId = tokenId;
	}
	
	public void setArgument(Object object) {
		arguments.add(object);
	}

	public List<Object> getArgs() {
		return arguments;
	}
}
