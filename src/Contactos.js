import React, {Component} from 'react';
import { withScriptjs, withGoogleMap, GoogleMap } from "react-google-maps";
import {BrowserRouter as Router} from "react-router-dom";
import { withScriptjs, withGoogleMap, GoogleMap } from "react-google-maps"

let FooterMap = withScriptjs(withGoogleMap((props) =>
    <GoogleMap
        defaultZoom={15}
        defaultCenter={{ lat: 38.6617255, lng: -9.2064357 }}
    >
    </GoogleMap>
));

class Contactos extends Component{

    render(){

        const style = {


        }

        return(
            <Router>
            <div>
                <h1 align="center">Contactos</h1>
                <div id="TextConctatos1"> <div id="TextConctatos2">
                    <h4>Contactos Telefónicos de Emergência</h4>
                    <ul>
                        <li>Emergência: 112</li>
                        <li>Emergência Florestal: 117</li>
                        <li>Autoridade Protecção Civil: 214 247 100</li>
                        <li>GNR - Comando Geral: 213 217 000</li>
                    </ul>

                    <h4> Nossa localização</h4>
                    <div id="location_map" style={style}>
                        <FooterMap
                            googleMapURL="https://maps.googleapis.com/maps/api/js?key=AIzaSyBMlXmYjzyVeTm2IpQ6xJY-l2n11u634r4&v=3.exp"
                            loadingElement={<div style={{ height: `100%` }} />}
                            containerElement={<div style={{ height: `150px` }} />}
                            mapElement={<div style={{ height: `100%` }} />}
                        />
                    </div>

                    <p>Quinta da Torre, Campus Universitário<br/>
                    2829-516 Caparica</p>

                </div>
                </div>
                </div>
            </Router>
);
}
}
export default Contactos;