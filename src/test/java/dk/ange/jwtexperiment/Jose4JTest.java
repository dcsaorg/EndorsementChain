package dk.ange.jwtexperiment;

import org.junit.Test;
import org.jose4j.jwe.*;
import org.jose4j.jwk.*;
import org.jose4j.jws.*;
import org.jose4j.jwt.*;
import org.jose4j.keys.*;
import org.jose4j.jwt.consumer.*;
import org.jose4j.lang.JoseException;
import org.jose4j.jwt.MalformedClaimException;
import java.util.List;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import static org.junit.Assert.*;

public class Jose4JTest {

    @Test
    public void createAndConsumeJWT() throws JoseException, MalformedClaimException, InvalidJwtException {
        RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
        rsaJsonWebKey.setKeyId("k1");
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("Issuer");
        claims.setAudience("Audience");
        claims.setExpirationTimeMinutesInTheFuture(10);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);
        claims.setSubject("subject");
        claims.setClaim("email","mail@example.com");
        claims.setClaim("nextHolder",rsaJsonWebKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
        List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
        claims.setStringListClaim("groups", groups);
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        String jwt = jws.getCompactSerialization();
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedIssuer("Issuer")
                .setExpectedAudience("Audience")
                .setVerificationKey(rsaJsonWebKey.getKey())
                .build();
        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
    }

    @Test
    public void createCarrierToShipperToBankTransfer() throws JoseException, MalformedClaimException, InvalidJwtException, NoSuchAlgorithmException {
        RsaJsonWebKey carrierKey = RsaJwkGenerator.generateJwk(2048);
        RsaJsonWebKey shipperKey = RsaJwkGenerator.generateJwk(2048);
        RsaJsonWebKey bankKey = RsaJwkGenerator.generateJwk(2048);

        JwtClaims carrierTDT = new JwtClaims(); //TDT carrier -> shipper
        carrierTDT.setClaim("holder", shipperKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
        carrierTDT.setClaim("possessor", shipperKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
        carrierTDT.setClaim("documentHash", "afe69d1b7ddb203ce7fc8b4ed4b972f126e0749a4cde160127a92eb1c74a2fa6"); //echo "TestDocument" | sha256sum
        carrierTDT.setClaim("isToOrder", "true");
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(carrierTDT.toJson());
        jws.setKey(carrierKey.getPrivateKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        final String carrierJwt = jws.getCompactSerialization();
        JwtConsumer jwtConsumerShipper = new JwtConsumerBuilder()
                .setVerificationKey(carrierKey.getKey())
                .build();

        //Transfer to shipper
        MessageDigest msg = MessageDigest.getInstance("SHA-256");
        byte[] hash = msg.digest(carrierJwt.getBytes());
        final String tdtUrl = "http://exampleregistry.com/v2/tdt/" + new String(Base64.getEncoder().encode(hash)); //URL as sent to the shipper as part of BL issuance

        JwtClaims shipperTDT = jwtConsumerShipper.processToClaims(carrierJwt);
        shipperTDT.setClaim("possessor", bankKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
        shipperTDT.setClaim("previousTransfer", tdtUrl);
        JsonWebSignature shipperJws = new JsonWebSignature();
        shipperJws.setPayload(shipperTDT.toJson());
        shipperJws.setKey(shipperKey.getPrivateKey());
        assertEquals(shipperJws.getKey(), shipperKey.getPrivateKey());
        shipperJws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
        final String shipperJwt = shipperJws.getCompactSerialization();

        //transferring to bank
        //get previous possessor (shipper) from previous TDT, in real life done by GET'ing the provided previousTransfer URL
        JwtConsumer jwtPreviousPosessor = new JwtConsumerBuilder()
                .setDisableRequireSignature() //we're just extracting the previous possessor's public key, no need to check signature
                .build();
        JwtClaims previousTDT = jwtConsumerShipper.processToClaims(carrierJwt);
        PublicJsonWebKey extractedPublicShipperKey = PublicJsonWebKey.Factory.newPublicJwk(previousTDT.getClaimValue("possessor",String.class));
        JwtConsumer jwtConsumerBank = new JwtConsumerBuilder()
                .setVerificationKey(extractedPublicShipperKey.getKey())
                .build();
        JwtClaims receivedClaims = jwtConsumerBank.processToClaims(shipperJwt);
    }
}
