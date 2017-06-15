package wtf.paulbaker.utils;

import lombok.extern.log4j.Log4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j
@SpringBootApplication
public class MitmRestDebuggerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MitmRestDebuggerApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        log.info("Good to go!");
    }
}
