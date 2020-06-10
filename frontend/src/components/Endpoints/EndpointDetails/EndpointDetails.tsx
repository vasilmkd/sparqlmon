import React, {useEffect, useState} from "react";
import {useParams} from "react-router";
import Endpoint from "../../../models/Endpoint";
import EndpointsService from "../../../services/EndpointsService";
import {EndpointHistory} from "../../../models/EndpointHistory";
import DrawChart from "../../DrawChart/DrawChart";
import moment from "moment";
import ReducedEndpoint from "../../../models/ReducedEndpoint";

export interface LineData {
    labels: string[],
    series: number[][]
}

const EndpointDetails = () => {

    const {url} = useParams();

    const [endpoint, setEndpoint] = useState<Endpoint>();
    const [data, setData] = useState<LineData | undefined>(undefined);
    const [dataDays, setDataDays] = useState<LineData | undefined>(undefined);
    const [dataAll, setDataAll] = useState<LineData | undefined>(undefined);

    useEffect(() => {
        EndpointsService.getEndpoint(decodeURIComponent(url)).then(response => {
            setEndpoint(response.data);
            const history: EndpointHistory[] = response.data.history;
            let groupedByDay: ReducedEndpoint[] = [];
            history.forEach(endpoint => {
                const date = endpoint.instant.split('T')[0];
                if (groupedByDay.some(el => el.day === date)) {
                    const element = groupedByDay.find(el => el.day === date);
                    if (element !== undefined) {
                        if (endpoint.up) {
                            element.upTime += 1
                        } else {
                            element.downCounter += 1
                        }
                    }
                } else {
                    groupedByDay.push(new ReducedEndpoint(date));
                }
            })
            const seriesDays = groupedByDay.slice(-15).map(el => {
                return el.upTime / (el.upTime + el.downCounter) * 100
            });
            const seriesAll = groupedByDay.map(el => {
                return el.upTime / (el.upTime + el.downCounter) * 100
            });
            const labels = history.slice(-12).map(el => moment(el.instant).format("HH:mm DD MMM"));
            const labelsDays = groupedByDay.slice(-15).map(el => moment(el.day).format("DD MMM"));
            const labelsAll = groupedByDay.map(el => moment(el.day).format("DD MMM"));
            // TODO
            // let series = [];
            // for (let i = 0; i < 12 && i < labels.length; i++) {
            //     series.push(Math.random() * 100)
            // }
            const series = history.slice(-12).map(el => el.up ? 100 : 0);
            const data = {
                labels: labels,
                series: [series]
            }
            setData(data);

            const dataDays = {
                labels: labelsDays,
                series: [seriesDays]
            }
            setDataDays(dataDays);

            const dataAll = {
                labels: labelsAll,
                series: [seriesAll]
            }
            setDataAll(dataAll);
        })
    }, [url])

    return (
        <div>
            <h2>Endpoint details</h2>
            <h3>
                Sparql endpoint url <a href={endpoint?.url} target="_blank" rel="noopener noreferrer">{endpoint?.url}</a>
            </h3>
            <div className="m-5">
                <h3>Availability in the last 3 hours</h3>
                <h5>15 minutes interval</h5>
                <DrawChart type={"Line"} data={data}/>
            </div>
            <div className="m-5">
                <h3>Availability in the last 15 days</h3>
                <DrawChart type={"Line"} data={dataDays}/>
            </div>
            <div className="m-5">
                <h3>Availability history</h3>
                <DrawChart type={"Line"} data={dataAll}/>
            </div>
        </div>
    );
};

export default EndpointDetails;
