package sinanaj.aldo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sinanaj.aldo.model.Account;
import sinanaj.aldo.repository.AccountRepository;
import sinanaj.aldo.util.JsonLocalDateDeserializer;
import sinanaj.aldo.util.JsonLocalDateSerializer;

import java.time.LocalDate;

@EntityScan(
        basePackageClasses = { AppConfig.class, Jsr310JpaConverters.class }
)
@SpringBootApplication
public class AppConfig {

    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    @Primary
    public ObjectMapper serializingObjectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new JsonLocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new JsonLocalDateDeserializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner addUserAdmin() {
        return args -> {
            Account account = new Account("admin", passwordEncoder().encode("WhisperAdmin"));
            account.setEnabled(true);
            account.setRole("ROLE_ADMIN");

            accountRepository.save(account);
        };
    }
}
