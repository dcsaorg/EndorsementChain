The DCSA eBL Endorsement Chain POC
==================================

This repository contains a proof of concept of an endorsement chain for an electronic bill of lading.

The purpose of the POC is to illustrate how transfer of title and possession can be standardized, in view of establishing interoperability between eBL solution providers.

It revolves around a GET and a POST endpoint for *document transfer blocks*, each block consisting of:

 * a hash of the document whose ownership is managed
 * the public key of the current title holder
 * the public key of the current possessor
 * a boolean indicating whether the eBL is to order
 * the hash of the previous transfer block

The block (a JWT) is signed by the previous possessor, in effect creating a cryptographically linked list, functionally equivalent to the list of signatures on the paper bill of lading. One of the purposes of the POC is to determine if this signing scheme is implementable in practice by the existing eBL solution providers.

To build and run it:
```
cat initdb.sql |sudo -u postgres psql
keytool -genkeypair -alias dcsa-kid -keyalg RSA -keystore dcsa-jwk.jks -storepass dcsa-pass
keytool -genkeypair -alias springboot-https -keyalg RSA -storetype PKCS12 -keystore springboot-https.p12 -storepass your_key-store_password
mv dcsa-jwk.jks springboot-https.p12 src/main/resources
mvn spring-boot:run
```

Then point your browser to https://localhost:8443/static/issuebl.html

Test keys can be generated at https://localhost:8443/static/generatersakeypair.html

Run the front-end tests at https://localhost:8443/static/SpecRunner.html

To populate the database with test data run:
```
./populatedatabase.sh
```
