package dk.ange.jwtexperiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import java.security.interfaces.RSAPublicKey;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.core.io.ClassPathResource;
import java.security.KeyPair;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

//key store generated with:
// keytool -genkeypair -alias dcsa-kid -keyalg RSA  -keypass dcsa-pass -keystore dcsa-jwt.jks  -storepass dcsa-pass

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public KeyPair platformKeyPair() {
        ClassPathResource ksFile = new ClassPathResource("dcsa-jwt.jks");
        KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, "dcsa-pass".toCharArray());
        return ksFactory.getKeyPair("dcsa-kid");
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public JWKSet jwkSet() {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) platformKeyPair().getPublic())
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID("dcsa-kid");
        return new JWKSet(builder.build());
    }

}
