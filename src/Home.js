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
                         ​​​​​​<p>A nossa missão é facilitar a <b>Prevenção de Incêndios</b>, ajudar o país e as pessoas e contribuir para que o sistema funcione melhor para todos nós.</p>
                            <div style={style}>
                                ​​​​​​<ul><b>Integridade:</b> Honrar compromissos.</ul>
                                <ul><b>Compreensão:</b> Pomo-nos na pele dos nossos clientes e dos nossos colaboradores.</ul>
                                <ul><b>Relacionamento​:</b> Construir confiança apostando na colaboração.</ul>
                                <ul><b>Inovação:</b> Inventar o futuro, aprender com o passado.​</ul>
                                <ul><b>Desempenho:</b> Demonstrar excelência em tudo o que fazemos.</ul>
                            </div>
                    </div>
                    <br/>
                    <h2 id = "title1">Com a sua ajuda, conseguimos farejar os fogos e desta forma prevenir!</h2>
                    <br/>
                </div>
            </div>



      );
    }
}

export default Home;