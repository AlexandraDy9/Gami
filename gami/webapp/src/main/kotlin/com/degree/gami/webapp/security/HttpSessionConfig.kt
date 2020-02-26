package com.degree.gami.webapp.security

import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession

@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 60*60*24*7)
class HttpSessionConfig