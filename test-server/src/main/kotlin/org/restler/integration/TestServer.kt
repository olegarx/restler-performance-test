package org.restler.integration

import com.fasterxml.jackson.module.paranamer.ParanamerModule
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.session.HashSessionManager
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.restler.integration.springdata.SpringDataRestConfig
import org.springframework.context.annotation.Import
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@EnableWebMvc
@Import(SpringDataRestConfig::class)
open class WebConfig : WebMvcConfigurerAdapter() {

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val paranamerModule = ParanamerModule()
        converters.filterIsInstance(MappingJackson2HttpMessageConverter::class.java).forEach {
            it.objectMapper.registerModule(paranamerModule)
        }
    }
}

fun main(args: Array<String>) {
    val server = server()

    server.start()
    server.join()
}

fun server(): Server {
    val applicationContext = AnnotationConfigWebApplicationContext()
    applicationContext.register(WebConfig::class.java)

    val servletHolder = ServletHolder(DispatcherServlet(applicationContext))
    val context = ServletContextHandler()
    context.sessionHandler = SessionHandler(HashSessionManager())
    context.contextPath = "/"
    context.addServlet(servletHolder, "/*")
    context.addEventListener(ContextLoaderListener(applicationContext))

    val webPort = System.getenv("PORT") ?: "8080"

    val server = Server(Integer.valueOf(webPort))

    server.handler = context
    return server
}

