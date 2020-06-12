import React, {useEffect, useState} from "react";
import "./EndpointApp.css"
import EndpointsService from "../../services/EndpointsService";
import DrawChart from "../DrawChart/DrawChart";
import EndpointStatus from "../../models/EndpointStatus";
import moment from "moment";
import {Link} from "react-router-dom";

export interface DrawData {
    labels: string[],
    series: {}[]
}

const EndpointsApp = () => {

    const [endpoints, setEndpoints] = useState<EndpointStatus[]>([]);

    const [data, setData] = useState<DrawData | undefined>(undefined);

    useEffect(() => {
        EndpointsService.getOverviewStatus().then(response => {
            const endpointsStatus = response.data;
            setEndpoints(endpointsStatus);
            const upEndpoints = endpointsStatus.filter(e => e.status.up).length;

            const downEndpoints = endpointsStatus.length - upEndpoints;

            const availabilityData: DrawData = {
                labels: ["Up Endpoints", "Down Endpoints"],
                series: [{value: upEndpoints, className: "green"}, {value: downEndpoints, className: "red"}]
            };
            setData(availabilityData);
        })
    }, [])

    let renderList = (
        <table className="table table-hover container-fluid w-75">
            <thead className="thead-dark">
            <tr>
                <th scope="col">Endpoint URL</th>
                <th>Last check for availability</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            {endpoints.map(endpoint => {
                return <tr key={endpoint.url} className={(endpoint.status.up && "table-success") || "table-danger"}>
                    <td>
                        <Link to={`/endpoints/${encodeURIComponent(endpoint.url)}`} className="link"
                              style={{color: "black"}}>
                            {endpoint.url}
                        </Link>
                    </td>
                    <td>
                        {moment(endpoint.status.instant,).format("HH:mm:ss DD MMM yyyy")}
                    </td>
                    <td>
                        {(endpoint.status.up && "UP") || "DOWN"}
                    </td>
                </tr>
            })}
            </tbody>
        </table>
    );

    if (data && endpoints.length > 0) {
        return (
            <div className="row">
                <div className="col-6">
                    <h1 className="card-title">Availability</h1>
                    <DrawChart type={"Pie"} data={data}/>
                    <div className="card-text">
                        Total registered endpoints: {endpoints.length}
                    </div>
                </div>
                <div className="col-6">
                    <h3>List of endpoints</h3>
                    {renderList}
                </div>
            </div>
        );
    } else {
        return (
            <div className="m-5">
                <h1>
                    No data!
                </h1>
                <p>
                    Please register new endpoint.
                </p>
            </div>


        );
    }
};

export default EndpointsApp
