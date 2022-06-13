package dk.ange.jwtexperiment;

import javax.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.util.UUID;

@Entity
@Table(name = "party")
@Getter @Setter @RequiredArgsConstructor @NoArgsConstructor
public class Party {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID id;

    @Column(unique = true)
    @NonNull
    private String partyReference;

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
