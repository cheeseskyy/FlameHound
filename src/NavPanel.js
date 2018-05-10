import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link} from "react-router-dom";
import {RegisterForm, LoginForm} from "./Forms";
import logo from "./imagens/Logo.png";

function log(text) {
    console.log(text);
}

const loginLinks = {}

class NavPanel extends Component {
    render() {
        return (

            <Router>
                <div id="NavPanel" className="Column">
                    <div className="logo">
                        <div id="image"><img src={logo} alt="company logo"/></div>
                    </div>
                    <nav>
                        <Route exact path="/"
                               render={() =>
                                   <div>
                                       <p><Link to="/login">Login </Link></p>
                                       <p><Link to="/register">Register</Link></p>
                                       <p><Link to="/about">About</Link></p>
                                       <p><Link to="/contact">Contact</Link></p>
                                   </div>
                               }
                        />
                        <Route path="/register" component={RegisterForm}/>
                        <Route path="/login" component={LoginForm}/>

                        <Route path="/map" render={() =>
                            <div>
                                <p><Link to="/submitOcurrence">Criar Ocorrência</Link></p>
                                <p><Link to="/profile">Perfil </Link></p>
                                <p><Link to="/">Logout </Link></p>
                            </div>
                        }
                        />
                    </nav>
                </div>
            </Router>

        );
    }
}

export default NavPanel;
