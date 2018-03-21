package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {

	public String username;
	public String password;
	public String email;
	public String confirmation;

	public RegisterData() {}

	public RegisterData(String username, String password, String email, String confirmation) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.confirmation = confirmation;
	}

	public boolean validRegistration() {
		if(username.equals("") || email.equals("") || password.equals("") || confirmation.equals(""))
			return false;
		if(!confirmation.equals(password))
			return false;
		return true;
	}
}
