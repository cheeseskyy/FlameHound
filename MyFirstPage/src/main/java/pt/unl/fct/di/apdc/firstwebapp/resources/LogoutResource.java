package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutResource extends HttpServlet{
	
	public LogoutResource() {
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException{
		
		
		RequestDispatcher r = request.getRequestDispatcher("pages/login.html");
		r.forward(request, response);
	}
}
