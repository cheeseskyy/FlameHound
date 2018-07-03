import React, {Component} from 'react';
import {Switch, Route} from 'react-router-dom';
import "./Admin.css";

export class AdminArea extends Component{




    render(){
        return(
            <div className={"AdminPage"}>
                <h1>Página de Moderação</h1>
                <Switch>
                    <Route path={"/admin/reports"}>
                        <Report/>
                    </Route>
                    <Route path={"/admin/users"}>
                        <Users/>
                    </Route>
                    <Route path={"/admin/logs"}>

                    </Route>
                    <Route path={"/admin/occurrences"}>

                    </Route>
                </Switch>
            </div>
        )
    }

}

export class Report extends Component{

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

        xhttp.open("POST", "/rest/rM/getReport/all", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsernameAdmin');
        var token = sessionStorage.getItem('sessionTokenAdmin');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
                console.log("Got Reports");
                const reports = JSON.parse(xhttp.response);
                console.log("Response in function: ");
                console.log(JSON.parse(xhttp.response));
                this.setState({reports: reports});
            }
        };
    }

    ReportRow = (props) => {
      return (
          <div className={"TableRow"}>
              <div className={"RowEntry Report"}>{props.id}</div>
              <div className={"RowEntry Report"}>{props.reporter}</div>
              <div className={"RowEntry Report"}>{props.reported}</div>
              <div className={"RowEntry LastEntry Report"}>{props.ocID}</div>
          </div>
      )
    };

    render(){
        return(
            <div className="AdminBox">
                {this.state.reports.map(report => {
                    return(
                        <this.ReportRow id = {report.reportId} reporter = {report.reporterInfo} reported = {report.reportedInfo} ocID = {report.ocID}/>
                    )
                })}
                <this.ReportRow id = "asjdasd" reporter = "cheese" reported = "your mom" ocID="12345"/>
            </div>
        )
    }
}

export class Users extends Component{

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

        xhttp.open("POST", "/rest/UM/getUsers/all", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        var username = sessionStorage.getItem('sessionUsernameAdmin');
        var token = sessionStorage.getItem('sessionTokenAdmin');
        var jSonObj = JSON.stringify({"username": username, "tokenId": token});
        xhttp.send(jSonObj);
        xhttp.onreadystatechange = () =>  {
            if (xhttp.readyState == 4 && xhttp.status == 200) {
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
          <div className={"TableRow"}>
              <div className={"RowEntry User"}> {props.user}</div>
              <div className={"RowEntry LastEntry User"}> {props.user}</div>
          </div>
      )
    };

    render(){
        return(
            <div className="AdminBox">
                Caixa de Utilizadores
                {this.state.users.map(user => {
                    return (
                        <this.UsersRow user={user.user_name} email={user.email}/>
                    )
                })}
            </div>
        )
    }
}
