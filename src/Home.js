import React, {Component} from 'react';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";


class Home extends Component{

    render(){
        const style = {
            width: '84%',


        };
      return(

          <div style={style} className="Column">
              <div align="center">
                  <h1 align="center">FlameHound</h1>
                  <br/>
              </div>
              <br/>
              <div style={style} id="text">
                  <ul>​​​​​​A nossa missão é facilitar a <b>Prevenção de Incêndios</b>, ajudar o país e as pessoas e
                      contribuir para que o sistema funcione melhor para todos nós.
                  </ul>
                  ​​​​​​
                  <ul><b>Integridade:</b> Honrar compromissos.</ul>
                  <ul><b>Compreensão:</b> Colocarmo-nos na pele dos nossos clientes/utilizadores.</ul>
                  <ul><b>Relacionamento​:</b> Construir confiança, apostando na colaboração.</ul>
                  <ul><b>Inovação:</b> Inventar o futuro e aprender com o passado.​</ul>
                  <ul><b>Desempenho:</b> Demonstrar excelência em tudo o que fazemos.</ul>

              </div>
              <br/>

              <h2 id="title1">Com a sua ajuda, conseguimos farejar os fogos e desta forma prevenir!</h2>
              <br/>
          </div>




      );
    }
}

export default Home;