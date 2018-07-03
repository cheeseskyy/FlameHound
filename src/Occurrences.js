import React, {Component} from 'react';
import placeHolder1 from './images/placeholders/indo_fire_1.jpg';
import placeHolder2 from './images/placeholders/IncendioCasa.jpg';
import placeHolder3 from './images/placeholders/LixoFlorestas.jpg';
import placeHolder4 from './images/placeholders/IncendioEstrada.jpg';
import './Occurrences.css';
import {CommentList} from "./Comments";
import {Link} from 'react-router-dom';


export class OccurrenceList extends Component {



    renderOccurrences() {
        return this.props.list.map((occurrence, i) => {
            var extension = occurrence.mediaURI[0].split(".")[1];
            return <OccurrencePreview key={i} title={occurrence.title} user={occurrence.user}
                                      description={occurrence.description}
                                      image={occurrence.mediaURI[0]}
                                      randomProp={console.log("created occurrence " + occurrence.title)}/>
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
                var blob = new Blob( [ arrayBufferView ], { type: "image/jpeg"} );
                var urlCreator = window.URL || window.webkitURL;
                var imageUrl = urlCreator.createObjectURL( blob );
                this.setState({
                    image: imageUrl
                })
            }
        };
    }
    hasImage = false;

    render() {

        let image;

        if(this.state != null && this.state.image != null){
            image = this.state.image;
        } else {
            image = this.props.image;
        }

        if(!this.hasImage)
            this.getImage(this.props.image);

        return(
            <div className="occurrence">
                <Link to="/occurrence">
                        <div id="occurrenceImage">
                    {console.log(this.props.image)}
                            {console.log(image)}
                    <img id="occurrenceLogo" src={image}/>
                </div>
                <div>
                    <p id = "occurrenceTitle"> {this.props.title}</p>
                    <p> {this.props.user}</p>
                    <p> {this.props.description}</p>
                    <p> {this.props.location}</p>
                </div>
                </Link>
                    <div id="occurrenceButtons">
                    <button>
                        +1
                    </button>
                    <button>
                        -1
                    </button>
                    <button>
                        flag
                    </button>
                </div>
            </div>
        );
    }
}

export class OccurrencePage extends Component {

    render() {
        return (
            <div className="OccurrencePage">
                <div>
                    <p id="occurrenceTitle"> {this.props.title} </p>
                    <div id="occurrenceImage">
                        <img alt="Occurrence Image" src={this.props.image ? this.props.image : placeHolder1}/>
                    </div>
                </div>
                <CommentList/>
            </div>
        )
    };

}