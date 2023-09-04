package org.mvasylchuk.pfcc.platform.email.template;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class TemplateDto {
    @NotEmpty
    private final String name;
    @NotEmpty
    private final String html;
    @NotEmpty
    private final String subject;
    @NotEmpty
    private final String text;
}
