	function uploadOc(mediaURI, username){
		alert(mediaURI);
		var xhttp = new XMLHttpRequest();
 	    xhttp.open("POST", "/rest/occurrency/saveOccurrency", true);
 	    xhttp.setRequestHeader("Content-type", "application/json");
 	    getCodedAddress(document.getElementById("location").value, xhttp, "", username);
		
		xhttp.onreadystatechange = function() {
	   		if(xhttp.readyState == 4 && xhttp.status == 200){
		  		alert("Saved Occurrency");
			}
	   	};
	}
	
	function getCodedAddress(addr, xhttp, mediaURI, username){
		geocoder = new google.maps.Geocoder();
		geocoder.geocode({ address: addr,
			componentRestrictions: {
				country: 'PT'
			}
		}, function(results, status) {
					if(status == 'OK') {
						var coordinates = results[0].geometry.location;
						var list = ["a", "b"];
						var jSonList = JSON.stringify(list);
					    var jSonInfo = JSON.stringify({"title": document.getElementById("title").value,
					    	"description": document.getElementById("description").value,
					    	"user": username, 
					    	"location": JSON.stringify(coordinates), 
					    	"type": document.getElementById("type").value, 
					    	"mediaURI": list});
				  		xhttp.send(jSonInfo);
					}
					else {
						alert('Geocode was not successful for the following reason: '+status);
					}
			});
	}

	function saveOc(){
		var xhttp = new XMLHttpRequest();
	    xhttp.open("POST", "/rest/utils/validLogin", true);
	    xhttp.setRequestHeader("Content-type", "application/json");
	    var username = sessionStorage.getItem('sessionUsername');
	    var token = sessionStorage.getItem('sessionToken');
	    var jSonObj = JSON.stringify({"username": username, "tokenId": token});
	    xhttp.send(jSonObj);
	    xhttp.onreadystatechange = function() {
	    	if(xhttp.readyState == 4 && xhttp.status == 200){
	    		  var fileInput = document.getElementById("image");
	   			  var files = fileInput.files;
	   			  var file = files[0];
	 			  var fileUploaded = true;
	    		  if (file) {
	    			  	filesUploaded = false;
	    				var extension = file.name.split(".")[1];	
	   					var reader = new FileReader();
	   			  		var xhttp2 = new XMLHttpRequest();
	   			  		
	   			  		reader.readAsArrayBuffer(file);
    			  		reader.onloadstart = alert("Starting to read");
	    		  		xhttp2.open("POST", "/rest/occurrency/saveImage/" + extension, true);
	   			  		xhttp2.setRequestHeader("Content-type", "application/octet-stream");
	   			  		var uri;
	   			  		reader.onloadend = function(){
	   			  			alert("Sending file");
    			  			var result = reader.result;
	    		  			xhttp2.send(result);
	    		  		};
	    		  		xhttp2.onreadystatechange = function() {
	   			  			if(xhttp2.readyState == 4 && xhttp.status == 200){
	   			  				uploadOc(JSON.parse(xhttp2.response), username);
	   						}
  						};
	    		 }
	    		  else
	    			uploadOc("", username);
	    	}
	    };
	}
