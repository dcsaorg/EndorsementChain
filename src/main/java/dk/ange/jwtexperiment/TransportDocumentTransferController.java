package dk.ange.jwtexperiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

//test with curl -X POST -H "Content-Type: application/json" -d '{"tdtHash": "dsfds", "transportDocumentTransfer": "sdfs"}' http://localhost:9090/api/v1/transport-document-transfers

@RestController
@RequestMapping("/api/v1")
public class TransportDocumentTransferController {
    @Autowired
    TransportDocumentTransferService transportDocumentTransferService;

    @GetMapping("/transport-document-transfers/{transportDocumentTransferId}")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<TransportDocumentTransfer> getTransportDocumentTransfer(@PathVariable String transportDocumentTransferId){
        Optional<TransportDocumentTransfer> transportDocumentTransfer = transportDocumentTransferService.findById(transportDocumentTransferId);
        return new ResponseEntity<>(transportDocumentTransfer.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/transport-document-transfers",consumes = {"application/json"},produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<TransportDocumentTransfer> addTransportDocumentTransfer(@RequestBody TransferBlockRequest transferBlockRequest, UriComponentsBuilder builder) throws java.text.ParseException, IOException, com.nimbusds.jose.JOSEException, NoSuchAlgorithmException {
        String hash = transportDocumentTransferService.save(transferBlockRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/transport-document-transfers/{id}").buildAndExpand(hash).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

}
