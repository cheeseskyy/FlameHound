import React, {Component} from 'react';

class SubmitOcccurrence extends Component{
    render() {
        return (
            <div>
                <p>Please fill in the form below to submit an occurrence</p>

                <label for="title">
                    <b>Title</b>
                </label>
                <input type="text" placeholder="Enter a title" id="title" required/>
                    <br/>

                        <label for="description">
                            <b>Description</b>
                        </label>
                        <input type="text" placeholder="Describe the occurrence" id="description" required/>
                            <br/>

                                <label for="location">
                                    <b>Location</b>
                                </label>
                                <input type="text" placeholder="Enter the location" id="location" required/>
                                    <br/>

                                        <label for="type">
                                            <b>Location</b>
                                        </label>
                                        <input type="text" placeholder="Enter the type" id="type" required/>
                                            <br/>

                                                <input type="file" placeholder="Submit an image" id="image" required/>
                                                    <br/>

                                                        <button type="button" onclick="saveOc()">Submit</button>
                                                        <br/>
            </div>
    )
    }
    }

    export default SubmitOcccurrence;