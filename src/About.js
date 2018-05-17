import React, {Component} from 'react';
import { BrowserRouter as Router} from "react-router-dom";
import pick1 from "./imagens/colegas.png";

class About extends Component{

    render(){

        const style = {
            'text-align': 'center',
            width: '84vw'
        }
        return(
        <Router>
            <div style={style} className="Column">
                <h1>FlameHound</h1>
                <br/>

                <div id ="text">
                    <p> Somos uma equipa que ao longo de vários meses, aceitou o desafio de desafio de desenvolver um site web com uma componente móvel para indicação das zonas a limpar com uma forte componente social.</p>
                    <p> Todos nós sabemos como os incêndios assolaram Portugal no ano de 2017, com um registo mortal enorme, nunca antes visto, neste tipo de ocorrências.</p>
                    <p> Como tal, o nosso objectivo é erradicar estes acontecimentos, nunca os esquecendo mas sim utilizando-os como motor para o desenvolvimento deste projecto.</p>
                    <p> Uma actuação rápida, sem burocracia, e de cómodo uso.</p>
                    <p> Deste modo promovemos uma nova ferramenta que entidades podem utilizar ou mesmo, no futuro, promover.</p>
                </div>
                <br/>
                <img src={pick1}/>
            </div>
        </Router>



        );
    }
}

export default About;