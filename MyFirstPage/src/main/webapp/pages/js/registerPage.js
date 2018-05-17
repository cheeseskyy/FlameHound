function register(){
	var userInfo = {"name": document.getElementById("name").value, 
					"username": document.getElementById("username").value, 
					"email": document.getElementById("email").value, 
					"role": document.getElementById("role").value, 
					"homeNumber": document.getElementById("homeNumber").value, 
					"phoneNumber": document.getElementById("phoneNumber").value, 
					"address": document.getElementById("address").value, 
					"nif": document.getElementById("nif").value, 
					"cc": document.getElementById("cc").value, 
					"password": document.getElementById("psw").value,
					"confirmation": document.getElementById("psw-repeat").value};
	var JsonUserInfo = JSON.stringify(userInfo);
	var xhttp = new XMLHttpRequest();
	xhttp.open("POST", "/rest/register/v3", true);
	xhttp.setRequestHeader("Content-type", "application/json");
	xhttp.send(JsonUserInfo);
	xhttp.onreadystatechange = function() {
			if(xhttp.readyState == 4 && xhttp.status == 200){
   				alert("Successful registration.")
   	    		window.location.replace("/login")
   	   	 }
   			if(xhttp.status == 400){
   				alert("Invalid information, please fill the mandatory areas or username already exist.")
   	    		window.location.replace("/register");
   			}
   };
}