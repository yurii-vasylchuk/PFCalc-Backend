package org.mvasylchuk.pfcc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.user.Language;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private String email;
    private String name;
    private Language preferredLanguage;
    private PfccDto aims;
    private Boolean profileConfigured;
}
