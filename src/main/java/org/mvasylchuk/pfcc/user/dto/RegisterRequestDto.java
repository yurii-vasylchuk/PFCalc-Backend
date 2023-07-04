package org.mvasylchuk.pfcc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterRequestDto {
    private String email;
    private String password;
}
