package pt.unl.fct.di.apdc.firstwebapp.util;

public class RegisterData {

	public String name;
	public String username;
	public String email;
	public String role;
	public String homeNumber;
	public String phoneNumber;
	public String address;
	public String nif;
	public String cc;
	public String password;
	public String confirmation;

	public RegisterData() {

	}

	public RegisterData(String name, String username, String email, String role, String homeNumber, String phoneNumber,
			String address, String nif, String cc, String password, String confirmation) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.role = role;
		this.homeNumber = homeNumber;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.nif = nif;
		this.cc = cc;
		this.password = password;
		this.confirmation = confirmation;
	}

	private boolean nonEmptyField(String field) {
		if(field.isEmpty()) {
			System.out.println("empty: " + field);
		}
		return !field.isEmpty();
	}

	public boolean validRegistration() {
		return nonEmptyField(name) && nonEmptyField(username) && nonEmptyField(email) && nonEmptyField(address)
				&& nonEmptyField(password) && nonEmptyField(confirmation) && password.equals(confirmation);
	}
}
