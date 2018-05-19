import React, {Component} from 'react';
// import the Google Maps API Wrapper from google-maps-react
import {GoogleApiWrapper} from 'google-maps-react'
// import child component
import MapContainer from './MapContainer';
import Occurrences from "./OccurrenceList";
import "./MapView.css";

function getOcImage(id) {
    //var id = document.getElementById("imageID").value;
    alert(id);
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/getImage/gif/" + id, true);
    xhttp.setRequestHeader("Content-type", "application/json");
    var username = sessionStorage.getItem('sessionUsername');
    var token = sessionStorage.getItem('sessionToken');
    var jSonObj = JSON.stringify({"username": username, "tokenId": token});
    xhttp.responseType = "arraybuffer";
    xhttp.send(jSonObj);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var arrayBufferView = new Uint8Array(xhttp.response);
            var blob = new Blob([arrayBufferView], {type: "image/gif"});
            var urlCreator = window.URL || window.webkitURL;
            var imageUrl = urlCreator.createObjectURL(blob);
            var img = document.getElementById("ItemPreview");
            img.src = imageUrl;
        }
    };
}

function getOc() {
    console.log("Getting OC");
    var xhttp = new XMLHttpRequest();

    xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/getOccurrency/all", true);
    xhttp.setRequestHeader("Content-type", "application/json");

    var username = sessionStorage.getItem('sessionUsername');
    var token = sessionStorage.getItem('sessionToken');
    var jSonObj = JSON.stringify({"username": username, "tokenId": token});

    xhttp.send(jSonObj);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var response = JSON.parse(xhttp.response);
            return response;
        }
    };
}

class MapView extends Component {

    constructor(props){
        super(props);
        this.state = {
            markers: [

            ]
        }
    }

    getOc() {
        console.log("Getting OC");
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/getOccurrency/all", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        var response;
        var func = this.setState;
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("Got Occurrence");
                response = JSON.parse(xhttp.response);
                func({markers: response});
                console.log(response);
                return response;
            }
        };
        this.setState({markers: response});
        this.render();
        console.log("response: " + response);
    }


    changeState(){

    }


    isLoggedIn() {
        console.log("checking for login");
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "rest/utils/validLogin", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        let username = sessionStorage.getItem('sessionUsername');
        let token = sessionStorage.getItem('sessionToken');
        console.log("username: " + username + "token: " + token);
        let jsonObj = JSON.stringify({
            'username': username,
            'tokenId': token
        });
        xhttp.send(jsonObj);
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState == 4) {
                if (xhttp.status == 200) {
                    console.log("user is logged in");
                } else if (xhttp.status == 403 || xhttp.status == 500) {
                    alert("You're not logged in, please do so.");
                    window.location.replace("/");
                }
            }

        };
        if (xhttp.status == 500) {
            alert("You're not logged in, please do so.");
            window.location.replace("/");
        }
    }

    consolelog() {
        console.log("this");
    }

    componentDidMount(){
        this.getOc();
        console.log("state markers:" + this.state.markers);
    }

    render() {
        const style = { // MUST specify dimensions of the Google map or it will not work. Also works best when style is specified inside the render function and created as an object
            width: '50vw', // 90vw basically means take up 90% of the width screen. px also works.
            height: '100vh', // 75vh similarly will take up roughly 75% of the height of the screen. px also works.
            zIndex: '1'
        };

        this.isLoggedIn();

        const response = this.getOc();
        while(!response){
            console.log("waiting for " + response);
        }

        return (

            <div>
                <div style={style} className="Column">
                    <MapContainer google={this.props.google} markers={this.state.markers} />
                </div>
                <Occurrences/>
            </div>
        );
    }
}

export default GoogleApiWrapper({
    apiKey: 'AIzaSyBMlXmYjzyVeTm2IpQ6xJY-l2n11u634r4',
})(MapView)