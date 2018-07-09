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

        const request = "https://my-first-project-196314.appspot.com/rest/social/" + this.props.ocID + "/getAll";
        const xhttp =  new XMLHttpRequest();
        xhttp.open("POST", request, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        const username = sessionStorage.getItem('sessionUsername');
        const token = sessionStorage.getItem('sessionToken');
        const jsonObj = JSON.stringify({
            username: username,
            tokenId: token
        });
        xhttp.send(jsonObj);
        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                console.log("getComment response status = " + xhttp.status);
                if(xhttp.status === 200){
                    console.log(JSON.parse(xhttp.response));
                    this.setState({
                        comments: JSON.parse(xhttp.response)
                    });
                }
            }
        }
    }

    componentDidMount(){
        this.getComments();
    }

    render(){
        return(
            <div className="CommentList">
                <div id="mainComment">

                        <Link to={"/profile/" + this.props.user}><ProfileImage user={this.props.user}/></Link>
                    <div style = {{textAlign:"center"}}>
                         {this.props.description? this.props.description:"Main Comment Text"}
                    </div>
                    <div id="commentButtons" style={{textAlign:"center"}}>
                        <button>+1</button>
                        <button>-1</button>
                        <button>report</button>
                    </div>
                </div>
                {
                    this.state.comments.map(comment => {
                        return <CommentBox author={comment.username} text={comment.comment} postDate={comment.postDate} key={comment.id}/>

                    })
                }
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

        const comment = document.getElementById("newCommentInput").value;
        if(comment === ""){
            return;
        }

        console.log("Submitted comment");
        const request = "https://my-first-project-196314.appspot.com/rest/social/" + this.props.ocID + "/post";
        const xhttp =  new XMLHttpRequest();
        xhttp.open("POST", request, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        const username = sessionStorage.getItem('sessionUsername');
        const token = sessionStorage.getItem('sessionToken');
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
        };
    };

    render(){
        return(
            <div id="newComment" style={{textAlign:"center"}}>
                <input id={"newCommentInput"} style={{padding:"20px 40px"}} type={"text"} onKeyPress={this.sendComment}/>
            </div>
        )
    }

}