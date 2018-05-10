import React, {Component} from 'react';

class Occurrence extends Component{

    render(){
        const title = this.props.title;
        const user = this.props.user;
        const location = this.props.location;
        const description = this.props.description;
        const media = this.props.media;

        return(
          <h1>Isto é uma Ocorrência</h1>
        );
    }
}

export default Occurrence