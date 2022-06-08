package dk.ange.jwtexperiment;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.util.Optional;


@RestController
@RequestMapping("${spring.application.api-path}")
public class TransportDocumentController {

    public static final String API_PATH = "/transport-documents";

    @Value("${spring.application.api-path}")
    private String contextPath;

    @Autowired
    TransportDocumentRepository transportDocumentRepo;

    @GetMapping(value = API_PATH + "/{transportDocumentId}", produces = {"application/json"})
    @ResponseBody
    @CrossOrigin(origins = "*")
    public ResponseEntity<TransportDocument> getTransportDocument(@PathVariable String transportDocumentId){
        Optional<TransportDocument> transportDocument = transportDocumentRepo.findById(transportDocumentId);
        return new ResponseEntity<TransportDocument>(transportDocument.get(), HttpStatus.OK);
    }

    @GetMapping(value = API_PATH + "/{transportDocumentId}/pdf", produces = {"application/pdf"})
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

    @PostMapping(value = API_PATH, consumes = {"application/json"}, produces = {"application/json"})
    @ResponseBody
    public ResponseEntity<TransportDocument> addTransportDocument(@RequestBody TransportDocument transportDocument, UriComponentsBuilder builder){
        transportDocumentRepo.save(transportDocument);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path(contextPath + API_PATH + "/{id}").buildAndExpand(transportDocument.getDocumentHash()).toUri());
        return new ResponseEntity<TransportDocument>(headers, HttpStatus.CREATED);
    }

    @PutMapping(API_PATH)
    @ResponseBody
    public ResponseEntity<TransportDocument> updateTransportDocument(@RequestBody TransportDocument transportDocument){
        if(transportDocument != null){
            transportDocumentRepo.save(transportDocument);
        }
        return new ResponseEntity<TransportDocument>(transportDocument, HttpStatus.OK);
    }

    @DeleteMapping(API_PATH + "/{transportDocumentId}")
    @ResponseBody
    public ResponseEntity<Void> deleteTransportDocument(@PathVariable String transportDocumentId){
        Optional<TransportDocument> transportDocument = transportDocumentRepo.findById(transportDocumentId);
        transportDocumentRepo.delete(transportDocument.get());
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
