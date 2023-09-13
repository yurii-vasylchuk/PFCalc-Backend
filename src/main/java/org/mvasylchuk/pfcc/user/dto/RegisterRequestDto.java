package org.mvasylchuk.pfcc.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mvasylchuk.pfcc.user.Language;

@Getter
@AllArgsConstructor
public class RegisterRequestDto {
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Size(min = 4)
    private String password;
    @NotEmpty
    private String name;
    @NotNull
    private Language preferredLanguage;
}
