\set ON_ERROR_STOP true
DROP DATABASE IF EXISTS tdt_registry;
DROP USER IF EXISTS tdt_db_owner;
CREATE USER tdt_db_owner WITH PASSWORD 'Voo0voVo';
CREATE DATABASE tdt_registry OWNER tdt_db_owner;
