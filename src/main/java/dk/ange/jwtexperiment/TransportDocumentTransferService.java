package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.TransportDocumentTransfer;
import dk.ange.jwtexperiment.TransportDocumentTransferRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.KeyPair;


@Service
public class TransportDocumentTransferService {
    @Autowired
    private TransportDocumentTransferRepository transportDocumentTransferRepository;

    @Autowired
    private KeyPair platformKeyPair;

    public Optional<TransportDocumentTransfer> findById(String id) {
        return transportDocumentTransferRepository.findById(id);
    }

    public void save(TransportDocumentTransfer transportDocumentTransfer) throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException, com.nimbusds.jose.JOSEException {
        String previousTDThash = transportDocumentTransfer.getPreviousTDThash();
        if (previousTDThash != null) {
            Optional<TransportDocumentTransfer> previousTDT = transportDocumentTransferRepository.findById(previousTDThash);
            previousTDT.get().setTransferStatus("transferred");
            transportDocumentTransferRepository.save(previousTDT.get());
        }
        if (transportDocumentTransfer.isInterRegistryTransfer()) {
            transportDocumentTransfer.addPlatformSignature(platformKeyPair);
            transportDocumentTransfer.setTransferStatus("transferred");
        }
        transportDocumentTransferRepository.save(transportDocumentTransfer);
    }

    public void delete(TransportDocumentTransfer transportDocumentTransfer) {
        transportDocumentTransferRepository.delete(transportDocumentTransfer);
    }

}
