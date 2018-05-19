import React, {Component} from 'react';
import ReactDOM from 'react-dom'

let googleMap;
let globalGoogle;

let showingMarker = false;
let marker;
let infoWindow;

export default class MapContainer extends Component {

    constructor(props) {
        super();
        this.state = {
            locations: []
        }
    }

    addMarker(title, lat, lng) {
        new this.maps.Marker({
            position: {lat: lat, lng: lng},
            map: this.map,
            title: title
        });
        console.log("new marker!");
    }


    // ======================
    // ADD LOCATIONS TO STATE
    // ======================

    google;
    maps;


    componentDidMount() {
        this.loadMap(); // call loadMap function to load the google map
    }

    loadMap() {
        if (this.props && this.props.google) { // checks to make sure that props have been passed
            const {google} = this.props; // sets props equal to google
            this.google = google;
            const maps = google.maps; // sets maps to google maps props
            this.maps = maps;

            const mapRef = this.refs.map; // looks for HTML div ref 'map'. Returned in render below.
            const node = ReactDOM.findDOMNode(mapRef); // finds the 'map' div in the React DOM, names it node

            const mapConfig = Object.assign({}, {
                center: {lat: 40.7485722, lng: -74.0068633}, // sets center of google map to NYC.
                zoom: 11, // sets zoom. Lower numbers are zoomed further out.
                mapTypeId: 'roadmap' // optional main map layer. Terrain, satellite, hybrid or roadmap--if unspecified, defaults to roadmap.
            })
            this.map = new maps.Map(node, mapConfig); // creates a new Google map on the specified node (ref='map') with the specified configuration set above.
            googleMap = this.map;
            globalGoogle = this.google;


            this.map.addListener('click', function(e) {
                if (!showingMarker) {
                    infoWindow = new globalGoogle.maps.InfoWindow({
                        content: '<a href="/submitOccurrence">Submit Occurrence?</a>'
                    });

                    sessionStorage.setItem('selectedLocation', e.latLng);

                    marker = new maps.Marker({
                        position: e.latLng,
                        map: googleMap
                    });

                    infoWindow.open(googleMap, marker);
                    showingMarker = true;
                } else {
                    showingMarker = false;
                    marker.setMap(null);
                }
                console.log("Added empty marker: lat = " + e.latLng)
            });


            // ==================
            // ADD MARKERS TO MAP
            // ==================
            /*this.state.locations.push({
                name: "New York County Supreme Court",
                location: {lat: 40.7143033, lng: -74.0036919}
            });*/
            this.state.locations.forEach(location => { // iterate through locations saved in state
                const marker = new google.maps.Marker({ // creates a new Google maps Marker object.
                    position: {lat: location.location.lat, lng: location.location.lng}, // sets position of marker to specified location
                    map: this.map, // sets markers to appear on the map we just created on line 35
                    title: location.name // the title of the marker is set to the name of the location
                });
            });
        }
    }

    render() {
        const style = { // MUST specify dimensions of the Google map or it will not work. Also works best when style is specified inside the render function and created as an object
            width: '100%', // 90vw basically means take up 90% of the width screen. px also works.
            height: '100%' // 75vh similarly will take up roughly 75% of the height of the screen. px also works.
        }

        return ( // in our return function you must return a div with ref='map' and style.
            <div ref="map" style={style}>
                loading map...
            </div>
        )
    }
}