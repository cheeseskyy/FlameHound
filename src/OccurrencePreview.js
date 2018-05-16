import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Link} from "react-router-dom";
import {RegisterForm, LoginForm} from "./Forms";


class OccurrencePreview extends Component{

    render(){
        const title = this.props.title;
        const user = this.props.user;
        const location = this.props.location;
        const description = this.props.description;
        const media = this.props.media;

        return(
            <div>
                <p> {this.props.title}</p>
                <p> {this.props.user}</p>
                <p> {this.props.description}</p>
                <p> {this.props.location}</p>
            </div>
        );
    }
}

export class Occurence extends Component{

}

export default OccurrencePreview