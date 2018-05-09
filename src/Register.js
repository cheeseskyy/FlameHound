import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";

class RegisterForm extends Component{
    render(){
        return(
            <Router>
                <div>
                    <p>Please fill in the form below to submit an occurrence</p>

                    <label htmlFor="title">
                    <b>Title</b>
                    </label>
                    <input type="text" placeholder="Enter a title" id="title" required></input>
                    <br></br>
                    <label htmlFor="description">
                    <b>Description</b>
                    </label>
                    <input type="text" placeholder="Describe the occurrence" id="description" required></input>
                    <br></br>

                    <label htmlFor="location">
                    <b>Location</b>
                    </label>
                    <input type="text" placeholder="Enter the location" id="location" required></input>
                    <br></br>

                    <input type="file" placeholder="Submit an image" id="image" required/>
                    <br></br>

                    <button type="button" onClick="register()">Submit</button>
                    <br></br>
               </div>
           </Router>
        );
    }
}

export default RegisterForm;