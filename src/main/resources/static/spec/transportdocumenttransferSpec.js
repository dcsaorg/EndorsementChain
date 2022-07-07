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
    transferredTitleBlock.init(endorseePublicKeyJWK, Date.now(), null, {"documentHash": documentHash, "isToOrder": true}, shipperPrivateKeyFromPem);

    beforeEach(async function() {
        titleBlock = new TitleTransferBlock();
        titleBlock.init(shipperPublicKeyJWK, 1657187073323, null, {"documentHash": documentHash, "isToOrder": true}, carrierPrivateKeyFromPem);
        possessionBlock = new PossessionTransferBlock();
        possessionBlock.init(shipperPublicKeyJWK, 1657187073323, null, {"titleTransferBlockHash": await titleBlock.blockHash(), "isToOrder": true}, carrierPrivateKeyFromPem);
    });

    it("should return proper SHA256 hash", async function() {
        expect(await titleBlock.blockHash()).toEqual("45a4312cc75e6175e54cfd93ffde5ff43e3a1dce5940f7a67d1ece3ee3bd7f19");
        expect(await titleBlock.blockHash()).not.toEqual("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"); //hash of null
        expect(await possessionBlock.blockHash()).toEqual("efc164e897e6a2226ec155781bd83e79b4d3b5e29b96894f95dad9de3c8916c7");
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
        transferredPossessionBlock.init(shipperPublicKeyJWK, Date.now(), null, {"titleTransferBlockHash": await transferredTitleBlock.blockHash(), "isToOrder": true}, shipperPrivateKeyFromPem);
        const receivedTitleBlock = new TitleTransferBlock(transferredTitleBlock.JWS);
        const receivedPossessionBlock = new PossessionTransferBlock(transferredPossessionBlock.JWS);
        expect(await transferredPossessionBlock.transfereeThumbprint()).toEqual(await receivedPossessionBlock.transfereeThumbprint());
        expect(await receivedTitleBlock.transfereeThumbprint()).toEqual("a162bc5f6402209fa797");
        expect(await receivedTitleBlock.documentHash()).toEqual(documentHash);
        expect(await receivedPossessionBlock.blockHash()).toEqual(await transferredPossessionBlock.blockHash());
    });


    it("should have proper pointer (hash) to sent block", async function() {
        let transferredPossessionBlock = new PossessionTransferBlock();
        transferredPossessionBlock.init(shipperPublicKeyJWK, Date.now(), null,
                                        {"titleTransferBlockHash": await transferredTitleBlock.blockHash(), "isToOrder": true}, shipperPrivateKeyFromPem);
        const receivedTitleBlock = new TitleTransferBlock(transferredTitleBlock.JWS);
        const receivedPossessionBlock = new PossessionTransferBlock(transferredPossessionBlock.JWS);
        expect(await transferredPossessionBlock.transfereeThumbprint()).toEqual(await receivedPossessionBlock.transfereeThumbprint());
        expect(await receivedTitleBlock.transfereeThumbprint()).toEqual("a162bc5f6402209fa797");
        expect(await receivedTitleBlock.documentHash()).toEqual(documentHash);
        expect(await receivedPossessionBlock.blockHash()).toEqual(await transferredPossessionBlock.blockHash());
    });

});
