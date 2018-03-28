package pt.unl.fct.di.apdc.firstwebapp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.unl.fct.di.apdc.firstwebapp.resources.LoginResource;

public class HelloAppEngine extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException, ServletException {

	  RequestDispatcher r = request.getRequestDispatcher("HtmlPages/loginPage.html");
	  r.forward(request, response);
	  
	  /*writer.println("<html>\n" + 
	  		"<head>\n" + 
	  		"<meta charset=\"UTF-8\">\n" + 
	  		"<title>Login</title>\n" + 
	  		"</head>\n" + 
	  		"<body>\n" + 
	  		"<div class=\"login-page\">\n" + 
	  		"    <form class=\"login-form\" method = \"post\" >\n" + 
	  		"      <input type=\"text\" placeholder=\"username\" name = \"username\"/>\n" + 
	  		"      <input type=\"password\" placeholder=\"password\" name = \"password\"/>\n" + 
	  		"      <button>login</button>\n" + 
	  		"      <p class=\"message\">Not registered? <a href=\"#\">Create an account</a></p>\n" + 
	  		"    </form>\n" + 
	  		"  </div>\n" + 
	  		"</body>\n" + 
	  		"</html>");*/
  }
  
 @Override
 public void doPost(HttpServletRequest request,
          HttpServletResponse response) throws ServletException, IOException {
	 
	 String username = request.getParameter("username");
	 String password = request.getParameter("password");
	 
	 if(username.length() <= 4) {
		 usernameTooShort(request, response);
		 return;
	 }
		 if(password.length() <= 6) {
		 passwordTooShort(request, response);
		 return;
	}
	 //response.encodeRedirectURL()
		 
  }

private void passwordTooShort(HttpServletRequest request, HttpServletResponse response) throws IOException {
	  response.setContentType("text/html");
	  PrintWriter writer = response.getWriter();
	  
	  writer.println("<html>\n" + 
	  		"<head>\n" + 
	  		"<meta charset=\"UTF-8\">\n" + 
	  		"<title>Login</title>\n" + 
	  		"</head>\n" + 
	  		"<body>\n" + 
	  		"<tr> "
	  		+ "<td> Password too Short, it needs to be at least 6 characters </td>"+
	  		"</tr>" +
	  		"<div class=\"login-page\">\n" + 
	  		"    <form class=\"login-form\" method = \"post\" >\n" + 
	  		"      <input type=\"text\" placeholder=\"username\" name = \"username\"/>\n" + 
	  		"      <input type=\"password\" placeholder=\"password\" name = \"password\"/>\n" + 
	  		"      <button>login</button>\n" + 
	  		"      <p class=\"message\">Not registered? <a href=\"#\">Create an account</a></p>\n" + 
	  		"    </form>\n" + 
	  		"  </div>\n" + 
	  		"</body>\n" + 
	  		"</html>");
	
}

private void usernameTooShort(HttpServletRequest request, HttpServletResponse response) throws IOException {
	 response.setContentType("text/html");
	  PrintWriter writer = response.getWriter();
	  
	  writer.println("<html>\n" + 
	  		"<head>\n" + 
	  		"<meta charset=\"UTF-8\">\n" + 
	  		"<title>Login</title>\n" + 
	  		"</head>\n" + 
	  		"<body>\n" + 
	  		"<tr> "
	  		+ "<td> Username too Short, it needs to be at least 4 characters </td>"+
	  		"</tr>" +
	  		"<div class=\"login-page\">\n" + 
	  		"    <form class=\"login-form\" method = \"post\" >\n" + 
	  		"      <input type=\"text\" placeholder=\"username\" name = \"username\"/>\n" + 
	  		"      <input type=\"password\" placeholder=\"password\" name = \"password\"/>\n" + 
	  		"      <button>login</button>\n" + 
	  		"      <p class=\"message\">Not registered? <a href=\"#\">Create an account</a></p>\n" + 
	  		"    </form>\n" + 
	  		"  </div>\n" + 
	  		"</body>\n" + 
	  		"</html>");
}
}