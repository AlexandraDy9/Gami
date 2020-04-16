package com.degree.gami.webapp

import com.degree.gami.service.ServiceModule
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Import


@SpringBootApplication
@Import(ServiceModule::class)
class GamiApplication: SpringBootServletInitializer() {

	override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
		return builder.sources(GamiApplication::class.java)
	}

	companion object{
		@JvmStatic
		fun main(args: Array<String>){
			SpringApplication.run(GamiApplication::class.java, *args)
		}
	}
}