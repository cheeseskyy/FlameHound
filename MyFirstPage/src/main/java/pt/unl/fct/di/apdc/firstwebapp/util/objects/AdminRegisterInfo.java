package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class AdminRegisterInfo {
	public String username;
	public String password;
	public String registerUsername;
	public String tokenId;
	public String email;
	public String name;
	
	public AdminRegisterInfo() {

	}

	public AdminRegisterInfo(String tokenId, String username, String password, String registerUsername, String registerToken) {
		this.tokenId = tokenId;
		this.username = username;
		this.password = password;
		this.registerUsername = registerUsername;
	}

	public AdminRegisterInfo(String username, String password, String registerUsername, String tokenId, String email,
			String name) {
		super();
		this.username = username;
		this.password = password;
		this.registerUsername = registerUsername;
		this.tokenId = tokenId;
		this.email = email;
		this.name = name;
	}
	
	
	
}