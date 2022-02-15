package dk.ange.jwtexperiment;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;

import java.util.List;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonSerializationJoseTest {

    @Test
    public void createAndConsumeJWT() throws com.nimbusds.jose.JOSEException, java.text.ParseException {
        RSAKey rsaJWK = new RSAKeyGenerator(2048).keyID("123").generate();
        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();
        JWSSigner signer = new RSASSASigner(rsaJWK);
        JWSObject jwsObject = new JWSObject(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
            new Payload("In RSA we trust!")
        );
        jwsObject.sign(signer);
        String s = jwsObject.serialize();
        jwsObject = JWSObject.parse(s);
        JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);
        Assertions.assertTrue(jwsObject.verify(verifier));
        Assertions.assertEquals("In RSA we trust!", jwsObject.getPayload().toString());
    }

    @Test
    public void createCarrierToShipperToBankTransfer() throws com.nimbusds.jose.JOSEException, java.text.ParseException, com.fasterxml.jackson.core.JsonProcessingException  {
        RSAKey carrierJWK = new RSAKeyGenerator(2048).algorithm(JWSAlgorithm.RS256).keyID("123").generate();
        RSAKey shipperJWK = new RSAKeyGenerator(2048).algorithm(JWSAlgorithm.RS256).keyID("456").generate();
        RSAKey bankJWK = new RSAKeyGenerator(2048).algorithm(JWSAlgorithm.RS256).keyID("789").generate();
        RSAKey platformJWK = new RSAKeyGenerator(2048).algorithm(JWSAlgorithm.RS256).keyID("0ab").generate();
        RSAKey carrierPublicJWK = carrierJWK.toPublicJWK();
        RSAKey shipperPublicJWK = shipperJWK.toPublicJWK();
        RSAKey bankPublicJWK = bankJWK.toPublicJWK();
        RSAKey platformPublicJWK = platformJWK.toPublicJWK();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode transferBlock = mapper.createObjectNode();
        transferBlock.put("holder", shipperPublicJWK.toString());
        transferBlock.put("possessor", shipperPublicJWK.toString());
        transferBlock.put("documentHash", "afe69d1b7ddb203ce7fc8b4ed4b972f126e0749a4cde160127a92eb1c74a2fa6"); //echo "TestDocument" | sha256sum
        transferBlock.put("isToOrder", "true");
        final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(transferBlock);
        JWSObjectJSON transferBlockJws = new JWSObjectJSON(
            new Payload(mapper.writeValueAsString(transferBlock))
        );
        transferBlockJws.sign(
            new JWSHeader.Builder((JWSAlgorithm) carrierJWK.getAlgorithm())
                .keyID(carrierJWK.getKeyID())
                .build(),
            new RSASSASigner(carrierJWK)
        );
        final String firstTransferJson = transferBlockJws.serializeGeneral();

        //transfer to shipper
        JWSObjectJSON receivedBlockByShipper = JWSObjectJSON.parse(firstTransferJson);
        JWSObjectJSON.Signature sig1 = receivedBlockByShipper.getSignatures().get(0);
        sig1.verify(new RSASSAVerifier(carrierPublicJWK));
        Assertions.assertTrue(JWSObjectJSON.State.VERIFIED.equals(receivedBlockByShipper.getState()));

        System.out.println(receivedBlockByShipper.getPayload().toString());
        ObjectNode secondTransferBlock = (ObjectNode) mapper.readTree(receivedBlockByShipper.getPayload().toString());
        secondTransferBlock.put("possessor", bankPublicJWK.toString());
        JWSObjectJSON secondTransferBlockJWS = new JWSObjectJSON(
            new Payload(mapper.writeValueAsString(secondTransferBlock))
        );
        secondTransferBlockJWS.sign(
            new JWSHeader.Builder((JWSAlgorithm) shipperJWK.getAlgorithm())
                .keyID(shipperJWK.getKeyID())
                .build(),
            new RSASSASigner(shipperJWK)
        );

        //serialize to json (and back) and then sign by platform
        final String secondTransferJson = secondTransferBlockJWS.serializeGeneral();
        JWSObjectJSON receivedBlockByShipperPlatform = JWSObjectJSON.parse(secondTransferJson);
        receivedBlockByShipperPlatform.sign(
            new JWSHeader.Builder((JWSAlgorithm) platformJWK.getAlgorithm())
                .keyID(platformJWK.getKeyID())
                .build(),
            new RSASSASigner(platformJWK)
        );

        for (JWSObjectJSON.Signature sig: receivedBlockByShipperPlatform.getSignatures()) {
            if (shipperPublicJWK.getKeyID().equals(sig.getHeader().getKeyID())) {
                if (! sig.verify(new RSASSAVerifier(shipperPublicJWK))) {
                    System.out.println("Invalid RSA signature for key " + shipperPublicJWK.getKeyID());
                }
            }
            if (platformPublicJWK.getKeyID().equals(sig.getHeader().getKeyID())) {
                if (! sig.verify(new RSASSAVerifier(platformPublicJWK))) {
                    System.out.println("Invalid RSA signature for key " + platformPublicJWK.getKeyID());
                }
            }
        }
        Assertions.assertTrue(JWSObjectJSON.State.VERIFIED.equals(receivedBlockByShipperPlatform.getState()));
    }
}
