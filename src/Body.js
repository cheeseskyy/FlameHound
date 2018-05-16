import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";
import MapView from './MapView';
import NavPanel from "./NavPanel";


import './Body.css';
import {RegisterForm} from "./Forms";
import Home from "./Home";


class Body extends Component{

    render(){
        return(
            <Router>
                <div>
                    <NavPanel/>
                    <div>
                        <Route path="/map" component={MapView}/>
                        <Route exact path="/" component={Home}/>
                    </div>
                </div>
            </Router>
        );
    }
}

export default Body;