package sinanaj.aldo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import sinanaj.aldo.model.Account;
import sinanaj.aldo.repository.AccountRepository;

@Configuration
@EnableGlobalAuthentication
public class JdbcSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    UserDetailsService userDetailsService() {

        return username -> {
            Account account = accountRepository.findAccountByUsername(username);

            return new User(
                    account.getUsername(),
                    account.getPassword(),
                    account.isEnabled(),
                    account.isEnabled(),
                    account.isEnabled(),
                    account.isEnabled(),
                    AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")
            );
        };
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void init(AuthenticationManagerBuilder authManager) throws Exception {
        authManager.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder);
    }
}
