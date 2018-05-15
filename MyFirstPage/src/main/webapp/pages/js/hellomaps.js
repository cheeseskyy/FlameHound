var map;
var geocoder;

function codeAddress(addr) {
    geocoder.geocode({ address: addr}, function(results, status) {
        if(status == 'OK') {
            map.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({ position: results[0].geometry.location, map: map});
        }
        else {
            alert('Geocode was not successful for the following reason: '+status);
        }
    });
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
					    var jSonInfo = JSON.stringify({"user": username, "location": JSON.stringify(coordinates), "type": "light", "mediaURI": list});
				  		xhttp.send(jSonInfo);
					}
					else {
						alert('Geocode was not successful for the following reason: '+status);
					}
			});
}

function initMap() 
{
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat:  38.659784, lng:  -9.202765},
        zoom: 16
    });

    geocoder = new google.maps.Geocoder();

    var portaria = new google.maps.LatLng(38.66104,  -9.2032);

    var marker = new google.maps.Marker({
          position: portaria,
          map: map
        });

    var contentString = '<div id="content">'+
        '<h1 id="title">116-II</h1>'+
        '<p>A Sala 116-II é a sala onde decorrem as sessões de formação de APDC PEI</p>' +
        '<div id="media">'+
            '<img src="media/img/DSC_0001.JPG">'+
        '</div>';

    var infowindow = new google.maps.InfoWindow({content: contentString});

    marker.addListener('click', function() {
        infowindow.open(map, marker);
    });
}
