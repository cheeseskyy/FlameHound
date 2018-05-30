import React, {Component} from 'react';
import logo from './images/logo/FlameHound Logo with Orange Background +Text@2x.png';
import './App.css';
import Body from "./Body";
import NavPanel from "./NavPanel";
import {BrowserRouter as Router} from 'react-router-dom';

class App extends Component {
    render() {
        return (
            <Router>
                <div>
                    <NavPanel/>
                    <Body/>
                </div>
            </Router>
        );
    }
}

export default App;
