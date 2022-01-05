package dk.ange.jwtexperiment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.Optional;
import java.util.List;

@DataJpaTest
public class AddressBookEntryTest {
    
    @Autowired
    private AddressBookEntryRepository repository;

    @Test
    public void saveEntry() {
        repository.save(new AddressBookEntry("1", "Ali Engros", "1234321"));
        Optional<AddressBookEntry> entry = repository.findById("1");
        Assertions.assertEquals(entry.get().getId(),"1");
    }

    @Test
    public void testGetAllEntries() {
        repository.save(new AddressBookEntry("2", "Zuan Zu", "89732984"));
        repository.save(new AddressBookEntry("3", "Hejmeddig", "01927341"));
        List<AddressBookEntry> entryList = repository.findAll();
        Assertions.assertEquals(entryList.size(),2);
    }
}
