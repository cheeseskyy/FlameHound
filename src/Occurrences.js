import React, {Component} from 'react';
import Occurrence from "./Occurrence";


class Occurrences extends Component {

    render(){
        return(
          <div id="Occurrences" className="Column">
              Occurrences Panel
              <ul>
                  <li><Occurrence/></li>
                  <li><Occurrence/></li>
                  <li><Occurrence/></li>
                  <li><Occurrence/></li>
                  <li><Occurrence/></li>
              </ul>
          </div>
        );
    }
}

export default Occurrences;