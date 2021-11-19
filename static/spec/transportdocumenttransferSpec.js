describe("TDT", function() {
    const shipperPublicKeyFromPem = KEYUTIL.getKey(shipperPublicKey);
    const shipperPublicKeyJWK = KEYUTIL.getJWKFromKey(shipperPublicKeyFromPem);
    var carrierPrivateKeyFromPem = new RSAKey();
    carrierPrivateKeyFromPem.readPrivateKeyFromPEMString(carrierPrivateKey);
    var tdt;

    beforeEach(function() {
        tdt = new TransportDocumentTransfer(shipperPublicKeyJWK, shipperPublicKeyJWK, "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", false, null, carrierPrivateKeyFromPem);
    });

    it("should return proper SHA256 hash", async function() {
        const hash = await tdt.tdtHash();
        expect(hash).toEqual( "1842cf2d3125671cbde4be4b8d8cfed0c77c076a3453bdfe26935ba5bfafc9db");
    });
});
