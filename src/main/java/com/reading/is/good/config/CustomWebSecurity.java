package com.reading.is.good.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reading.is.good.constant.ChallengeConstant;
import com.reading.is.good.filter.JWTFilter;
import com.reading.is.good.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
@Profile("!integration-test")
public class CustomWebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().and().authorizeRequests()
                .antMatchers(HttpMethod.POST, ChallengeConstant.AUTHORIZE_ENDPOINT).permitAll()
                .antMatchers(HttpMethod.POST, ChallengeConstant.TOKEN_ENDPOINT).permitAll()
                .antMatchers(HttpMethod.GET, ChallengeConstant.SWAGGER_ENDPOINT,
                        ChallengeConstant.SWAGGER_V2_API_DOCS, ChallengeConstant.SWAGGER_RESOURCES).permitAll()
                .antMatchers(HttpMethod.GET, "/*").permitAll()
                .antMatchers(HttpMethod.GET, "/assets/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JWTFilter(userService, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
