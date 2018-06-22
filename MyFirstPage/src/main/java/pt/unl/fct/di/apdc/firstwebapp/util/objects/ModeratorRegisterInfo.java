package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class ModeratorRegisterInfo {
	public String username;
	public String password;
	public String registerUsername;
	public String registerToken;
	public String tokenId;
	public String entity;
	
	public ModeratorRegisterInfo() {

	}

	public ModeratorRegisterInfo(String tokenId, String username, String password, String registerUsername, String registerToken, String entity) {
		this.tokenId = tokenId;
		this.username = username;
		this.password = password;
		this.registerToken = registerToken;
		this.registerUsername = registerUsername;
		this.entity = entity;
	}
}