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

        xhttp.open("POST", "https://my-first-project-196314.appspot.com//rest/rM/getReport/all", true);
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
          <div className={"TableEntry"}>
              <div className={"EntryInfo Report"}>{props.id}</div>
              <div className={"EntryInfo Report"}>{props.reporter}</div>
              <div className={"EntryInfo Report"}>{props.reported}</div>
              <div className={"EntryInfo Report"}>{props.description}</div>
              <div className={"EntryInfo Report"}>{props.ocID}</div>
          </div>
      )
    };

    render(){
        return(
            <div className="AdminBox">
                {this.state.reports.map(report => {
                    return(
                        <this.ReportRow key={report} id = {report.reportId} reporter = {report.reporterInfo} reported = {report.reportedInfo} ocID = {report.ocID} description={report.description}/>
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

        xhttp.open("POST", "https://my-first-project-196314.appspot.com//rest/UM/getUsers/all", true);
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
          <div className={"TableEntry"}>
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

class Logs extends Component{

    LogsRow = (props) => {
        return(
            <div className={"TableEntry"}>
                <div className={"RowEntry Log"}> {props.user}</div>
                <div className={"RowEntry LastEntry Log"}> {props.user}</div>
            </div>
        )
    };

    render(){
        return(
            <div className="AdminBox">
                {this.state.logs.map(user => {
                    return(
                        <this.LogsRow/>
                    )
            })}
            </div>
        )
    }

}

class Occurrences extends Component{

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
                {this.state.logs.map(user => {
                    return(
                        <this.OccurrencesRow/>
                    )
                })}
            </div>
        )
    }

}
