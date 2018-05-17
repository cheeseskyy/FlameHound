import React, {Component} from 'react';
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
            'text-align': 'center',

        }

        return(
            <body style={style}>
                <div id="contactos">
                  <h1 align="center">Contactos</h1>
                    <ul style={style}>
                        <h4>Contactos Telefónicos de Emergência</h4>
                        <li>Emergência: 112</li>
                        <li>Emergência Florestal: 117</li>
                        <li>Autoridade Protecção Civil: 214 247 100</li>
                        <li>GNR - Comando Geral: 213 217 000</li>
                 </ul>
                </div>

                    <h4> Nossa localização</h4>
                    <div id="location_map" style={style}>
                        <FooterMap
                            googleMapURL="https://maps.googleapis.com/maps/api/js?key=AIzaSyBMlXmYjzyVeTm2IpQ6xJY-l2n11u634r4&v=3.exp"
                            loadingElement={<div style={{ height: `100%` }} />}
                            containerElement={<div style={{ height: `150px` }} />}
                            mapElement={<div style={{ height: `100%` }} />}
                        />
                    </div>

                    <h4>Morada</h4>
                    <p>Quinta da Torre, Campus Universitário</p>
                    <p>2829-516 Caparica</p>
            </body>
);
}
}
export default Contactos;