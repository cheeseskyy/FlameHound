import React, {Component} from 'react';
import Occurrence from "./Occurrence";


class OccurrenceList extends Component {

    render(){
        const style = {
            cssFloat: 'right'
        }
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

export default OccurrenceList;