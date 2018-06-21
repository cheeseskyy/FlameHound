import React, {Component} from 'react';
import "./PerfilPage.css";

class PerfilPage extends Component{

    render(){
        return(
            <div className={"PerfilPage"}>

                <div id = "fotoUser">
                    <img src = "../images/placeholders/user.png"></img>
                </div>

                <div id = "informations">

                </div>

                <div id = "statistics">

                </div>

            </div>
        );
    }
}

export default PerfilPage;