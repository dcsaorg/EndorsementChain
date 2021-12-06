package dk.ange.jwtexperiment;

import javax.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "addressbookentry")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AddressBookEntry {

    @Id
    @Column
    private String id;

    @Column
    private String name;

    @Column
    private String publicKey;

}
