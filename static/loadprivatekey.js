/*
 * A function to load a text (a private key) into a text area
 */

function loadPrivateKeyFromFile(loadKey, keyTextArea){
    const loadChosenFile = document.getElementById(loadKey).files[0];
    const privateKeyfileReader = new FileReader();
    privateKeyfileReader.onload = function(fileLoadedEvent) {
        const textFromPrivateKey = fileLoadedEvent.target.result;
        document.getElementById(keyTextArea).value = textFromPrivateKey;
    };
    privateKeyfileReader.readAsText(loadChosenFile, "UTF-8");
}
