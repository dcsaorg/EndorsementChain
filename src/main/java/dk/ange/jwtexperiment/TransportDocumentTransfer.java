package dk.ange.jwtexperiment;

import javax.persistence.*;

@Entity
@Table(name = "transportdocumenttransfer")
public class TransportDocumentTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String tdtHash;

    @Column(columnDefinition="text")
    private String transportDocumentTransfer;

    public long getId() {
        return id;
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
