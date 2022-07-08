package dk.ange.jwtexperiment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * The Transport Document Transfer object
 * Represents a transfer itself (the *transfer block") from a possessor (or a holder) to the next possessor (or holder, endorsee)
 * and contains additional bookkeeping information (the hash of the block and its status)
 */

@Entity
@Table(name = "transferblock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferBlock {

  /*
   * The hash of the transferBlock. Included for convenience, could be calculated from the transfer block
   */
  @Id @Column private String transferBlockHash;

  /*
   * The transfer block as such. Type (possession, title) depends on the json itself
   */
  @Column(columnDefinition = "text")
  private String transferBlock;

  @JsonRawValue
  public String getTransferBlock() {
    return transferBlock;
  }

  public void setTransferBlock(JsonNode node) {
    this.transferBlock = node.toString();
  }

  /*
   * The transfer status ("current", "transferred", "surrendered"), used for bookkeeping
   */
  @Column(columnDefinition = "varchar(255) default 'current'")
  private String transferStatus;

  public static TransferBlock of(String rawTransferBlockRequest) throws NoSuchAlgorithmException {
    String transferBlockHash = DigestUtils.sha256Hex(rawTransferBlockRequest);
    return new TransferBlock(transferBlockHash, rawTransferBlockRequest, "current");
  }

  private JsonNode transferBlockAsJsonNode()
      throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
    JWSObjectJSON transferBlockAsJson = JWSObjectJSON.parse(this.getTransferBlock());
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(transferBlockAsJson.getPayload().toString());
  }

  /*
   * Tells if the transfer is to another platform (identified by its public key)
   */
  public boolean isCrossPlatformTransfer()
      throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
    JsonNode transferBlockJson = transferBlockAsJsonNode();
    return transferBlockJson.get("blockPayload").hasNonNull("nextRegistryJWK");
  }

  /*
   * Appends a second (the platform's) signature
   */
  public void addPlatformSignature(KeyPair hostPlatformKeyPair)
      throws java.text.ParseException, com.nimbusds.jose.JOSEException {
    JWSObjectJSON transferBlockAsJson = JWSObjectJSON.parse(this.getTransferBlock());
    transferBlockAsJson.sign(
        new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
        new RSASSASigner(hostPlatformKeyPair.getPrivate()));
    transferBlock = transferBlockAsJson.serializeGeneral();
  }

  /*
   * Extract the pointer to the previous transfer block
   */
  public final String previousTransferBlockHash()
      throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
    JsonNode transferBlockJson = transferBlockAsJsonNode();
    String previousBlockHash = transferBlockJson.get("previousBlockHash").asText();
    if (previousBlockHash == "null") {
      return null;
    }
    return previousBlockHash;
  }

  @SneakyThrows
  public Map<String, Object> extractPayloadFromTransferBlock() {
    JWSObjectJSON exportTransferBlockJson = JWSObjectJSON.parse(this.getTransferBlock());
    return exportTransferBlockJson.getPayload().toJSONObject();
  }

  @JsonIgnore
  public String getTitleTransferBlockHash() {
    return Optional.ofNullable(this.extractBlockPayloadFromTransferBlockPayload())
        .map(stringObjectMap -> stringObjectMap.get("titleTransferBlockHash"))
        .map(Object::toString)
        .orElseThrow(() -> new IllegalStateException("No titleTransferBlockHash available for transfer"));
  }

  @JsonIgnore
  public String getDocumentHash() {
    return Optional.ofNullable(this.extractBlockPayloadFromTransferBlockPayload())
      .map(stringObjectMap -> stringObjectMap.get("documentHash"))
      .map(Object::toString)
      .orElseThrow(() -> new IllegalStateException("No documentHash present in titletransferblock"));
  }

  private Map<String, Object> extractBlockPayloadFromTransferBlockPayload() {
    return extractPayloadFromTransferBlock().entrySet().stream()
      .filter(stringObjectEntry -> stringObjectEntry.getKey().equals("blockPayload"))
      .map(Map.Entry::getValue)
      .flatMap(o -> ((Map<String, Object>) o).entrySet().stream())
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public void transformToImportTransportBlock(
      String previousRegistryUrl, KeyPair hostPlatformKeyPair) throws JOSEException {

    Map<String, Object> tmpTransferBlockPayload = extractPayloadFromTransferBlock();
    Map<String, String> transferee = correctTransfereeFieldOrder(tmpTransferBlockPayload);

    Map<String, String> tmpTransferBlockInnerPayload =
        (Map<String, String>) tmpTransferBlockPayload.get("blockPayload");

    tmpTransferBlockInnerPayload.putIfAbsent("previousRegistryURL", previousRegistryUrl);
    tmpTransferBlockInnerPayload.remove("nextRegistryHost");
    tmpTransferBlockInnerPayload.remove("nextRegistryJWK");
    tmpTransferBlockPayload.replace("transferee", transferee);
    Payload importTransferBlockJWSPayload = new Payload(tmpTransferBlockPayload);

    JWSObjectJSON importTransferBlockJson = new JWSObjectJSON(importTransferBlockJWSPayload);
    importTransferBlockJson.sign(
        new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
        new RSASSASigner(hostPlatformKeyPair.getPrivate()));

    transferBlock = importTransferBlockJson.serializeGeneral();
  }

  // This is a workaround since the UI calculates the fingerprint on the transferee json object. So
  // the order of the fields in the JWK need to be fixed.
  // Ultimately this should be fixed by calculating the fingerprint in a stable manner
  private Map<String, String> correctTransfereeFieldOrder(
      Map<String, Object> tmpTransferBlockPayload) {
    Map<String, String> transferee = new LinkedHashMap<>();
    transferee.put("kty", ((Map) tmpTransferBlockPayload.get("transferee")).get("kty").toString());
    transferee.put("n", ((Map) tmpTransferBlockPayload.get("transferee")).get("n").toString());
    transferee.put("e", ((Map) tmpTransferBlockPayload.get("transferee")).get("e").toString());
    return transferee;
  }
}
