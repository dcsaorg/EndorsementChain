A step-by-step introduction to the POC
======================================

The purpose of this document is to guide a user through the simulated process of creating an electronic bill of lading and transferring title and/or possession, acting as different parties (carrier, shipper, endorsee) during the exercise.

It assumes that the user has access to one or more running instances of an eBL platform conforming to the API. (How to install the reference implementation in this repository is described in [INSTALL.md](INSTALL.md)). It also assumes that the eBL platform's database is populated with address book data *or* that the user is capable of generating cryptographic key pairs. When running the reference implementation locally, this can be done at https://localhost:8443/static/generatersakeypair.html. If everything else fails, you can practice using the test keys at https://localhost:8443/static/testkeys.js

In the following, we assume that the server is running on the server at https://localhost:8443. Replace localhost with the server name of the platform.


Step 1: As a carrier, issue the bill of lading
----------------------------------------------
You are now acting as the carrier.

Point your browser to https://localhost:8443/static/issuebl.html and fill in the bill of lading information.

Paste in the public key of the shipper and your private (carrier) key to sign the issuance

Once you click **Issue**, you should be redirected to the page https://localhost:8443/static/transportdocumenttransfer.html?id=SECRET_HASH, allowing you to transfer title and/or possession.


Step 2: As a shipper, transfer title
------------------------------------
You are now acting as the shipper.

If you want to transfer the title, aka. *endorsing* the bill of lading, click the **Transfer** button (Title), paste in the public key of the endorsee and paste in your private key in the endorser's field. Then press **Transfer**.


Step 3: Transfer possession to another platform
-----------------------------------------------
Note: if you are running a local instance (localhost), this won't work as you won't subsequently be able to import the BL on the target platform. To avoid confusion, we now refer to the exporting server as *server1* and the importing server as *server2*.

To transfer possession, click the **Transfer** (Possession). Fill in the name of the receiving platform, fill in the public key of the recipient and your private key. Then press **Transfer**

Your bill of lading has now been exported and you can copy the link that the recipient will need on the importing platform.


Step 4: Import possession on the other platform
-----------------------------------------------
You are now acting as the recipient on the other platform.

On the importing platform, point your browser to https://server2:8443/static/importbl.html. Then paste in the link you have received from the exporter on the previous platform and your private key. After clicking **Import**, the bill of lading has now been fully transferred.
