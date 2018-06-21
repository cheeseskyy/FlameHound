package pt.unl.fct.di.apdc.firstwebapp.util.objects;
import java.util.List;

import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyFlags;
import pt.unl.fct.di.apdc.firstwebapp.util.Enums.OccurrencyTypes;

public class OccurrencyStatsData {

	public String user;
	public long upvotes;
	public long downvotes;
	
	public OccurrencyStatsData() {
		
	}
	
	public OccurrencyStatsData(String user) {
		this.user = user;
		upvotes = 0;
		downvotes = 0;
	}
	
	public OccurrencyStatsData(String user, long upvotes, long downvotes) {
		this.user = user;
		this.upvotes = upvotes;
		this.downvotes = downvotes;
	}
	
	
}