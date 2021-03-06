package com.helloztt.logistics.config;

import com.helloztt.logistics.service.user.UserService;
import com.helloztt.logistics.service.user.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by helloztt on 2016/5/7.
 */
@Configuration
@EnableWebSecurity
//@PreAuthorize,@PreFilter,@PostAuthorize,@PostFilter Their use is enabled through the configure
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String LOGIN_PAGE = "/login";
    public static final String LOGIN_SUCCESS_URL = "/index";
    public static final String LOGOUT_SUCCESS_URL = "/";
    public static final String LOGIN_ERROR_URL = "/loginFailed";

    private static String[] STATIC_RESOURCE_PATH = {
            "/resource/**",
            "/loginFailed"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService springDataUserDetailsService() {
        return new UserServiceImpl();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(STATIC_RESOURCE_PATH);
    }

    // Since MultiSecurityConfig does not extend GlobalMethodSecurityConfiguration and
    // define an AuthenticationManager, it will try using the globally defined
    // AuthenticationManagerBuilder to create one
    // The @Enable*Security annotations create a global AuthenticationManagerBuilder
    // that can optionally be used for creating an AuthenticationManager that is shared
    // The key to using it is to use the @Autowired annotation
    /*@Autowired
    public void registerSharedAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        //密码不加密
        auth.userDetailsService(userDetailsService);
    }*/

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin()
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(LOGIN_SUCCESS_URL)
                .failureUrl(LOGIN_ERROR_URL)
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }
}
