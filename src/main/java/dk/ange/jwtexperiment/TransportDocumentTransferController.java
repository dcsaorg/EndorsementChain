package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.TransportDocumentTransfer;
import dk.ange.jwtexperiment.TransportDocumentTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;
import java.util.Optional;

//test with curl -X POST -H "Content-Type: application/json" -d '{"tdtHash": "dsfds", "transportDocumentTransfer": "sdfs"}' http://localhost:9090/api/v1/add

@RestController
@RequestMapping("/api/v1")
public class TransportDocumentTransferController {
    @Autowired
    TransportDocumentTransferService transportDocumentTransferService;

    @GetMapping("/transport-document-transfers/{transportDocumentTransferId}")
    @ResponseBody
    public ResponseEntity<TransportDocumentTransfer> getTransportDocumentTransfer(@PathVariable String transportDocumentTransferId){
        Optional<TransportDocumentTransfer> transportDocumentTransfer = transportDocumentTransferService.findById(transportDocumentTransferId);
        return new ResponseEntity<TransportDocumentTransfer>(transportDocumentTransfer.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/transport-document-transfers",consumes = {"application/json"},produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<TransportDocumentTransfer> addTransportDocumentTransfer(@RequestBody TransportDocumentTransfer transportDocumentTransfer, UriComponentsBuilder builder) throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
        transportDocumentTransfer.setTransferStatus("current");
        transportDocumentTransferService.save(transportDocumentTransfer);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/transport-document-transfers/{id}").buildAndExpand(transportDocumentTransfer.getTdtHash()).toUri());
        return new ResponseEntity<TransportDocumentTransfer>(headers, HttpStatus.CREATED);
    }

}
