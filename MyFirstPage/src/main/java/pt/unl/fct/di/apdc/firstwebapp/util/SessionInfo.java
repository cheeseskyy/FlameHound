package pt.unl.fct.di.apdc.firstwebapp.util;

public class SessionInfo {
	
	public String username;
	public String tokenId;
	
	public SessionInfo() {
	}
	
	public SessionInfo(String username, String tokenId) {
		this.username = username;
		this.tokenId = tokenId;
	}
}
