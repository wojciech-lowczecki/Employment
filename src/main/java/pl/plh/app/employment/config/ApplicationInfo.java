package pl.plh.app.employment.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApplicationInfo {
    @Value("${application.name}")
    private String appName;

    @Value("${application.description}")
    private String appDescription;

    @Value("${application.version}")
    private String appVersion;

    @Value("${application.contact.name}")
    private String appContactName;

    @Value("${application.contact.url}")
    private String appContactUrl;

    @Value("${application.contact.email}")
    private String appContactEmail;
}
