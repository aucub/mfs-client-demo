package com.example.demo;

import io.netty.handler.ssl.SslContextBuilder;
import io.rsocket.transport.netty.client.TcpClientTransport;
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

//未启用
public class PKITransportFactory {

    public TcpClientTransport tcpClientTransport() throws SSLException {
        TrustManager trustManager = null;
        try {
            TrustManagerFactory.getInstance("PKCS12").init(KeyStore.getInstance(new File("/keystore.p12"), "changeit".toCharArray()));
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
