package dk.ange.jwtexperiment;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Component
class GitInfoExtracter {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig
            = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        return propsConfig;
    }
}
