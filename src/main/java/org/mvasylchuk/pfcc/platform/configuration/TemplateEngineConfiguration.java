package org.mvasylchuk.pfcc.platform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Set;

@Configuration
public class TemplateEngineConfiguration {
    @Bean
    public TemplateEngine templateEngine() {
        TemplateEngine engine = new TemplateEngine();

        engine.setTemplateResolvers(Set.of(htmlResolver(), cssResolver()));
        engine.setMessageResolver(messageResolver());
        engine.addDialect(new Java8TimeDialect());

        return engine;
    }

    private ITemplateResolver htmlResolver() {
        AbstractConfigurableTemplateResolver resolver = new ClassLoaderTemplateResolver();

        resolver.setPrefix("reports/");
        resolver.getResolvablePatternSpec().addPattern("*.html");
        resolver.setTemplateMode(TemplateMode.HTML);

        return resolver;
    }

    private ITemplateResolver cssResolver() {
        AbstractConfigurableTemplateResolver resolver = new ClassLoaderTemplateResolver();

        resolver.setPrefix("reports/");
        resolver.getResolvablePatternSpec().addPattern("*.css");
        resolver.setTemplateMode(TemplateMode.CSS);

        return resolver;
    }

    private IMessageResolver messageResolver() {
        return new StandardMessageResolver();
    }
}
