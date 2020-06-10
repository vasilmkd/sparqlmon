import React, {useState} from "react";
import {useHistory} from "react-router";

type EndpointAddProps = {
    registerEndpoint: (endpoint: {url: string, email?: string}, onComplete?: () => void) => void
}

type EndpointDTOType = {
    url: string;
    email?: string;
}

const EndpointAdd = ({registerEndpoint}: EndpointAddProps) => {

    const history = useHistory();

    const tempEndpoint = {
        url: "",
        email: ""
    };

    const [endpoint, setEndpoint] = useState(tempEndpoint);

    const addNewEndpoint = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        let endpointDTO: EndpointDTOType = {
            url: endpoint.url
        };
        if (endpoint.email !== "")
            endpointDTO.email = endpoint.email

        registerEndpoint(endpointDTO, () => {
            history.push(`/endpoints/${encodeURIComponent(endpoint.url)}`)
        });
    }

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const target = event.target;
        const name = target.name;
        const value = target.value;

        const changedEndpoint = {
            ...endpoint,
            [name]: value
        };

        setEndpoint(changedEndpoint);
    };

    return (
        <div className="card container-fluid w-50">
            <h2 className="card-title">Register New Endpoint</h2>
            <div className="card-body">
                <form onSubmit={addNewEndpoint}>
                    <div className="form-group row">
                        <label htmlFor="url" className="col-sm-2 col-form-label">Endpoint URL</label>
                        <div className="col-sm-10">
                            <input type="url"
                                   required={true}
                                   className="form-control"
                                   id="url"
                                   name="url"
                                   value={endpoint.url} onChange={handleInputChange}
                                   placeholder="Endpoint URL"/>
                        </div>
                    </div>
                    <div className="form-group row">
                        <label htmlFor="inputEmail3" className="col-sm-2 col-form-label">Email</label>
                        <div className="col-sm-10">
                            <input type="email"
                                   // required={true}
                                   className="form-control"
                                   name="email"
                                   id="email"
                                   value={endpoint.email} onChange={handleInputChange}
                                   placeholder="Email"/>
                            <small id="emailHelp" className="form-text text-muted">
                                You will receive an email if the endpoint is down for more than one hour.<br />
                                We'll never share your email with anyone else.
                            </small>
                        </div>
                    </div>
                    <div className="row float-right">
                        <div
                            className="text-center">
                            <button
                                type="submit"
                                className="btn btn-success text-upper"><span className="fa fa-save"/>
                                Register
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EndpointAdd;
