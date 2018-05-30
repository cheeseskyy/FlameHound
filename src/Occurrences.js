import React, {Component} from 'react';
import placeHolder from './images/placeholders/indo_fire_1.jpg';
import './Occurrences.css';

export class OccurrenceList extends Component {

    render(){
        const style = {
            cssFloat: 'right'
        }
        return(
            <div id="Occurrences" className="Column">

                <div>
                    <div id="OccurrencesTitle">
                        <h2>Lista de Ocorrências</h2>
                    </div>



                    <div className="TextOccurrence">
                        <OccurrencePreview title = "Título: Fogo em Almada" user = "João Seco" description = "Exemplo de uma descrição" location = "Almada" image={placeHolder}/>
                    </div>
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
            <div>
                <div className="OccurrenceImage">
                    <img src={this.props.image}/>
                </div>
                <p> {this.props.title}</p>
                <p> {this.props.user}</p>
                <p> {this.props.description}</p>
                <p> {this.props.location}</p>
            </div>
        );
    }
}