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
    var titleBlock;
    var possessionBlock;

    beforeEach(async function() {
        titleBlock = new TitleTransferBlock();
        titleBlock.init(shipperPublicKeyJWK, {"documentHash": documentHash, "isToOrder": true}, carrierPrivateKeyFromPem);
        possessionBlock = new PossessionTransferBlock();
        possessionBlock.init(shipperPublicKeyJWK, {"titleTransferBlockHash": await titleBlock.blockHash(), "isToOrder": true}, carrierPrivateKeyFromPem);
    });

    it("should return proper SHA256 hash", async function() {
        expect(await titleBlock.blockHash()).toEqual("570c9ee436468bed4ea38b8c6c5829c5af69b08ba3ead91dbacadde16082af12");
        expect(await titleBlock.blockHash()).not.toEqual("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"); //hash of null
        expect(await possessionBlock.blockHash()).toEqual("145f36888513e9775458e7e32675cb123dc05a84ef3c93ac81bf1a9d614eecbd");
    });

    it("should have valid signature", function() {
        expect(possessionBlock.verifyNth(0, carrierPublicKeyFromPem, {alg: ['RS256']})).toEqual(true);
        expect(titleBlock.verifyNth(0, carrierPublicKeyFromPem, {alg: ['RS256']})).toEqual(true);
    });

    it("should have expected thumbprints", async function() {
        expect(await possessionBlock.transfereeThumbprint()).toEqual("6c0a3fce271760042dbb6f2fe83b901dc187a47fd69edb6275475f696c1d7038");
        expect(await titleBlock.transfereeThumbprint()).toEqual("6c0a3fce271760042dbb6f2fe83b901dc187a47fd69edb6275475f696c1d7038");
    });

    it("should have proper holder and possessor thumbprint, document hash and (previous) tdt hash after transfer", async function() {
        let transferredTitleBlock = new TitleTransferBlock();
        transferredTitleBlock.init(endorseePublicKeyJWK, {"documentHash": documentHash, "isToOrder": true}, shipperPrivateKeyFromPem);
        let transferredPossessionBlock = new PossessionTransferBlock();
        transferredPossessionBlock.init(shipperPublicKeyJWK, {"titleTransferBlockHash": await transferredTitleBlock.blockHash(), "isToOrder": true}, shipperPrivateKeyFromPem);
        const receivedTitleBlock = new TitleTransferBlock(transferredTitleBlock.JWS);
        const receivedPossessionBlock = new PossessionTransferBlock(transferredPossessionBlock.JWS);
        expect(await transferredPossessionBlock.transfereeThumbprint()).toEqual(await receivedPossessionBlock.transfereeThumbprint());
        expect(await receivedTitleBlock.transfereeThumbprint()).toEqual("a162bc5f6402209fa797e86b6861fd1af68656688833809144063c27a9ccfef9");
        expect(await receivedTitleBlock.documentHash()).toEqual(documentHash);
        expect(await receivedPossessionBlock.blockHash()).toEqual(await transferredPossessionBlock.blockHash());
    });

});
