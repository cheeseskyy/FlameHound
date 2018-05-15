import React, { Component } from 'react';
// import the Google Maps API Wrapper from google-maps-react
import { GoogleApiWrapper } from 'google-maps-react'
import {GoogleMap } from 'react-google-maps';
// import child component
import MapContainer from './MapContainer';
import Occurrences from "./OccurrenceList";
import "./MapView.css";

class MapView extends Component{

    consolelog(){
        console.log("this");
    }

    render() {
        const style = { // MUST specify dimensions of the Google map or it will not work. Also works best when style is specified inside the render function and created as an object
            width: '50vw', // 90vw basically means take up 90% of the width screen. px also works.
            height: '100vh', // 75vh similarly will take up roughly 75% of the height of the screen. px also works.
            zIndex: '1'
        }

        return (
            <div>
                <div style={style} className="Column">
                    <MapContainer google={this.props.google} onClick={this.consolelog}/>
                </div>
                <Occurrences/>
            </div>
        );
    }
}

export default GoogleApiWrapper({
    apiKey: 'AIzaSyBMlXmYjzyVeTm2IpQ6xJY-l2n11u634r4',
})(MapView)