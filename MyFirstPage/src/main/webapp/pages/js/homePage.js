 
    function getInfo(){
    	var xhttp = new XMLHttpRequest();
    	xhttp.open("POST", "/rest/session/getUserInfo", true);
	    xhttp.setRequestHeader("Content-type", "application/json");
	    var username = sessionStorage.getItem('sessionUsername');
	    var token = sessionStorage.getItem('sessionToken');
	    var jSonObj = JSON.stringify({"username": username, "tokenId": token});
	    xhttp.send(jSonObj);
	    xhttp.onreadystatechange = function() {
	    	if(xhttp.readyState == 4 && xhttp.status == 200){
	    		var userInfo = JSON.parse(xhttp.response);
	    		var info = "Username: " + userInfo.username + "\n"
				+ "Name: " + userInfo.name + "\n"
				+ "Address: "+userInfo.address + "\n"
				+ "Email: " + userInfo.email;
	    		alert(info);
	    	}
	    };	
    }
    
    function onLoad(){
    	if(sessionStorage.getItem('sessionToken') == "0"){
    		alert("You are not logged in, please do so. Redirecting...")
    		window.location.replace("/login");
    	}
    		
    }
    	
		function logout(){
			var xhttp = new XMLHttpRequest();
		    xhttp.open("POST", "/rest/session/logout", true);
		    xhttp.setRequestHeader("Content-type", "application/json");
		    var jSonObj = sessionStorage.getItem('sessionUsername');
		    xhttp.send(jSonObj);
		    xhttp.onreadystatechange = function() {
		    	if(xhttp.readyState == 4 && xhttp.status == 200){
		    		 	alert("Successful logout.");
		    		 	sessionStorage.setItem('username', "");
		    		 	sessionStorage.setItem('tokenId', "0");
		    	    	window.location.replace("/");
		    	}
		    };
		}
		
		function geocode(){
			var xhttp = new XMLHttpRequest();
		    xhttp.open("POST", "/rest/session/geocodeAddress", true);
		    xhttp.setRequestHeader("Content-type", "application/json");
		    var username = sessionStorage.getItem('sessionUsername');
		    var token = sessionStorage.getItem('sessionToken');
		    var jSonObj = JSON.stringify({"username": username, "tokenId": token});
		    xhttp.send(jSonObj);
		    xhttp.onreadystatechange = function() {
		    	if(xhttp.readyState == 4 && xhttp.status == 200){
					codeAddress(document.getElementById("cp").value);
		    	}
		    	if(xhttp.readyState == 4 && xhttp.status == 403){
		    		 alert("User not logged in, redirecting you to login page");
		    		 window.location.replace("/login");
		    	 }
		    };
		}
		
		function testGeocoding(){
			getCodedAddress(document.getElementById("cp").value);
		}
		
		function occurrenciesByType(){
			var type = document.getElementById("ocType").value;
			alert(type);
			var xhttp = new XMLHttpRequest();
		    xhttp.open("POST", "/rest/occurrency/occurrencyByType/"+type, true);
		    xhttp.setRequestHeader("Content-type", "application/json");
		    var username = sessionStorage.getItem('sessionUsername');
		    var token = sessionStorage.getItem('sessionToken');
		    alert(token);
		    alert(username);
		    var jSonObj = JSON.stringify({"username": username, "tokenId": token});
		    xhttp.send(jSonObj);
		    xhttp.onreadystatechange = function() {
		    	if(xhttp.readyState == 4 && xhttp.status == 200){
					alert(xhttp.response);
		    	}
		    	if(xhttp.readyState == 4 && xhttp.status == 403){
		    		 alert("User not logged in, redirecting you to login page");
		    		 window.location.replace("/login");
		    	 }
		    };
		}
		
		function centerUserAddress(){
			var xhttp = new XMLHttpRequest();
		    xhttp.open("POST", "/rest/session/getAddress", true);
		    xhttp.setRequestHeader("Content-type", "application/json");
		    var username = sessionStorage.getItem('sessionUsername');
		    var token = sessionStorage.getItem('sessionToken');
		    var jSonObj = JSON.stringify({"username": username, "tokenId": token});
		    xhttp.send(jSonObj);
		    xhttp.onreadystatechange = function() {
		    	if(xhttp.readyState == 4 && xhttp.status == 200){
					codeAddress(JSON.parse(xhttp.response));
		    	}
		    	if(xhttp.readyState == 4 && xhttp.status == 403){
		    		 alert("User not logged in, redirecting you to login page");
		    		 window.location.replace("/login");
		    	 }
		    };
		}
		
		function saveOccurrency(){
			var xhttp = new XMLHttpRequest();
		    xhttp.open("POST", "/rest/session/saveOccurrency", true);
		    xhttp.setRequestHeader("Content-type", "application/json");
		    var username = sessionStorage.getItem('sessionUsername');
		    var token = sessionStorage.getItem('sessionToken');
		    var jSonObj = JSON.stringify({"username": username, "tokenId": token});
		    xhttp.send(jSonObj);
		    xhttp.onreadystatechange = function() {
		    	if(xhttp.readyState == 4 && xhttp.status == 200){
					//codeAddress(JSON.parse(xhttp.response));
					alert("Saved");
		    	}
		    	if(xhttp.readyState == 4 && xhttp.status == 403){
		    		 alert("User not logged in, redirecting you to login page");
		    		 window.location.replace("/login");
		    	 }
		    };
		}
		