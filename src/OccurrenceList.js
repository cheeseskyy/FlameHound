import React, {Component} from 'react';
import OccurrencePreview, {Occurence} from "./OccurrencePreview";
import {BrowserRouter as Router, Route, Link} from "react-router-dom";
import logo from "./imagens/indo_fire_1.jpg";

class OccurrenceList extends Component {

    render(){
        const style = {
            cssFloat: 'right'
        }
        return(
          <div id="Occurrences" className="Column">

              <b>Lista de Ocorrências</b>

              <OccurrencePreview title = "Fogo"/>
              <div className="logo">
                  <div id="image"><img src={logo} alt="company logo" align="right" height="150" width="150" /></div>
              </div>
              <OccurrencePreview description = "António Batata"/>
              <OccurrencePreview description = "Pequeno incêncio na mata"/>
              <OccurrencePreview description = "Parque da Paz"/>

              <p><Link to = "/ocurrencePreview"> Adicionar Ocorrência </Link></p>

          </div>
        );
    }
}

export default OccurrenceList;