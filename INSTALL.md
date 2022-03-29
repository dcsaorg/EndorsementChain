Installation instructions
=========================


```
source dbconfiguration.env
cat initdb.sql |sudo -u postgres psql -v DATABASE_PASSWORD=$DATABASE_PASSWORD # initialize / reset the DB
keytool -genkeypair -alias dcsa-kid -keyalg RSA -keystore dcsa-jwk.jks -storepass dcsa-pass # key pair for cross-platform transfers
keytool -genkeypair -alias springboot-https -keyalg RSA -storetype PKCS12 -keystore springboot-https.p12 -storepass your_key-store_password #https self-issued certificate
mv dcsa-jwk.jks springboot-https.p12 src/main/resources/certificates
mvn spring-boot:run
```

Run the front-end tests at https://localhost:8443/static/SpecRunner.html

To populate the database with test data run:
```
cat populateaddressbook.sql | sudo -u postgres psql tdt_registry
```

To use an existing SSL certificate (convert from .pem):
```
openssl pkcs12 -export -name "springboot-https" -out springboot-https.p12 -in fullchain.pem -inkey privkey.pem
```

Deploying using Docker
```
cd docker-compose
./build
docker-compose up
```
