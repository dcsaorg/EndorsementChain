package dk.ange.jwtexperiment;

import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.InvalidJwtException;

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

    /*
     * Extract the JSON structure from the JWT
     */
    public JwtClaims asJwtClaims() throws InvalidJwtException {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
            .setSkipSignatureVerification()
            .build();
        JwtClaims jsonTDT = jwtConsumer.processToClaims(transportDocumentTransfer);
        return jsonTDT;
    }
}
