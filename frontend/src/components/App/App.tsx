import React, {useEffect, useState} from 'react';
import './App.css';
import EndpointsApp from "../Endpoints/EndpointsApp";
import Header from "../Header/Header";
import {Route, Switch} from "react-router";
import {BrowserRouter} from "react-router-dom";
import EndpointAdd from "../Endpoints/EndpointAdd/EndpointAdd";
import EndpointDetails from "../Endpoints/EndpointDetails/EndpointDetails";
import EndpointsService from "../../services/EndpointsService";
import SimpleEndpoint from "../../models/SimpleEndpoint";

function App() {

    const [endpointURLs, setEndpointsURLs] = useState<SimpleEndpoint[]>([]);

    useEffect(() => {
        EndpointsService.getAllEndpoints().then(response => {
            setEndpointsURLs(response.data);
        })
    }, []);

    const registerEndpoint = (endpoint: {url: string, email?: string}, onComplete?: () => void) => {
        EndpointsService.registerEndpoint(endpoint).then(_ => {
            const newEndpoints: SimpleEndpoint[] = [...endpointURLs, endpoint];
            setEndpointsURLs(newEndpoints);
            onComplete?.();
        }).catch(error => {
            console.log(error);
            alert("The Endpoint does not exist or it is currently down!")
        })
    };

    return (
        <div className="App">
            <BrowserRouter>
                <Header endpoints={endpointURLs}/>
                <main>
                    <Switch>

                        <Route path={"/"} exact component={EndpointsApp}/>
                        <Route path={"/endpoints"} exact component={EndpointsApp}/>
                        <Route path={"/register"} exact>
                            <EndpointAdd registerEndpoint={registerEndpoint}/>
                        </Route>
                        <Route path={"/endpoints/:url"} component={EndpointDetails}/>

                    </Switch>
                </main>
            </BrowserRouter>
        </div>
    );
}

export default App;
