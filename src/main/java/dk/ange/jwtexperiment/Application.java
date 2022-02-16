package dk.ange.jwtexperiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import java.security.interfaces.RSAPublicKey;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.core.io.ClassPathResource;
import java.security.KeyPair;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//key store generated with:
// keytool -genkeypair -alias dcsa-kid -keyalg RSA  -keypass dcsa-pass -keystore dcsa-jwt.jks  -storepass dcsa-pass

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public JWKSet jwkSet() {
        ClassPathResource ksFile = new ClassPathResource("dcsa-jwt.jks");
        KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, "dcsa-pass".toCharArray());
        KeyPair keyPair = ksFactory.getKeyPair("dcsa-kid");
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID("dcsa-kid");
        return new JWKSet(builder.build());
    }
}
