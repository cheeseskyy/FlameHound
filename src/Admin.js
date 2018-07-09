import React, {Component} from 'react';
import {Switch, Route, Link, withRouter} from 'react-router-dom';
import "./Admin.css";
import {OccurrencePreview} from "./Occurrences";

export class AdminArea extends Component{

    role = "MODERATOR";

    getRole() {
        console.log("called getRole");
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/user/getRole", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);

        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200) {
                    const role = JSON.parse(xhttp.response);
                    console.log("getRole response: " + role);
                    sessionStorage.setItem("userRole", role);
                    if (role !== "ADMIN" && role !== "MODERATOR") {
                        alert("Não está autorizado a aceder a esta página. A redireccionar...");
                        this.props.history.goBack();
                    }
                } else {
                    alert("A sua sessão expirou. A redireccionar...");
                    this.props.history.push("/");
                }
            }
        }
    }

    verifyAdmin(){
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/validLogin", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                if(xhttp.status === 200) {
                    console.log("Correct user logged in with role " + xhttp.response);
                    this.role = xhttp.response;
                }
                else{
                    alert("Não tem permissão para aceder a esta área, a redireccionar...");
                    this.props.history.goBack();
                }
            }
        };
    }

    componentDidMount(){
        this.getRole();
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
                    <Route path={"/admin/users/create/worker"}>
                        {withRouter(CreateWorkerForm)}
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

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
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
        let xhttp = new XMLHttpRequest();

        xhttp.open("DELETE", "https://my-first-project-196314.appspot.com/rest/oM/delete/" + id, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        const username = sessionStorage.getItem('sessionUsername');
        const token = sessionStorage.getItem('sessionToken');
        const jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200){
                    alert("Ocorrência removida com sucesso");
                } else{
                    console.log("deleteOC error: " + xhttp.status);
                    alert("Ocorreu um erro a remover a ocorrência, tente novamente mais tarde");
                }

            }
        }
    }

    deleteRep(id){
        console.log("removing occurrence with ID: " + id);
        let xhttp = new XMLHttpRequest();

        xhttp.open("DELETE", "https://my-first-project-196314.appspot.com/rest/rM/delete/" + id, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        const username = sessionStorage.getItem('sessionUsername');
        const token = sessionStorage.getItem('sessionToken');
        const jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200){
                    alert("Denúncia removida com sucesso");
                } else{
                    console.log("deleteOC error: " + xhttp.status);
                    alert("Ocorreu um erro a remover a denúncia, tente novamente mais tarde");
                }

            }
        }
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
              <button onClick={() => {}}>Rejeitar report</button>
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

    deleteUser(user){
        console.log("removing user with username: " + user);
        let xhttp = new XMLHttpRequest();

        xhttp.open("DELETE", "https://my-first-project-196314.appspot.com/rest/UM/delete/" + user, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        const username = sessionStorage.getItem('sessionUsername');
        const token = sessionStorage.getItem('sessionToken');
        const jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200){
                    alert("Utilizador removido com sucesso");
                    this.forceUpdate();
                } else{
                    console.log("deleteOC error: " + xhttp.status);
                    alert("Ocorreu um erro a remover o utilizador, tente novamente mais tarde");
                }

            }
        }
    }

    getUsers() {
        console.log("Getting all users");
        var xhttp = new XMLHttpRequest();

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/UM/getUsers/all", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
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
              <div className={"RowEntry User"}> <p>{props.role}</p></div>
              <div className={"RowEntry User"}> <p>{props.user}</p></div>
              <div className={"RowEntry User"}> <p>{props.name}</p></div>
              <div className={"RowEntry LastEntry User"}> <p>{props.email}</p></div>
              <button onClick={() => this.deleteUser(props.user)}>Apagar Utilizador</button>
          </div>
      )
    };

    render(){

        const moderatorLink = () => {
            if(sessionStorage.getItem("userRole") === "ADMIN"){
                return <Link to={"/admin/users/create/moderator"} style={{width: "33%", float: "center"}}>Criar Moderador</Link>;
            }
        };

        return(
            <div style={{height: "100%"}}>
                <div className="AdminBox">
                    Caixa de Utilizadores

                    {this.state.users.map(user => {
                        return (
                            <this.UsersRow key={user} user={user.username} email={user.email} name={user.name} role={user.role}/>
                        )
                    })}
                    <div>
                        <Link to={"/admin/users/create/worker"} style={{width: "34%", float: "left"}}>Criar Trabalhador</Link>
                        {moderatorLink()}
                        <Link to={"/admin/users/create/admin"} style={{width: "33%", float: "right"}}>Criar Administrador</Link>
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
            logs: []
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

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState === 4 && xhttp.status === 200) {
                console.log("Got logs");
                const logs = JSON.parse(xhttp.response);
                console.log("Response in function: ");
                console.log(logs);
                this.setState({logs: logs});
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
                {this.state.logs.map((log, i) => {
                    return <p key={i}>{log}</p>
                })}
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

    deleteOc(id){
        console.log("removing occurrence with ID: " + id);
        let xhttp = new XMLHttpRequest();

        xhttp.open("DELETE", "https://my-first-project-196314.appspot.com/rest/oM/delete/" + id, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        const username = sessionStorage.getItem('sessionUsername');
        const token = sessionStorage.getItem('sessionToken');
        const jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200){
                    alert("Ocorrência removida com sucesso");
                } else{
                    console.log("deleteOC error: " + xhttp.status);
                    alert("Ocorreu um erro a remover a ocorrência, tente novamente mais tarde");
                }

            }
        }
    }

    confirmOc(id, flag){
        if(flag === "solving" || flag === "solved"){
            alert("Ocorrência já está a ser tratada");
            return;
        }
        console.log("removing occurrence with ID: " + id);
        let xhttp = new XMLHttpRequest();

        xhttp.open("PUT", "https://my-first-project-196314.appspot.com/rest/oM/confirm/" + id, true);
        xhttp.setRequestHeader("Content-type", "application/json");

        const username = sessionStorage.getItem('sessionUsername');
        const token = sessionStorage.getItem('sessionToken');
        const jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if (xhttp.readyState === 4) {
                if(xhttp.status === 200){
                    alert("Ocorrência confirmada com sucesso");
                } else{
                    console.log("deleteOC error: " + xhttp.status);
                    alert("Ocorreu um erro a confirmar a ocorrência, tente novamente mais tarde");
                }

            }
        }
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

    confirmOcButton = (id, flag) => {
      if(flag === "unconfirmed"){
          return <button onClick={() => this.confirmOc(id, flag)}>Confirmar Ocorrência</button>;
      }
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
                                               flag={occurrence.flag}
                                               creationDate={occurrence.creationDate} worker={occurrence.worker}
                            />
                            {this.confirmOcButton(occurrence.id, occurrence.flag)}
                            <button onClick={() => this.deleteOc(occurrence.id)}>Remover Ocorrência</button>
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

        const userRegist = document.getElementById("username").value;

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({
            "username": userRegist,
            "name": document.getElementById("name").value,
            "email": document.getElementById("email").value,
            "password": document.getElementById("psw").value,
            "entity": document.getElementById("entity").value,
            "registerUsername": username,
            "tokenId": token
        });
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    alert("Moderador " + userRegist + " registado. A redireccionar...");
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
              <input id={"username"} type="text" placeholder="Utilizador"/>
              <p>Nome:</p>
              <input id={"name"} type={"text"} placeholder={"Nome"}/>
              <p>Email</p>
              <input id={"email"} type={"text"} placeholder={"name@mailservice.com"}/>
              <p>Palavra-passe:</p>
              <input id={"psw"} type="password"/>
              <p>Confirme a Palavra-passe</p>
              <input type="password"/>
              <p>Entidade:</p>
              <input id={"entity"} type="text"/>
              <button onClick={() => this.sendModerator()}>Criar Moderador</button>
          </div>
        );
    }
}

class CreateAdminForm extends Component{

    sendAdmin() {
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/addAdmin");
        xhttp.setRequestHeader("Content-type", "application/json");

        const userRegist = document.getElementById("username").value;

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({
            "username": userRegist,
            "password": document.getElementById("psw").value,
            "name": document.getElementById("name").value,
            "email": document.getElementById("email").value,
            "registerUsername": username,
            "tokenId": token
        });
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    alert("Administrador " + userRegist + " registado. A redireccionar...");
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
                <input id={"username"} type="text" placeholder="Utilizador"/>
                <p>Nome:</p>
                <input id={"name"} type={"text"} placeholder={"Nome"}/>
                <p>Email</p>
                <input id={"email"} type={"text"} placeholder={"name@mailservice.com"}/>
                <p>Palavra-passe:</p>
                <input id={"psw"} type="password"/>
                <p>Confirme a Palavra-passe</p>
                <input type="password"/>
                <button onClick={() => this.sendAdmin()}>Criar Administrador</button>
            </div>
        )
    }
}

class CreateWorkerForm extends Component{

    sendWorker() {
        let xhttp = new XMLHttpRequest();
        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/_be/_admin/addWorker");
        xhttp.setRequestHeader("Content-type", "application/json");

        const userRegist = document.getElementById("username").value;

        var username = sessionStorage.getItem('sessionUsername');
        var token = sessionStorage.getItem('sessionToken');
        var jSonObj = JSON.stringify({
            "username": userRegist,
            "password": document.getElementById("psw").value,
            "entity": document.getElementById("entity").value,
            "name": document.getElementById("name").value,
            "email": document.getElementById("email").value,
            "registerUsername": username,
            "tokenId": token
        });
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () => {
            if(xhttp.readyState === 4){
                if(xhttp.status === 200){
                    alert("Trabalhador " + userRegist + " registado. A redireccionar...");
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
                <p>Utilizador:</p>
                <input id={"username"} type="text" placeholder="Utilizador"/>
                <p>Nome:</p>
                <input id={"name"} type={"text"} placeholder={"Nome"}/>
                <p>Email</p>
                <input id={"email"} type={"text"} placeholder={"name@mailservice.com"}/>
                <p>Palavra-passe:</p>
                <input id={"psw"} type="password"/>
                <p>Confirme a Palavra-passe</p>
                <input type="password"/>
                <p>Entidade:</p>
                <input id={"entity"} type="text"/>
                <button onClick={() => this.sendWorker()}>Criar Trabalhador</button>
            </div>
        );
    }
}