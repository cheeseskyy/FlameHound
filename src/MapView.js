import React, { Component } from 'react';
// import the Google Maps API Wrapper from google-maps-react
import { GoogleApiWrapper } from 'google-maps-react'
// import child component
import MapContainer from './MapContainer'
import Occurrences from "./OccurrenceList";

class MapView extends Component{



    render() {
        const style = { // MUST specify dimensions of the Google map or it will not work. Also works best when style is specified inside the render function and created as an object
            width: '55vw', // 90vw basically means take up 90% of the width screen. px also works.
            height: '100vh' // 75vh similarly will take up roughly 75% of the height of the screen. px also works.
        }

        return (
            <div>
                <div style={style} className="Column">
                    <MapContainer google={this.props.google}/>
                </div>
                <Occurrences/>
            </div>
        );
    }
}

export default GoogleApiWrapper({
    apiKey: 'AIzaSyBMlXmYjzyVeTm2IpQ6xJY-l2n11u634r4',
})(MapView)