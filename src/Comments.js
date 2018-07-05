import React, {Component} from 'react';
import {Link} from 'react-router-dom';
import "./Comments.css";
import placeholderImg from "./images/placeholders/IncendioCasa.jpg";
import placeholderLogo from "./images/placeholders/FotoUser.png";
import {ProfileImage} from "./PerfilPage";

export class CommentList extends Component{

    render(){
        return(
            <div className="CommentList">
                <div id="mainComment">
                    <div id="authorLogo" >
                        <Link to="/profile"><ProfileImage user={this.props.user}/></Link>
                    </div>
                    <div style = {{display: "inline", verticalAlign: "middle"}}>
                         {this.props.description? this.props.description:"Main Comment Text"}
                    </div>
                    <div id="commentButtons">
                        <button>+1</button>
                        <button>-1</button>
                        <button>report</button>
                    </div>
                </div>
                <br/>
                <CommentBox text={"placeholding all the stuff!"}/>
                <br/>
                <CommentBox text={"Another placeholder"}/>
            </div>
        )
    };

}

export class CommentBox extends Component{

    render(){
        return(
            <div className="CommentBox">
                {this.props.text}
            </div>
        )
    };

}