import React, {Component} from 'react';
import "./WorkerPage.css";
import placeHolder1 from './images/placeholders/indo_fire_1.jpg';
import placeHolder2 from './images/placeholders/IncendioCasa.jpg';
import placeHolder3 from './images/placeholders/LixoFlorestas.jpg';
import placeHolder4 from './images/placeholders/IncendioEstrada.jpg';
import {OccurrencePreview} from "./Occurrences";

class WorkerPage extends Component{

    componentDidMount(){
        this.getWorkerInfo();
    }

    getOccurrences(){
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/getByUser/" + this.props.id , true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                this.setState(
                    {
                        logo: this.state.logo,
                        occurrences: JSON.parse(xhttp.response),
                        info: this.state.info,
                        stats: this.state.stats
                    }
                )
            }
        };
    }

    getWorkerInfo(){

        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_bo/_worker/" + this.props.id , true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);

        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    console.log("Worker info received");
                    console.log(JSON.parse(xhttp.response));
                }
            }
        }


    }

    render(){
        return(
          <div className="EntityPage">
              {/* div separada para titulo e assim. para se alinhar tudo*/}
              <div id= "entityTitle">
                  <div id = "coverImage">
                      <img src={this.props.coverImage} alt = {this.props.name + "'s cover image"}/>
                  </div>
                  <div id = "entityLogo">
                      <img src={this.props.logo} alt = {this.props.name + "'s logo"} />
                  </div>
                  <div id = "entityDescription">
                      <div id = "nameTitle">
                          {this.props.name}
                      </div>
                      <div id = "aboutUs">
                          {this.props.description}
                      </div>
                  </div>
              </div>
              <div id="entityBody">
                  <div id = "statistics">
                      <p>Ocorrências tratadas: 9</p>
                      <p>Taxa de aprovação: 80%</p>
                  </div>
                  <div id = "treatedOccurrences">
                      <p>Lista de Ocorrências influenciadas:</p>

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
          </div>
        );
    }
}

export default WorkerPage;