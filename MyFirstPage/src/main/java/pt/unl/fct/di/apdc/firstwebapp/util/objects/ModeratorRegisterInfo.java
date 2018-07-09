package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class ModeratorRegisterInfo {
	public String username;
	public String password;
	public String registerUsername;
	public String tokenId;
	public String entity;
	public String email;
	public String name;
	
	public ModeratorRegisterInfo() {

	}

	public ModeratorRegisterInfo(String tokenId, String username, String password, String registerUsername, String registerToken, String entity) {
		this.tokenId = tokenId;
		this.username = username;
		this.password = password;
		this.registerUsername = registerUsername;
		this.entity = entity;
	}

	public ModeratorRegisterInfo(String username, String password, String registerUsername, String tokenId,
			String entity, String email, String name) {
		super();
		this.username = username;
		this.password = password;
		this.registerUsername = registerUsername;
		this.tokenId = tokenId;
		this.entity = entity;
		this.email = email;
		this.name = name;
	}
	
	
}