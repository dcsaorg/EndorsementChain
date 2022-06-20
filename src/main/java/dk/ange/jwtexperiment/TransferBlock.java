package dk.ange.jwtexperiment;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObjectJSON;
import com.nimbusds.jose.crypto.RSASSASigner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

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
    return transferBlockJson.hasNonNull("nextRegistryJWK");
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

}
