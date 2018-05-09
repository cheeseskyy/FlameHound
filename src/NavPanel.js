import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";
import {RegisterForm, LoginForm} from "./Forms";

function log(text){
    console.log(text);
}

const loginLinks = {

}

class NavPanel extends Component{
    render(){
        return(

            <Router>
                <div className={this.props.class} id="NavPanel">
                  <div className="title">
                      <img src={"imagens/Mock-up Logo.PNG"} alt="company logo" width={150} height={150}/>
                      <h1>FlameHound</h1>
                      <p>Conseguimos farejar os fogos!</p>
                  </div>
                  <nav>
                      <Route exact path="/"
                             render = {()=>
                             <div>
                                <p><Link to="/login">Login </Link></p>
                                <Link to="/register">
                                Create an account?
                                </Link>
                             </div>
                             }
                      />

                      <Route path="/register" component={RegisterForm} />
                      <Route path="/login" component={LoginForm}/>

                  </nav>
                </div>
            </Router>

        );
    }
}

export default NavPanel;
