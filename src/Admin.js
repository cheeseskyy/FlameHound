import React, {Component} from 'react';
import "./Admin.css";

export class AdminArea extends Component{




    render(){
        return(
            <div className={"AdminPage"}>
                <h1>Página de Moderação</h1>
                <Report/>
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

        xhttp.open("POST", "https://my-first-project-196314.appspot.com/rest/rM/getReport/all", true);
        xhttp.setRequestHeader("Content-type", "application/json");

        //Aqui o username vai ter que ser o username admin (pq podem haver contas com username da conta normal diferente do username admin, se calhar guardas outro username no session storage como usernameAdmin ou assim

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
                //Nao sei o que fazer aqui
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

    UsersRow = (props) => {
      return(
          <div className={"TableRow"}>
              <div className={"RowEntry User"}> </div>
              <div className={"RowEntry User"}> </div>
              <div className={"RowEntry User"}> </div>
              <div className={"RowEntry User"}> </div>
              <div className={"RowEntry User"}> </div>
          </div>
      )
    };

    render(){
        return(
            <div className="AdminBox">
                Caixa de Utilizadores
            </div>
        )
    }
}
