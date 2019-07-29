package org.openpaas.paasta.marketplace.api.config;

import java.util.Collections;

import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final String AUTH_HEADER_NAME = "Authorization";

    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.antMatcher("/**")
            .addFilterAfter(requestHeaderAuthenticationFilter(), BasicAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers("/admin/**").hasAuthority("Admin")
                .anyRequest().authenticated()
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
            .and()
                .csrf().disable();
        // @formatter:on

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setPrincipalRequestHeader(AUTH_HEADER_NAME);
        filter.setCredentialsRequestHeader(AUTH_HEADER_NAME);
        filter.setAuthenticationManager(authenticationManager());
        filter.setExceptionIfHeaderMissing(false);

        return filter;
    }

    @Override
    protected AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(authorizationUserDetailsService());
        authenticationProvider.setThrowExceptionWhenTokenRejected(false);

        return authenticationProvider;
    }

    @Bean
    public AuthorizationUserDetailsService authorizationUserDetailsService() {
        AuthorizationUserDetailsService authorizationUserDetailsService = new AuthorizationUserDetailsService();
        // authorizationUserDetailsService.setAuthRest(authRest());
        authorizationUserDetailsService.setUserRepository(userRepository);

        return authorizationUserDetailsService;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new Http403ForbiddenEntryPoint();
    }

}
