package dk.ange.jwtexperiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class TransferBlockService {
  @Autowired private TransferBlockRepository transferBlockRepository;

  @Autowired private KeyPair platformKeyPair;

  @Autowired private ObjectMapper mapper;

  public Optional<TransferBlock> findById(String id) {
    return transferBlockRepository.findById(id);
  }

  public String save(TransferBlockRequest transferBlockRequest)
      throws java.text.ParseException, IOException, com.nimbusds.jose.JOSEException,
          NoSuchAlgorithmException {
    String rawTransferBlockRequest = mapper.writeValueAsString(transferBlockRequest);
    TransferBlock transferBlock = TransferBlock.of(rawTransferBlockRequest);

    String previousTDThash = transferBlock.previousTransferBlockHash();

    if (previousTDThash != null) {
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

    return transferBlock.getTransferBlockHash();
  }

  public void delete(TransferBlock transferBlock) {
    transferBlockRepository.delete(transferBlock);
  }
}
