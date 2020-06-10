import {EndpointHistory} from "./EndpointHistory";

class Endpoint {
    url: string
    history: Array<EndpointHistory>

    constructor(url: string) {
        this.url = url
        this.history = new Array<EndpointHistory>()
    }

    addHistory(history: EndpointHistory) {
        this.history.push(history);
    }

    static getLastHistory(endpoint: Endpoint) {
        const lastUpdate = endpoint.history.pop()
        if (lastUpdate !== undefined) {
            endpoint.history.push(lastUpdate);
            return lastUpdate.up;
        }
        return false;
    }
}

export default Endpoint;
