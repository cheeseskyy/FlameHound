import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";


class Home extends Component{

    render(){

        const style = {
            'text-align': 'center',
            width: '84vw'
        }
      return(

            <div style={style} className="Column">
                <h4 id="title">FlameHound</h4>
                <p align="bottom">Todos nós sabemos como os incêndios assolaram Portugal no ano de 2017, com um registo mortal enorme, nunca antes visto, neste tipo de ocorrências.
                    O nosso objectivo é erradicar estes acontecimentos, nunca os esquecendo mas sim utilizando-os como motor para o desenvolvimento deste projecto. Uma actuação rápida, sem burocracia, e de cómodo uso.
                    Uma aplicação/página web, que promova o sentido comunitário, a entre ajuda na prevenção e na luta contra o desflorestamento do nosso país.
                    Deste modo promovemos uma nova ferramenta que entidades podem utilizar ou mesmo, no futuro, promover.
                </p>
                </div>



      );
    }
}

export default Home;