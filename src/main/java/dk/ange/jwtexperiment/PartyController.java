package dk.ange.jwtexperiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PartyController {
  @Autowired PartyRepository partyRepository;

  @GetMapping("/parties/{partyReference}")
  @CrossOrigin(origins = "*")
  @ResponseBody
  public ResponseEntity<Party> getAddressBookEntry(@PathVariable String partyReference) {
    return partyRepository
        .findByPartyReference(partyReference)
        .map(party -> ResponseEntity.ok().body(party))
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/parties")
  @CrossOrigin(origins = "*")
  @ResponseBody
  public ResponseEntity<List<Party>> getAddressBookEntries(
      @RequestParam(required = false) String thumbprint) {
    final List<Party> addressBookEntries =
        ObjectUtils.isEmpty(thumbprint)
            ? partyRepository.findAll()
            : partyRepository.findByThumbprint(thumbprint);
    return new ResponseEntity<>(addressBookEntries, HttpStatus.OK);
  }

  @PostMapping(
      value = "/parties",
      consumes = {"application/json"},
      produces = {"application/json"})
  @ResponseBody
  public ResponseEntity<Party> addAddressBookEntry(
      @RequestBody Party party, UriComponentsBuilder builder) {
    party.setPartyReference(UUID.randomUUID().toString());
    partyRepository.save(party);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(
        builder.path("/api/v1/address-book-entries/{id}").buildAndExpand(party.getId()).toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }
}
