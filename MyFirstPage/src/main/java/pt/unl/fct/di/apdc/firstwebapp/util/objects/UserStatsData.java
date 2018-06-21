package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class UserStatsData {

	public long upvotes;
	public long downvotes;
	public long occurrenciesPosted;
	public long occurrenciesConfirmed;
	public long userRating;
	
	public UserStatsData() {
		this.upvotes = 0;
		this.downvotes = 0;
		this.occurrenciesPosted = 0;
		this.occurrenciesConfirmed = 0;
		userRating = 0;
	}
	
	public UserStatsData(long upvotes, long downvotes, long occurrenciesPosted, long occurrenciesConfirmed) {
		this.upvotes = upvotes;
		this.downvotes = downvotes;
		this.occurrenciesPosted = occurrenciesPosted;
		this.occurrenciesConfirmed = occurrenciesConfirmed;
		calculateUserRating();
	}
	
	public void calculateUserRating() {
		if(occurrenciesPosted > 0)
			userRating = occurrenciesConfirmed/occurrenciesPosted;
		else
			userRating = 0;
	}
}