
import React, { Component } from 'react';
import MapView from './MapView';
import './App.css';
import Body from "./Body";

class App extends Component {
    render() {
        return (
            <Body/>
        );
    }
}
// OTHER MOST IMPORTANT: Here we are exporting the App component WITH the GoogleApiWrapper. You pass it down with an object containing your API key
export default App;