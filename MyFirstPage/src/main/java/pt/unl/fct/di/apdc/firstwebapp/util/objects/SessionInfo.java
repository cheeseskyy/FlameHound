package pt.unl.fct.di.apdc.firstwebapp.util.objects;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;

public class SessionInfo {
	
	public String username;
	public String tokenId;
	public List<Object> arguments;
	
	public SessionInfo() {
	}
	
	public SessionInfo(String username, String tokenId) {
		this.username = username;
		this.tokenId = tokenId;
	}
	
	public SessionInfo(String username, String tokenId, List<Object> arguments) {
		this.username = username;
		this.tokenId = tokenId;
		this.arguments = arguments;
	}
	
	public void setArgument(Object object) {
		arguments.add(object);
	}

	public List<Object> getArgs() {
		return arguments;
	}
}
