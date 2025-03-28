package org.example.prspatientregistrationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class PrsPatientRegistrationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrsPatientRegistrationSystemApplication.class, args);
    }

    @RestController
    @RequestMapping("/api")
    public class RestTest {

        @GetMapping("/{number}")
        public String getBigNumber(@PathVariable Integer number) {
            Integer result = fibo(number);
            return String.valueOf(result);
        }

        private Integer fibo(int n) {
            if (n == 1 || n == 2) {
                return 1;
            } else return fibo(n - 1) + fibo(n - 2);
        }
    }

}
