package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class AdminRegisterInfo {
	public String username;
	public String password;
	public String registerUsername;
	public String tokenId;
	
	public AdminRegisterInfo() {

	}

	public AdminRegisterInfo(String tokenId, String username, String password, String registerUsername, String registerToken) {
		this.tokenId = tokenId;
		this.username = username;
		this.password = password;
		this.registerUsername = registerUsername;
	}
}