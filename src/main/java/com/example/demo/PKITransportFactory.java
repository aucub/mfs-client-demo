package com.example.demo;

import io.netty.handler.ssl.SslContextBuilder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.val;
import org.springframework.context.annotation.Configuration;
import reactor.netty.tcp.SslProvider;
import reactor.netty.tcp.TcpClient;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
@Configuration
public class PKITransportFactory {

    public TcpClientTransport tcpClientTransport() throws SSLException {
        TrustManager trustManager = null;
        System.out.println("8888888888888888");
        try {
            TrustManagerFactory.getInstance("PKCS12").init(KeyStore.getInstance(new File("/keystore.p12"),"changeit".toCharArray()));
        } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException e) {
            throw new RuntimeException(e);
        }

        return TcpClientTransport.create(
                TcpClient.create()
                        .host("127.0.0.1")
                        .port(9898).secure(SslProvider.builder().sslContext(SslContextBuilder
                                .forClient()
                                .keyStoreType("PKCS12")
                                .trustManager(trustManager).build()).build()
                        ));
    }
}