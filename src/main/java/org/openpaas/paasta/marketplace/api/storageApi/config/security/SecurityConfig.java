//package org.openpaas.paasta.marketplace.api.storageApi.config.security;
//
//import org.openpaas.paasta.marketplace.api.storageApi.config.SwiftOSConstants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Collections;
//
///**
// * The type Security config.
// */
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
//
//    @Value("${spring.security.username}")
//    String username;
//
//    @Value("${spring.security.password}")
//    String password;
//
//    /**
//     * Configure global.
//     *
//     * @throws Exception the exception
//     */
//
//    private PasswordEncoder passwordEncoder = null;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        if (null == passwordEncoder) {
//            passwordEncoder = new BCryptPasswordEncoder();
//        }
//
//        return passwordEncoder;
//    }
//
//    @Autowired
//    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        LOGGER.info("User : {} / Password : {}", username, password);
//        auth.inMemoryAuthentication()
//                /* .passwordEncoder( passwordEncoder() ) */
//                .withUser(username).password(password).roles("USER");
//    }
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                //Spring boot Admin 정보 접근 URL -  시작
//                .antMatchers("/").permitAll()
//                .antMatchers("/index").permitAll()
//                .antMatchers("/info").permitAll()
//                .antMatchers(SwiftOSConstants.SwiftOSControllerURI.OBJECT_STORAGE_HELLO_SERVICE).permitAll()
//                .antMatchers(
//                        SwiftOSConstants.SwiftOSControllerURI.OBJECT_STORAGE_ROOT_URI + "/**").hasRole("USER")
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic()
//                .and()
//                .csrf().disable().cors().configurationSource(corsConfiguration());
//    }
//
//    private CorsConfigurationSource corsConfiguration(){
//        return new CorsConfigurationSource() {
//            @Override
//            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                CorsConfiguration config = new CorsConfiguration();
//                config.setAllowedHeaders(Collections.singletonList("*"));
//                config.setAllowedMethods(Collections.singletonList("*"));
//                config.addAllowedOrigin("*");
//                config.setAllowCredentials(true);
//                return config;
//            }
//        };
//    }
//
//}
