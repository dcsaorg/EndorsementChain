package dk.ange.jwtexperiment;

import javax.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "addressbookentry")
@Getter @Setter @RequiredArgsConstructor @NoArgsConstructor
public class AddressBookEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column(columnDefinition="text")
    @NonNull
    private String name;

    @Column(columnDefinition="text")
    @NonNull
    private String publicKey;
}
