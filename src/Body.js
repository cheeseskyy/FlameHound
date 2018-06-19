import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link, Switch} from "react-router-dom";
import MapView from './MapView';
import './Body.css';
import placeHolder from './images/placeholders/colegas.png';


import './Body.css';
import {OccurrenceForm} from "./Forms";
import {withScriptjs, withGoogleMap, GoogleMap} from "react-google-maps";
import EntityPage from "./EntityPage";
import PerfilPage from "./PerfilPage";
import {CommentList} from "./Comments";
import {OccurrencePage} from "./Occurrences";

const Home = () =>
    <div className="FullBody">

        <h1 align="center">FlameHound</h1>
        <br/>

        <div id="text">
            <ul>​​​​​​A nossa missão é facilitar a <b>Prevenção de Incêndios</b>, ajudar o país e as pessoas e
                contribuir para que o sistema funcione melhor para todos nós.
            </ul>
            ​​​​​​
            <ul><b>Integridade:</b> Honrar compromissos.</ul>
            <ul><b>Compreensão:</b> Colocarmo-nos na pele dos nossos clientes/utilizadores.</ul>
            <ul><b>Relacionamento​:</b> Construir confiança, apostando na colaboração.</ul>
            <ul><b>Inovação:</b> Inventar o futuro e aprender com o passado.​</ul>
            <ul><b>Desempenho:</b> Demonstrar excelência em tudo o que fazemos.</ul>

        </div>
        <br/>

        <h2 id="title">Com a sua ajuda, conseguimos farejar os fogos e desta forma prevenir!</h2>
        <br/>
    </div>
;

let FooterMap = withScriptjs(withGoogleMap((props) =>
    <GoogleMap
        defaultZoom={15}
        defaultCenter={{lat: 38.6617255, lng: -9.2064357}}
    >
    </GoogleMap>
));

const Contacts = () =>
    <div className="FullBody">
        <h1 align="center">Contactos</h1>
        <div id="TextConctatos1">
            <div id="TextConctatos2">
                <h4>Contactos Telefónicos de Emergência</h4>
                <ul>
                    <li>Emergência: 112</li>
                    <li>Emergência Florestal: 117</li>
                    <li>Autoridade Protecção Civil: 214 247 100</li>
                    <li>GNR - Comando Geral: 213 217 000</li>
                </ul>

                <h4> Nós</h4>
                <div>
                    flamehoundapdc@gmail.com
                </div>
                <br/>
                <div id="location_map">
                    <FooterMap
                        googleMapURL="https://maps.googleapis.com/maps/api/js?key=AIzaSyBMlXmYjzyVeTm2IpQ6xJY-l2n11u634r4&v=3.exp"
                        loadingElement={<div style={{height: `100%`}}/>}
                        containerElement={<div style={{height: `150px`}}/>}
                        mapElement={<div style={{height: `100%`}}/>}
                    />
                </div>

                <p>Quinta da Torre, Campus Universitário<br/>
                    2829-516 Caparica</p>
            </div>
        </div>
    </div>
;

const About = () =>
    <div className="FullBody">
        <h1 align="center">FlameHound</h1>
        <br/>

        <div id="text">
            <p> Somos uma equipa que ao longo de vários meses, aceitou o desafio de desafio de desenvolver um
                site web com uma componente móvel para indicação das zonas a limpar com uma forte componente
                social.</p>
            <p> Todos nós sabemos como os incêndios assolaram Portugal no ano de 2017, com um registo mortal
                enorme, nunca antes visto, neste tipo de ocorrências.</p>
            <p> Como tal, o nosso objectivo é erradicar estes acontecimentos, nunca os esquecendo mas sim
                utilizando-os como motor para o desenvolvimento deste projecto. Uma actuação rápida, sem
                burocracia, e de cómodo uso.</p>
            <p> Deste modo promovemos uma nova ferramenta que entidades podem utilizar ou mesmo, no futuro,
                promover.</p>
        </div>
        <br/><br/><br/>
        <div align="center">
            <img src={placeHolder}/>
        </div>
    </div>
;

class Body extends Component {

    constructor(props) {
        super(props);
    }


    render() {

        return (
            <div className = "Column Body">
                <Switch>
                    <Route path="/map" component={MapView}/>
                    <Route path="/about" component={About}/>
                    <Route path="/contact" component={Contacts}/>
                    <Route path="/submitOccurrence" component={OccurrenceForm}/>
                    <Route path="/EntityPage" render={(props) =>
                        <EntityPage name = "Placeholder" description = "Placeholder is a company that holds places"/>
                    }/>
                    <Route path="/PerfilPage" render={(props) =>
                        <PerfilPage name = "Placeholder" />
                    }/>
                    <Route path="/Occurrence" render={(props) =>
                        <OccurrencePage title={"some occurrence"}/>} />
                    <Route path="/" component={Home}/>
                </Switch>
            </div>
        );
    }
}

export default Body;