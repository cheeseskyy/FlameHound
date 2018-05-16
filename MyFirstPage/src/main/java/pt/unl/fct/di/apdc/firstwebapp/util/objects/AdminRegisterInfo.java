package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class AdminRegisterInfo {
	public String username;
	public String password;
	public String registerUsername;
	public String registerToken;
	
	public AdminRegisterInfo() {

	}

	public AdminRegisterInfo(String username, String password, String registerUsername, String registerToken) {
		this.username = username;
		this.password = password;
		this.registerToken = registerToken;
		this.registerUsername = registerUsername;
	}
}