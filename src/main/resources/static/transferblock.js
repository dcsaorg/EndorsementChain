/*
 * The basic TransferBlock object
 * The basic tranfer block is a JWS consisting of a transferee field and a payload field. It is signed by one or more tranferrers
 * 
 * It is based on the JSON Serialization JWS, see https://datatracker.ietf.org/doc/html/rfc7515
 */
class TransferBlock {

    constructor(jws) {
        this.JWS = jws;
    }

    init(transferee, previousBlockHash, blockPayload, transferrerPrivateKey) {
        let tmpJWS =  KJUR.jws.JWS.sign(null, {alg: "RS256"},
                                     JSON.stringify({transferee: transferee, timestamp: Date.now(),
                                         previousBlockHash: previousBlockHash, blockPayload: blockPayload}),
                                     transferrerPrivateKey);
        this.JWS = new KJUR.jws.JWSJS();
        this.JWS.initWithJWS(tmpJWS);
        this.JWS=this.JWS.getJSON();
        this.JWS = this.toValidJWS();
    }

    //workaround for a bug in jsrsasign consisting of signatures not being valid json objects
    //we choose to have the internal JWS representation be valid and convert back and forth to jsrsasign's version
    //on demand
    asRsasignJWSJS() {
        let illFormedJWS = {headers: [], payload: this.JWS.payload, signatures: []};
        for (var i = 0; i < this.JWS.signatures.length; ++i) {
            illFormedJWS.headers.push(this.JWS.signatures[i]["protected"]);
            illFormedJWS.signatures.push(this.JWS.signatures[i]["signature"]);
        }
        return illFormedJWS;
    };

    toValidJWS() {
        let validSignatures = [];
        for (var i = 0; i < this.JWS.signatures.length; ++i) {
            validSignatures.push({"protected" : this.JWS.headers[i],  "signature" : this.JWS.signatures[i]});
        }
        var validJWS = {};
        validJWS.payload = this.JWS.payload;
        validJWS.signatures = validSignatures;
        return validJWS;
    };

    async blockHash() {
        return ArrayBuffertohex(await crypto.subtle.digest("SHA-256", new TextEncoder("utf-8").encode(JSON.stringify(this.JWS))));
    }

    transferee() {
        return JSON.parse(b64utos(this.JWS.payload))["transferee"];
    }

    async transfereeThumbprint() {
        const longThumbprint = ArrayBuffertohex(await crypto.subtle.digest('SHA-256', Uint8Array.from(JSON.stringify(this.transferee()))));
        return longThumbprint.slice(0,20);
    }

    previousBlockHash() {
        return JSON.parse(b64utos(this.JWS.payload))["previousBlockHash"];
    }

    verifyNth(idx, key, acceptAlgs) {
        let verifierJWS = new KJUR.jws.JWSJS();
        verifierJWS.readJWSJS(this.asRsasignJWSJS());
        return verifierJWS.verifyNth(idx, key, acceptAlgs);
    }

    blockPayloadAsJson() {
        return JSON.parse(b64utos(this.JWS.payload))["blockPayload"];
    }

}

/*
 * The PossessionTransferBlock object
 *
 * Expresses a transfer of possession
 * The blockPayload is required to have the form
 * {
 *   titleTransferBlockHash: hash of the corresponding titleTransferBlock
 * }
 */
class PossessionTransferBlock extends TransferBlock {
    titleTransferBlockHash() {
        return this.blockPayloadAsJson()["titleTransferBlockHash"];
    }
}

/*
 * The TitleTransferBlock object
 *
 * Expresses a transfer of title of a bill of lading
 * The blockPayload is required to have the form
 * {
 *   documentHash: hash of the managed (bill of lading) documentHash
 *   isToOrder: indicates if the the bill of lading is to order
 * }
 */
class TitleTransferBlock extends TransferBlock {
    documentHash() {
         return this.blockPayloadAsJson()["documentHash"];
    }
    isToOrder() {
         return this.blockPayloadAsJson()["isToOrder"];
    }
    titleHolderPlatform() {
         return this.blockPayloadAsJson()["titleHolderPlatform"];
    }
}

/*
 * The PlatformExportTransferBlock object
 *
 * Expresses a transfer (continuation) of a possession chain on another platform, export part.
 * The blockPayload is required to have the form
 * {
 *   titleTransferBlockHash: inherited from PossessionTransferBlock
 *   nextRegistryJWK: the key of the importing platform
 *   nextRegistryHost: hostname of the importing platform. Should ideally not be necessary, but would require it to be
 *                    findable based on the platform's public key, say in a public registry
 * }
 */
class PlatformExportTransferBlock extends PossessionTransferBlock {
    nextRegistryJWK() {
        return this.blockPayloadAsJson()["nextRegistryJWK"];
    }
    nextRegistryHost() {
        return this.blockPayloadAsJson()["nextRegistryHost"];
    }
}

/*
 * The PlatformImportTransferBlock object
 *
 * Expresses a transfer (continuation) of a possession chain on another platform, import part.
 * The blockPayload is required to have the form
 * {
 *   previousRegistryURL: for convenience, the URL of the exporting platform, useful when tracing back the chain
 *   //note: should be key-based rather than DNS-based
 * }
 */
class PlatformImportTransferBlock extends PossessionTransferBlock {
    previousRegistryURL() {
        return this.blockPayloadAsJson()["previousRegistryURL"];
    }
}
