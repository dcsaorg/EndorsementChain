package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.TransportDocumentTransfer;
import dk.ange.jwtexperiment.TransportDocumentTransferRepository;
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
    TransportDocumentTransferRepository transportDocumentTransferRepo;

    @GetMapping("/transportDocumentTransfer/{transportDocumentTransferId}")
    @ResponseBody
    public ResponseEntity<TransportDocumentTransfer> getTransportDocumentTransfer(@PathVariable String transportDocumentTransferId){
        Optional<TransportDocumentTransfer> transportDocumentTransfer = transportDocumentTransferRepo.findById(transportDocumentTransferId);
        return new ResponseEntity<TransportDocumentTransfer>(transportDocumentTransfer.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/addTransportDocumentTransfer",consumes = {"application/json"},produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<TransportDocumentTransfer> addTransportDocumentTransfer(@RequestBody TransportDocumentTransfer transportDocumentTransfer, UriComponentsBuilder builder){
        transportDocumentTransferRepo.save(transportDocumentTransfer);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/addTransportDocumentTransfer/{id}").buildAndExpand(transportDocumentTransfer.getTdtHash()).toUri());
        return new ResponseEntity<TransportDocumentTransfer>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/updateTransportDocumentTransfer")
    @ResponseBody
    public ResponseEntity<TransportDocumentTransfer> updateTransportDocumentTransfer(@RequestBody TransportDocumentTransfer transportDocumentTransfer){
        if(transportDocumentTransfer != null){
            transportDocumentTransferRepo.save(transportDocumentTransfer);
        }
        return new ResponseEntity<TransportDocumentTransfer>(transportDocumentTransfer, HttpStatus.OK);
    }

    @DeleteMapping("/deleteTransportDocumentTransfer/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteTransportDocumentTransfer(@PathVariable String id){
        Optional<TransportDocumentTransfer> transportDocumentTransfer = transportDocumentTransferRepo.findById(id);
        transportDocumentTransferRepo.delete(transportDocumentTransfer.get());
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
