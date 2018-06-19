import React, {Component} from 'react';
import logo from './images/logo/FlameHound Logo with Orange Background +Text@2x.png';
import './App.css';
import Body from "./Body";
import NavPanel from "./NavPanel";
import {BrowserRouter as Router, Route} from 'react-router-dom';



class App extends Component {
    render() {
        return (
            <Router>
                <Route>
                    <div>
                        <NavPanel history = {this.props.history}/>
                        <Body/>
                    </div>
                </Route>
            </Router>
        );
    }
}

export default App;
