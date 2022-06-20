package dk.ange.jwtexperiment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartyRepository extends JpaRepository<Party, UUID> {

    List<Party> findByThumbprint(String thumbprint);
    Optional<Party> findByPartyReference(String partyReference);
    Optional<Party> findByEblPlatformContains(String eblPlatform);
}
