import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";
import MapView from './MapView';
import NavPanel from "./NavPanel";


import './Body.css';
import {RegisterForm} from "./Forms";
import Home from "./Home";
import GoogleMapView from "./MapTest";


class Body extends Component{

    render(){
        return(
            <Router>
                <div>
                    <NavPanel/>
                    <div>
                        <Route path="/map" component={MapView}/>
                        <Route exact path="/" component={Home}/>
                        <Route path = "/mapTest" component={GoogleMapView}/>
                    </div>
                </div>
            </Router>
        );
    }
}

export default Body;