import React, {Component} from 'react';
import placeHolder from './images/placeholders/FotoUser.png';
import placeHolder1 from './images/placeholders/indo_fire_1.jpg';
import placeHolder2 from './images/placeholders/IncendioCasa.jpg';
import placeHolder3 from './images/placeholders/LixoFlorestas.jpg';
import placeHolder4 from './images/placeholders/IncendioEstrada.jpg';
import './PerfilPage.css';



export class PerfilPage extends Component{

    render(){
        return(
            <div className="perfilPage">

                <div id = "coverImage"></div>

                <div id="separação" style={{height: "30px"}}>
                    <div id = "UserFoto">
                        <img src={placeHolder}/>
                    </div>
                </div>

                <div id="sobreMim">
                    <h3 style={{textAlign: "center"}}>Info About Me! :) </h3>
                    <p style={{paddingLeft: "5px"}}>Nome: </p>
                    <p style={{paddingLeft: "5px"}}>Username:</p>
                    <p style={{paddingLeft: "5px"}}>Morada:</p>
                    <p style={{paddingLeft: "5px"}}>Telefone:</p>
                    <p style={{paddingLeft: "5px"}}>CC:</p>
                    <p style={{paddingLeft: "5px"}}>NIF:</p>
                </div>

                <div id="minhasOcurrencias">
                    <h3 style={{textAlign: "center"}}>Minha Lista de Ocorrências </h3>
                    <div id="occurrenceList">
                        <OccurrencePreview title="Fogo no Parque da Paz"
                                               description="Incêndio já com algumas dimensões!" location="Almada"
                                               image={placeHolder1}/>
                        <OccurrencePreview title="Ajuda! Fogo na minha casa!"
                                               description="Incêndio na minha casa, não sei como isto aconteceu..."
                                               location="Gaia" image={placeHolder2}/>
                        <OccurrencePreview title="Limpesa da mata"
                                               description="Mata com bastante lixo; o que pode provocar algum incêndio."
                                               location="Trafaria" image={placeHolder3}/>
                        <OccurrencePreview title="Fogo em Pedrogrão Grande "
                                               description="Fogo de grandes dimensões. O Fogo já chega à auto estrada!"
                                               location="Pedrogão Grande" image={placeHolder4}/>

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
                    <div id="imagensOcurrencias">
                        <img src={this.props.image}/>
                    </div>
                    <p id = "occurrenceTitle"> {this.props.title}</p>
                    <p> {this.props.description}</p>
                    <p> {this.props.location}</p>
            </div>
        );
    }
}

