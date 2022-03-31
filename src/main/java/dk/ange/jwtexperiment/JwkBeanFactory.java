package dk.ange.jwtexperiment;

import org.springframework.context.annotation.Bean;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import java.security.interfaces.RSAPublicKey;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.core.io.ClassPathResource;
import java.security.KeyPair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
class JwkBeanFactory {
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public KeyPair platformKeyPair() {
        ClassPathResource ksFile = new ClassPathResource("certificates/dcsa-jwk.jks");
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
