package org.mvasylchuk.pfcc.platform.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.mvasylchuk.pfcc.platform.configuration.model.PfccSecurityConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class JwtSigningKeyConfiguration {
    public static final String JWT_SIGNING_KEY_BEAN_NAME = "JWT_SIGNING_RSA_KEY";

    private final PfccSecurityConfigurationProperties conf;

    @Bean
    @Qualifier(JWT_SIGNING_KEY_BEAN_NAME)
    public KeyPair jwtSigningKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance(this.conf.jwt.keyAlgorithm);

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(this.conf.jwt.publicKey));
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(this.conf.jwt.privateKey));

        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(publicKeySpec);
        RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }
}
