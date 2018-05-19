import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link, Switch} from "react-router-dom";
import MapView from './MapView';
import NavPanel from "./NavPanel";


import './Body.css';
import {OccurrenceForm, RegisterForm} from "./Forms";
import Home from "./Home";
import Contactos from "./Contactos";
import About from "./About";
import SubmitOcccurrence from "./SubmitOccurrence";


class Body extends Component {

    constructor(props){
        super(props);
    }

    render() {

        return (
            <Router>
                <div>
                    <NavPanel/>
                    <div>
                        <Switch>
                        <Route path="/map" component={MapView}/>
                        <Route path="/about" component={About}/>
                        <Route path="/contact" component={Contactos}/>
                        <Route path="/submitOccurrence" component={OccurrenceForm}/>
                        <Route path="/" component={Home}/>
                        </Switch>
                    </div>
                </div>
            </Router>
        );
    }
}

export default Body;