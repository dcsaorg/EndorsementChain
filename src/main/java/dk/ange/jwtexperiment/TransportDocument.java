package dk.ange.jwtexperiment;

import javax.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "transportdocument")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransportDocument {

    @Id
    @Column
    private String documentHash;

    @Column(columnDefinition="TEXT")
    private String transportDocumentJson;

}
