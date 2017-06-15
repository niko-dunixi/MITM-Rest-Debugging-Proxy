package io.paulbaker.mitm;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@Log4j
@SpringBootApplication
public class MitmRestDebuggerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MitmRestDebuggerApplication.class, args);
    }

    @Autowired
    private ProxyProperties proxyProperties;

    @Override
    public void run(String... strings) throws Exception {
        log.info("Good to go!");
        String alias = proxyProperties.getAlias();
        File pemFile = new File(proxyProperties.getKeystoreDirectory(), alias + ".pem");
        log.info("Make sure that you import the " + pemFile + " file into your keystore!");
        File cacertsFile = new File(System.getProperty("java.home"), "lib/security/cacerts");
        log.info("Default cacerts file exists? " + cacertsFile.exists());
        if (cacertsFile.exists()) {
            log.info("Import into cacerts with this command:");
            log.info("keytool -import -alias \"" + alias + "\" -keystore \"" + cacertsFile + "\" -file \"" + pemFile + "\"");
        } else {
            log.info("I cannot find your default cacerts file. You'll need to located it manually, and then run:");
            log.info("keytool -import -alias \"" + alias + "\" -keystore \"path to your cacerts file\" -file \"" + pemFile + "\"");
        }
        log.info("NOTE: The default password to cacerts is \"changeit\".");
    }
}
