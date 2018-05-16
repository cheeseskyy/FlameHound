import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";


class About extends Component{

    render(){

        const style = {
            'text-align': 'center',
            width: '84vw'
        }
        return(

            <div style={style} className="Column">
                <h4 id="title">FlameHound</h4>
            </div>



        );
    }
}

export default About;