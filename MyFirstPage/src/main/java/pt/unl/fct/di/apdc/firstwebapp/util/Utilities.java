package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.UUID;

public class Utilities {

	public static String generateID() {
		return UUID.randomUUID().toString();
	}

}