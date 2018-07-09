import React, {Component} from 'react';
import placeHolder1 from './images/placeholders/indo_fire_1.jpg';
import placeHolder2 from './images/placeholders/IncendioCasa.jpg';
import placeHolder3 from './images/placeholders/LixoFlorestas.jpg';
import placeHolder4 from './images/placeholders/IncendioEstrada.jpg';
import './Occurrences.css';
import {CommentList} from "./Comments";
import {Link} from 'react-router-dom';
import WorkerPage from "./WorkerPage";


export class OccurrenceList extends Component {



    renderOccurrences() {
        return this.props.list.map((occurrence, i) => {
            return <OccurrencePreview key={i} id={occurrence.id} title={occurrence.title} user={occurrence.user}
                                      description={occurrence.description}
                                      image={occurrence.mediaURI[0]}
                                      flag={occurrence.flag}
                                      creationDate={occurrence.creationDate} worker={occurrence.worker}/>
        })
    }

    render() {

        return (
            <div id="Occurrences">
                <div id="OccurrencesTitle">
                    <h2>Lista de Ocorrências</h2>
                </div>
                <div id="occurrenceList">

                    {this.renderOccurrences()}

                </div>


            </div>

        );
    }
}

export class OccurrencePreview extends Component {

    constructor(props){
        super(props);
        this.state = {
            image: ""
        }
    }

    getImage(mediaURI) {
        this.hasImage = true;
        const extension = mediaURI.split(".")[1];
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/getImage/" + mediaURI, true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.responseType ="arraybuffer";
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                var arrayBufferView = new Uint8Array( xhttp.response );
                var blob = new Blob( [ arrayBufferView ], { type: "image/" + extension} );
                var urlCreator = window.URL || window.webkitURL;
                var imageUrl = urlCreator.createObjectURL( blob );
                this.setState({
                    image: imageUrl
                })
            }
        };
    }

    treatOc(){
        console.log("trying to treat oc with tag: " + this.props.flag);
        if(this.props.flag === "confirmed"){
            this.tagOc();
        } else if(this.props.flag === "solving"){
            this.solveOc();
        } else if(this.props.flag === "solved"){
            alert("Ocorrência já resolvida");
        }
    }

    tagOc(){
        console.log("Tagging Occurrence");
        var xhttp = new XMLHttpRequest();
        xhttp.open("PUT", "https://my-first-project-196314.appspot.com/rest/_bo/_worker/tag/" + this.props.id, true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);

        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    console.log("Occurrence tag changed")
                }
                else{
                    alert("Ocorreu um erro ao notificar o servidor, tente novamente mais tarde...");
                    console.log("Error: " + xhttp.status);
                }
            }
        }


    }

    solveOc(){
        console.log("Solving Occurrence");
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_bo/_worker/solve/" + this.props.id + "/noimage", true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);

        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    console.log("Occurrence tag changed")
                }
                else{
                    alert("Ocorreu um erro ao notificar o servidor, tente novamente mais tarde...");
                    console.log("Error: " + xhttp.status);
                }
            }
        }
    }

    componentDidMount(){
        //this.getImage(this.props.image);
    }

    WorkerButton = () => {
      if(sessionStorage.getItem("userRole") === "WORKER" && this.props.tag !== "unconfirmed") {

          return <button onClick={() => this.treatOc()}>Tratar</button>
      }
    };

    sendVote(op){
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/user/vote/" + op + "/" + this.props.id, true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    console.log("Vote sent");
                } else{
                    alert("Ocorreu um erro ao votar nesta ocorrência, tente novamente mais tarde.");
                    console.log("Vote error: " + xhttp.status);
                }
            }
        }
    }

    reportOc(){
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/user/report/"+ this.props.user + "/" + this.props.id, true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);

        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    console.log("Report sent");
                } else{
                    alert("Ocorreu um erro ao denunciar nesta ocorrência, tente novamente mais tarde.");
                    console.log("Report error: " + xhttp.status);
                }
            }
        }
    }

    render() {

        return(
            <div className="occurrence">
                <Link to={"/occurrence/"+this.props.id}>
                        <div id="occurrenceImage">
                    <img id="occurrenceLogo" src={"https://my-first-project-196314.appspot.com/rest/occurrency/getImageUri/" + this.props.image}/>
                </div>
                <div>
                    <p id = "occurrenceTitle"> {this.props.title}</p>
                    <p> {this.props.user}</p>
                    <p> {this.props.description}</p>
                    <p> {this.props.location}</p>
                    <p> {this.props.flag}</p>
                    <p> {this.props.creationDate}</p>
                    <p> {this.props.worker}</p>
                </div>
                </Link>
                <div id="occurrenceButtons">
                    <button onClick={() => this.sendVote("upvote")}>
                        +1
                    </button>
                    <button onClick={() => this.sendVote("downvote")}>
                        -1
                    </button>
                    <button onClick={() =>{this.reportOc()}}>
                        Denunciar
                    </button>
                    {this.WorkerButton()}

                </div>
            </div>
        );
    }
}

export class OccurrencePage extends Component {

    constructor(props){
        super(props);
        this.state = {
            info: [],
            image: ""
        }
    }

    componentDidMount(){
        this.getOcInfo();
    }

    getOcInfo(){
        console.log("Getting OC");
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/getOccurrency/" + this.props.id, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        var result;
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("Got Occurrence");
                console.log("Response in function: ");
                console.log(JSON.parse(xhttp.response));
                this.setState({
                    info: JSON.parse(xhttp.response),
                    image: JSON.parse(xhttp.response).mediaURI[0]
                });
                //this.getOcImage(this.state.info.mediaURI[0]);
            }
        };
    }

    getOcImage(mediaURI){
        this.hasImage = true;
        const extension = mediaURI.split(".")[1];
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/getImage/" + mediaURI[0], true);
        xhttp.setRequestHeader("Content-type", "application/json");
        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.responseType ="arraybuffer";
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                var arrayBufferView = new Uint8Array( xhttp.response );
                var blob = new Blob( [ arrayBufferView ], { type: "image/" + extension} );
                var urlCreator = window.URL || window.webkitURL;
                var imageUrl = urlCreator.createObjectURL( blob );
                this.setState({
                    info: this.state.info,
                    image: imageUrl
                })
            }
        };
    }

    render() {
        return (
            <div className="OccurrencePage">
                <div>
                    <h2 id="occurrenceTitle"> Descrição da Ocorrência: {this.state.info.title} </h2>
                    <div id="occurrenceImage" align="center">
                        <img alt="Occurrence Image" src={"https://my-first-project-196314.appspot.com/rest/occurrency/getImageUri/" + this.state.image}/>
                    </div>
                </div>
                <CommentList description={this.state.info.description} user={this.state.info.user} ocID={this.props.id}/>
            </div>
        )
    };

}