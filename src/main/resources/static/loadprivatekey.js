/*
 * A function to load a text (a private key) into a text area
 * Optionally checks that the private corresponds to a provided public key (JWK)
 */

function loadPrivateKeyFromFile(loadKey, keyTextArea, publicKeyJWK, whenValidDo){
    const loadChosenFile = document.getElementById(loadKey).files[0];
    const privateKeyfileReader = new FileReader();
    privateKeyfileReader.onload = function(fileLoadedEvent) {
        const textFromPrivateKey = fileLoadedEvent.target.result;
        document.getElementById(keyTextArea).value = textFromPrivateKey;
        if (publicKeyJWK) {
            let challengeDummyBlock = new TransferBlock();
            challengeDummyBlock.init(null, null, textFromPrivateKey);
            const isValid = challengeDummyBlock.verifyNth(0, KEYUTIL.getKey(publicKeyJWK), {alg: ['RS256']});
            whenValidDo(isValid);
        }
    };
    privateKeyfileReader.readAsText(loadChosenFile, "UTF-8");
}
