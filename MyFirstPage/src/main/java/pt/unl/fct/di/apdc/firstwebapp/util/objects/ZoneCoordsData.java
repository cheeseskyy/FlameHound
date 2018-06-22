package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class ZoneCoordsData {

	public long topX;
	public long topY;
	public long botX;
	public long botY;
	
	public String username;
	public String tokenId;
	
	public ZoneCoordsData() {
		
	}

	public ZoneCoordsData(String username, String tokenId, long topX, long topY, long botX, long botY) {
		this.topX = topX;
		this.topY = topY;
		this.botX = botX;
		this.botY = botY;
		this.username = username;
		this.tokenId = tokenId;
	}
}