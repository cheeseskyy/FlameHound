import React, {Component} from 'react';
import GoogleMapReact from 'google-map-react'

function uploadOc(mediaURI, username) {
    alert(mediaURI);
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/saveOccurrency", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    getCodedAddress(document.getElementById("location").value, xhttp, "", username);

    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            alert("Saved Occurrency");
        }
    };
}

function getCodedAddress(addr, xhttp, mediaURI, username) {
    var list = ["a", "b"];
    var jSonInfo = JSON.stringify({
        "title": document.getElementById("title").value,
        "description": document.getElementById("description").value,
        "user": username,
        "location": JSON.stringify(sessionStorage.getItem("selectedLocation")),
        "type": document.getElementById("type").value,
        "mediaURI": list
    });
    xhttp.send(jSonInfo);
}

function saveOc() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/utils/validLogin", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    var username = sessionStorage.getItem('sessionUsername');
    var token = sessionStorage.getItem('sessionToken');
    var jSonObj = JSON.stringify({"username": username, "tokenId": token});
    xhttp.send(jSonObj);
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var fileInput = document.getElementById("image");
            var files = fileInput.files;
            var file = files[0];
            let filesUploaded = true;
            if (file) {
                filesUploaded = false;
                var extension = file.name.split(".")[1];
                var reader = new FileReader();
                var xhttp2 = new XMLHttpRequest();

                reader.readAsArrayBuffer(file);
                reader.onloadstart = alert("Starting to read");
                xhttp2.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/saveImage/" + extension, true);
                xhttp2.setRequestHeader("Content-type", "application/octet-stream");
                var uri;
                reader.onloadend = function () {
                    alert("Sending file");
                    var result = reader.result;
                    xhttp2.send(result);
                };
                xhttp2.onreadystatechange = function () {
                    if (xhttp2.readyState == 4 && xhttp.status == 200) {
                        uploadOc(JSON.parse(xhttp2.response), username);
                    }
                };
            }
            else
                uploadOc("", username);
        }
    };
}

class SubmitOccurrence extends Component {


    saveOc() {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/utils/validLogin", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                var fileInput = document.getElementById("image");
                var files = fileInput.files;
                var file = files[0];
                let filesUploaded = true;
                if (file) {
                    filesUploaded = false;
                    var extension = file.name.split(".")[1];
                    var reader = new FileReader();
                    var xhttp2 = new XMLHttpRequest();

                    reader.readAsArrayBuffer(file);
                    reader.onloadstart = alert("Starting to read");
                    xhttp2.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/saveImage/" + extension, true);
                    xhttp2.setRequestHeader("Content-type", "application/octet-stream");
                    var uri;
                    reader.onloadend = function () {
                        alert("Sending file");
                        var result = reader.result;
                        xhttp2.send(result);
                    };
                    xhttp2.onreadystatechange = function () {
                        if (xhttp2.readyState == 4 && xhttp.status == 200) {
                            uploadOc(JSON.parse(xhttp2.response), username);
                        }
                    };
                }
                else
                    uploadOc("", username);
            }
        };
    }

    //------------------------------------------------------------------------------------------------------------------

    render() {
        return (
            <div>
                <p>Please fill in the form below to submit an occurrence</p>

                <label for="title">
                    <b>Title</b>
                </label>
                <input type="text" placeholder="Enter a title" id="title" required/>
                <br/>

                <label for="description">
                    <b>Description</b>
                </label>
                <input type="text" placeholder="Describe the occurrence" id="description" required/>
                <br/>

                <label for="location">
                    <b>Location</b>
                </label>
                <input type="text" placeholder="Enter the location" id="location" required/>
                <br/>

                <label for="type">
                    <b>Location</b>
                </label>
                <input type="text" placeholder="Enter the type" id="type" required/>
                <br/>

                <input type="file" placeholder="Submit an image" id="image" required/>
                <br/>

                <button type="button" onClick={() => saveOc()}>Submit</button>
                <br/>
            </div>
        )
    }
}

export default SubmitOccurrence;