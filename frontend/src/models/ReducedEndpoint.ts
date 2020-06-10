class ReducedEndpoint {
    day: string
    upTime: number = 0
    downCounter: number = 0

    constructor(day: string) {
        this.day = day
    }
}

export default ReducedEndpoint;
