package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.AddressBookEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressBookEntryRepository extends JpaRepository<AddressBookEntry, Integer> {

    public List<AddressBookEntry> findByThumbprint(String thumbprint);

}
