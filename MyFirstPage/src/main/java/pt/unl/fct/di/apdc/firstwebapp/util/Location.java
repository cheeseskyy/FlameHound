package pt.unl.fct.di.apdc.firstwebapp.util;

public class Location extends GeoLocation{

	private String address;
	
	
	public Location() {
	}
	
	public Location(String address) {
		this.address = address;
	}
		
	public String getAddress() {
		return address;
	}
}