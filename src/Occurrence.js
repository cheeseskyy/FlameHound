import React, {Component} from 'react';

class Occurrence extends Component{

    render(){
        const title = this.props.title;
        const user = this.props.user;
        const location = this.props.location;
        const description = this.props.description;
        const media = this.props.media;

        return(<div>
          Isto é uma Ocorrência
            </div>
        );
    }
}

export default Occurrence