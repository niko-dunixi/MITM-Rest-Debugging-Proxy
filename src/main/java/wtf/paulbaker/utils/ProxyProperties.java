package wtf.paulbaker.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by paul.baker on 6/15/17.
 */
@Data
@Component
@ConfigurationProperties("mitm")
public class ProxyProperties {
    private int port;

    private String keystoreDirectory;
    private String alias;
    private String password;
    private String commonName;
    private String organization;
    private String organizationalUnitName;
    private String certificateOrganization;
    private String certificateOrganizationUnitName;
}
