import React from "react";
import "./DrawChart.css"
import ChartistGraph from "react-chartist";
import {DrawData} from "../Endpoints/EndpointsApp";
import {LineData} from "../Endpoints/EndpointDetails/EndpointDetails";

interface DrawChartProps {
    type: string,
    data: DrawData | LineData | undefined
}

const DrawChart = (props: DrawChartProps) => {

    const options = {
        labelInterpolationFnc: function (value: string, idx: number) {
            // @ts-ignore
            // eslint-disable-next-line @typescript-eslint/no-unused-expressions
            const temp = Object.values(props.data?.series[idx])[0];
            // @ts-ignore
            const length = props.data.series.map(s => s.value).reduce((sum, current) => sum + current, 0);
            // @ts-ignore
            const result = Math.round(temp / length * 100) + '%';
            // @ts-ignore
            if (temp > 0) {
                return value + "\t" + result;
            } else {
                return "";
            }
        }
    };

    const lineOption = {
        high: 100,
        low: 0,
        showArea: true,
        height: 500
    }

    if (props.data) {
        if (props.type === "Pie") {
            return (
                <ChartistGraph style={{height: "500px"}}
                               type={props.type}
                               data={props.data}
                               options={options}
                />
            );
        } else if (props.type === "Line") {
            return (
                <div className="container-fluid w-75">
                    <ChartistGraph
                        style={{height: "500px", width: "1500px"}}
                        type={props.type}
                        data={props.data}
                        options={lineOption}
                    />
                </div>
            );
        } else {
            return (
                <div>No mapping</div>
            );
        }
    } else {
        return <div>Data not yet defined!</div>
    }
}

export default DrawChart;
