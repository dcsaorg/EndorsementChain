package dk.ange.jwtexperiment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class TransferBlockService {
  @Autowired private TransferBlockRepository transferBlockRepository;
  @Autowired private PartyRepository partyRepository;
  @Autowired private RestTemplate restTemplate;
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

  public Optional<String> fetchTransferBlockByNotification(final TransferBlockNotification transferBlockNotification) throws URISyntaxException, ParseException, IOException, NoSuchAlgorithmException, JOSEException {
    final URI transferBlockUrl = new URI(transferBlockNotification.getTransferBlockURL());

    String transferBlockHash = null;
    if(!isValidTransferBlockHost(transferBlockUrl.getHost())) {
      return Optional.empty();
    }
    ResponseEntity<TransferBlock> transferBlockResponseEntity = restTemplate.getForEntity(transferBlockUrl, TransferBlock.class);
    if(transferBlockResponseEntity.getStatusCode().is2xxSuccessful()) {
      TransferBlock transferBlock = transferBlockResponseEntity.getBody();
      TransferBlockRequest transferBlockRequest = mapper.readValue(transferBlock.getTransferBlock(), TransferBlockRequest.class);
      transferBlockHash = save(transferBlockRequest);
    }
    return Optional.ofNullable(transferBlockHash);
  }

  private boolean isValidTransferBlockHost(String host) {
    return partyRepository.findByEblPlatformContains(host).isPresent();
  }
}
