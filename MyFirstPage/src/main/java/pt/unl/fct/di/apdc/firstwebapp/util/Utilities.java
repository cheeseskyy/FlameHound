package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.taglibs.standard.lang.jstl.test.Bean1;

import pt.unl.fct.di.apdc.firstwebapp.resources.BackEndResource;
import pt.unl.fct.di.apdc.firstwebapp.util.objects.SessionInfo;

public class Utilities {

	public BackEndResource be = new BackEndResource();
	
	public static String generateID() {
		return UUID.randomUUID().toString();
	}
}