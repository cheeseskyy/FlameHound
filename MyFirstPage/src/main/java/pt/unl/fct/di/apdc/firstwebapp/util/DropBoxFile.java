package pt.unl.fct.di.apdc.firstwebapp.util;

public class DropBoxFile extends GeoLocation{

	private String uuid;
	private String extension;
	
	
	public DropBoxFile() {
	}
	
	public DropBoxFile(String uuid, String extension) {
		this.uuid = uuid;
		this.extension = extension;
	}
		
	public String getuuid() {
		return uuid;
	}
	
	public String getExtension() {
		return extension;
	}
}