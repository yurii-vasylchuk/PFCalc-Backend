package org.mvasylchuk.pfcc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mvasylchuk.pfcc.user.Language;

@Getter
@AllArgsConstructor
public class RegisterRequestDto {
    private String email;
    private String password;
    private String name;
    private Language preferredLanguage;
}
