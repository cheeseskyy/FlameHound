import React, {Component} from 'react';
import {BrowserRouter as Router} from "react-router-dom";
import {OccurrenceTips} from "./TipsPanels";
import './Body.css';

function uploadOc(mediaURI, username) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/saveOccurrency", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    getCodedAddress(document.getElementById("location").value, xhttp, mediaURI, username);
    console.log("Sending Occurrence");
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            alert("Saved Occurrence");
        }
    };
}

function getCodedAddress(addr, xhttp, mediaURI, username) {
    var list = [];
    //list.append(mediaURI);
    var jSonInfo = JSON.stringify({
        "title": document.getElementById("title3").value,
        "description": document.getElementById("description").value,
        "user": username,
        "location": JSON.stringify(sessionStorage.getItem("selectedLocation").substring(1)),
        "type": document.getElementById("type").value,
        "mediaURI": [mediaURI]
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
            var fileInput = document.getElementById("imageFile");
            var files = fileInput.files;

            console.log(files);
            let filesUploaded = true;
            if (files.length > 0) {
                var file = files[0];
                filesUploaded = false;
                var extension = file.name.split(".")[1];
                var reader = new FileReader();
                var xhttp2 = new XMLHttpRequest();

                reader.readAsArrayBuffer(file);
                reader.onloadstart = console.log("Starting to read");
                xhttp2.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/saveImage/" + extension, true);
                xhttp2.setRequestHeader("Content-type", "application/octet-stream");
                var uri;
                reader.onloadend = function () {
                    console.log("Sending file");
                    var result = reader.result;
                    xhttp2.send(result);
                };
                xhttp2.onreadystatechange = function () {
                    if (xhttp2.readyState == 4 && xhttp.status == 200) {
                        console.log(JSON.parse(xhttp2.response));
                        uploadOc(JSON.parse(xhttp2.response), username);
                    }
                };
            }
            else
                uploadOc("", username);
        }
    };
}

export class RegisterForm extends Component {

    register(){
        let userInfo = {"name": document.getElementById("name").value,
            "username": document.getElementById("username").value,
            "email": document.getElementById("email").value,
            "role": "USER",
            "homeNumber": document.getElementById("homeNumber").value,
            "phoneNumber": document.getElementById("phoneNumber").value,
            "address": document.getElementById("address").value,
            "nif": document.getElementById("nif").value,
            "cc": document.getElementById("cc").value,
            "password": document.getElementById("psw").value,
            "confirmation": document.getElementById("psw-repeat").value};
        let JsonUserInfo = JSON.stringify(userInfo);
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/register/v3", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send(JsonUserInfo);
        xhttp.onreadystatechange = function() {
            if(xhttp.readyState == 4 && xhttp.status == 200){
                alert("Successful registration.");
                //window.location.replace("/login");
                //this.props.history.push("/login");
                this.props.resetForms();
            }
            if(xhttp.status == 400){
                alert("Invalid information, please fill the mandatory areas or username already exist.");
                //window.location.replace("/register");
                this.props.history.push("/register");
                this.props.resetForms();
            }
        };
    }

    render() {
        return (
            <Router>
                <div className="form">
                    <br/>
                    <label htmlFor="name"><b>Name*</b></label><br/>
                    <input type="text" placeholder="Enter Name" id="name" required></input>
                    <br></br>

                    <label htmlFor="username"><b>Username*</b></label><br/>
                    <input type="text" placeholder="Enter Username" id="username" required></input>
                    <br></br>

                    <label htmlFor="email"><b>Email*</b></label><br/>
                    <input type="text" placeholder="Enter Email" id="email" required></input>
                    <br></br>

                    <label
                        htmlFor="psw"><b>Password*</b></label><br/>
                    <input type="password"
                           placeholder="Enter Password"
                           id="psw" required></input>
                    <br></br>

                    <label
                        htmlFor="psw-repeat"><b>Repeat Password*</b></label> <br/>
                    <input type="password"
                           placeholder="Repeat Password"
                           id="psw-repeat"
                           required></input>
                    <br></br>

                    <label htmlFor="homeNumber"><b>Home Number</b></label><br/>
                    <input type="text" placeholder="Enter Home Number" id="homeNumber"></input>
                    <br></br>

                    <label htmlFor="phoneNumber"><b>Phone Number</b></label><br/>
                    <input type="text" placeholder="Enter Phone Number"
                           id="phoneNumber"></input><br/>

                    <label htmlFor="address"><b>Address</b></label><br/>
                    <input type="text" placeholder="Enter Andress"
                           id="address" required></input>
                    <br></br>

                    <label htmlFor="nif"><b>NIF</b></label><br/>
                    <input type="text" placeholder="Enter NIF"
                           id="nif"></input>
                    <br></br>

                    <label htmlFor="cc"><b>CC</b></label><br/>
                    <input type="text"
                           placeholder="Enter CC" id="cc"></input>
                    <br></br>



                    <button type="button" onClick={this.register}>Submit</button>
                    <br></br>
                    <button onClick={this.props.resetForms}>Back</button>
                </div>
            </Router>
        );
    }
}

export class LoginForm extends Component {

    login() {
        console.log("Function login called");
        var usernameV = document.getElementById("un").value;
        var passwordV = document.getElementById("pw").value;
        var errorText = document.getElementById("errorMessage");

        var loginInfo = {"username": usernameV, "password": passwordV};
        var jSonLoginInfo = JSON.stringify(loginInfo);
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/login/v2", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        //xhttp.setRequestHeader("Access-Control-Allow-Origin")
        xhttp.send(jSonLoginInfo);
        xhttp.onreadystatechange = () => {

            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("Login successful");
                if (document.getElementById('rememberMe').checked == true) {
                    localStorage.setItem('rememberUsername', usernameV);
                }
                var sessionInfo = JSON.parse(xhttp.response);
                sessionStorage.setItem('sessionUsername', sessionInfo.username);
                sessionStorage.setItem('sessionToken', sessionInfo.tokenId);
                {this.props.resetForms()}
                {this.props.getRole()}
                this.props.history.push("/map");
            }
            if (xhttp.readyState == 4 && xhttp.status == 403) {
                errorText.innerHTML = "Wrong Username or Password";
            }
        };
    }


    render() {

        return (
            <Router>
                <div className="loginForm">
                    {console.log(this.props.match)}
                    <label htmlFor="uname"><b>Username</b></label><br/>

                    <input type="text" placeholder="Enter Username" name="uname" id="un" defaultValue={localStorage.getItem('rememberUsername')} required></input><br/>

                    <label htmlFor="psw"><b>Password</b></label><br/>

                    <input type="password" placeholder="Enter Password" name="psw" id="pw" required></input><br/>

                    <button type="submit" onClick={() => this.login()}>Login</button>

                    <label> <input type="checkbox" id="rememberMe" name="remember"></input>Remember me</label><br/>
                    <button onClick={this.props.resetForms}>Back</button>

                    <p id="errorMessage"/>
                </div>
            </Router>
        );
    }
}

export class OccurrenceForm extends Component{

    tipsPanel = <div className="Column">

    </div>;



    render() {

        return (
            <Router>
                <div className="ocurrenceform">
                    <div id="xpto" className="Column">
                        <h3>Please fill in the form below to submit an occurrence</h3><br/>

                        <label htmlFor="title"><b>Title</b></label><br/>
                        <input  type="text" size="40" placeholder="Enter a title" id="title3" required></input><br/>
                        <br/>

                        <label htmlFor="description"><b>Description</b></label><br/>
                        <input  type="text"  size="40" placeholder="Describe the occurrence"  id="description"
                               required></input><br/>
                        <br/>

                        <label htmlFor="location"><b>Location</b></label><br/>
                        <input type="text" size="40" placeholder="Enter the location" id="location"
                               required></input><br/>
                        <br/>

                        <label htmlFor="type"><b>Type</b></label><br/>
                        <select id="type">
                            <option value="light">Ligeiro</option>
                            <option value="important">Importante</option>
                            <option value="urgent">Urgente</option>
                            <option value="severe">Severa</option>
                        </select>
                        <br/><br/>

                        <input type="file" placeholder="Submit an image" id="imageFile"></input><br/>
                        <br/><br/>

                        <button type="button" onClick={saveOc}>Submit</button>

                        <button onClick={this.props.history.goBack}>Back</button>

                    </div>
                    <OccurrenceTips/>
                </div>
            </Router>

        );
    }

}

export class AdminLogin extends Component{

    adminLogin() {
        console.log("Function adminLogin called");
        var usernameV = document.getElementById("un").value;
        var passwordV = document.getElementById("pw").value;
        var errorText = document.getElementById("errorMessage");

        var loginInfo = {"username": usernameV, "password": passwordV};
        var jSonLoginInfo = JSON.stringify(loginInfo);
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/loginAdmin", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        //xhttp.setRequestHeader("Access-Control-Allow-Origin")
        xhttp.send(jSonLoginInfo);
        xhttp.onreadystatechange = () => {

            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("Login successful");
                var sessionInfo = JSON.parse(xhttp.response);
                sessionStorage.setItem('sessionUsernameAdmin', sessionInfo.username);
                sessionStorage.setItem('sessionTokenAdmin', sessionInfo.tokenId);
                {this.props.resetForms()}
                this.props.history.push("/admin");
            }
            if (xhttp.readyState == 4 && xhttp.status == 403) {
                errorText.innerHTML = "Wrong Username or Password";
            }
        };
    }

    render(){
        return(<Router>
                <div className="loginForm">
                    {console.log(this.props.match)}
                    <label htmlFor="uname"><b>Admin Username</b></label><br/>

                    <input type="text" placeholder="Enter Username" name="uname" id="un" defaultValue={localStorage.getItem('rememberUsername')} required></input><br/>

                    <label htmlFor="psw"><b>Admin Password</b></label><br/>

                    <input type="password" placeholder="Enter Password" name="psw" id="pw" required></input><br/>

                    <button type="submit" onClick={() => this.adminLogin()}>Login</button>
                    <button onClick={this.props.resetForms}>Back</button>

                    <p id="errorMessage"/>
                </div>
            </Router>
        );
    }

}

