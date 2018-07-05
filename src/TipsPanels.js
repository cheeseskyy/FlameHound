import React, {Component} from 'react';
import './Occurrences.css';
import './TipsPanels.css';

import placeHolder from './images/placeholders/FotoUser.png';
import placeHolder1 from './images/placeholders/indo_fire_1.jpg';
import placeHolder2 from './images/placeholders/IncendioCasa.jpg';
import placeHolder3 from './images/placeholders/LixoFlorestas.jpg';
import placeHolder4 from './images/placeholders/IncendioEstrada.jpg';

export class OccurrenceTips extends Component{

    render(){
        return(

            <div className= "TipsPanel">
                <div>
                    <h4>Alguns exemplos de ocorrências:</h4>
                    <div id="occurrenceList1">
                        <OccurrencePreview title="Incêndio"
                                           description="Incêndio já com algumas dimensões!" location="Serra Monsanto"
                                           image={placeHolder1}/>
                        <OccurrencePreview title="Ajuda! Fogo na minha casa!"
                                           description="Incêndio na minha casa, não sei como isto aconteceu..."
                                           location="Gaia" image={placeHolder2}/>
                        <OccurrencePreview title="Limpesa da mata"
                                           description="Mata com bastante lixo; pode impedir o caminho das entidades combatentes de fogos."
                                           location="Trafaria" image={placeHolder3}/>

                    </div>
                </div>

            </div>
        );
    }
}
export class OccurrencePreview extends Component {

    render() {
        return(
            <div className="occurrence">
                <div id="imagensOcurrencias1">
                    <img src={this.props.image}/>
                </div>
                <p id = "occurrenceTitle"> {this.props.title}</p>
                <p> {this.props.description}</p>
                <p> {this.props.location}</p>
            </div>
        );
    }
}