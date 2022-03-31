package dk.ange.jwtexperiment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.util.Optional;
import java.util.List;

@DataJpaTest
public class AddressBookEntryTest {
    
    @Autowired
    private AddressBookEntryRepository repository;

    @Test
    public void saveEntry() {
        final AddressBookEntry testEntry = new AddressBookEntry("044751664ed6f00162a6","Ali Engros", "044751664ed6f00162a66fbe92b0752f4c64292f6a718ba48b3fbd7714c8e6719a1df6eef21c814d9a0329fcf29066de5bb02432d51bca55772696b6d7cbbc896b", "");
        repository.save(testEntry);
        Optional<AddressBookEntry> entry = repository.findById(testEntry.getId());
        Assertions.assertEquals(entry.get().getThumbprint(),"044751664ed6f00162a6");
    }

    @Test
    public void testGetAllEntries() {
        repository.save(new AddressBookEntry("044473a9a4584ce8251d","Zuan Zu", "044473a9a4584ce8251dcbbcfc7e69fbc476eef3e42e3cb5c329733e8264d3bc00045e796d1ad441b57ff26cb6ded1e1d4a6c8bce871b1138a7f61ae4be07d4c88", ""));
        repository.save(new AddressBookEntry("04ee20715e555c0ea0c3","Deep Sea Industries", "04ee20715e555c0ea0c3d0bbe3d8947878cd83a4a84dbc344d73fa22c07a02ff92c2868381b5cb7de8f4d3a87d886145cbdbe0ef59c643375c0128772fd3e263fd", ""));
        List<AddressBookEntry> entryList = repository.findAll();
        Assertions.assertEquals(entryList.size(),2);
        Optional<AddressBookEntry> entry = repository.findById(entryList.get(0).getId());
        Assertions.assertEquals(entry.get().getName(),"Zuan Zu");
    }

    @Test
    public void testGetEntriesWithThumbprints() {
        repository.save(new AddressBookEntry("044473a9a4584ce8251d","Zuan Zu", "044473a9a4584ce8251dcbbcfc7e69fbc476eef3e42e3cb5c329733e8264d3bc00045e796d1ad441b57ff26cb6ded1e1d4a6c8bce871b1138a7f61ae4be07d4c88", "ebl.server1.com"));
        repository.save(new AddressBookEntry("044473a9a4584ce8251d","Zuan Zu", "044473a9a4584ce8251dcbbcfc7e69fbc476eef3e42e3cb5c329733e8264d3bc00045e796d1ad441b57ff26cb6ded1e1d4a6c8bce871b1138a7f61ae4be07d4c88", "ebl.server2.com"));
        repository.save(new AddressBookEntry("71b1138a7f61ae4be07d4c88","Third Customer", "96d1ad441b57ff26cb6ded1e1d4a6c8bce871b1138a7f61ae4be07d4c88044473a9a4584ce8251dcbbcfc7e69fbc476eef3e42e3cb5c329733e8264d3bc00045e7", "ebl.server2.com"));
        List<AddressBookEntry> entryList = repository.findByThumbprint("044473a9a4584ce8251d");
        Assertions.assertEquals(entryList.size(),2);
    }
}
