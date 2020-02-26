package com.degree.gami.service

import com.degree.gami.persistence.PersistenceModule
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan
@Import(PersistenceModule::class)
class ServiceModule