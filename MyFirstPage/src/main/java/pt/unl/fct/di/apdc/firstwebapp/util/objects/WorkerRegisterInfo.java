package pt.unl.fct.di.apdc.firstwebapp.util.objects;

public class WorkerRegisterInfo {
	public String username;
	public String password;
	public String registerUsername;
	public String tokenId;
	public String entity;
	public String email;
	public String name;
	
	public WorkerRegisterInfo() {

	}

	public WorkerRegisterInfo(String username, String password, String registerUsername, String tokenId,
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