package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class WorkerRegisterInfo {
	public String username;
	public String password;
	public String registerUsername;
	public String registerToken;
	
	public WorkerRegisterInfo() {

	}

	public WorkerRegisterInfo(String username, String password, String registerUsername, String registerToken) {
		this.username = username;
		this.password = password;
		this.registerToken = registerToken;
		this.registerUsername = registerUsername;
	}
}