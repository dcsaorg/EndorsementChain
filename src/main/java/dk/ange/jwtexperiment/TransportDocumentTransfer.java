package dk.ange.jwtexperiment;

import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "transportdocumenttransfer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransportDocumentTransfer {

    @Id
    @Column
    private String tdtHash;

    @Column(columnDefinition="text")
    private String transportDocumentTransfer;

    @Column
    private String transferStatus; //"current", "transferred", "surrendered"

}
