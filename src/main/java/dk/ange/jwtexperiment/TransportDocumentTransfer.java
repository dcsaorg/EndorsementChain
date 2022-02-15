package dk.ange.jwtexperiment;

import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.persistence.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

/*
 * The Transport Document Transfer object
 * Represents a transfer from a possessor (or a holder) to the next possessor (or holder, endorsee)
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

    public String getPreviousTDThash() throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JWSObjectJSON transferBlockAsJson = JWSObjectJSON.parse(transportDocumentTransfer);
        JsonNode transferBlock = (JsonNode) mapper.readTree(transferBlockAsJson.getPayload().toString());
        return transferBlock.hasNonNull("previousTDThash")? transferBlock.get("previousTDThash").textValue() : null;
    }
}
