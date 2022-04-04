#!/bin/bash
source ../dbconfiguration.docker.env
cat ../initdb.sql | psql -v DATABASE_PASSWORD=$DATABASE_PASSWORD
