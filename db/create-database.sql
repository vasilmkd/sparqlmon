create table endpoint (
    url text not null,
    registered timestamp not null,
    email text,
    primary key (url)
);

create table availability (
    url text not null,
    instant timestamp not null,
    up boolean not null,
    primary key (url, instant)
);

create table status (
    url text not null,
    instant timestamp not null,
    up boolean not null,
    primary key (url)
);
