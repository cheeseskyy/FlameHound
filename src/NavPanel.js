import React, {Component} from 'react';

class NavPanel extends Component{
    render(){
        return(

            <div className={this.props.class} id="NavPanel">
              <div className="title">
                  <center><img src={"imagens/Mock-up Logo.PNG"} alt="company logo" width={150} height={150}/></center>
                  <h1>FlameHound!</h1>
                  <p>Conseguimos farejar os fogos!</p>
              </div>
              <nav>
                  <p>Login Here <button> Login </button> </p>

                  <p>Create an account?</p>
                  <a href="#" onClick="console.log('The link was clicked.'); return false">
                      Click me
                  </a>
              </nav>
            </div>

        );
    }
}

export default NavPanel;
