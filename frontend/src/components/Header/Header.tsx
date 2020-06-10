import React, {useEffect, useState} from "react";
import {Link} from 'react-router-dom'
import Select from 'react-dropdown-select';
import SimpleEndpoint from "../../models/SimpleEndpoint";

type HeaderProps = {
    endpoints: SimpleEndpoint[]
}

type DropdownUrlOption = {
    id: number;
    name: string;
    key: string;
}

const Header = ({endpoints}: HeaderProps) => {

    const [urls, setUrls] = useState<DropdownUrlOption[]>([])
    const [selected, setSelected] = useState<string>("");

    const [endpoint, setEndpoint] = useState<string>("");

    useEffect(() => {
        const urls: DropdownUrlOption[] = endpoints.map((url, idx) => {
            return {
                id: idx,
                name: url.url,
                key: url.url
            }
        });
        setUrls(urls);
    }, [endpoints])

    const onFormSubmit = (event: any) => {
        event.preventDefault();
        setSelected("");
        setEndpoint("");
        window.location.href = `/endpoints/${encodeURIComponent(endpoint)}`;
    }

    const onChange = (values: DropdownUrlOption[]) => {
        if (values.length > 0) {
            setEndpoint(values[0].name);
            setSelected(values[0].name);
        }
    }

    const renderSelectDropDown = () => {
        return (
            <Select options={urls}
                    multi={false}
                    onChange={onChange}
                    values={urls.filter(u => u.name === selected)}
                    labelField="name"
                    searchBy="name"
                    placeholder="Search endpoint"
                    clearable={true}
                    style={{width: "350px", backgroundColor: "white"}}/>
        );
    }

    return (
        <header>
            <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
                <Link to={"/"} className="navbar-brand">
                    SPARQLMON
                </Link>
                <button className="navbar-toggler" type="button" data-toggle="collapse"
                        data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                        aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"/>
                </button>

                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <ul className="navbar-nav mr-auto">
                        <li className="nav-item active">
                            <Link to={"/register"} className="btn btn-secondary">
                                Register Endpoint
                            </Link>
                        </li>
                    </ul>
                    <form className="form-inline my-2 my-lg-0" onSubmit={onFormSubmit}>
                        {renderSelectDropDown()}
                        <button className="btn btn-outline-success my-2 my-sm-0 ml-3" type="submit">Search</button>
                    </form>
                </div>
            </nav>
        </header>
    );
};

export default Header;
