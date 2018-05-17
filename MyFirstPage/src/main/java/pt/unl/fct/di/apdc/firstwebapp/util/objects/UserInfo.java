package pt.unl.fct.di.apdc.firstwebapp.util.objects;

import java.util.UUID;

public class UserInfo {
	public String name;
	public String username;
	public String email;
	public String homeNumber;
	public String phoneNumber;
	public String address;
	public String nif;
	public String cc;
	
	public UserInfo() {

	}

	public UserInfo(String name, String username, String email, String homeNumber, String phoneNumber,
			String address, String nif, String cc) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.homeNumber = homeNumber;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.nif = nif;
		this.cc = cc;
	}
}