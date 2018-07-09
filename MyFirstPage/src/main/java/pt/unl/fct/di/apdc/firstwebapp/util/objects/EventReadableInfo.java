package pt.unl.fct.di.apdc.firstwebapp.util.objects;

import java.util.List;

public class EventReadableInfo {
	public String username;
	public String title;
	public String description;
	public String location;
	public String date;
	public List<String> participants;
	public long upvotes;
	public long downvotes;
	
	public EventReadableInfo(String username, String title, String description, String location, String date,
			List<String> participants, long upvotes, long downvotes) {
		super();
		this.username = username;
		this.title = title;
		this.description = description;
		this.location = location;
		this.date = date;
		this.participants = participants;
		this.upvotes = upvotes;
		this.downvotes = downvotes;
	}

	public EventReadableInfo() {
	}

	public EventReadableInfo(String username, String title, String description, String location,
			String date) {
		this.username = username;
		this.title = title;
		this.description = description;
		this.location = location;
		this.date = date;
	}
	
	
}