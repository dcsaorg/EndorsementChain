package dk.ange.jwtexperiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ange.jwtexperiment.TransportDocumentTransfer;
import dk.ange.jwtexperiment.TransportDocumentTransferRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.KeyPair;


@Service
public class TransportDocumentTransferService {
    @Autowired
    private TransportDocumentTransferRepository transportDocumentTransferRepository;

    @Autowired
    private KeyPair platformKeyPair;

    @Autowired
    private ObjectMapper mapper;

    public Optional<TransportDocumentTransfer> findById(String id) {
        return transportDocumentTransferRepository.findById(id);
    }

    public String save(TransferBlockRequest transferBlockRequest) throws java.text.ParseException, IOException, com.nimbusds.jose.JOSEException, NoSuchAlgorithmException {
        String previousTransferBlockHash = null; //ToDo previous Hash will be part of payload
        String rawTransferBlockRequest = mapper.writeValueAsString(transferBlockRequest);
        TransportDocumentTransfer transportDocumentTransfer = TransportDocumentTransfer.of(previousTransferBlockHash, rawTransferBlockRequest);

        String previousTDThash = transportDocumentTransfer.getPreviousTransferBlockHash();
        if (previousTDThash != null ) {
            Optional<TransportDocumentTransfer> previousTDT = transportDocumentTransferRepository.findById(previousTDThash);
            if(previousTDT.isPresent()) { //TODO: if there is no previous TDT, getting here should only be possible as a result of a
                                          //cross-platform import and the signature of the previous platform should then be verified
                    previousTDT.get().setTransferStatus("transferred");
                    transportDocumentTransferRepository.save(previousTDT.get());
            }
        }
        if (transportDocumentTransfer.isCrossPlatformTransfer()) {
            transportDocumentTransfer.addPlatformSignature(platformKeyPair);
            transportDocumentTransfer.setTransferStatus("transferred");
        }
        transportDocumentTransferRepository.save(transportDocumentTransfer);

        return transportDocumentTransfer.getTransferBlockHash();
    }

    public void delete(TransportDocumentTransfer transportDocumentTransfer) {
        transportDocumentTransferRepository.delete(transportDocumentTransfer);
    }

}
