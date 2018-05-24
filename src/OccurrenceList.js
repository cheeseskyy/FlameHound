import React, {Component} from 'react';
import OccurrencePreview, {Occurence} from "./OccurrencePreview";
import {BrowserRouter as Router, Route, Link} from "react-router-dom";
import pick1 from "./imagens/indo_fire_1.jpg";

class OccurrenceList extends Component {

    render(){
        const style = {
            cssFloat: 'right'
        }
        return(
          <div id="Occurrences" className="Column">

            <div>
              <div id="occu">
                <h2>Lista de Ocorrências</h2>
              </div>



              <div className="TextOccurrence">
                      <OccurrencePreview title = "Título: Fogo em Almada" user = "João Seco" description = "Exemplo de uma descrição" location = "Almada" image={pick1}/>
              </div>
            </div>

              <div>
                  <p><Link to = "/ocurrencePreview"> Adicionar Ocorrência... </Link></p>
              </div>

          </div>

        );
    }
}

export default OccurrenceList;