package dk.ange.jwtexperiment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportDocumentTransferRepository extends JpaRepository<TransportDocumentTransfer, String> {
}
