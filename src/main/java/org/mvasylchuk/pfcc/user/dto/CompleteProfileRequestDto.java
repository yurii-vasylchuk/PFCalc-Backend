package org.mvasylchuk.pfcc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;

@Getter
@AllArgsConstructor
public class CompleteProfileRequestDto {
    private PfccDto aims;

}
