package dk.ange.jwtexperiment;

import javax.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "addressbookentry")
@Getter @Setter @RequiredArgsConstructor @NoArgsConstructor
public class AddressBookEntry {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    @NonNull
    private String thumbprint;

    @Column(columnDefinition="text")
    @NonNull
    private String name;

    @Column(columnDefinition="text")
    @NonNull
    private String publicKey;

    @Column
    @NonNull
    private String eblPlatform;
}
