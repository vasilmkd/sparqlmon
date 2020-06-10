class EndpointStatus {
    url: string;
    status: {instant: string, up: boolean}

    constructor(url: string, status: {instant: string, up: boolean}) {
        this.url = url;
        this.status = status;
    }
}

export default EndpointStatus;
