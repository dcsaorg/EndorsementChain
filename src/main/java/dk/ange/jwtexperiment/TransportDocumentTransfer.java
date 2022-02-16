package dk.ange.jwtexperiment;

import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.security.KeyPair;

/*
 * The Transport Document Transfer object
 * Represents a transfer itself (the *transfer block") from a possessor (or a holder) to the next possessor (or holder, endorsee)
 * and contains additional bookkeeping information (the hash of the block and its status)
 */

@Entity
@Table(name = "transportdocumenttransfer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransportDocumentTransfer {

    @Id
    @Column
    private String tdtHash;

    @Column(columnDefinition="text")
    private String transportDocumentTransfer;

    @Column(columnDefinition = "varchar(255) default 'current'")
    private String transferStatus; //"current", "transferred", "surrendered"

    private JsonNode transferBlockAsJsonNode() throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
        JWSObjectJSON transferBlockAsJson = JWSObjectJSON.parse(transportDocumentTransfer);
        ObjectMapper mapper = new ObjectMapper();
        return (JsonNode) mapper.readTree(transferBlockAsJson.getPayload().toString());
    }

    public String getPreviousTDThash() throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
        JsonNode transferBlock = transferBlockAsJsonNode();
        return transferBlock.hasNonNull("previousTDThash")? transferBlock.get("previousTDThash").textValue() : null;
    }

    /*
     * Tells if the transfer is to another platform (identified by its public key)
     */
    public boolean isInterRegistryTransfer() throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
        JsonNode transferBlock = transferBlockAsJsonNode();
        return transferBlock.hasNonNull("nextRegistry");
    }

    public void addPlatformSignature(KeyPair hostPlatformKeyPair) throws java.text.ParseException, com.nimbusds.jose.JOSEException {
        JWSObjectJSON transferBlockAsJson = JWSObjectJSON.parse(transportDocumentTransfer);
        transferBlockAsJson.sign(
            new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
            new RSASSASigner(hostPlatformKeyPair.getPrivate())
        );
        transportDocumentTransfer = transferBlockAsJson.serializeGeneral();
    }
}
