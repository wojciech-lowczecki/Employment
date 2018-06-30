package pl.plh.app.employment.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.plh.app.employment.config.ApplicationInfo;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Profile({"swagger"})
@EnableSwagger2
@Configuration
public class SwaggerConfiguration implements WebMvcConfigurer {
    @Autowired
    private ApplicationInfo appInfo;

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
            .displayRequestDuration(true)
            .validatorUrl("") // to mask a bug #2204 (https://github.com/springfox/springfox/issues/2204)
            .build();
    }

    @Bean
    public Docket api() throws Exception {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.any())
            .build()
            // todo(medium) in the next version
            // .tags(new Tag("Searching", "Searching operations"))
            .apiInfo(apiInfo());
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // Required by Swagger UI configuration
        registry.addResourceHandler("/lib/**").addResourceLocations("/lib/").setCachePeriod(0);
        registry.addResourceHandler("/images/**").addResourceLocations("/images/").setCachePeriod(0);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(0);
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("REST API documentation for " + appInfo.getAppName(),
                           appInfo.getAppDescription(),
                           appInfo.getAppVersion(),
                           null,
                           new Contact(appInfo.getAppContactName(),
                                       appInfo.getAppContactUrl(),
                                       appInfo.getAppContactEmail()),
                           null,
                           null,
                           Collections.emptyList());
    }
}