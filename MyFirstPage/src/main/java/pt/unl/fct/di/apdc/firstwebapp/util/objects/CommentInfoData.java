package pt.unl.fct.di.apdc.firstwebapp.util.objects;
import java.util.List;

import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyFlags;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyTypes;

public class CommentInfoData {

	public String user;
	public String comment;
	public String replyingTo;
	public String tokenId;
	
	
	public CommentInfoData() {
		
	}


	public CommentInfoData(String user, String comment, String replyingTo, String tokenId) {
		this.user = user;
		this.comment = comment;
		this.replyingTo = replyingTo;
		this.tokenId = tokenId;
	}
	
	
}