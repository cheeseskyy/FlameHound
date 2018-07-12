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
            info: {},
            stats:{},
            workerInfo: {}
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
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                console.log("Got user Ocs");
                this.setState(
                    {
                        logo: this.state.logo,
                        occurrences: JSON.parse(xhttp.response),
                        info: this.state.info,
                        stats: this.state.stats,
                        workerInfo: this.state.workerInfo
                    }
                )
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
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                console.log("Got use stats");
                this.setState(
                    {
                        logo: this.state.logo,
                        occurrences: this.state.occurrences,
                        info: this.state.info,
                        stats: JSON.parse(xhttp.response),
                        workerInfo: this.state.workerInfo
                    }
                )
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
                    console.log("Got user info");
                    this.setState(
                        {
                            logo: this.state.logo,
                            occurrences: this.state.occurrences,
                            info: JSON.parse(xhttp.response),
                            stats: this.state.stats,
                            workerInfo: this.state.workerInfo
                        }
                    );
                    if (this.state.info.role === "WORKER") {
                        this.getWorkerInfo();
                    }
                }
                else if(xhttp.status === 404){
                    alert("Este utilizador não existe, verifique o nome do utilizador e tente outra vez. A redireccionar...");
                    this.props.history.goBack();
                }
            }
        };
    }

    getWorkerInfo(){

        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_bo/_worker/getWorkerInfo/" + this.props.id , true);
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
                    this.setState(
                        {
                            logo: this.state.logo,
                            occurrences: this.state.occurrences,
                            info: this.state.info,
                            stats: this.state.stats,
                            workerInfo: JSON.parse(xhttp.response)
                        }
                    )
                }
                else{
                    console.log("error: " + xhttp.status);
                }
            }
        }


    }

    isHimself = true;

    componentDidMount(){
        //request Occurrences
        this.getOccurrences();
        this.getStats();
        this.getInfo();
    }



    request = "/occurrency/getByUser/"; //{username}

    workerStats = () => {
        if(this.state.info.role === "WORKER"){
            return(
                <div id={"workerStats"}>
                    <h3 style={{textAlign: "center"}}>Estatísticas de Trabalhador:</h3>
                    <p style={{paddingLeft: "5px"}}>Taxa de Aprovação: {this.state.workerInfo.approvalRate} </p>
                    <p style={{paddingLeft: "5px"}}>Taxa de Desaprovação: {this.state.workerInfo.disapprovalRate} </p>
                    <p style={{paddingLeft: "5px"}}>Ocorrências Tratadas: {this.state.workerInfo.occurrenciesTreated} </p>
                </div>
            )
        }
    };

    workerOcs = () => {
        if(this.state.info.role === "WORKER" && this.state.workerInfo.occurrencies){
            return(<div>
                <h3 style={{textAlign: "center"}}>Ocorrências tratadas: </h3>;
                {
                    this.state.workerInfo.occurrencies.map(occurrence => {
                    return <OccurrencePreview key={""} id={occurrence.id} title={occurrence.title} user={occurrence.user}
                    description={occurrence.description}
                    image={occurrence.mediaURI}
                    />
                    })
                }
            </div>
            )
        }
    };

    render(){

        this.isHimself = (sessionStorage.getItem("sessionUsername") === this.props.id);

        return(
            <div className="perfilPage">

                <div id = "coverImage"></div>

                <div id="separação" style={{height: "30px"}}>
                    <ProfileImage id={this.props.id} isHimself={this.isHimself}/>
                </div>

                <div id="sobreMim">
                    <h3 style={{textAlign: "center"}}>Sobre mim </h3>
                    <Link to={"/profile/changeProfile"}></Link>
                    <p style={{paddingLeft: "5px"}}>Nome: {this.state.info.name}</p>
                    <p style={{paddingLeft: "5px"}}>Username: {this.state.info.username}</p>

                    <div id={"stats"}>
                        <h3 style={{textAlign: "center"}}>Estatísticas:</h3>
                        <p style={{paddingLeft: "5px"}}>Votos Positivos: {this.state.stats.upvotes} </p>
                        <p style={{paddingLeft: "5px"}}>Votos Negativos: {this.state.stats.downvotes} </p>
                        <p style={{paddingLeft: "5px"}}>Ocorrências Criadas: {this.state.stats.occurrenciesPosted} </p>
                        <p style={{paddingLeft: "5px"}}>Ocorrências Confirmadas: {this.state.stats.occurrenciesConfirmed} </p>
                    </div>
                    {this.workerStats()}
                </div>

                <div id="minhasOcurrencias">
                    <h3 style={{textAlign: "center"}}>Ocorrências Criadas: </h3>
                    <div id="occurrenceList">

                        {
                            this.state.occurrences.map(occurrence => {
                                return <OccurrencePreview key={""} id={occurrence.id} title={occurrence.title} user={occurrence.user}
                                                          description={occurrence.description}
                                                          image={occurrence.mediaURI[0]}
                                                          randomProp={console.log("created occurrence " + occurrence.title)}/>

                            })
                        }
                        {this.workerOcs()}
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

    workerOcs = () => {

    };

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