describe("TDT", function() {
    const carrierPublicKeyFromPem = KEYUTIL.getKey(carrierPublicKey);
    const carrierPublicKeyJWK = KEYUTIL.getJWKFromKey(carrierPublicKeyFromPem);
    const shipperPublicKeyFromPem = KEYUTIL.getKey(shipperPublicKey);
    const shipperPublicKeyJWK = KEYUTIL.getJWKFromKey(shipperPublicKeyFromPem);
    const endorseePublicKeyFromPem = KEYUTIL.getKey(endorseePublicKey);
    const endorseePublicKeyJWK = KEYUTIL.getJWKFromKey(endorseePublicKeyFromPem);
    var carrierPrivateKeyFromPem = new RSAKey();
    carrierPrivateKeyFromPem.readPrivateKeyFromPEMString(carrierPrivateKey);
    var shipperPrivateKeyFromPem = new RSAKey();
    shipperPrivateKeyFromPem.readPrivateKeyFromPEMString(shipperPrivateKey);
    const documentHash = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824";
    var tdt;

    beforeEach(async function() {
        tdt = new TransportDocumentTransfer();
        tdt.createTdt(shipperPublicKeyJWK, shipperPublicKeyJWK, documentHash, false, null, carrierPrivateKeyFromPem);
    });

    it("should return proper SHA256 hash", async function() {
        const hash = await tdt.tdtHash();
        expect(hash).toEqual("1842cf2d3125671cbde4be4b8d8cfed0c77c076a3453bdfe26935ba5bfafc9db");
    });

    it("should have valid JWT", function() {
        const signatureIsValid = KJUR.jws.JWS.verifyJWT(tdt.asJWT(), carrierPublicKeyFromPem, {alg: ['RS256']});
        expect(signatureIsValid).toEqual(true);
    });

    it("should have expected thumbprints", async function() {
        expect(await tdt.possessorThumbprint()).toEqual("6c0a3fce271760042dbb6f2fe83b901dc187a47fd69edb6275475f696c1d7038");
        expect(await tdt.holderThumbprint()).toEqual("6c0a3fce271760042dbb6f2fe83b901dc187a47fd69edb6275475f696c1d7038");
    });

    it("should have proper holder and possessor thumbprint, document hash and (previous) tdt hash after transfer", async function() {
        let transferredTDT = new TransportDocumentTransfer();
        transferredTDT.createTdt(endorseePublicKeyJWK, tdt.possessor(), documentHash, true, await tdt.tdtHash(), shipperPrivateKeyFromPem);
        const receivedTransferredTDT = new TransportDocumentTransfer(transferredTDT.asJWT());
        expect(await receivedTransferredTDT.possessorThumbprint()).toEqual(await tdt.possessorThumbprint());
        expect(await receivedTransferredTDT.holderThumbprint()).toEqual("a162bc5f6402209fa797e86b6861fd1af68656688833809144063c27a9ccfef9");
        expect(await receivedTransferredTDT.documentHash()).toEqual(documentHash);
        expect(await receivedTransferredTDT.previousTDThash()).toEqual(await tdt.tdtHash());
    });

    it("should have proper possessor after transfer of title and possession", async function() {
        const receivedIssuedBLTDT = new TransportDocumentTransfer(tdt.asJWT());
        let transferredTitleTDT = new TransportDocumentTransfer();
        transferredTitleTDT.createTdt(endorseePublicKeyJWK, receivedIssuedBLTDT.possessor(),
                                      receivedIssuedBLTDT.documentHash(), receivedIssuedBLTDT.isToOrder(),
                                      await receivedIssuedBLTDT.tdtHash(), shipperPrivateKeyFromPem);
        const receivedTransferredTitleTDT = new TransportDocumentTransfer(transferredTitleTDT.asJWT());
        let transferredPossessionTDT = new TransportDocumentTransfer();
        transferredPossessionTDT.createTdt(receivedTransferredTitleTDT.holder(), endorseePublicKeyJWK,
                                      receivedTransferredTitleTDT.documentHash(), receivedTransferredTitleTDT.isToOrder(),
                                      await receivedTransferredTitleTDT.tdtHash(), shipperPrivateKeyFromPem);
        const receivedTransferredPossessionTDT = new TransportDocumentTransfer(transferredPossessionTDT.asJWT());
        expect(await receivedTransferredTitleTDT.holderThumbprint()).not.toEqual(await receivedIssuedBLTDT.holderThumbprint());
        expect(await receivedTransferredPossessionTDT.holderThumbprint())
            .toEqual(await receivedTransferredPossessionTDT.possessorThumbprint());
    });

});
