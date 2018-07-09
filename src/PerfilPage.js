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
            logo: "",
            occurrences: [],
            info: {}
        }
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
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200) {
                    this.setState(
                        {
                            logo: this.state.logo,
                            occurrences: JSON.parse(xhttp.response),
                            info: this.state.info
                        }
                    )
                }
            }
        };
    }

    getInfo(){
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/user/getUserInfo/" + this.props.id , true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200) {
                    console.log("user info:");
                    console.log(JSON.parse(xhttp.response));
                    this.setState(
                        {
                            logo: this.state.logo,
                            occurrences: this.state.occurrences,
                            info: JSON.parse(xhttp.response)
                        }
                    )
                } else if (xhttp.status === 404) {
                    alert("Este utilizador não existe. A redireccionar...");
                    this.props.history.goBack();
                }
            }
        };
    }

    getStats(){
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/user/getStats/" + this.props.id , true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200) {
                    console.log("user info:");
                    console.log(JSON.parse(xhttp.response));
                    this.setState(
                        {
                            logo: this.state.logo,
                            occurrences: this.state.occurrences,
                            info: this.state.info
                        }
                    )
                } else if (xhttp.status === 404) {
                    alert("Este utilizador não existe. A redireccionar...");
                    this.props.history.goBack();
                }
            }
        };
    }

    componentDidMount(){
        //request Occurrences
        this.getOccurrences();
        this.getInfo();
        this.getStats();
    }

    isHimself = false;

    request = "/occurrency/getByUser/"; //{username}

    render(){

        if(this.props.id === sessionStorage.getItem("sessionUsername"))
            this.isHimself = true;

        return(
            <div className="perfilPage">

                <div id = "coverImage"></div>

                <div id="separação" style={{height: "30px"}}>
                    <ProfileImage id={this.props.id} isHimself={this.isHimself}/>
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

                        {
                            this.state.occurrences.map(occurrence => {
                                return <OccurrencePreview key={""} id={occurrence.id} title={occurrence.title} user={occurrence.user}
                                                          description={occurrence.description}
                                                          image={occurrence.mediaURI[0]}
                                                          randomProp={console.log("created occurrence " + occurrence.title)}/>

                            })
                        }
                        </div>
                    </div>
                </div>
        );
    }
}

export class ProfileImage extends Component{

    changeImage(id) {
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
                if (files.length > 0) {
                    var file = files[0];
                    var extension = file.name.split(".")[1];
                    var reader = new FileReader();
                    var xhttp2 = new XMLHttpRequest();

                    reader.readAsArrayBuffer(file);
                    reader.onloadstart = console.log("Starting to read");
                    xhttp2.open("POST", "https://my-first-project-196314.appspot.com/rest/user/saveProfileImage/" + id + "." + extension, true);
                    xhttp2.setRequestHeader("Content-type", "application/octet-stream");
                    reader.onloadend = function () {
                        console.log("Sending file");
                        var result = reader.result;
                        xhttp2.send(result);
                    };
                    xhttp2.onreadystatechange = function () {
                        if (xhttp2.readyState == 4 && xhttp.status == 200) {
                        }
                    };
                }
            }
        };
    }

    constructor(props){
        super(props);
        this.state={
            logo: ""
        }
    }

    componentDidMount(){
        //getImage(this.props.user)
    }

    imageOptions(){
        if(this.props.isHimself){
            return(
                <div>
                    <input id = "profileImageInput" type="file" placeholder={"Escolha uma imagem"} required/>
                    <input type="submit" value={"Submeter"} onClick={() => this.changeImage(this.props.id)}/>
                </div>
            )
        }
    }

    render(){
        return(
            <div id = "UserFoto">
                <img src={"https://my-first-project-196314.appspot.com/rest/user/getImageUri/" + this.props.id}/>
                {this.imageOptions()}
            </div>
        )
    }

}