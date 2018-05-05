package pt.unl.fct.di.apdc.firstwebapp.util;

public class Coordinates extends GeoLocation{

	private long x;
	private long y;
	
	
	public Coordinates() {
	}
	
	public Coordinates(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}
	
	public String coordToString() {
		return "(" + x + "," + y + ")";
	}
	
}