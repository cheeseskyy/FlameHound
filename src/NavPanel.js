import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link, Switch, matchPath} from "react-router-dom";
import {RegisterForm, LoginForm} from "./Forms";
import logo from "./imagens/FlameHound Logo with Transparency@2x.png";
import mapLogo from "./imagens/FlameHound Logo with Orange Background@2x.png";

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
                <div id="NavPanel" className="Column">
                    <Link to="/">
                        <div className="logo">
                            <Switch>
                                <Route path="/map"><div id="image"><img src={mapLogo} alt="company logo"/></div></Route>
                                <Route path="/"><div id="image"><img src={logo} alt="company logo"/></div></Route>
                            </Switch>
                        </div>
                    </Link>
                    <nav>
                        <Switch>
                        <Route path="/register" component={RegisterForm}/>
                        <Route path='/login' component={LoginForm}/>
                        <Route path="/logout" onEnter={()=>
                            this.logout()}/>

                        <Route path="/map" render={({match}) =>
                            <div>
                                <p><Link to={match.url + "/submitOccurrence"}>Criar Ocorrência</Link></p>
                                <p><Link to="/profile">Perfil </Link></p>
                                <p><Link to="/logout" onClick = {()=>
                                    this.logout()}>Logout </Link></p>
                            </div>
                        }
                        />
                            <Route path='/'
                                   render={({match}) =>
                                       <div>
                                           <p><Link to='/login'>Login </Link></p>
                                           <p><Link to="/register">Register</Link></p>
                                           <p><Link to="/about">About</Link></p>
                                           <Link to="/contact">Contact</Link>
                                       </div>
                                   }
                            />
                        </Switch>
                    </nav>
                    <img id="ItemPreview"/>
                </div>

        );
    }
}

export default NavPanel;
