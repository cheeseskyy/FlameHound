import React, {Component} from 'react';
import { BrowserRouter as Router} from "react-router-dom";


class About extends Component{

    render(){

        const style = {
            'text-align': 'center',
            width: '84vw'
        }
        return(
        <Router>
            <div style={style} className="Column">
                <h4 id="title">FlameHound</h4>
            </div>
        </Router>



        );
    }
}

export default About;