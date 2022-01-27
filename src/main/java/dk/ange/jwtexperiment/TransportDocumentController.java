package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.TransportDocument;
import dk.ange.jwtexperiment.TransportDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.io.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;


@RestController
@RequestMapping("/api/v1")
public class TransportDocumentController {
    @Autowired
    TransportDocumentRepository transportDocumentRepo;

    @GetMapping(value = "/transport-documents/{transportDocumentId}", produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<TransportDocument> getTransportDocument(@PathVariable String transportDocumentId){
        Optional<TransportDocument> transportDocument = transportDocumentRepo.findById(transportDocumentId);
        return new ResponseEntity<TransportDocument>(transportDocument.get(), HttpStatus.OK);
    }

    @GetMapping(value = "/transport-documents/{transportDocumentId}/pdf", produces = {"application/pdf"})
    @ResponseBody
    public byte[] getTransportDocumentAsPdf(@PathVariable String transportDocumentId) throws java.io.IOException, java.lang.IllegalAccessException {
        Optional<TransportDocument> transportDocument = transportDocumentRepo.findById(transportDocumentId);
        BillOfLading bol = new BillOfLading(transportDocument.get().getTransportDocumentJson());
        PDDocument bolPdf = bol.toPdf();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bolPdf.save(baos);
        bolPdf.close();
        return baos.toByteArray();
    }

    @PostMapping(value = "/transport-documents", consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<TransportDocument> addTransportDocument(@RequestBody TransportDocument transportDocument, UriComponentsBuilder builder){
        transportDocumentRepo.save(transportDocument);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/transport-document/{id}").buildAndExpand(transportDocument.getDocumentHash()).toUri());
        return new ResponseEntity<TransportDocument>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/transport-documents")
    @ResponseBody
    public ResponseEntity<TransportDocument> updateTransportDocument(@RequestBody TransportDocument transportDocument){
        if(transportDocument != null){
            transportDocumentRepo.save(transportDocument);
        }
        return new ResponseEntity<TransportDocument>(transportDocument, HttpStatus.OK);
    }

    @DeleteMapping("/transport-documents/{transportDocumentId}")
    @ResponseBody
    public ResponseEntity<Void> deleteTransportDocument(@PathVariable String transportDocumentId){
        Optional<TransportDocument> transportDocument = transportDocumentRepo.findById(transportDocumentId);
        transportDocumentRepo.delete(transportDocument.get());
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
