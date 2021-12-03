package dk.ange.jwtexperiment;

import dk.ange.jwtexperiment.TransportDocumentTransfer;
import dk.ange.jwtexperiment.TransportDocumentTransferRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.jose4j.jwt.consumer.InvalidJwtException;

@Service
public class TransportDocumentTransferService {
    @Autowired
    private TransportDocumentTransferRepository transportDocumentTransferRepository;

    public Optional<TransportDocumentTransfer> findById(String id) {
        return transportDocumentTransferRepository.findById(id);
    }

    public void save(TransportDocumentTransfer transportDocumentTransfer) throws InvalidJwtException {
        Object previousTDThash = transportDocumentTransfer.asJwtClaims().getClaimValue("previousTDThash");
        if (previousTDThash != null) {
            Optional<TransportDocumentTransfer> previousTDT = transportDocumentTransferRepository.findById(previousTDThash.toString());
            previousTDT.get().setTransferStatus("transferred");
            transportDocumentTransferRepository.save(previousTDT.get());
        }
        transportDocumentTransferRepository.save(transportDocumentTransfer);
    }

    public void delete(TransportDocumentTransfer transportDocumentTransfer) {
        transportDocumentTransferRepository.delete(transportDocumentTransfer);
    }

}
