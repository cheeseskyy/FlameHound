package pt.unl.fct.di.apdc.firstwebapp.util.objects;
import java.util.List;

import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyFlags;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyTypes;

public class CommentData {

	public String username;
	public String comment;
	public String replyingTo;
	public String postDate;
	public long upvotes;
	public long downvotes;
	public String id;
	
	public CommentData() {
		
	}

	public CommentData(String id, String username, String comment, String replyingTo, String postDate, long upvotes, long downvotes) {
		this.id = id;
		this.username = username;
		this.comment = comment;
		this.replyingTo = replyingTo;
		this.postDate = postDate;
		this.upvotes = upvotes;
		this.downvotes = downvotes;
	}
	
	
}