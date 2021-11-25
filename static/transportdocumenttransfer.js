/*
 * Javascript representation of the Transport Document Transfer object
 */

function TransportDocumentTransfer(jwt) {

    this.JWT = jwt;
    this.createTdt = function(holder, possessor, documentHash, isToOrder, previousTDThash, transfererPrivateKey) {
        this.JWT =  KJUR.jws.JWS.sign(null, {alg: "RS256"},
                                     JSON.stringify({holder: holder, possessor: possessor, documentHash: documentHash, isToOrder: isToOrder, previousTDThash: previousTDThash}),
                                     transfererPrivateKey);
    }

    this.createFromJWT = function(jwt) {
        this.JWT = jwt;
    }

    this.asJWT = function() {
        return this.JWT;
    };

    this.tdtHash = async function() {
        return ArrayBuffertohex(await crypto.subtle.digest('SHA-256', Uint8Array.from(this.asJWT())));
    }

    this.holder = function() {
        return JSON.parse(KJUR.jws.JWS.parse(this.JWT).payloadPP)["holder"];
    }

    this.possessor = function() {
        return JSON.parse(KJUR.jws.JWS.parse(this.JWT).payloadPP)["possessor"];
    }

    this.documentHash = function() {
        return JSON.parse(KJUR.jws.JWS.parse(this.JWT).payloadPP)["documentHash"];
    }

    this.isToOrder = function() {
        return JSON.parse(KJUR.jws.JWS.parse(this.JWT).payloadPP)["isToOrder"];
    }

    this.previousTDThash = function() {
        return JSON.parse(KJUR.jws.JWS.parse(this.JWT).payloadPP)["previousTDThash"];
    }

    this.holderThumbprint = async function () {
        return ArrayBuffertohex(await crypto.subtle.digest('SHA-256', Uint8Array.from(JSON.stringify(this.holder()))));
    }

    this.possessorThumbprint = async function() {
        return ArrayBuffertohex(await crypto.subtle.digest('SHA-256', Uint8Array.from(JSON.stringify(this.possessor()))));
    }
}
