package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.TransportDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportDocumentRepository extends JpaRepository<TransportDocument, String> {
}
