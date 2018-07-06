import React, {Component} from 'react';
import {Link} from 'react-router-dom';
import "./Comments.css";
import placeholderImg from "./images/placeholders/IncendioCasa.jpg";
import placeholderLogo from "./images/placeholders/FotoUser.png";
import {ProfileImage} from "./PerfilPage";

export class CommentList extends Component{

    constructor(props){
        super(props);
        this.state = {
            comments: []
        }
    }

    getComments() {

    }

    componentDidMount(){
        this.getComments();
    }

    render(){
        return(
            <div className="CommentList">
                <div id="mainComment">
                    <div id="authorLogo" >
                        <Link to={"/profile/" + this.props.user}><ProfileImage user={this.props.user}/></Link>
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
                <br/>
                <AddCommentBox ocID={this.props.ocID} replyingTo={this.props.user}/>
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

export class AddCommentBox extends Component{

    sendComment = (e) => {

        if(!(e.key === 'Enter')) {
            return;
        }
        console.log("Submitted comment");
        const request = "https://my-first-project-196314.appspot.com/rest/social/" + this.props.ocID + "/post";
        const xhttp =  new XMLHttpRequest();
        xhttp.open("POST", request, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        const username = sessionStorage.getItem('sessionUsername');
        const token = sessionStorage.getItem('sessionToken');
        const comment = document.getElementById("newCommentInput").value;
        const replyingTo = this.props.replyingTo;

        const jsonObj = JSON.stringify({
            user: username,
            tokenId: token,
            comment: comment,
            replyingTo: replyingTo
        });
        console.log("Sending Comment");
        xhttp.send(jsonObj);
        xhttp.onreadystatechange = function () {
            if(xhttp.readyState === 4){
                console.log("Comment response status = " + xhttp.status);
            }
        }

    }

    render(){
        return(
            <div>
                <input id={"newCommentInput"} type={"text"} onKeyPress={this.sendComment}/>
            </div>
        )
    }

}