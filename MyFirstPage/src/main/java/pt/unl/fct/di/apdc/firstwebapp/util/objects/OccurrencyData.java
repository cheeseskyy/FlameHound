package pt.unl.fct.di.apdc.firstwebapp.util.objects;
import java.util.List;

import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyFlags;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyTypes;

public class OccurrencyData {

	public String user;
	public String location;
	public OccurrencyTypes type;
	public List<String> mediaURI;
	public String title;
	public String description;
	public OccurrencyFlags flag;
	
	
	public OccurrencyData() {
		
	}


	public OccurrencyData(String title, String description, String user, String location, String type, List<String> mediaURI) {
		this.title = title;
		this.description = description;
		this.user = user;
		this.location = location;
		this.type = selectType(type);
		this.mediaURI = mediaURI;
		this.flag = OccurrencyFlags.unconfirmed;
	}
	
	public OccurrencyData(String title, String description, String user, String location, String type, List<String> mediaURI, String flag) {
		this.title = title;
		this.description = description;
		this.user = user;
		this.location = location;
		this.type = selectType(type);
		this.mediaURI = mediaURI;
		this.flag = OccurrencyFlags.valueOf(flag);
	}


	private OccurrencyTypes selectType(String type) {
		return OccurrencyTypes.valueOf(type);
	}


	public String getUser() {
		return user;
	}


	public String getLocation() {
		return location;
	}


	public OccurrencyTypes getType() {
		return type;
	}


	public List<String> getMediaURI() {
		return mediaURI;
	}


	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	
	
	
	
}