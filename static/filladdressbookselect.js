/*
 * A function to fill an HTML select with address book entries
 */

function fillAddressBookSelect(addressBookSelectDiv, transfereePublicKeyTextAreaId, receivingRegistryInputDiv, addressBookEntries) {
    let selectTransferTitle = document.getElementById(addressBookSelectDiv);
    addressBookEntries.forEach(function(entry){
        let newOption = document.createElement("option");
        newOption.value = JSON.stringify({publicKey: entry.publicKey, eblPlatform: entry.eblPlatform});
        newOption.thumbprint = entry.thumbprint;
        newOption.innerText = entry.name + " (" + entry.thumbprint + ")";
        selectTransferTitle.appendChild(newOption);
        document.getElementById(addressBookSelectDiv).addEventListener("change", function (event){
            document.getElementById(transfereePublicKeyTextAreaId).value = JSON.parse(event.target.value)["publicKey"];
            document.getElementById(receivingRegistryInputDiv).value = JSON.parse(event.target.value)["eblPlatform"];
        });
    });
}
