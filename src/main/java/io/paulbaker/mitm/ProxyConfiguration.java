package io.paulbaker.mitm;

import lombok.extern.log4j.Log4j;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.mitm.Authority;
import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.littleshoot.proxy.mitm.RootCertificateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Created by paul.baker on 6/13/17.
 */
@Log4j
@Configuration
public class ProxyConfiguration {

    @Autowired
    private ProxyProperties proxyProperties;

    private File getKeystoreDirectory() {
        String sanitizedDirectory = proxyProperties.getKeystoreDirectory().replaceAll("~", System.getProperty("user.home"));
        proxyProperties.setKeystoreDirectory(sanitizedDirectory);
        File keystoreDirectoryFile = new File(sanitizedDirectory);
        keystoreDirectoryFile.mkdirs();
        log.info("Using keystore: " + keystoreDirectoryFile);
        return keystoreDirectoryFile;
    }

    @Bean
    public Authority authority() {
        return new Authority(getKeystoreDirectory(), proxyProperties.getAlias(), proxyProperties.getPassword().toCharArray(),
                proxyProperties.getCommonName(), proxyProperties.getOrganization(), proxyProperties.getOrganizationalUnitName(),
                proxyProperties.getCertificateOrganization(), proxyProperties.getCertificateOrganizationUnitName());
    }

    @Bean
    public CertificateSniffingMitmManager certificateSniffingMitmManager(Authority authority) throws RootCertificateException {
        return new CertificateSniffingMitmManager(authority);
    }

    @Bean
    public HttpFiltersSourceAdapter consoleLoggingHttpFilterSourceAdapter() {
        return new ConsoleLoggingHttpFilterSourceAdapter();
    }

    @Bean
    public HttpProxyServerBootstrap httpProxyServerBootstrap(CertificateSniffingMitmManager certificateSniffingMitmManager, HttpFiltersSourceAdapter httpFiltersSourceAdapter) {
        return DefaultHttpProxyServer.bootstrap()
                .withPort(9090)
                .withManInTheMiddle(certificateSniffingMitmManager)
                .withFiltersSource(httpFiltersSourceAdapter);
    }

    @Bean
    public HttpProxyServer httpProxyServer(HttpProxyServerBootstrap httpProxyServerBootstrap) {
        return httpProxyServerBootstrap.start();
    }
}
