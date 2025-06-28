CREATE TABLE ui_location_override (
    component_id SERIAL PRIMARY KEY,
    user_id INTEGER,
    reference varchar,
    column_num INTEGER,
    row_num INTEGER,
    state INTEGER,
    status INTEGER
);

CREATE TABLE components (
    component_id SERIAL PRIMARY KEY,
    name VARCHAR(30),
    display_name VARCHAR(60),
    default_access CHAR(4),
    attributes varchar(255),
    identifier INTEGER,
    state INTEGER,
    status INTEGER,
    column_num INTEGER,
    row_num INTEGER
);

CREATE TABLE organization_users (
    organization_id INTEGER,
    user_id INTEGER,
    active boolean,
    role CHAR(10),
    PRIMARY KEY(organization_id, user_id)
);

CREATE TABLE permissions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    organization_id INTEGER,
    service_id INTEGER,
    page_id INTEGER,
    component_id INTEGER,
    enabled boolean,
    access INTEGER,
    attribute VARCHAR
);

CREATE TABLE pages_components (
    component_id INTEGER,
    page_id INTEGER,
    identifier INTEGER,
    PRIMARY KEY(component_id, page_id, identifier)
);

CREATE TABLE organizations (
    organization_id SERIAL PRIMARY KEY,
    fusebill_id INTEGER UNIQUE,
    parent_id INTEGER,
    name VARCHAR(30),
    org_code CHAR(3) UNIQUE,
    status INTEGER,
    icon VARCHAR(255)
    intacct_id INTEGER
    billing_platform billing_platorm
);

CREATE TABLE services (
    service_id SERIAL PRIMARY KEY,
    name VARCHAR(30),
    description VARCHAR,
    display_name VARCHAR(60)
);

CREATE TABLE pages (
    page_id SERIAL PRIMARY KEY,
    service_id INTEGER,
    name VARCHAR(30),
    display_name VARCHAR(60),
    default_access INTEGER
);

CREATE TABLE organization_services (
    organization_id INTEGER,
    service_id INTEGER,
    active BOOLEAN
);

CREATE TABLE storage_subnet (
  subnet_id serial,
  subnet VARCHAR,
  subnet_mask INTEGER,
  vlan CHAR(4),
  organization_id INTEGER,
  created_dt timestamp with time zone,
  updated_dt timestamp with time zone
);


CREATE TABLE storage_ip_inventory (
    ip_address VARCHAR,
    mask INTEGER,
    subnet_id INTEGER,
    status INTEGER,
    purpose VARCHAR(30),
    description VARCHAR,
    storage_id INTEGER,
    created_dt timestamp with time zone,
    updated_dt timestamp with time zone
);

alter table storage_whitelist add column created_dt timestamp with time zone;
alter table storage_whitelist alter column created_dt set default now();
alter table storage_whitelist add column updated_dt timestamp with time zone;
ALTER SEQUENCE storage_whitelist_id_seq RESTART WITH 200;


CREATE TABLE datacenter_loadbalancers (
  name VARCHAR,
  datacenter_id INTEGER,
  url VARCHAR
);
INSERT INTO public.datacenter_loadbalancers (name, datacenter_id, url) VALUES ('ATL', 1, 'https://dev-kemp.orlabsprd.com/');
INSERT INTO public.datacenter_loadbalancers (name, datacenter_id, url) VALUES ('CHA', 2, 'https://dev-kemp.orlabsprd.com/');
INSERT INTO public.datacenter_loadbalancers (name, datacenter_id, url) VALUES ('HSV', 3, 'https://dev-kemp.orlabsprd.com/');
INSERT INTO public.datacenter_loadbalancers (name, datacenter_id, url) VALUES ('BHM', 4, 'https://dev-kemp.orlabsprd.com/');