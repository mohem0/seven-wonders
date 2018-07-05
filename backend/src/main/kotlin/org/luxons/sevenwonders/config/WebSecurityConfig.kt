package org.luxons.sevenwonders.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    // this disables default authentication settings
    override fun configure(httpSecurity: HttpSecurity) = Unit
}
