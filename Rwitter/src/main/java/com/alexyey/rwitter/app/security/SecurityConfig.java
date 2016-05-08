package com.alexyey.rwitter.app.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter   {

	   /* Basic security configuration that allows an unauthenticated access 
	    * only for the login and register action, return error 401 otherwise
	    */
	
	   private RESTAuthenticationEntryPoint authenticationEntryPoint;
	
	   @Override
	   protected void configure(HttpSecurity http) throws Exception {
	           
		   			http.authorizeRequests()
	               .antMatchers("/login").permitAll()
	               .antMatchers("/register").permitAll()
	               .antMatchers("/error").permitAll()
	               .anyRequest().authenticated();
	                
		            http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
	        
	   }

	 

}
