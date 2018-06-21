import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link, Switch, withRouter} from "react-router-dom";
import {RegisterForm as RegForm, LoginForm as LogForm} from "./Forms";
import logo from "./images/logo/FlameHound Logo with Transparency@2x.png";
import mapLogo from "./images/logo/FlameHound Logo with Orange Background@2x.png";
import './NavPanel.css';


const LoginForm = withRouter(LogForm);
const RegisterForm = withRouter(RegForm);
const navPanelState = ["REGISTER", "LOGIN", "IS_LOGGED_IN"];

class NavPanel extends Component {
    log() {
        console.log("clicked");
    }

    getRole(){
        console.log("called getRole");
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/user/getRole", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("getRole response: " + xhttp.response);
                this.setState(() => ({
                    isRegister: this.state.isRegister,
                    isLogin: this.state.isRegister,
                    role: xhttp.response
                }));
                this.forceUpdate();
            }
        };
    }

    constructor(props) {
        super(props);
        this.state = {
            isRegister: false,
            isLogin: false,
            role: "USER"
        }
        this.resetForms = this.resetForms.bind(this);
        this.getRole = this.getRole.bind(this);
    }

    resetForms() {
        console.log("UpdateForms called");
        this.setState({
            isRegister: false,
            isLogin: false
        });
    }

    showAdminPanel(){
        console.log("role at if: " + this.state.role);
        if(this.state.role === 'ADMIN'){
            console.log("User role is ADMIN");
            return <div>
                ADMIN
            </div>
        }
    }

    panel =
        <Switch>

            <Route exact path='/'
                   render={({match}) =>
                       <div>
                           <p><a onClick={() => this.setState({isRegister: false, isLogin: true, role: "USER"})}>Login </a></p>
                           <p><a onClick={() => this.setState({isRegister: true, isLogin: false, role: "USER"})}>Register</a></p>
                           <p><Link to="/about">About</Link></p>
                           <Link to="/contact">Contact</Link>
                       </div>
                   }
            />
            <Route path="/" render={({match}) =>
                <div>
                    <p><Link to={"/map"}>Mapa</Link></p>
                    <p><Link to={match.url + "/submitOccurrence"}>Criar Ocorrência</Link></p>
                    <p><Link to="/profile">Perfil </Link></p>
                    {console.log("Running showAdminPanel()")}
                    {this.showAdminPanel()}
                    <p><Link to="/logout" onClick={() =>
                        this.logout()}>Logout </Link></p>
                </div>
            }
            />

        </Switch>;


    getRoleNav () {
        console.log("called getRoleNav");
        this.props.getRole();
    }

    showForms(panel) {
        console.log(this.state);
        if (this.state.isLogin) {
            return <LoginForm resetForms={this.resetForms} getRole={this.getRole}/>;
        } else if (this.state.isRegister) {
            return <RegisterForm resetForms={this.resetForms}/>;
        } else {
            console.log("printing navPanel");
            return panel;
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

        const panel =
            <Switch>

                <Route exact path='/'
                       render={({match}) =>
                           <div>
                               <p><a onClick={() => this.setState({isRegister: false, isLogin: true, role: "USER"})}>Login </a></p>
                               <p><a onClick={() => this.setState({isRegister: true, isLogin: false, role: "USER"})}>Register</a></p>
                               <p><Link to="/about">About</Link></p>
                               <Link to="/contact">Contact</Link>
                           </div>
                       }
                />
                <Route path="/" render={({match}) =>
                    <div>
                        <p><Link to={match.url + "/submitOccurrence"}>Criar Ocorrência</Link></p>
                        <p><Link to="/profile">Perfil </Link></p>
                        {console.log("Running showAdminPanel()")}
                        {this.showAdminPanel()}
                        <p><Link to="/logout" onClick={() =>
                            this.logout()}>Logout </Link></p>
                    </div>
                }
                />

            </Switch>;

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
                    {console.log("user role at navPanel render is: " + this.state.role)}
                    {this.showForms(panel)}
                </nav>
                <img id="ItemPreview"/>
            </div>
        );
    }
}

export default withRouter(NavPanel);
