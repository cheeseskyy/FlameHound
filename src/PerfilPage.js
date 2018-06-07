import React, {Component} from 'react';
import "./PerfilPage.css";

class PerfilPage extends Component{

    render(){
        return(
            <div className={"PerfilPage"}>
                {/* div separada para titulo e assim. para se alinhar tudo*/}
                <div id= "title">
                    <div id = "coverImage">
                        <img src={this.props.coverImage} alt = {this.props.name + "'s cover image"}/>
                    </div>
                    <div id = "entityLogo">
                        <img src={this.props.logo} alt = {this.props.name + "'s logo"} />
                    </div>
                    <div id = "nameTitle">
                        {this.props.name}
                    </div>
                </div>
                <div id = "statistics">

                </div>
            </div>
        );
    }
}

export default EntityPage;