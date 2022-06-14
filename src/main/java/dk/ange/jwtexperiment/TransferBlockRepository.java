package dk.ange.jwtexperiment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferBlockRepository extends JpaRepository<TransferBlock, String> {
}
