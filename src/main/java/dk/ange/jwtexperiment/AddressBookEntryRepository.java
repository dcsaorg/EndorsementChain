package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.AddressBookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressBookEntryRepository extends JpaRepository<AddressBookEntry, String> {
}
