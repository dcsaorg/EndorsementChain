package dk.ange.jwtexperiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

//test with curl -X POST -H "Content-Type: application/json" -d '{"tdtHash": "dsfds", "transportDocumentTransfer": "sdfs"}' http://localhost:9090/api/v1/add

@RestController
@RequestMapping("/api/v1")
public class TransferBlockController {
    @Autowired
    TransferBlockService transferBlockService;

    @GetMapping("/transferblocks/{transferBlockId}")
    @CrossOrigin(origins = "*")
    @ResponseBody
    public ResponseEntity<TransferBlock> getTransportDocumentTransfer(@PathVariable String transferBlockId){
        Optional<TransferBlock> transportDocumentTransfer = transferBlockService.findById(transferBlockId);
        return new ResponseEntity<>(transportDocumentTransfer.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/transferblocks",consumes = {"application/json"},produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<TransferBlock> addTransportDocumentTransfer(@RequestBody TransferBlock transferBlock, UriComponentsBuilder builder) throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException, com.nimbusds.jose.JOSEException {
        transferBlock.setTransferStatus("current");
        transferBlockService.save(transferBlock);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/api/v1/transferblocks/{id}").buildAndExpand(transferBlock.getTransferBlockHash()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

}
