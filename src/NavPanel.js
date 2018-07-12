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

    isLoggedIn;

    log() {
        console.log("clicked");
    }

    validLogIn() {
        console.log("checking for login");
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/utils/validLogin", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        let username = sessionStorage.getItem('sessionUsername');
        let token = sessionStorage.getItem('sessionToken');
        let jsonObj = JSON.stringify({
            'username': username,
            'tokenId': token
        });
        xhttp.send(jsonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if (xhttp.status === 200) {
                    console.log("user is logged in");
                    this.isLoggedIn = true;
                    this.forceUpdate();
                } else if (xhttp.status === 403 || xhttp.status === 500) {
                    console.log("user is not logged in");
                    sessionStorage.removeItem('sessionUsername');
                    sessionStorage.removeItem('sessionToken');
                } else{
                    console.log("user validLogin went wrong, status: " + xhttp.status);
                }
            }

        };
    }

    componentDidMount(){
        this.isLoggedIn = false;
        this.validLogIn();
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
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                console.log("getRole response: " + JSON.parse(xhttp.response));
                sessionStorage.setItem("userRole", JSON.parse(xhttp.response));
                this.isLoggedIn = true;
                this.setState(() => ({
                    navState: navPanelState[0] //REGULAR
                }));
            }
        };
    }

    constructor(props) {
        super(props);
        this.state = {
            navState: navPanelState[0] //REGULAR
        };
        this.resetForms = this.resetForms.bind(this);
        this.getRole = this.getRole.bind(this);
    }

    resetForms() {
        console.log("UpdateForms called");
        this.setState({
            navState: navPanelState[0]
        });
    }

    showAdminPanel(){
        const role = sessionStorage.getItem("userRole");
        console.log("role at if: " + this.state.role);
        if(role === 'ADMIN' || role === 'MODERATOR'){
            console.log("User role is " + role);
            return <Link to={"/admin"}> Moderação </Link>;
        }
    }


    getRoleNav () {
        console.log("called getRoleNav");
        this.props.getRole();
    }

    showForms(panel) {
        console.log(this.state);
        if (this.state.navState === navPanelState[2]) { //LOGIN
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
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/session/logout", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var jSonObj = sessionStorage.getItem('sessionUsername');
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                alert("Successful logout.");
                sessionStorage.removeItem('sessionUsername');
                sessionStorage.removeItem('sessionToken');
                window.location.replace("/");
            }
        };
    }

    logoutAdmin() {

        console.log("Logging out");

        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/logout", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var jSonObj = sessionStorage.getItem('sessionUsernameAdmin');
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                alert("Successful logout.");
                sessionStorage.removeItem('sessionUsernameAdmin');
                sessionStorage.removeItem('sessionTokenAdmin');
            }
        };
    }

    NotLoggedInPanel =
        <div>
            <p><a onClick={() => this.setState({navState: navPanelState[2], role: this.state.role})}>Login </a></p>
            <p><a onClick={() => this.setState({navState: navPanelState[1], role: this.state.role})}>Register</a></p>
            <p><Link to="/about">About</Link></p>
            <Link to="/contact">Contact</Link>
        </div>;

    LoggedInPanel ({match}) {
        return (<div>
            <p><a onClick={() => this.setState({navState: navPanelState[2], role: this.state.role})}>Login </a></p>
            <p><a onClick={() => this.setState({navState: navPanelState[1], role: this.state.role})}>Register</a></p>
            <p><Link to="/about">About</Link></p>
            <Link to="/contact">Contact</Link>
        </div>)
    }

    renderCorrectPanel({match}){
        if(this.isLoggedIn){
            return <this.LoggedInPanel/>
        } else {
            return <this.NotLoggedInPanel/>
        }
    }

    logsLink= () => {
        if(sessionStorage.getItem("userRole") === "ADMIN"){
            return (<p><Link to={"/admin/logs"}>Registos</Link></p>)
        }
    };

    render() {

        const panel =
            <Switch>

                <Route path = '/admin'
                       render={() =>
                           <div>
                               <p><Link to={"/admin/users"}>Utilizadores</Link></p>
                               <p><Link to={"/admin/occurrences"}>Ocorrências</Link></p>
                               <p><Link to={"/admin/reports"}>Denúncias</Link></p>
                               {this.logsLink()}
                               <p><Link to={"/map"}>Mapa</Link></p>
                               <p><Link to="/" onClick={() =>
                                   this.logout()}>Logout </Link></p>
                           </div>
                       }
                />
                <Route path="/" render={({match}) =>{
                    if(this.isLoggedIn){
                    return(
                        <div>
                            <p><Link to={"/map"}>Mapa</Link></p>
                            <p><Link to={"/map"} onClick={() => alert("Clique no mapa para criar uma ocorrência")}>Criar Ocorrência?</Link></p>
                            <p><Link to={"/profile/" + sessionStorage.getItem('sessionUsername')}>Perfil </Link></p>
                            {console.log("Running showAdminPanel()")}
                            {this.showAdminPanel()}
                            <p><Link to="/logout" onClick={() =>
                                this.logout()}>Logout </Link></p>
                        </div>
                    )}else{
                    return(
                        <div>
                            <p><a onClick={() => this.setState({navState: navPanelState[2], role: this.state.role})}>Login</a></p>
                            <p><a onClick={() => this.setState({navState: navPanelState[1], role: this.state.role})}>Registar</a></p>
                            <p><Link to="/about">Sobre nós</Link></p>
                            <Link to="/contact">Contactos</Link>
                        </div>
                    )}
                }}
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
