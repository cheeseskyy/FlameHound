import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link, Switch} from "react-router-dom";
import {RegisterForm, LoginForm} from "./Forms";
import logo from "./imagens/FlameHound Logo.png";

function log(text) {
    console.log(text);
}

const loginLinks = {}

class NavPanel extends Component {
    logout(){

        console.log("Logging out");

        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com//rest/session/logout", true);
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

    render() {

        return (

            <Router>
                <div id="NavPanel" className="Column">
                    <div className="logo">
                        <div id="image"><img src={logo} alt="company logo"/></div>
                    </div>
                    <nav>
                        <Switch>
                        <Route path="/register" component={RegisterForm}/>
                        <Route path="/login" component={LoginForm}/>
                        <Route path="/logout" onEnter={()=>
                            this.logout()}/>

                        <Route path="/map" render={() =>
                            <div>
                                <p><Link to="/submitOccurrence">Criar Ocorrência</Link></p>
                                <p><Link to="/profile">Perfil </Link></p>
                                <p><Link to="/logout" onClick = {()=>
                                    this.logout()}>Logout </Link></p>
                            </div>
                        }
                        />
                            <Route path="/"
                                   render={() =>
                                       <div>
                                           <p><Link to="/login">Login </Link></p>
                                           <p><Link to="/register">Register</Link></p>
                                           <a href="/about">About</a> <br/><br/>
                                           <a href="/contact">Contact</a>
                                       </div>
                                   }
                            />
                        </Switch>
                    </nav>
                </div>
            </Router>

        );
    }
}

export default NavPanel;
