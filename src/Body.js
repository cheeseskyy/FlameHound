import React, {Component} from 'react';
import MapView from './MapView';
import NavPanel from "./NavPanel";

import './Body.css';


class Body extends Component{

    render(){
        return(
          <div>
              <NavPanel class = "Column"/>
              <MapView class = "Column"/>
          </div>
        );
    }
}

export default Body;