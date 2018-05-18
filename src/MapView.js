import React, { Component } from 'react';
// import the Google Maps API Wrapper from google-maps-react
import { GoogleApiWrapper } from 'google-maps-react'
// import child component
import MapContainer from './MapContainer';
import Occurrences from "./OccurrenceList";
import "./MapView.css";

class MapView extends Component{

    isLoggedIn(){
        console.log("checking for login");
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "rest/utils/validLogin", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        let username = sessionStorage.getItem('sessionUsername');
        let token = sessionStorage.getItem('sessionToken');
        console.log("username: " + username + "token: "+ token);
        let jsonObj = JSON.stringify({
            'username': username,
            'tokenId': token
        });
        xhttp.send(jsonObj);
        xhttp.onreadystatechange = function() {
            if (xhttp.readyState == 4) {
                if (xhttp.status == 200) {
                    console.log("user is logged in");
                } else if (xhttp.status == 403 || xhttp.status == 500) {
                    alert("You're not logged in, please do so.");
                    window.location.replace("/");
                }
            }

        };
        if(xhttp.status == 500){
            alert("You're not logged in, please do so.");
            window.location.replace("/");
        }
    }

    consolelog(){
        console.log("this");
    }

    render() {
        const style = { // MUST specify dimensions of the Google map or it will not work. Also works best when style is specified inside the render function and created as an object
            width: '50vw', // 90vw basically means take up 90% of the width screen. px also works.
            height: '100vh', // 75vh similarly will take up roughly 75% of the height of the screen. px also works.
            zIndex: '1'
        }

        this.isLoggedIn();

        return (
            <div>
                <div style={style} className="Column">
                    <MapContainer google={this.props.google}/>
                </div>
                <Occurrences/>
            </div>
        );
    }
}

export default GoogleApiWrapper({
    apiKey: 'AIzaSyBMlXmYjzyVeTm2IpQ6xJY-l2n11u634r4',
})(MapView)