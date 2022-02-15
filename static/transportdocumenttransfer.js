/*
 * Javascript representation of the Transport Document Transfer object
 */

function TransportDocumentTransfer(jwt) {

    this.JWT = jwt;
    this.createTdt = function(holder, possessor, documentHash, isToOrder, previousTDThash, transfererPrivateKey) {
        let tmpJWT =  KJUR.jws.JWS.sign(null, {alg: "RS256"},
                                     JSON.stringify({holder: holder, possessor: possessor, documentHash: documentHash, isToOrder: isToOrder, previousTDThash: previousTDThash}),
                                     transfererPrivateKey);

        this.JWT = new KJUR.jws.JWSJS();
        this.JWT.initWithJWS(tmpJWT);
        this.JWT=this.JWT.getJSON();
    }

    this.createFromJWT = function(jwt) {
        this.JWT = jwt;
    }

    this.asJWT = function() {
        return this.JWT;
    };

    //workaround for bug in jsrsasign consisting of signatures not being valid json objects
    this.asValidJWT = function() {
        validSignatures = [];
        for (var i = 0; i < this.JWT.signatures.length; ++i) {
            validSignatures.push({"protected" : this.JWT.headers[i],  "signature" : this.JWT.signatures[i]});
        }
        var validJWT = {};
        validJWT.payload = this.JWT.payload;
        validJWT.signatures = validSignatures;
        return validJWT;
    };

    this.tdtHash = async function() {
        //note on security: ok to use the payload as documentHash makes it unguessable
        return ArrayBuffertohex(await crypto.subtle.digest('SHA-256', Uint8Array.from(this.JWT.payload)));
    }

    this.holder = function() {
        return JSON.parse(b64utos(this.JWT.payload))["holder"];
    }

    this.possessor = function() {
        return JSON.parse(b64utos(this.JWT.payload))["possessor"];
    }

    this.documentHash = function() {
        return JSON.parse(b64utos(this.JWT.payload))["documentHash"];
    }

    this.isToOrder = function() {
        return JSON.parse(b64utos(this.JWT.payload))["isToOrder"];
    }

    this.previousTDThash = function() {
        return JSON.parse(b64utos(this.JWT.payload))["previousTDThash"];
    }

    this.holderThumbprint = async function () {
        return ArrayBuffertohex(await crypto.subtle.digest('SHA-256', Uint8Array.from(JSON.stringify(this.holder()))));
    }

    this.possessorThumbprint = async function() {
        return ArrayBuffertohex(await crypto.subtle.digest('SHA-256', Uint8Array.from(JSON.stringify(this.possessor()))));
    }
}
