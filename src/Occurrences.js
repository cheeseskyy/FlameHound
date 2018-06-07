import React, {Component} from 'react';
import placeHolder1 from './images/placeholders/indo_fire_1.jpg';
import placeHolder2 from './images/placeholders/IncendioCasa.jpg';
import placeHolder3 from './images/placeholders/LixoFlorestas.jpg';
import placeHolder4 from './images/placeholders/IncendioEstrada.jpg';
import './Occurrences.css';


export class OccurrenceList extends Component {

    render(){

        return(
            <div id="Occurrences"  >
                <div id="OccurrencesTitle">
                    <h2>Lista de Ocorrências</h2>
                </div>
                <div id="occurrenceList">

                    <OccurrencePreview title="Título: Fogo no Parque da Paz" user="João Batista"
                                       description="Incêndio já com algumas dimensões!" location="Almada" image={placeHolder1}/>
                    <OccurrencePreview title="Título: Ajuda! Fogo na minha casa!" user="Maria Mendes"
                                           description="Incêndio na minha casa, não sei como isto aconteceu..." location="Gaia" image={placeHolder2}/>
                    <OccurrencePreview title="Título: Limpesa da mata" user="Manuel Batata"
                                       description="Mata com bastante lixo; o que pode provocar algum incêndio." location="Trafaria" image={placeHolder3}/>
                    <OccurrencePreview title="Título: Fogo em Pedrogrão Grande " user="Joaquina Martins"
                                       description="Fogo de grandes dimensões. O Fogo já chega à auto estrada!" location="Pedrogão Grande" image={placeHolder4}/>

                </div>


            </div>

        );
    }
}

export class OccurrencePreview extends Component{

    render(){
        const title = this.props.title;
        const user = this.props.user;
        const location = this.props.location;
        const description = this.props.description;
        const media = this.props.image;

        return(
            <div id="occurrence">
                <div className="OccurrenceImage">
                    <img src={this.props.image}/>
                </div>
                <div>
                    <p> {this.props.title}</p>
                    <p> {this.props.user}</p>
                    <p> {this.props.description}</p>
                    <p> {this.props.location}</p>
                </div>
            </div>
        );
    }
}