package dk.ange.jwtexperiment;

import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.KeyPair;


@Service
public class TransferBlockService {
    @Autowired
    private TransferBlockRepository transferBlockRepository;

    @Autowired
    private KeyPair platformKeyPair;

    public Optional<TransferBlock> findById(String id) {
        return transferBlockRepository.findById(id);
    }

    public void save(TransferBlock transferBlock) throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException, com.nimbusds.jose.JOSEException {
        String previousTDThash = transferBlock.getPreviousTransferBlockHash();
        if (previousTDThash != null ) {
            Optional<TransferBlock> previousTDT = transferBlockRepository.findById(previousTDThash);
            if(previousTDT.isPresent()) { //TODO: if there is no previous TDT, getting here should only be possible as a result of a
                                          //cross-platform import and the signature of the previous platform should then be verified
                    previousTDT.get().setTransferStatus("transferred");
                    transferBlockRepository.save(previousTDT.get());
            }
        }
        if (transferBlock.isCrossPlatformTransfer()) {
            transferBlock.addPlatformSignature(platformKeyPair);
            transferBlock.setTransferStatus("transferred");
        }
        transferBlockRepository.save(transferBlock);
    }

    public void delete(TransferBlock transferBlock) {
        transferBlockRepository.delete(transferBlock);
    }

}
