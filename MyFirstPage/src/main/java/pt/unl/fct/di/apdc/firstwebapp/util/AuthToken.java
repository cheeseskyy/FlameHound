package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.UUID;

public class AuthToken {
	
	public static final long EXPIRATION_TIME = 100;//1000*60*60*2; //2h
	
	public String username;
	public String tokenID;
	public long creationData;
	public long expirationData;
	
	public AuthToken(String username) {
		this.username = username;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
	}
	
	public AuthToken() {
		this.tokenID = UUID.randomUUID().toString();
	}

	public static long getExpirationTime() {
		return EXPIRATION_TIME;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setCreationData(long creationData) {
		this.creationData = creationData;
	}

	public void setExpirationData(long expirationData) {
		this.expirationData = expirationData;
	}
	
	
}