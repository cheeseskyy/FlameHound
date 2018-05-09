import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";
import RegisterForm from "./Register";

function log(text){
    console.log(text);
}

class NavPanel extends Component{
    render(){
        return(

            <Router>
                <div className={this.props.class} id="NavPanel">
                  <div className="title">
                      <center><img src={"imagens/Mock-up Logo.PNG"} alt="company logo" width={150} height={150}/></center>
                      <h1>FlameHound!</h1>
                      <p>Conseguimos farejar os fogos!</p>
                  </div>
                  <nav>
                      <p>Login Here <button> Login </button> </p>

                      <p>Create an account?</p>
                      <Link to="/register">
                          Click me
                      </Link>

                      <Route path="/register" component={RegisterForm} />

                  </nav>
                </div>
            </Router>

        );
    }
}

export default NavPanel;
