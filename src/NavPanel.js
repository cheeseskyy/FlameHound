import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link, Switch, withRouter} from "react-router-dom";
import {RegisterForm as RegForm, LoginForm as LogForm, AdminLogin as AdmLogin} from "./Forms";
import logo from "./images/logo/FlameHound Logo with Transparency@2x.png";
import mapLogo from "./images/logo/FlameHound Logo with Orange Background@2x.png";
import './NavPanel.css';

const AdminLogin = withRouter(AdmLogin);
const LoginForm = withRouter(LogForm);
const RegisterForm = withRouter(RegForm);
const navPanelState = ["REGULAR", "REGISTER", "LOGIN", "ADMIN_LOGIN"];

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
                console.log("getRole response: " + JSON.parse(xhttp.response));
                this.setState(() => ({
                    isRegister: this.state.isRegister,
                    isLogin: this.state.isRegister,
                    role: JSON.parse(xhttp.response)
                }));
            }
        };
    }

    constructor(props) {
        super(props);
        this.state = {
            navState: navPanelState[0], //REGULAR
            role: "USER"
        };
        this.resetForms = this.resetForms.bind(this);
        this.getRole = this.getRole.bind(this);
    }

    resetForms() {
        console.log("UpdateForms called");
        this.setState({
            navState: navPanelState[0],
            role: this.state.role
        });
    }

    showAdminPanel(){
        console.log("role at if: " + this.state.role);
        if(this.state.role === 'ADMIN'){
            console.log("User role is ADMIN");
            return <div>
                <p><a onClick={() => this.setState({navState: navPanelState[3], role: this.state.role})}>Login as Admin...</a></p>
            </div>
        }
    }


    getRoleNav () {
        console.log("called getRoleNav");
        this.props.getRole();
    }

    showForms(panel) {
        console.log(this.state);
        if (this.state.navState === navPanelState[3]){
            return <AdminLogin resetForms={this.resetForms}/>
        } else if (this.state.navState === navPanelState[2]) { //LOGIN
            return <LoginForm resetForms={this.resetForms} getRole={this.getRole}/>;
        } else if (this.state.navState === navPanelState[1]) { //REGISTER
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

                <Route path = '/admin'
                       render={() =>
                           <div>
                               <p><a>Utilizadores</a></p>
                               <p><a>Ocorrências</a></p>
                               <p><a>Reports</a></p>
                               <p><a>Registos</a></p>
                           </div>
                       }
                />
                <Route exact path='/'
                       render={({match}) =>
                           <div>
                               <p><a onClick={() => this.setState({navState: navPanelState[2], role: this.state.role})}>Login </a></p>
                               <p><a onClick={() => this.setState({navState: navPanelState[1], role: this.state.role})}>Register</a></p>
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
