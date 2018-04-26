package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.List;

public class OccurrencyData {

	public String user;
	public Coordinates location;
	public OccurrencyTypes type;
	public List<String> mediaURI;
	
	
	public OccurrencyData() {
		
	}


	public OccurrencyData(String user, Coordinates location, OccurrencyTypes type, List<String> mediaURI) {
		this.user = user;
		this.location = location;
		this.type = type;
		this.mediaURI = mediaURI;
	}


	public String getUser() {
		return user;
	}


	public Coordinates getLocation() {
		return location;
	}


	public OccurrencyTypes getType() {
		return type;
	}


	public List<String> getMediaURI() {
		return mediaURI;
	}
	
	
	
	
	
}