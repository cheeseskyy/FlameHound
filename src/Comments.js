import React, {Component} from 'react';
import "./Comments.css";

export class CommentList extends Component{

    render(){
        return(
            <div className="CommentBox">
                <CommentBox text={"placeholding all the stuff!"}/>

                <CommentBox text={"Another placeholder"}/>
            </div>
        )
    };

}

export class CommentBox extends Component{

    render(){
        return(
            <div>
                {this.props.text}
            </div>
        )
    };

}