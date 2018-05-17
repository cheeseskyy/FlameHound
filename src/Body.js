import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";
import MapView from './MapView';
import NavPanel from "./NavPanel";


import './Body.css';
import {RegisterForm} from "./Forms";
import Home from "./Home";
import Contactos from "./Contactos";
import About from "./About";
import SubmitOcccurrence from "./SubmitOccurrence";


class Body extends Component{

    render(){
        return(
            <Router>
                <div>
                    <NavPanel/>
                    <div>
                        <Route path="/map" component={MapView}/>
                        <Route path="/about" component={About}/>
                        <Route path="/contact" component={Contactos}/>
                        <Route path="/submitOccurrence" component={SubmitOcccurrence}/>
                        <Route exact path="/" component={Home}/>

                    </div>
                </div>
            </Router>
        );
    }
}

export default Body;