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
        File pemFile = new File(proxyProperties.getKeystoreDirectory(), proxyProperties.getAlias() + ".pem");
        log.info("Make sure that you import the " + pemFile + " file into your keystore!");
    }
}
