package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.AddressBookEntry;
import dk.ange.jwtexperiment.AddressBookEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1")
public class AddressBookEntryController {
    @Autowired
    AddressBookEntryRepository addressBookEntryRepo;

    @GetMapping("/AddressBookEntry/{AddressBookEntryId}")
    @ResponseBody
    public ResponseEntity<AddressBookEntry> getAddressBookEntry(@PathVariable String AddressBookEntryId){
        Optional<AddressBookEntry> addressBookEntry = addressBookEntryRepo.findById(AddressBookEntryId);
        return new ResponseEntity<AddressBookEntry>(addressBookEntry.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/addAddressBookEntry",consumes = {"application/json"},produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<AddressBookEntry> getAddressBookEntry(@RequestBody AddressBookEntry addressBookEntry, UriComponentsBuilder builder){
        addressBookEntryRepo.save(addressBookEntry);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/addAddressBookEntry/{id}").buildAndExpand(addressBookEntry.getName()).toUri());
        return new ResponseEntity<AddressBookEntry>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/updateAddressBookEntry")
    @ResponseBody
    public ResponseEntity<AddressBookEntry> updateAddressBookEntry(@RequestBody AddressBookEntry addressBookEntry){
        if(addressBookEntry != null){
            addressBookEntryRepo.save(addressBookEntry);
        }
        return new ResponseEntity<AddressBookEntry>(addressBookEntry, HttpStatus.OK);
    }

    @DeleteMapping("/deleteAddressBookEntry/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAddressBookEntry(@PathVariable String id){
        Optional<AddressBookEntry> addressBookEntry = addressBookEntryRepo.findById(id);
        addressBookEntryRepo.delete(addressBookEntry.get());
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
