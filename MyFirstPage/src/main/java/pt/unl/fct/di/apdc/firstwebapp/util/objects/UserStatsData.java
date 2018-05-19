package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class UserStatsData {

	public long upvotes;
	public long downvotes;
	public long occurrenciesPosted;
	public long occurrenciesConfirmed;
	public long userRating;
	
	public UserStatsData() {
		
	}
	
	public UserStatsData(long upvotes, long downvotes, long occurrenciesPosted, long occurrenciesConfirmed) {
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		this.occurrenciesPosted = occurrenciesPosted;
		this.occurrenciesConfirmed = occurrenciesConfirmed;
		userRating = occurrenciesConfirmed/occurrenciesPosted;
	}
	
}