import React, {Component} from 'react';
import {Switch, Route, Link, withRouter} from 'react-router-dom';
import "./Admin.css";
import {OccurrencePreview} from "./Occurrences";

export class AdminArea extends Component{

    role = "MODERATOR";

    verifyAdmin(){
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/validLogin", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsernameAdmin');
        var token = sessionStorage.getItem('sessionTokenAdmin');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                if(xhttp.status === 200) {
                    console.log("Correct user logged in with role " + xhttp.response);
                    this.role = xhttp.response;
                }
                else{
                    alert("Não tem permissão para aceder a esta área, a redireccionar...")
                    this.props.history.goBack();
                }
            }
        };
    }

    render(){

        this.verifyAdmin();

        return(
            <div className={"AdminPage"}>
                <h1>Página de Moderação</h1>
                <Switch>
                    <Route path={"/admin/reports"}>
                        <Report/>
                    </Route>
                    <Route path={"/admin/users/create/admin"}>
                        {withRouter(CreateAdminForm)}
                    </Route>
                    <Route path={"/admin/users/create/moderator"}>
                        {withRouter(CreateModeratorForm)}
                    </Route>
                    <Route path={"/admin/users"}>
                        <Users/>
                    </Route>
                    <Route path={"/admin/logs"}>
                        <Logs/>
                    </Route>
                    <Route path={"/admin/occurrences"}>
                        <Occurrences/>
                    </Route>
                </Switch>
            </div>
        )
    }

}

class Report extends Component{

    constructor(props){
        super(props);
        this.state = {
            reports: []
        }
    }

    componentDidMount(){
        this.getReports();
    }

    getReports() {
        console.log("Getting all reports");
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/rM/getReport/all", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsernameAdmin');
        var token = sessionStorage.getItem('sessionTokenAdmin');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                console.log("Got Reports");
                const reports = JSON.parse(xhttp.response);
                console.log("Response in function: ");
                console.log(JSON.parse(xhttp.response));
                this.setState({reports: reports});
            }
        };
    }

    deleteOc(id){
        console.log("removing occurrence with ID: " + id);
    }

    ReportRow = (props) => {
      return (
          <div className={"TableEntry"}>
              <div className={"EntryInfo Report"}>{props.id}</div>
              <div className={"EntryInfo Report"}>{props.reporter}</div>
              <div className={"EntryInfo Report"}>{props.reported}</div>
              <div className={"EntryInfo Report"}>{props.description}</div>
              <div className={"EntryInfo Report"}>{props.ocID}</div>
              <button onClick={() => {this.deleteOc(props.id)}}>Apagar Ocorrência</button>
              <button>Rejeitar report</button>
          </div>
      )
    };

    render(){
        return(
            <div className="AdminBox">
                {this.state.reports.map(report => {
                    return(
                        <div className={"TableEntry"}>
                            <div className={"EntryInfo Report"}>{report.reportId}</div>
                            <div className={"EntryInfo Report"}>{report.reporter}</div>
                            <div className={"EntryInfo Report"}>{report.reported}</div>
                            <div className={"EntryInfo Report"}>{report.description}</div>
                            <div className={"EntryInfo Report"}>{report.ocID}</div>
                            <button onClick={() => this.deleteOc(report.ocID)}>Apagar Ocorrência</button>
                            <button>Rejeitar report</button>
                        </div>
                    )
                })}
            </div>
        )
    }
}

class Users extends Component{

    constructor(props){
        super(props);
        this.state = {
            users: []
        }
    }

    componentDidMount(){
        this.getUsers();
    }

    getUsers() {
        console.log("Getting all users");
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/UM/getUsers/all", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsernameAdmin');
        var token = sessionStorage.getItem('sessionTokenAdmin');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                console.log("Got Users");
                const users = JSON.parse(xhttp.response);
                console.log("Response in function: ");
                console.log(JSON.parse(xhttp.response));
                this.setState({users: users});
            }
        };
    }

    UsersRow = (props) => {
      return(
          <div className={"TableEntry"}>
              <div className={"RowEntry User"}> <p>{props.user}</p></div>
              <div className={"RowEntry User"}> <p>{props.name}</p></div>
              <div className={"RowEntry LastEntry User"}> <p>{props.email}</p></div>
          </div>
      )
    };

    render(){
        return(
            <div style={{height: "100%"}}>
                <div className="AdminBox">
                    Caixa de Utilizadores

                    {this.state.users.map(user => {
                        return (
                            <this.UsersRow key={user} user={user.username} email={user.email} name={user.name}/>
                        )
                    })}
                    <div>
                        <Link to={"/admin/users/create/moderator"} style={{width: "50%"}}>Criar Moderador</Link>
                        <Link to={"/admin/users/create/admin"} style={{width: "50%", float: "right"}}>Criar Administrador</Link>
                    </div>
                </div>
            </div>
        )
    }
}

class Logs extends Component{

    constructor(props){
        super(props);
        this.state = {
            logs: ""
        }

    }

    componentDidMount(){
        this.getLogs();
    }

    getLogs() {
        console.log("Getting all logs");
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/getLogs", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsernameAdmin');
        var token = sessionStorage.getItem('sessionTokenAdmin');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("Got logs");
                const logs = JSON.parse(xhttp.response);
                console.log("Response in function: ");
                console.log(JSON.parse(xhttp.response));
                this.setState({logs: logs.message});
            }
        };
    }

    LogsRow = (props) => {
        return(
            <div className={"TableEntry"}>
                <div className={"Log"}> {props.logText}</div>
            </div>
        )
    };

    render(){
        return(
            <div className="AdminBox" style={{backgroundColor:"white"}}>
                <br/>
                {this.state.logs}
            </div>
        )
    }

}

class Occurrences extends Component{

    constructor(props){
        super(props);
        this.state ={
            ocs: []
        }
    }

    componentDidMount(){
        this.getOcs();
    }

    getOcs() {
        console.log("Getting all occurrences");
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/occurrency/getOccurrency/all", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("Got occurrences");
                const ocs = JSON.parse(xhttp.response);
                console.log("Response in function: ");
                console.log(JSON.parse(xhttp.response));
                this.setState({ocs: ocs});
            }
        };
    }

    OccurrencesRow = (props) => {
        return(
            <div className={"TableEntry"}>
                <div className={"RowEntry Occurrences"}> {props.user}</div>
                <div className={"RowEntry LastEntry Occurrences"}> {props.user}</div>
            </div>
        )
    };

    render(){
        return(
            <div className="AdminBox">
                {this.state.ocs.map(occurrence => {
                    return(
                        <div>
                            <OccurrencePreview key={occurrence.id} id={occurrence.id} title={occurrence.title} user={occurrence.user}
                                               description={occurrence.description}
                                               image={occurrence.mediaURI[0]}
                            />
                            <button>Confirmar Ocorrência</button>
                            <button>Remover Ocorrência</button>
                        </div>
                    )
                })}
            </div>
        )
    }

}

class CreateModeratorForm extends Component{

    sendModerator() {
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/addModerator");
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsernameAdmin');
        var token = sessionStorage.getItem('sessionTokenAdmin');
        var jSonObj = JSON.stringify({
            "username": document.getElementById("username").value,
            "password": document.getElementById("psw").value,
            "entity": document.getElementById("entity").value,
            "registerUsername": username,
            "tokenId": token
        });
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    alert("Moderador " + jSonObj.username + " registado. A redireccionar...");
                    this.props.history.goBack();
                } else if(xhttp.status > 200) {
                    alert("Ocorreu um erro, verifique a informação e tente novamente.");
                    console.log("Error: " + xhttp.status);
                }
            }
        }
    }


    render(){
        return(
          <div>
              <p>Nome de Utilizador:</p>
              <input id={"username"} type="text" placeholder="Nome de Utilizador"/>
              <p>Palavra-passe:</p>
              <input id={"psw"} type="password"/>
              <p>Confirme a Palavra-passe</p>
              <input type="password"/>
              <p>Entidade:</p>
              <input id={"entity"} type="text"/>
              <button onClick={() => this.sendModerator()}>Criar</button>
          </div>
        );
    }
}

class CreateAdminForm extends Component{

    sendAdmin() {
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/addAdmin");
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsernameAdmin');
        var token = sessionStorage.getItem('sessionTokenAdmin');
        var jSonObj = JSON.stringify({
            "username": document.getElementById("username").value,
            "password": document.getElementById("psw").value,
            "registerUsername": username,
            "tokenId": token
        });
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    alert("Moderador " + jSonObj.username + " registado. A redireccionar...");
                    this.props.history.goBack();
                } else if(xhttp.status > 200) {
                    alert("Ocorreu um erro, verifique a informação e tente novamente.");
                    console.log("Error: " + xhttp.status);
                }
            }
        }
    }

    render(){
        return(
            <div>
                <p>Nome de Utilizador:</p>
                <input id={"username"} type="text" placeholder="Nome de Utilizador"/>
                <p>Palavra-passe:</p>
                <input id={"psw"} type="password"/>
                <p>Confirme a Palavra-passe</p>
                <input type="password"/>
                <button onClick={() => this.sendAdmin()}>Criar</button>
            </div>
        )
    }
}