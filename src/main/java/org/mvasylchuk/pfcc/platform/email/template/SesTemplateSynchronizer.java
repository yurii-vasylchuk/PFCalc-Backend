package org.mvasylchuk.pfcc.platform.email.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mvasylchuk.pfcc.platform.configuration.annotations.ConditionalOnMailEnabled;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.EmailTemplateMetadata;
import software.amazon.awssdk.services.sesv2.model.GetEmailTemplateResponse;
import software.amazon.awssdk.services.sesv2.model.ListEmailTemplatesResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnMailEnabled
class SesTemplateSynchronizer {
    private static final String TEMPLATES_PATH = "/templates";
    private static final int LIST_TEMPLATE_PAGE_SIZE = 50;

    private final ObjectMapper objectMapper;
    private final SesV2Client sesClient;
    private final TaskExecutor taskExecutor;

    @PostConstruct
    public void init() {
        taskExecutor.execute(() -> {
            try {
                synchronizeTemplates();
            } catch (Exception e) {
                log.error("Can't synchronize email templates", e);
            }
        });
    }

    void synchronizeTemplates() throws URISyntaxException, IOException {
        List<TemplateDto> localTemplates = loadTemplates();

        List<String> sesTemplatesNames = loadAwsTemplatesNames();

        //Delete old templates
        for (Iterator<String> iter = sesTemplatesNames.iterator(); iter.hasNext(); ) {
            String sesTemplatesName = iter.next();
            if (localTemplates.stream().noneMatch(dto -> dto.getName().equals(sesTemplatesName))) {
                sesClient.deleteEmailTemplate(b -> b.templateName(sesTemplatesName));
                iter.remove();
            }
        }

        // Create new templates
        for (Iterator<TemplateDto> iter = localTemplates.iterator(); iter.hasNext(); ) {
            TemplateDto localTemplate = iter.next();
            String localTemplateName = localTemplate.getName();
            if (sesTemplatesNames.stream().noneMatch(localTemplateName::equals)) {
                sesClient.createEmailTemplate(b -> b.templateName(localTemplateName)
                                                    .templateContent(tb -> tb.html(localTemplate.getHtml())
                                                                             .subject(localTemplate.getSubject())
                                                                             .text(localTemplate.getText())));
                iter.remove();
            }
        }

        //Check and update existent
        for (String sesTemplateName : sesTemplatesNames) {
            GetEmailTemplateResponse sesTemplate = sesClient.getEmailTemplate(b -> b.templateName(sesTemplateName));
            TemplateDto localTemplate = localTemplates.stream()
                                                      .filter(lt -> lt.getName().equals(sesTemplate.templateName()))
                                                      .findFirst()
                                                      // Throw should not happen, as we already checked for
                                                      // non-existent-anymore templates
                                                      // as well as for new templates
                                                      .orElseThrow();

            if (!(localTemplate.getText().equals(sesTemplate.templateContent().text()) &&
                    localTemplate.getHtml().equals(sesTemplate.templateContent().html()) &&
                    localTemplate.getSubject().equals(sesTemplate.templateContent().subject()))) {

                sesClient.updateEmailTemplate(b -> b.templateName(localTemplate.getName())
                                                    .templateContent(cb -> cb.text(localTemplate.getText())
                                                                             .html(localTemplate.getHtml())
                                                                             .subject(localTemplate.getSubject())));
            }
        }

    }

    @NotNull
    List<String> loadAwsTemplatesNames() {
        List<String> sesTemplatesNames = new ArrayList<>();
        String nextToken = null;
        do {
            String finalNextToken = nextToken;
            ListEmailTemplatesResponse listTemplatesResponse = sesClient.listEmailTemplates(builder -> {
                builder.pageSize(LIST_TEMPLATE_PAGE_SIZE);
                if (finalNextToken != null) {
                    builder.nextToken(finalNextToken);
                }
            });

            listTemplatesResponse.templatesMetadata().stream()
                                 .map(EmailTemplateMetadata::templateName)
                                 .forEach(sesTemplatesNames::add);

            nextToken = listTemplatesResponse.nextToken();
        } while (nextToken != null);
        return sesTemplatesNames;
    }

    @NotNull
    List<TemplateDto> loadTemplates() throws URISyntaxException, IOException {
        URI templatesUri = SesTemplateSynchronizer.class.getResource(TEMPLATES_PATH).toURI();
        List<Path> templatesPaths = Files.walk(Paths.get(templatesUri))
                                         .filter(path -> !Files.isDirectory(path) && path.getFileName()
                                                                                         .toString()
                                                                                         .endsWith(".json"))
                                         .toList();

        List<TemplateDto> localTemplates = new ArrayList<>();
        for (Path templatePath : templatesPaths) {
            try {
                localTemplates.add(objectMapper.readValue(templatePath.toFile(), TemplateDto.class));
            } catch (Exception e) {
                log.error("Unable to parse template: %s".formatted(templatePath.toString()), e);
            }
        }
        return localTemplates;
    }
}
