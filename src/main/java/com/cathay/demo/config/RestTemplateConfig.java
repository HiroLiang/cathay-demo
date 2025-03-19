package com.cathay.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

/**
 * 製作 Client，實作為 Rest Template
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${spring.profiles.active:test}")
    private String PROFILE_ACTIVE;

    @Value("${http-client.certs.path:}")
    private List<String> certs;

    @Value("${http-client.max-total-connections:5}")
    private int MAX_TOTAL_CONNECTIONS;

    @Value("${http-client.max-connections-per-route:5}")
    private int MAX_CONNECTIONS_PER_ROUTE;

    private final ResourceLoader resourceLoader;

    public RestTemplateConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public RestTemplate restTemplate() {
        try {
            // 取得 SSL 憑證配置
            final SSLContext sslContext = getSSLContext();

            final SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            // 註冊 https 只允許限定的權證，http 則不限制
            final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("https", factory)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            // 限制 max connection
            final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
            connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
            connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            // 設定請求逾時時間
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            requestFactory.setConnectionRequestTimeout(30000);
            requestFactory.setConnectTimeout(10000);

            // 製作 rest template 並設置 Charset
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            setCharset(restTemplate);

            log.info("RestTemplate configured.");
            return restTemplate;
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException |
                 KeyManagementException e) {
            log.error("Generate RestTemplate failed." ,e);
            throw new RuntimeException(e);
        }
    }

    // Set SSL config if needed
    private SSLContext getSSLContext() throws CertificateException, KeyStoreException, IOException,
            NoSuchAlgorithmException, KeyManagementException {
        final KeyStore keyStore = loadPEMAsKeyStore();
        return SSLContextBuilder.create()
                .loadTrustMaterial(keyStore, null)
                .build();
    }

    private KeyStore loadPEMAsKeyStore() throws KeyStoreException,
            CertificateException, IOException, NoSuchAlgorithmException {
        // 初始化 KeyStore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);

        // 讀取 path 中信任憑證
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate certificate;
        if ("test".equals(PROFILE_ACTIVE)) {
            for (int i = 0; i < certs.size(); i++) {
                Resource certResource = resourceLoader.getResource("classpath:certs/" + certs.get(i));
                try (InputStream fis = certResource.getInputStream()) {
                    certificate = factory.generateCertificate(fis);
                    keyStore.setCertificateEntry("cert" + (i +1), certificate);
                    log.info("Test certificate loaded: {}", i + 1);
                }
            }
        } else {
            for (int i = 0; i < certs.size(); i++) {
                try (FileInputStream fis = new FileInputStream(certs.get(i))) {
                    certificate = factory.generateCertificate(fis);
                    keyStore.setCertificateEntry("cert" + (i +1), certificate);
                    log.info("Certificate loaded: {}", i + 1);
                }
            }
        }


        return keyStore;
    }

    // Set charset as UTF-8
    private void setCharset(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (messageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) messageConverter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }

}
