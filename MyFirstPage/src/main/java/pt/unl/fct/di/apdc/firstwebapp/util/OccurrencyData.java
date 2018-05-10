package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.List;

public class OccurrencyData {

	public String user;
	/*GeoLocation*/ public String location;
	/*OccurrencyType*/public String type;
	public List<String> mediaURI;
	
	
	public OccurrencyData() {
		
	}


	public OccurrencyData(String user, String location, String type/*, List<String> mediaURI*/) {
		this.user = user;
		this.location = location;
		this.type = type;
		this.mediaURI = mediaURI;
	}


	public String getUser() {
		return user;
	}


	public String getLocation() {
		return location;
	}


	public String getType() {
		return type;
	}


	public List<String> getMediaURI() {
		return mediaURI;
	}
	
	
	
	
	
}