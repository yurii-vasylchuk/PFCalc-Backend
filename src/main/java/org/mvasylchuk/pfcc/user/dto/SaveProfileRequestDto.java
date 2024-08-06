package org.mvasylchuk.pfcc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveProfileRequestDto {
    private PfccDto aims;
    private String name;
    private String preferredLanguage;
    private String password;
}
