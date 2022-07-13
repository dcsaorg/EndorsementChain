package dk.ange.jwtexperiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class TransferBlockService {
  @Autowired private TransferBlockRepository transferBlockRepository;
  @Autowired private TransportDocumentRepository transportDocumentRepository;
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
      if (previousTDT
          .isPresent()) { // TODO: if there is no previous TDT, getting here should only be possible
        // as a result of a
        // cross-platform import and the signature of the previous platform should then be verified
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

  public Optional<String> fetchTransferBlockByNotification(
      final TransferBlockNotification transferBlockNotification) throws URISyntaxException {
    URI transferBlockUrl = new URI(transferBlockNotification.getTransferBlockURL());

    ResponseEntity<TransferBlock> transferBlockResponseEntity =
        restTemplate.getForEntity(transferBlockUrl, TransferBlock.class);

    if (transferBlockResponseEntity.getStatusCode().isError()) {
      return Optional.empty();
    }

    String exportTitleTransferBlockHash =
        Optional.ofNullable(transferBlockResponseEntity.getBody())
            .map(TransferBlock::getTitleTransferBlockHash)
            .orElseThrow(() -> new IllegalStateException("Exportblock could not be retrieved."));

    getAndSaveTitleTransferBlock(transferBlockUrl, exportTitleTransferBlockHash);

    return Optional.ofNullable(transferBlockResponseEntity.getBody())
        .map(transferBlock -> generateAndSaveImportTransferBlock(transferBlockUrl, transferBlock));
  }

  private void getAndSaveTitleTransferBlock(URI transferBlockUrl, String titleTransferBlockHash) {
    String titleTransferBlockUrl =
        transferBlockUrl.getScheme()
            + "://"
            + transferBlockUrl.getHost()
            + ":"
            + transferBlockUrl.getPort()
            + "/api/v1/transferblocks/"
            + titleTransferBlockHash;
    ResponseEntity<TransferBlock> transferBlockResponseEntity =
        restTemplate.getForEntity(titleTransferBlockUrl, TransferBlock.class);

    if (transferBlockResponseEntity.getStatusCode().isError()) {
      throw new IllegalStateException("TitletransferBlock transfer failed");
    }

    Optional.ofNullable(transferBlockResponseEntity.getBody())
        .map(transferBlockRepository::save)
        .map(TransferBlock::getDocumentHash)
        .ifPresentOrElse(
            documentHash -> getAndSaveTransportDocument(transferBlockUrl, documentHash),
            () -> {
              throw new IllegalStateException("TitletransferBlock could not be retrieved");
            });
  }

  private void getAndSaveTransportDocument(URI transferBlockUrl, String documentHash) {
    String transportDocumentUrl =
        transferBlockUrl.getScheme()
            + "://"
            + transferBlockUrl.getHost()
            + ":"
            + transferBlockUrl.getPort()
            + "/api/v1/transport-documents/"
            + documentHash;

    ResponseEntity<TransportDocument> transferBlockResponseEntity =
        restTemplate.getForEntity(transportDocumentUrl, TransportDocument.class);

    if (transferBlockResponseEntity.getStatusCode().isError()) {
      throw new IllegalStateException("TransportDocument transfer failed");
    }
    TransportDocument transportDocument = transferBlockResponseEntity.getBody();
    transportDocumentRepository.save(transportDocument);
  }

  @SneakyThrows
  private String generateAndSaveImportTransferBlock(
      URI transferBlockUrl, TransferBlock exportTransferBlock) {
    String previousRegistryUrl = transferBlockUrl.toString();
    exportTransferBlock.transformToImportTransportBlock(previousRegistryUrl, platformKeyPair);
    TransferBlock importTransferBlock = TransferBlock.of(exportTransferBlock.getTransferBlock());
    transferBlockRepository.save(importTransferBlock);
    return importTransferBlock.getTransferBlockHash();
  }
}
