import React, { Component } from 'react';

export class MarkerGroup extends Component{
    constructor(props){
        super(props);
        this.state={
            markers: [

            ]
        };
    }

    addMarker(lat, lng){
        this.state.markers.push({name: "New York County Supreme Court", location: {lat:{lat}, lng:{lng}}})
    }
}