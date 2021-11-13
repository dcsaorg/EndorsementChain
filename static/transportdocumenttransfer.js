/*
 * Javascript representation of the Transport Document Transfer object
 */

var TransportDocumentTransfer = function(holder, possessor, documentHash, isToOrder, previousTDThash, transfererPrivateKey) {
    this.payload = {holder: holder, possessor: possessor, documentHash: documentHash, isToOrder: isToOrder, previousTDThash: previousTDThash};
    this.asJWT = KJUR.jws.JWS.sign(null, {alg: "RS256"}, JSON.stringify(this.payload), transfererPrivateKey);
    this.tdtHash = async function() {
        return ArrayBuffertohex(await crypto.subtle.digest('SHA-256', Uint8Array.from(this.asJWT)));
    }
}
