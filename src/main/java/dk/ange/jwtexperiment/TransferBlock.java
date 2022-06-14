package dk.ange.jwtexperiment;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.KeyPair;

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
   * The hash of the previous transferBlock in the chain (they are supposed to come in chains).
   * Included for convenience, the order of the blocks could in theory be deduced from the tranferee / transferrer
   * pairs of the set of transfer blocks
   */
  @Column(columnDefinition = "text")
  private String previousTransferBlockHash;

  /*
   * The transfer block as such. Type (possession, title) depends on the json itself
   */
  @Column(columnDefinition = "text")
  private String transferBlock;

  /*
   * The transfer status ("current", "transferred", "surrendered"), used for bookkeeping
   */
  @Column(columnDefinition = "varchar(255) default 'current'")
  private String transferStatus;

  private JsonNode transferBlockAsJsonNode()
      throws java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException {
    JWSObjectJSON transferBlockAsJson = JWSObjectJSON.parse(transferBlock);
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

  public void addPlatformSignature(KeyPair hostPlatformKeyPair)
      throws java.text.ParseException, com.nimbusds.jose.JOSEException {
    JWSObjectJSON transferBlockAsJson = JWSObjectJSON.parse(transferBlock);
    transferBlockAsJson.sign(
        new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
        new RSASSASigner(hostPlatformKeyPair.getPrivate()));
    transferBlock = transferBlockAsJson.serializeGeneral();
  }
}
