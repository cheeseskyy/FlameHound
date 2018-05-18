import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link} from "react-router-dom";
import {OccurrenceTips} from "./TipsPanels";

export class RegisterForm extends Component {
    register(){
        let userInfo = {"name": document.getElementById("name").value,
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
        let JsonUserInfo = JSON.stringify(userInfo);
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/register/v3", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send(JsonUserInfo);
        xhttp.onreadystatechange = function() {
            if(xhttp.readyState == 4 && xhttp.status == 200){
                alert("Successful registration.");
                window.location.replace("/login");
            }
            if(xhttp.status == 400){
                alert("Invalid information, please fill the mandatory areas or username already exist.");
                window.location.replace("/register");
            }
        };
    }

    render() {
        return (
            <Router>
                <div className="form">
                    <br/>
                    <label htmlFor="name"><b>Name</b></label><br/>
                    <input type="text" placeholder="Enter Name" id="name" required></input>
                    <br></br>

                    <label htmlFor="username"><b>Username</b></label><br/>
                    <input type="text" placeholder="Enter Username" id="username" required></input>
                    <br></br>

                    <label htmlFor="email"><b>Email</b></label><br/>
                    <input type="text" placeholder="Enter Email" id="email" required></input>
                    <br></br>

                    <label htmlFor="role"><b>Role</b></label><br/>
                    <select id="role" defaultValue="USER">
                        <option value="USER">User</option>
                        <option value="GBO">Gestor BackOffice</option>
                        <option value="GS">Gestor Sistema</option>
                        <option value="OPE">Operacional BackEnd</option>
                    </select>
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

                    <label
                        htmlFor="psw"><b>Password</b></label><br/>
                    <input type="password"
                           placeholder="Enter Password"
                           id="psw" required></input>
                    <br></br>

                    <label
                        htmlFor="psw-repeat"><b>Repeat Password</b></label> <br/>
                    <input type="password"
                           placeholder="Repeat Password"
                           id="psw-repeat"
                           required></input>
                    <br></br>

                    <button type="button" onClick={this.register}>Submit</button>
                    <br></br>
                    <button onClick={this.props.history.goBack}>Back</button>
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
        if (document.getElementById('rememberMe').checked == true) {
            localStorage.setItem('rememberUsername', usernameV);
        }
        var loginInfo = {"username": usernameV, "password": passwordV};
        var jSonLoginInfo = JSON.stringify(loginInfo);
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/login/v2", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        xhttp.send(jSonLoginInfo);
        xhttp.onreadystatechange = function () {

            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("200");
                alert("Successful login. Redirecting to home page...");
                var sessionInfo = JSON.parse(xhttp.response);
                sessionStorage.setItem('sessionUsername', sessionInfo.username);
                sessionStorage.setItem('sessionToken', sessionInfo.tokenId);
                window.location.replace("/map");
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
                    <label htmlFor="uname"><b>Username</b></label><br/>

                    <input type="text" placeholder="Enter Username" name="uname" id="un" required></input><br/>

                    <label htmlFor="psw"><b>Password</b></label><br/>

                    <input type="password" placeholder="Enter Password" name="psw" id="pw" required></input><br/>

                    <button type="submit" onClick={this.login}>Login</button>

                    <label> <input type="checkbox" id="rememberMe" name="remember"></input>Remember me</label><br/>
                    <button onClick={this.props.history.goBack}>Back</button>

                    <p id="errorMessage"/>
                </div>
            </Router>
        );
    }
}


function login() {
    console.log("Function login called");
    /*var usernameV = document.getElementById("un").value;
    var passwordV = document.getElementById("pw").value;
    if(document.getElementById('rememberMe').checked == true){
        localStorage.setItem('rememberUsername', usernameV);
    }
    var loginInfo = {"username":usernameV , "password":passwordV};
    var jSonLoginInfo = JSON.stringify(loginInfo);
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/login/v2", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(jSonLoginInfo);
    xhttp.onreadystatechange = function() {

        if(xhttp.readyState == 4 && xhttp.status == 200){
            console.log("200");
            alert("Successful login. Redirecting to home page...");
            var sessionInfo = JSON.parse(xhttp.response);
            sessionStorage.setItem('sessionUsername', sessionInfo.username);
            sessionStorage.setItem('sessionToken', sessionInfo.tokenId);
            window.location.replace("/homePage");
        }
        if(xhttp.readyState == 4 && xhttp.status == 403){
            alert("Password or username incorrect");
        }
    };*/
}

export class OccurrenceForm extends Component{

    tipsPanel = <div className="Column">
        ighoijhuolkjoikl
    </div>;



    render() {

        return (
            <Router>
                <div className="ocurrenceform">
                    <div id="xpto" className="Column">
                        <h3>Please fill in the form below to submit an occurrence</h3><br/>

                        <label htmlFor="title"><b>Title</b></label><br/>
                        <input type="text" size="40" placeholder="Enter a title" id="title3" required></input><br/>
                        <br/>

                        <label htmlFor="description"><b>Description</b></label><br/>
                        <input type="text"  size="40" placeholder="Describe the occurrence"  id="description"
                               required></input><br/>
                        <br/>

                        <label htmlFor="location"><b>Location</b></label><br/>
                        <input type="text" size="40" placeholder="Enter the location" id="location"
                               required></input><br/>
                        <br/>

                        <label htmlFor="type"><b>Type</b></label><br/>
                        <input type="text" size="40"  placeholder="Enter the type" id="type" required></input><br/>
                        <br/><br/>

                        <input type="file" placeholder="Submit an image" id="image" required></input><br/>
                        <br/><br/>

                        <button type="button" onClick="saveOc()">Submit</button>

                        <button onClick={this.props.history.goBack}>Back</button>
                    </div>
                    <OccurrenceTips/>
                </div>
            </Router>

        );
    }

}

