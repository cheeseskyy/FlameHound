import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";


class Home extends Component{

    render(){

        const style = {
            'text-align': 'center',
            width: '84vw'
        }
      return(
          <div style={style} className="Column">
              <p id="title">FlameHound</p>
          </div>

      );
    }
}

export default Home;