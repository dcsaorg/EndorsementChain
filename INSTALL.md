Installation instructions
=========================


```
cat initdb.sql |sudo -u postgres psql # initialize / reset the DB
keytool -genkeypair -alias dcsa-kid -keyalg RSA -keystore dcsa-jwk.jks -storepass dcsa-pass # key pair for cross-platform transfers
keytool -genkeypair -alias springboot-https -keyalg RSA -storetype PKCS12 -keystore springboot-https.p12 -storepass your_key-store_password #https self-issued certificate
mv dcsa-jwk.jks springboot-https.p12 src/main/resources
mvn spring-boot:run
```

Run the front-end tests at https://localhost:8443/static/SpecRunner.html

To populate the database with test data run:
```
./populatedatabase.sh
```
