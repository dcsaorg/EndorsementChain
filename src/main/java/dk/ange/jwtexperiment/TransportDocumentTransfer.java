package dk.ange.jwtexperiment;

import javax.persistence.*;

@Entity
@Table(name = "transportdocumenttransfer")
public class TransportDocumentTransfer {

    @Id
    @Column
    private String tdtHash;

    @Column(columnDefinition="text")
    private String transportDocumentTransfer;

    @Column
    private String transferStatus; //"current", "transferred", "surrendered"

    public String getId() {
        return tdtHash;
    }

    public TransportDocumentTransfer() {
    }

    public TransportDocumentTransfer(String tdtHash, String transportDocumentTransfer) {
        this.tdtHash = tdtHash;
        this.transportDocumentTransfer = transportDocumentTransfer;
    }

    public String tdtHash() {
        return tdtHash;
    }

    public String getTdtHash() {
        return tdtHash;
    }

    public String getTransportDocumentTransfer() {
        return transportDocumentTransfer;
    }
}
