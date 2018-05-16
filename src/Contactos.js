import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";
import { withGoogleMap, GoogleMap } from "react-google-maps"

let FooterMap = withGoogleMap((props) =>
    <GoogleMap
        defaultZoom={15}
        defaultCenter={{ lat: 38.6617255, lng: -9.2064357 }}
    >
    </GoogleMap>
)


class Contactos extends Component{

    render(){

        const style = {
                'text-align': 'center',
                width: '84vw'
        }
        return(
            <body style={style}>
                <div id="contacts">
                  <h1>Contactos</h1>
                    <ul>
                        <h4>Contactos Telefónicos de Emergência</h4>
                        <li>Emergência: 112</li>
                        <li>Emergência Florestal: 117</li>
                        <li>Autoridade Protecção Civil: 214 247 100</li>
                        <li>GNR - Comando Geral: 213 217 000</li>
                        <h4>Outro contacto</h4>
                        <li>apdc3@gmail.com</li>
                 </ul>
                </div>

                <div id="location">
                    <h4> Nossa localização</h4>
                     <div id="location_map">

                    </div>
                </div>
                <div id="address">
                    <h4>Morada</h4>
                    <p>Quinta da Torre, Campus Universitário</p>
                    <p>2829-516 Caparica</p>
                </div>
            </body>
);
}
}
export default Contactos;