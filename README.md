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

The block (a JWT) is signed by the previous possessor, in effect creating a cryptographically linked list. One of the purposes of the POC is to determine if this signing scheme is implementable in practice by the existing eBL solution providers.

To build and run it:
```
cat initdb.sql |sudo -u postgres psql
mvn spring-boot:run
```

Then point your browser to http://localhost:9090/static/issuebl.html

Test keys can be generated at http://localhost:9090/static/generatersakeypair.html

Run the front-end tests at http://localhost:9090/static/SpecRunner.html
