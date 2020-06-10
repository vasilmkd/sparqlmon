export class EndpointHistory {
    instant: string
    up: boolean

    constructor(instant: string, up: boolean) {
        this.instant = instant;
        this.up = up;
    }
}

