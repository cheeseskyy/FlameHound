package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class EventRegisterInfo {
	public String username;
	public String tokenId;
	public String title;
	public String description;
	public String location;
	public String date;
	
	public EventRegisterInfo() {
	}

	public EventRegisterInfo(String username, String tokenId, String title, String description, String location,
			String date) {
		this.username = username;
		this.tokenId = tokenId;
		this.title = title;
		this.description = description;
		this.location = location;
		this.date = date;
	}
	
	
}