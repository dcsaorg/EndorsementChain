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
    let transferredTitleBlock = new TitleTransferBlock();
    transferredTitleBlock.init(endorseePublicKeyJWK, null, {"documentHash": documentHash, "isToOrder": true}, shipperPrivateKeyFromPem);

    beforeEach(async function() {
        titleBlock = new TitleTransferBlock();
        titleBlock.init(shipperPublicKeyJWK, null, {"documentHash": documentHash, "isToOrder": true}, carrierPrivateKeyFromPem);
        possessionBlock = new PossessionTransferBlock();
        possessionBlock.init(shipperPublicKeyJWK, null, {"titleTransferBlockHash": await titleBlock.blockHash(), "isToOrder": true}, carrierPrivateKeyFromPem);
    });

    it("should return proper SHA256 hash", async function() {
        expect(await titleBlock.blockHash()).toEqual("001ea6d8df1bea4b2fd5ccb9f9fd8a6dfb3f1903b8337ddf3e708dcc9a1dd906");
        expect(await titleBlock.blockHash()).not.toEqual("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"); //hash of null
        expect(await possessionBlock.blockHash()).toEqual("204fe28f31a39f86d5ac5f2deaf05b0985e1d0e3a45225029f2bd026d66e562f");
    });

    it("should have valid signature", function() {
        expect(possessionBlock.verifyNth(0, carrierPublicKeyFromPem, {alg: ['RS256']})).toEqual(true);
        expect(titleBlock.verifyNth(0, carrierPublicKeyFromPem, {alg: ['RS256']})).toEqual(true);
        expect(possessionBlock.verifyNth(0, KEYUTIL.getKey(carrierPublicKeyJWK), {alg: ['RS256']})).toEqual(true);
        expect(titleBlock.verifyNth(0, KEYUTIL.getKey(carrierPublicKeyJWK), {alg: ['RS256']})).toEqual(true);
    });

    it("should have expected thumbprints", async function() {
        expect(await possessionBlock.transfereeThumbprint()).toEqual("6c0a3fce271760042dbb");
        expect(await titleBlock.transfereeThumbprint()).toEqual("6c0a3fce271760042dbb");
    });

    it("should have proper holder and possessor thumbprint, document hash and (previous) tdt hash after transfer", async function() {
        let transferredPossessionBlock = new PossessionTransferBlock();
        transferredPossessionBlock.init(shipperPublicKeyJWK, null, {"titleTransferBlockHash": await transferredTitleBlock.blockHash(), "isToOrder": true}, shipperPrivateKeyFromPem);
        const receivedTitleBlock = new TitleTransferBlock(transferredTitleBlock.JWS);
        const receivedPossessionBlock = new PossessionTransferBlock(transferredPossessionBlock.JWS);
        expect(await transferredPossessionBlock.transfereeThumbprint()).toEqual(await receivedPossessionBlock.transfereeThumbprint());
        expect(await receivedTitleBlock.transfereeThumbprint()).toEqual("a162bc5f6402209fa797");
        expect(await receivedTitleBlock.documentHash()).toEqual(documentHash);
        expect(await receivedPossessionBlock.blockHash()).toEqual(await transferredPossessionBlock.blockHash());
    });


    it("should have proper pointer (hash) to sent block", async function() {
        let transferredPossessionBlock = new PossessionTransferBlock();
        transferredPossessionBlock.init(shipperPublicKeyJWK, null,
                                        {"titleTransferBlockHash": await transferredTitleBlock.blockHash(), "isToOrder": true}, shipperPrivateKeyFromPem);
        const receivedTitleBlock = new TitleTransferBlock(transferredTitleBlock.JWS);
        const receivedPossessionBlock = new PossessionTransferBlock(transferredPossessionBlock.JWS);
        expect(await transferredPossessionBlock.transfereeThumbprint()).toEqual(await receivedPossessionBlock.transfereeThumbprint());
        expect(await receivedTitleBlock.transfereeThumbprint()).toEqual("a162bc5f6402209fa797");
        expect(await receivedTitleBlock.documentHash()).toEqual(documentHash);
        expect(await receivedPossessionBlock.blockHash()).toEqual(await transferredPossessionBlock.blockHash());
    });

});
