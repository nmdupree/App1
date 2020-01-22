package com.lessons.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

import javax.annotation.Resource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurityConfig.class);


    @Value("${spring.profiles.active}")
    private String activeProfileName;

    /**
     * The authorization mode being
     * Possible values:  pki    (if user is sending PKI certificate directly to the spring boot webapp)
     *                   header (if a proxy is sending the PKI certificate info as headers to the spring boot webapp)
     */
    @Value("${security.mode}")
    private String securityMode;

    @Resource
    private MyAuthenticationManager myAuthenticationManager;

    @Resource
    private MyRequestHeaderAuthFilter requestHeaderAuthFilter;

    @Resource
    private SubjectX509PrincipalExtractor subjectX509PrincipalExtractor;

//    @Resource
//    private MyAuthorizationFilter myAuthorizationFilter;

    /**
     * Global configuration to set the authorization listener.
     * @param authenticationManagerBuilder
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        logger.debug("configureGlobal() started");
        super.configure(authenticationManagerBuilder);

        logger.debug("configureGlobal() finished");
    }



    /*************************************************************************
     * configure()
     *
     * Configure Spring Security
     *
     * If the security.authenticate.mechanism property holds 'header'
     *   then use the Request Header to getByUserId the DN from the header
     *
     * If the security.authenticate.mechanism property holds 'x509'
     *   then use the x509 filter to getByUserId the DN from the x509 certificate
     *************************************************************************/
    @Override
    public void configure(HttpSecurity aHttpSecurity) throws Exception {
        logger.debug("configure() started.");

        if (activeProfileName.equalsIgnoreCase("prod")) {
            // Running in prod mode
            aHttpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .and()
                    .authorizeRequests()    // Filters out any URLs that are ignored.  This should be before any authorization filters
                    .antMatchers("/resources/**", "/app1/resources/**", "/error").permitAll()
                    .antMatchers("/**").access("hasRole('ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS')")   // All users must have the grantedAuthority called ROLE_UserFoundInLdap to view all pages
                    .and()
                    .requiresChannel().antMatchers("/**").requiresSecure()    // Redirect http to https
                    .and()
                    .addFilter(x509Filter())                                  // Pull the DN from the user's X509 certificate
//                    .addFilterAfter(myAuthorizationFilter, MyRequestHeaderAuthFilter.class)
                    .headers().frameOptions().disable()                       // By default X-Frame-Options is set to denied.  Disable frameoptions to let this webapp work in OWF
                    .and()
                    .anonymous().disable();
        }
        else {
            // Running in non-prod mode
            aHttpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .and()
                    .authorizeRequests()     // Filters out any URLs that are ignored.  This should be before any authorization filters
                    .antMatchers("/resources/**", "/app1/resources/**", "/app1/error", "/error").permitAll()
                    .antMatchers("/**").access("hasRole('ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS')")   // All users must have the grantedAuthority called ROLE_USER_FOUND_IN_VALID_LIST_OF_USERS to view all pages
                    .anyRequest().authenticated()
                    .and()
                    .requiresChannel().antMatchers("/**").requiresInsecure()
                    .and()
                    .addFilter(requestHeaderAuthFilter)
//                    .addFilterAfter(myAuthorizationFilter, MyRequestHeaderAuthFilter.class)
                    .headers().frameOptions().disable()                       // By default X-Frame-Options is set to denied.
                    .and()
                    .anonymous().disable();
        }

        aHttpSecurity.csrf().disable();
    }



    /**
     * Configures the X509AuthenticationFilter for extracting information from the Cert
     * @return
     */
    @Bean
    public X509AuthenticationFilter x509Filter() {
        // Setup a filter that extracts the principal from the cert
        X509AuthenticationFilter x509Filter = new X509AuthenticationFilter();
        x509Filter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
        x509Filter.setAuthenticationManager(myAuthenticationManager);
        x509Filter.setPrincipalExtractor(subjectX509PrincipalExtractor);
        return x509Filter;
    }
}
