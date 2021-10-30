package dk.ange.jwtexperiment;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.PrivateKey.*;
import java.security.PublicKey;

import static org.junit.Assert.*;

public class JWTDemoTest {

    private static String SECRET_KEY = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKolVX8xNrQDcNRfVEdTZNOuOyqEGhXEbdJI-ZQ19k_o9MI0y3eZN2lp9jow55FfXMiINEdt1XR85VipRLSOkT6kSpzs2x-jbLDiz9iFVzkd81YKxMgPA7VfZeQUm4n-mOmnWMaVX30zGFU4L3oPBctYKkl4dYfqYWqRNfrgPJVi5DGFjywgxx0ASEiJHtV72paI3fDR2XwlSkyhhmY-ICjCRmsJN4fX1pdoL8a18-aQrvyu4j0Os6dVPYIoPvvY0SAZtWYKHfM15g7A3HD4cVREf9cUsprCRK93w";

    /*
        Create a simple JWT, decode it, and assert the claims
     */
    @Test
    public void createAndDecodeJWT() {

        String jwtId = "SOMEID1234";
        String jwtIssuer = "JWT Demo";
        String jwtSubject = "Andrew";
        int jwtTimeToLive = 800000;

        String jwt = JWTDemo.createJWT(
                jwtId, // claim = jti
                jwtIssuer, // claim = iss
                jwtSubject, // claim = sub
                jwtTimeToLive, // used to calculate expiration (claim = exp)
                SignatureAlgorithm.HS256,
                SECRET_KEY
        );
        Claims claims = JWTDemo.decodeJWT(jwt, SECRET_KEY);
        assertEquals(jwtId, claims.getId());
        assertEquals(jwtIssuer, claims.getIssuer());
        assertEquals(jwtSubject, claims.getSubject());

    }

    /*
        Attempt to decode a bogus JWT and expect an exception
     */
    @Test(expected = MalformedJwtException.class)
    public void decodeShouldFail() {

        String notAJwt = "This is not a JWT";

        // This will fail with expected exception listed above
        Claims claims = JWTDemo.decodeJWT(notAJwt, SECRET_KEY);

    }

    /*
    Create a simple JWT, modify it, and try to decode it
    */
    @Test(expected = SignatureException.class)
    public void createAndDecodeTamperedJWT() {

        String jwtId = "SOMEID1234";
        String jwtIssuer = "JWT Demo";
        String jwtSubject = "Andrew";
        int jwtTimeToLive = 800000;

        String jwt = JWTDemo.createJWT(
                jwtId, // claim = jti
                jwtIssuer, // claim = iss
                jwtSubject, // claim = sub
                jwtTimeToLive, // used to calculate expiration (claim = exp)
                SignatureAlgorithm.HS256,
                SECRET_KEY
        );

        // tamper with the JWT

        StringBuilder tamperedJwt = new StringBuilder(jwt);
        tamperedJwt.setCharAt(22, 'I');
        assertNotEquals(jwt, tamperedJwt);
        JWTDemo.decodeJWT(tamperedJwt.toString(), SECRET_KEY);
    }
}

