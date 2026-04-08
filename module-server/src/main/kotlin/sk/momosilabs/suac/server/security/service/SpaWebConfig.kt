package sk.momosilabs.suac.server.security.service

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver

@Configuration
open class SpaWebConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/public/")
            .resourceChain(true)
            .addResolver(object : PathResourceResolver() {

                override fun getResource(
                    resourcePath: String,
                    location: Resource,
                ): Resource? {

                    val requested = location.createRelative(resourcePath)

                    return if (requested.exists() && requested.isReadable) {
                        requested
                    } else {
                        ClassPathResource("/public/index.html")
                    }
                }
            })
    }
}
