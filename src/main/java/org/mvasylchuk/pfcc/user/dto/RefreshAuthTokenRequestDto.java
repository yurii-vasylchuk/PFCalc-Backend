package org.mvasylchuk.pfcc.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshAuthTokenRequestDto {
    @NotEmpty
    private String refreshToken;
}
