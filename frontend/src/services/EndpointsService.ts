import axios_instance from "../custom-axios/axios";
import {AxiosResponse} from "axios";
import SimpleEndpoint from "../models/SimpleEndpoint";
import Endpoint from "../models/Endpoint";
import EndpointStatus from "../models/EndpointStatus";

class EndpointsService {

    static getAllEndpoints(): Promise<AxiosResponse<SimpleEndpoint[]>> {
        return axios_instance.get(`endpoints`);
    }

    static registerEndpoint(endpointURL: { url: string, email?: string }) {
        return axios_instance.post(`register`, endpointURL)
    }

    static getEndpoint(url: string): Promise<AxiosResponse<Endpoint>> {
        return axios_instance.get(`availability?url=${url}`)
    }

    static getEndpointStatus(url: string): Promise<AxiosResponse<EndpointStatus>> {
        return axios_instance.get(`status?url=${url}`)
    }

    static getOverviewStatus(): Promise<AxiosResponse<EndpointStatus[]>> {
        return axios_instance.get(`status/overview`)
    }
}

export default EndpointsService;
