FROM postgres:14-bullseye
COPY initdb.sql .
COPY dbconfiguration.docker.env .
COPY initdb.sh /docker-entrypoint-initdb.d
ENV POSTGRES_PASSWORD lmascmlcaf235sdklfSDFJK
