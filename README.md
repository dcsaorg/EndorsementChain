The DCSA eBL Platform Interoperability POC
==========================================

This repository contains a proof of concept of a format for an electronic endorsement chain and how to transfer it across platforms.

The purpose of the POC is to illustrate how transfer of title and possession can be standardized, in view of establishing interoperability between eBL solution providers.

It revolves around a GET and a POST endpoint for *document transfer blocks*, 
the specifics of these *document transfer blocks* are described in: [TRANSFERBLOCK.md](TRANSFERBLOCK.md)  


One of the purposes of the POC is to determine if this signing scheme is implementable in practice by the existing eBL solution providers.

##Build & Run
To build and run it, see [INSTALL.md](INSTALL.md).

Then point your browser to https://localhost:8443/issuebl.html

Test keys can be obtained at https://localhost:8443/testkeys.js
