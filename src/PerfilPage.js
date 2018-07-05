import React, {Component} from 'react';
import placeHolder from './images/placeholders/FotoUser.png';
import placeHolder1 from './images/placeholders/indo_fire_1.jpg';
import placeHolder2 from './images/placeholders/IncendioCasa.jpg';
import placeHolder3 from './images/placeholders/LixoFlorestas.jpg';
import placeHolder4 from './images/placeholders/IncendioEstrada.jpg';
import './PerfilPage.css';
import {OccurrencePreview} from "./Occurrences";
import {Link} from "react-router-dom";


export class PerfilPage extends Component{

    constructor(props){
        super(props);
        this.state = {
            logo: ""
        }
    }

    request = "/occurrency/getByUser/"; //{username}

    render(){
        return(
            <div className="perfilPage">

                <div id = "coverImage"></div>

                <div id="separação" style={{height: "30px"}}>
                    <ProfileImage/>
                </div>

                <div id="sobreMim">
                    <h3 style={{textAlign: "center"}}>Info About Me! :) </h3>
                    <Link to={"/profile/changeProfile"}></Link>
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

class ProfileImage extends Component{

    changeImage() {
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/utils/validLogin", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = function () {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                var fileInput = document.getElementById("profileImageInput");
                var files = fileInput.files;

                console.log(files);
                let filesUploaded = true;
                if (files.length > 0) {
                    var file = files[0];
                    filesUploaded = false;
                    var extension = file.name.split(".")[1];
                    var reader = new FileReader();
                    var xhttp2 = new XMLHttpRequest();

                    reader.readAsArrayBuffer(file);
                    reader.onloadstart = console.log("Starting to read");
                    xhttp2.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/saveImage/" + extension, true);
                    xhttp2.setRequestHeader("Content-type", "application/octet-stream");
                    var uri;
                    reader.onloadend = function () {
                        console.log("Sending file");
                        var result = reader.result;
                    };
                }
            }
            ;
        }
    }

    constructor(props){
        super(props);
        this.state={
            logo: ""
        }
    }

    render(){
        return(
            <div id = "UserFoto">
                <img src={placeHolder}/>
                <input id = "profileImageInput" type="file" placeholder={"Escolha uma imagem"} required/>
                <input type="submit"/>
            </div>
        )
    }

}