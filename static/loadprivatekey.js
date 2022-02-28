function loadPrivateKeyFromFile(loadKey, keyTextArea){
    const loadChosenFile = document.getElementById(loadKey).files[0];
    const PrivateKeyfileReader = new FileReader();
    PrivateKeyfileReader.onload = function(fileLoadedEvent) {
        const textFromPrivateKey = fileLoadedEvent.target.result;
        document.getElementById(keyTextArea).value = textFromPrivateKey;
    };
    PrivateKeyfileReader.readAsText(loadChosenFile, "UTF-8");
}