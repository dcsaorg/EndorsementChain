A step-by-step introduction to the POC
======================================

The purpose of this document is to guide a user through the simulated process of creating an electronic bill of lading and transferring title and/or possession, acting as different parties (carrier, shipper, endorsee) during the exercise.

It assumes that the user has access to one or more running instances of an eBL platform conforming to the API. (How to install the reference implementation in this repository is described in [INSTALL.md](INSTALL.md)). It also assumes that the eBL platform's database is populated with address book data *or* that the user is capable of generating cryptographic key pairs. When running the reference implementation locally, this can be done at https://localhost:8443/adduser.html. If everything else fails, you can practice using the test keys at https://localhost:8443/testkeys.js

In the following, we assume that the server is running on the server at https://localhost:8443. Replace localhost with the server name of the platform.


Step 1: As a carrier, issue the bill of lading
----------------------------------------------
You are now acting as the carrier.

Point your browser to https://localhost:8443/issuebl.html and fill in the bill of lading information.

Paste in the public key of the shipper and your private (carrier) key to sign the issuance

Once you click **Issue**, you should be redirected to the page https://localhost:8443/transportdocumenttransfer.html?id=SECRET_HASH, allowing you to transfer title and/or possession.


Step 2: As a shipper, transfer title
------------------------------------
You are now acting as the shipper.

If you want to transfer the title, aka. *endorsing* the bill of lading, click the **Transfer** button (Title), paste in the public key of the endorsee and paste in your private key in the endorser's field. Then press **Transfer**.


Step 3: Transfer possession to another platform
-----------------------------------------------
Note: if you are running a single local instance (localhost), this won't work as there is no target platform available for receiving/importing the B/L. 
In order to test this out locally follow the instructions to setup two nodes as described: [here](MULTI_NODE_SETUP.md)

To avoid confusion, we now refer to the exporting server as *node1* and the importing server as *node2*.

To transfer possession, click the **Transfer** (Possession). Fill in the name of the receiving platform, fill in your private key. Then press **Transfer**

Your bill of lading has now been exported and a notification has been sent to the receiving/importing platform.
Which concequently automatically imports the B/L if this is successful the message **Document transferred successfully.** 
appears on the screen. As well as a link to the target platform to view the imported B/L.

Step 4: Import possession on the other platform
-----------------------------------------------
The import of the B/L happens automatically. And are here outlined for completeness. So importing does not need any manual actions:

The import is triggered by the receipt of a notification (sent from the exporting platform). 
In this notification a link to the export B/L is presented. The export transferblock is retrieved from this link.
The received export block is transformed into an import transferblock and saved on the imported platform. Based on the exportblock the corresponding title block is retrieved and saved.
Finally the B/L is also retrieved and saved in the importing platform. 