package org.example.prspatientregistrationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class PrsPatientRegistrationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrsPatientRegistrationSystemApplication.class, args);
    }

    @RestController
    @RequestMapping("/api")
    public class RestTest {

        @CrossOrigin(origins = "http://localhost:3000")
        @GetMapping("/{number}")
        public Integer getBigNumber(@PathVariable Integer number) {
            Integer result = fibo(number);
            System.out.println(result);
            return result;
        }

        private Integer fibo(int n) {
            if (n == 1 || n == 2) {
                return 1;
            } else return fibo(n - 1) + fibo(n - 2);
        }
    }
}