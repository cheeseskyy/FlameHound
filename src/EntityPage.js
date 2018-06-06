import React, {Component} from 'react';
import "./EntityPage.css";

class EntityPage extends Component{

    render(){
        return(
          <div className={"EntityPage"}>
              <div id = "coverImage">
                  <img src={this.props.coverImage} alt = {this.props.name + " cover image"}/>
              </div>
              <div id = "nameTitle">
                  {this.props.name}
              </div>
              <div id = "image">
                  <img src={this.props.logo} alt = {this.props.name + " logo"} />
              </div>
              <div id = "statistics">

              </div>
          </div>
        );
    }
}

export default EntityPage;