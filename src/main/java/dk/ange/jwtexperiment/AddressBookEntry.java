package dk.ange.jwtexperiment;

import javax.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "addressbookentry")
@Getter @Setter @AllArgsConstructor
public class AddressBookEntry {
    @Id
    @Column
    private String id;

    @Column(columnDefinition="text")
    @NonNull
    private String name;

    @Column(columnDefinition="text")
    @NonNull
    private String publicKey;
}
