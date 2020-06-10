import axios from 'axios'

const axios_instance = axios.create({
    baseURL: 'http://gateway:8080',
    headers: {
        "Origin": "http://frontend",
        "Access-Control-Allow-Origin": "*"
    }
});

export default axios_instance;
