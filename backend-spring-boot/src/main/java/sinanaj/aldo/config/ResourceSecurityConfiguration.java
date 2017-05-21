package sinanaj.aldo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class ResourceSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeRequests()
//                .anyRequest().fullyAuthenticated()
                .antMatchers("/account/update-password/**").access("hasRole('USER')")
                .antMatchers("/account/admin/**").access("hasRole('ADMIN')")
                .antMatchers("/api/booking/**").access("hasRole('ADMIN') or hasRole('USER')")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }

}
