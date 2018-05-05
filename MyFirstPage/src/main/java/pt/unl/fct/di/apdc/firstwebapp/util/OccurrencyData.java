package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.List;

public class OccurrencyData {

	public String user;
	public GeoLocation location;
	public OccurrencyTypes type;
	public List<String> mediaURI;
	
	
	public OccurrencyData() {
		
	}


	public OccurrencyData(String user, GeoLocation location, OccurrencyTypes type, List<String> mediaURI) {
		this.user = user;
		this.location = location;
		this.type = type;
		this.mediaURI = mediaURI;
	}


	public String getUser() {
		return user;
	}


	public GeoLocation getLocation() {
		return location;
	}


	public OccurrencyTypes getType() {
		return type;
	}


	public List<String> getMediaURI() {
		return mediaURI;
	}
	
	
	
	
	
}