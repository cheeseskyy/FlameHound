import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";


class Home extends Component{

    render(){

        const style = {
            'text-align': 'center',
            width: '84vw',
            whiteSpace: 'nowrap'

        };
        const estilo ={
            display: 'inline-block'
        };
      return(

            <div style={style} className="Column">
                <div style={estilo}>
                    <h1>FlameHound</h1>

                    <div id ="text">
                        <p> Todos nós sabemos como os incêndios assolaram Portugal no ano de 2017, com um registo mortal enorme, nunca antes visto, neste tipo de ocorrências.</p>
                        <p> O nosso objectivo é erradicar estes acontecimentos, nunca os esquecendo mas sim utilizando-os como motor para o desenvolvimento deste projecto.</p>
                        <p> Uma actuação rápida, sem burocracia, e de cómodo uso.</p>
                        <p> Uma aplicação/página web, que promova o sentido comunitário, a entre ajuda na prevenção e na luta contra o desflorestamento do nosso país.</p>
                        <p> Deste modo promovemos uma nova ferramenta que entidades podem utilizar ou mesmo, no futuro, promover.</p>
                    </div>

                    <br/>
                    <h2 id = "title1">Com a sua ajuda, conseguimos farejar os fogos!</h2>
                    <br/>
                </div>
            </div>



      );
    }
}

export default Home;