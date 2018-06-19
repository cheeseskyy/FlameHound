import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link, Switch, withRouter} from "react-router-dom";
import {RegisterForm as RegForm, LoginForm as LogForm} from "./Forms";
import logo from "./images/logo/FlameHound Logo with Transparency@2x.png";
import mapLogo from "./images/logo/FlameHound Logo with Orange Background@2x.png";
import './NavPanel.css';


const LoginForm = withRouter(LogForm);
const RegisterForm = withRouter(RegForm);

class NavPanel extends Component {
    log() {
        console.log("clicked");
    }

    constructor(props){
        super(props);
        this.state = {
            isRegister : false,
            isLogin : false
        }
        this.resetForms = this.resetForms.bind(this);
    }

    resetForms(){
        console.log("UpdateForms called");
        this.setState({
            isRegister : false,
            isLogin : false
        });
    }

    panel = <Switch>
                <Route path="/map" render={({match}) =>
                    <div>
                        <p><Link to={match.url + "/submitOccurrence"}>Criar Ocorrência</Link></p>
                        <p><Link to="/profile">Perfil </Link></p>
                        <p><Link to="/logout" onClick={() =>
                            this.logout()}>Logout </Link></p>
                    </div>
                }
                />
                <Route path='/'
                       render={({match}) =>
                           <div>
                               <p><a onClick={() => this.setState({isRegister:false, isLogin:true})} >Login </a></p>
                               <p><a onClick={() => this.setState({isRegister:true, isLogin:false})}>Register</a></p>
                               <p><Link to="/about">About</Link></p>
                               <Link to="/contact">Contact</Link>
                           </div>
                       }
                />
            </Switch>;

    showForms(){
        if(this.state.isLogin){
            return <LoginForm resetForms = {this.resetForms}/>;
        } else if(this.state.isRegister){
            return <RegisterForm resetForms = {this.resetForms}/>;
        } else{
            return this.panel;
        }
    }

    isLogin = false;
    isRegister = false;

    logout() {

        console.log("Logging out");

        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com//rest/session/logout", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var jSonObj = sessionStorage.getItem('sessionUsername');
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
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
                            <Route path="/map">
                                <div id="image"><img src={mapLogo} alt="company logo"/></div>
                            </Route>
                            <Route path="/">
                                <div id="image"><img src={logo} alt="company logo"/></div>
                            </Route>
                        </Switch>
                    </div>
                </Link>
                <nav>
                    {this.showForms()}
                </nav>
                <img id="ItemPreview"/>
            </div>
        );
    }
}

export default withRouter(NavPanel);
